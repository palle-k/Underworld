/******************************************************************************
 * Copyright (c) 2015 Palle Klewitz.                                          *
 * *
 * Permission is hereby granted, free of charge, to any person obtaining      *
 * a copy of this software and associated documentation files                 *
 * (the "Software"), to deal in the Software without restriction,             *
 * including without limitation the rights to use, copy, modify,             *
 * merge, publish, distribute, sublicense, and/or sell copies of             *
 * the Software, and to permit persons to whom the Software                  *
 * is furnished to do so, subject to the following conditions:               *
 * *
 * The above copyright notice and this permission notice shall                *
 * be included in all copies or substantial portions of the Software.         *
 * *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY                         *
 * OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT                        *
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS                     *
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.                             *
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS                        *
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,                      *
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,                      *
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE                            *
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.                    *
 ******************************************************************************/

package project.game.ui.controllers;

import project.game.data.Level;
import project.game.data.state.SavedGameState;
import project.game.ui.views.MapView;
import project.gui.components.TLabel;
import project.gui.components.TProgressBar;
import project.gui.controller.ViewController;
import project.gui.dynamics.animation.Animation;
import project.gui.dynamics.animation.AnimationHandler;
import project.gui.event.TEvent;
import project.gui.event.TEventHandler;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class LevelViewController extends ViewController
{
	private long    lastMoveTime;
	private Level   level;
	private MapView mapView;
	private boolean moveDownKeyPressed;
	private boolean moveLeftKeyPressed;
	private boolean moveRightKeyPressed;
	private boolean moveUpKeyPressed;

	@Override
	public void initializeView()
	{
		super.initializeView();
		try
		{
			level = new Level(Level.class.getResource("levels/level_big_sparse.properties"));
		} catch (IOException e)
		{
			e.printStackTrace();
			return;
		}

		TLabel playerHealthLabel = new TLabel();
		playerHealthLabel.setSize(10, 1);
		playerHealthLabel.setText("Health:");
		//getView().add(playerHealthLabel);

		TProgressBar playerHealth = new TProgressBar();
		playerHealth.setLocation(10, 0);
		playerHealth.setSize(30, 1);
		playerHealth.setValue(6.0);
		playerHealth.setMaxValue(10.0);
		//getView().add(playerHealth);

		mapView = new MapView();
		mapView.setSize(level.getWidth(), level.getHeight());
		mapView.setLevel(level);
		mapView.setMaskToBounds(true);
		getView().add(mapView);

		Animation scrollAnimation = new Animation(new AnimationHandler()
		{
			@Override
			public void updateAnimation(final double value)
			{
				mapView.setOffset(new Point(
						level.getPath()[(int) value].x - getView().getWidth() / 2,
						level.getPath()[(int) value].y - getView().getHeight() / 2));
				mapView.setPointOfVision(level.getPath()[(int) value]);
			}
		});
		scrollAnimation.setFromValue(0);
		scrollAnimation.setToValue(level.getPath().length - 1);
		scrollAnimation.setDuration(300);
		scrollAnimation.setInterpolationMode(Animation.ANIMATION_CURVE_LINEAR);
		//levelView.addAnimation(scrollAnimation);

		getView().setAllowsFirstResponder(true);
		getView().setEventHandler(new TEventHandler()
		{
			@Override
			public void keyDown(final TEvent event)
			{
				if (event.getKey() == KeyEvent.VK_ESCAPE)
					getNavigationController().pop();
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveUpKey())
				{
					moveUpKeyPressed = true;
					moveLeftKeyPressed = false;
					moveRightKeyPressed = false;
					moveDownKeyPressed = false;
				}
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveLeftKey())
				{
					moveUpKeyPressed = false;
					moveLeftKeyPressed = true;
					moveRightKeyPressed = false;
					moveDownKeyPressed = false;
				}
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveRightKey())
				{
					moveUpKeyPressed = false;
					moveLeftKeyPressed = false;
					moveRightKeyPressed = true;
					moveDownKeyPressed = false;
				}
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveDownKey())
				{
					moveUpKeyPressed = false;
					moveLeftKeyPressed = false;
					moveRightKeyPressed = false;
					moveDownKeyPressed = true;
				}

			}

			@Override
			public void keyUp(final TEvent event)
			{
				if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveUpKey())
				{
					moveUpKeyPressed = false;
					lastMoveTime = 0;
				}
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveLeftKey())
				{
					moveLeftKeyPressed = false;
					lastMoveTime = 0;
				}
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveRightKey())
				{
					moveRightKeyPressed = false;
					lastMoveTime = 0;
				}
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveDownKey())
				{
					moveDownKeyPressed = false;
					lastMoveTime = 0;
				}
			}
		});
		getView().requestFirstResponder();
	}

	@Override
	protected void updateViews(final double time, final double timeDelta)
	{
		super.updateViews(time, timeDelta);

		if (level == null)
			return;

		if (moveUpKeyPressed)
		{
			if (lastMoveTime == 0)
				lastMoveTime = (long) (level.getPlayer().getSpeed() * time);
			long moveTime = (long) (level.getPlayer().getSpeed() * time);
			long steps    = moveTime - lastMoveTime;

			for (int i = 0; i < steps; i++)
			{
				Point     topLeft      = new Point(
						(int) level.getPlayer().getBounds().getMinX(),
						level.getPlayer().getBounds().y - 1);
				Point     topRight     = new Point(
						(int) level.getPlayer().getBounds().getMaxX(),
						level.getPlayer().getBounds().y - 1);
				Rectangle playerBounds = level.getPlayer().getBounds();
				if (level.getMap().canMoveTo(topLeft))
				{
					playerBounds.translate(0, -1);
					level.getPlayer().setBounds(playerBounds);
				}
			}
			lastMoveTime = moveTime;
		}
		else if (moveLeftKeyPressed)
		{
			if (lastMoveTime == 0)
				lastMoveTime = (long) (level.getPlayer().getSpeed() * time);
			long moveTime = (long) (level.getPlayer().getSpeed() * time);
			long steps    = moveTime - lastMoveTime;

			for (int i = 0; i < steps; i++)
			{
				Point topLeft    = new Point(
						(int) level.getPlayer().getBounds().getMinX() - 1,
						level.getPlayer().getBounds().y);
				Point bottomLeft = new Point(
						(int) level.getPlayer().getBounds().getMinX() - 1,
						(int) level.getPlayer().getBounds().getMaxY());
				if (level.getMap().canMoveTo(topLeft) && level.getMap().canMoveTo(bottomLeft))
				{
					Rectangle playerBounds = level.getPlayer().getBounds();
					playerBounds.translate(-1, 0);
					level.getPlayer().setBounds(playerBounds);
					System.out.printf("move player left\n");
				}
			}
			lastMoveTime = moveTime;
		}
		else if (moveRightKeyPressed)
		{
			if (lastMoveTime == 0)
				lastMoveTime = (long) (level.getPlayer().getSpeed() * time);
			long moveTime = (long) (level.getPlayer().getSpeed() * time);
			long steps    = moveTime - lastMoveTime;

			for (int i = 0; i < steps; i++)
			{
				Point topRight    = new Point(
						(int) level.getPlayer().getBounds().getMaxX() + 1,
						level.getPlayer().getBounds().y);
				Point bottomRight = new Point(
						(int) level.getPlayer().getBounds().getMaxX() + 1,
						(int) level.getPlayer().getBounds().getMaxY());
				if (level.getMap().canMoveTo(bottomRight) && level.getMap().canMoveTo(topRight))
				{
					Rectangle playerBounds = level.getPlayer().getBounds();
					playerBounds.translate(1, 0);
					level.getPlayer().setBounds(playerBounds);
					System.out.printf("move player right\n");
				}
			}
			lastMoveTime = moveTime;
		}
		else if (moveDownKeyPressed)
		{
			if (lastMoveTime == 0)
				lastMoveTime = (long) (level.getPlayer().getSpeed() * time);
			long moveTime = (long) (level.getPlayer().getSpeed() * time);
			long steps    = moveTime - lastMoveTime;

			for (int i = 0; i < steps; i++)
			{
				Point bottomLeft  = new Point(
						(int) level.getPlayer().getBounds().getMinX(),
						(int) level.getPlayer().getBounds().getMaxY() + 1);
				Point bottomRight = new Point(
						(int) level.getPlayer().getBounds().getMaxX(),
						(int) level.getPlayer().getBounds().getMaxY() + 1);
				if (level.getMap().canMoveTo(bottomLeft) && level.getMap().canMoveTo(bottomRight))
				{
					Rectangle playerBounds = level.getPlayer().getBounds();
					playerBounds.translate(0, 1);
					level.getPlayer().setBounds(playerBounds);
				}
			}
			lastMoveTime = moveTime;
		}

		Point scrollCenter = level.getPlayer().getBounds().getLocation();
		scrollCenter.translate(getView().getWidth() / 2, getView().getHeight() / 2);
		mapView.setOffset(scrollCenter);
		mapView.setPointOfVision(level.getPlayer().getBounds().getLocation());
	}
}
