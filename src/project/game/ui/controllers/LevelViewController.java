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
import project.game.ui.views.LevelView;
import project.gui.components.TLabel;
import project.gui.components.TProgressBar;
import project.gui.components.TScrollView;
import project.gui.controller.ViewController;
import project.gui.dynamics.animation.Animation;
import project.gui.event.TEvent;
import project.gui.event.TEventHandler;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class LevelViewController extends ViewController
{
	private boolean moveDownKeyPressed;
	private boolean moveLeftKeyPressed;
	private boolean moveRightKeyPressed;
	private boolean moveUpKeyPressed;

	@Override
	public void initializeView()
	{
		super.initializeView();
		Level level;
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

		LevelView levelView = new LevelView();
		levelView.setSize(level.getWidth(), level.getHeight());
		levelView.setLevel(level);
		levelView.setMaskToBounds(true);

		TScrollView scrollView = new TScrollView(levelView);
		scrollView.setFrame(new Rectangle(0, 0, getView().getWidth(), getView().getHeight()));
		scrollView.setMaskToBounds(true);

		Animation scrollAnimation = new Animation((double value) -> scrollView.setOffset(new Dimension(level.getPath()[(int) value].x - getView().getWidth() / 2, level.getPath()[(int) value].y - getView().getHeight() / 2)));
		scrollAnimation.setFromValue(0);
		scrollAnimation.setToValue(level.getPath().length - 1);
		scrollAnimation.setDuration(60);
		scrollAnimation.setInterpolationMode(Animation.ANIMATION_CURVE_LINEAR);
		scrollView.addAnimation(scrollAnimation);

		getView().add(scrollView);
		getView().setAllowsFirstResponder(true);
		getView().setEventHandler(new TEventHandler()
		{
			@Override
			public void keyDown(final TEvent event)
			{
				if (event.getKey() == KeyEvent.VK_ESCAPE)
					getNavigationController().pop();
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveUpKey())
					moveUpKeyPressed = true;
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveLeftKey())
					moveLeftKeyPressed = true;
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveRightKey())
					moveRightKeyPressed = true;
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveDownKey())
					moveDownKeyPressed = true;

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
	}
}
