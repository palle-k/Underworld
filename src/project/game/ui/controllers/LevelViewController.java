/******************************************************************************
 * Copyright (c) 2016 Palle Klewitz.                                          *
 *                                                                            *
 * Permission is hereby granted, free of charge, to any person obtaining      *
 * a copy of this software and associated documentation files                 *
 * (the "Software"), to deal in the Software without restriction,             *
 *  including without limitation the rights to use, copy, modify,             *
 *  merge, publish, distribute, sublicense, and/or sell copies of             *
 *  the Software, and to permit persons to whom the Software                  *
 *  is furnished to do so, subject to the following conditions:               *
 *                                                                            *
 * The above copyright notice and this permission notice shall                *
 * be included in all copies or substantial portions of the Software.         *
 *                                                                            *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY                         *
 *  OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT                        *
 *  LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS                     *
 *  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.                             *
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS                        *
 *  BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,                      *
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,                      *
 *  ARISING FROM, OUT OF OR IN CONNECTION WITH THE                            *
 *  SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.                    *
 ******************************************************************************/

package project.game.ui.controllers;

import project.game.data.*;
import project.game.data.state.SavedGameState;
import project.game.ui.views.MapView;
import project.gui.components.TLabel;
import project.gui.components.TProgressBar;
import project.gui.controller.ViewController;
import project.gui.dynamics.animation.Animation;
import project.gui.dynamics.animation.AnimationHandler;
import project.gui.event.TEvent;
import project.gui.event.TEventHandler;
import project.gui.layout.FullSizeSubviewLayout;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class LevelViewController extends ViewController implements PlayerDelegate, GameActorDelegate, MapObjectDelegate
{
	private EnemyController[] enemyControllers;
	private Level             level;
	private MapView           mapView;
	private boolean           moveDownKeyPressed;
	private boolean           moveLeftKeyPressed;
	private boolean           moveRightKeyPressed;
	private boolean           moveUpKeyPressed;
	private PlayerController  playerController;

	@Override
	public void actorDidChangeState(final GameActor actor)
	{

	}

	@Override
	public void initializeView()
	{
		super.initializeView();
		try
		{
			level = new Level(Level.class.getResource("levels/level_big_sparse.properties"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}

		getView().setOnAnimationUpdate((double time, double timeDelta) -> updateViews(time, timeDelta));

		playerController = new PlayerController(level.getPlayer(), level.getMap(), null);

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
		//mapView.setSize(level.getWidth(), level.getHeight());
		mapView.setLevel(level);
		mapView.setMaskToBounds(true);
		getView().add(mapView);

		getView().setLayoutManager(new FullSizeSubviewLayout());

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
					moveUpKeyPressed = false;
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveLeftKey())
					moveLeftKeyPressed = false;
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveRightKey())
					moveRightKeyPressed = false;
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveDownKey())
					moveDownKeyPressed = false;
			}
		});
		getView().requestFirstResponder();
	}

	@Override
	public void mapObjectDidMove(final MapObject mapObject)
	{

	}

	@Override
	public void playerShouldShowAttackPotionOverlay(final Player player)
	{

	}

	@Override
	public void playerShouldShowHealthPotionOverlay(final Player player)
	{

	}

	@Override
	public void playerShouldShowSkill1Overlay(final Player player, final GameActor target)
	{

	}

	@Override
	public void playerShouldShowSkill1State(final Player player)
	{

	}

	@Override
	public void playerShouldShowSkill2Overlay(final Player player, final GameActor target)
	{

	}

	@Override
	public void playerShouldShowSkill2State(final Player player)
	{

	}

	@Override
	public void playerShouldShowSkill3Overlay(final Player player, final GameActor target)
	{

	}

	@Override
	public void playerShouldShowSkill3State(final Player player)
	{

	}

	@Override
	public void playerShouldShowSkill4Overlay(final Player player, final GameActor target)
	{

	}

	@Override
	public void playerShouldShowSkill4State(final Player player)
	{

	}

	@Override
	protected void updateViews(final double time, final double timeDelta)
	{
		super.updateViews(time, timeDelta);
		playerController.update(time);

		if (level == null)
			return;

		if (moveUpKeyPressed)
			playerController.moveUp();
		else if (moveLeftKeyPressed)
			playerController.moveLeft();
		else if (moveRightKeyPressed)
			playerController.moveRight();
		else if (moveDownKeyPressed)
			playerController.moveDown();

		Point scrollCenter = level.getPlayer().getBounds().getLocation();
		scrollCenter.translate(-getView().getWidth() / 2, -getView().getHeight() / 2);
		mapView.setOffset(scrollCenter);
		mapView.setPointOfVision(level.getPlayer().getCenter());
	}
}
