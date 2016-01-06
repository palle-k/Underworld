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

import project.game.data.Enemy;
import project.game.data.Key;
import project.game.data.Level;
import project.game.data.state.SavedGameState;
import project.game.localization.LocalizedString;
import project.game.ui.views.MapView;
import project.gui.components.TComponent;
import project.gui.components.TLabel;
import project.gui.components.TProgressBar;
import project.gui.controller.ViewController;
import project.gui.controller.dialog.Dialog;
import project.gui.controller.dialog.DialogDelegate;
import project.gui.controller.dialog.MessageDialog;
import project.gui.event.TEvent;
import project.gui.event.TEventHandler;
import project.util.StringUtils;

import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class LevelViewController extends ViewController
{
	private TLabel            collectedKeys;
	private EnemyController[] enemyControllers;
	private TProgressBar      enemyHealth;
	private TLabel            enemyLevel;
	private TLabel            enemyName;
	private boolean           initialized;
	private Level             level;
	private MapView           mapView;
	private boolean           moveDownKeyPressed;
	private boolean           moveLeftKeyPressed;
	private boolean           moveRightKeyPressed;
	private boolean           moveUpKeyPressed;
	private PlayerController  playerController;
	private TProgressBar      playerExperience;
	private TProgressBar      playerHealth;
	private TLabel            playerLabel;
	private TLabel            playerLevel;

	@Override
	protected void initializeView()
	{
		super.initializeView();
		try
		{
			level = new Level(Level.class.getResource("levels/tutorial_4.properties"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}

		TComponent topBar = new TComponent();
		getView().add(topBar);

		TLabel playerHealthLabel = new TLabel();
		playerHealthLabel.setSize(10, 1);
		playerHealthLabel.setText("Health:");
		topBar.add(playerHealthLabel);

		playerHealth = new TProgressBar();
		playerHealth.setLocation(10, 0);
		playerHealth.setSize(40, 1);
		playerHealth.setValue(10.0);
		playerHealth.setMaxValue(10.0);
		playerHealth.setColor(Color.RED);
		topBar.add(playerHealth);
		
		TLabel playerExperienceLabel = new TLabel();
		playerExperienceLabel.setSize(10, 1);
		playerExperienceLabel.setLocation(0, 1);
		playerExperienceLabel.setText("EXP:");
		topBar.add(playerExperienceLabel);

		playerExperience = new TProgressBar();
		playerExperience.setLocation(10, 1);
		playerExperience.setSize(40, 1);
		playerExperience.setValue(0.0);
		playerExperience.setMaxValue(10.0);
		playerExperience.setColor(Color.CYAN);
		topBar.add(playerExperience);

		collectedKeys = new TLabel();
		collectedKeys.setLocation(52, 0);
		collectedKeys.setSize(30, 1);
		collectedKeys.setText(String.format("Collected Keys: %d", 42));
		topBar.add(collectedKeys);

		TLabel playerLevel = new TLabel();
		playerLevel.setLocation(52, 1);
		playerLevel.setSize(20, 1);
		playerLevel.setText(String.format("Level %d", 42));
		topBar.add(playerLevel);

		enemyName = new TLabel();
		enemyName.setFrame(new Rectangle(76, 0, 20, 1));
		enemyName.setText("Enemy Name");
		enemyName.setVisible(false);
		topBar.add(enemyName);

		enemyLevel = new TLabel();
		enemyLevel.setText("Level 3");
		enemyLevel.setFrame(new Rectangle(98, 0, 20, 1));
		enemyLevel.setVisible(false);
		topBar.add(enemyLevel);

		enemyHealth = new TProgressBar();
		enemyHealth.setMinValue(0);
		enemyHealth.setMaxValue(42);
		enemyHealth.setValue(13.37);
		enemyHealth.setColor(Color.RED);
		enemyHealth.setFrame(new Rectangle(76, 1, 40, 1));
		enemyHealth.setVisible(false);
		topBar.add(enemyHealth);

		mapView = new MapView();
		mapView.setLevel(level);
		mapView.setMaskToBounds(true);
		getView().add(mapView);

		Rectangle  entranceBounds = level.getEntranceBounds();
		TComponent entrance       = new TComponent();
		entrance.setFrame(entranceBounds);
		entrance.setBackgroundColor(new Color(0, 100, 150));
		entrance.setDrawsBackground(true);
		mapView.add(entrance);

		Rectangle[] exitBounds = level.getExitBounds();
		for (Rectangle bounds : exitBounds)
		{
			TComponent finishView = new TComponent();
			finishView.setDrawsBackground(true);
			finishView.setBackgroundColor(new Color(150, 50, 50));
			finishView.setFrame(bounds);
			mapView.add(finishView);
		}

		Key[] keys = level.getKeys();
		for (Key key : keys)
		{
			TLabel keyLabel = new TLabel();
			keyLabel.setLocation(key.getBounds().getLocation());
			keyLabel.setSize(key.getBounds().getSize());
			keyLabel.setColor(key.getColor());
			keyLabel.setText(key.getRestingState());
			mapView.add(keyLabel);
		}

		Enemy[] enemies = level.getEnemies();
		enemyControllers = new EnemyController[enemies.length];
		for (int i = 0, enemiesLength = enemies.length; i < enemiesLength; i++)
		{
			Enemy  enemy      = enemies[i];
			TLabel enemyLabel = new TLabel();
			enemyLabel.setText(enemy.getRestingState());
			enemyLabel.setLocation(enemy.getBounds().getLocation());
			enemyLabel.setSize(StringUtils.getStringDimensions(enemy.getRestingState()));
			enemyLabel.setColor(enemy.getColor());
			enemyLabel.setMaskToBounds(false);
			mapView.add(enemyLabel);
			enemyControllers[i] = new EnemyController(enemy, level.getMap(), level.getPlayer(), enemyLabel);
		}

		playerLabel = new TLabel();
		playerLabel.setLocation(level.getPlayer().getBounds().getLocation());
		playerLabel.setSize(StringUtils.getStringDimensions(level.getPlayer().getRestingState()));
		playerLabel.setText(level.getPlayer().getRestingState());
		playerLabel.setColor(level.getPlayer().getColor());
		mapView.add(playerLabel);

		playerController = new PlayerController(level.getPlayer(), level.getMap(), enemies, playerLabel, this);

		TComponent bottomBar = new TComponent();
		getView().add(bottomBar);

		getView().setLayoutManager(component ->
		{
			TComponent[] children = component.getChildren();
			if (children.length != 3)
				throw new RuntimeException("View managed by level view controller layout must have 3 children.");
			int height    = component.getHeight();
			int width     = component.getWidth();
			int barHeight = 4;
			children[0].setLocation(0, 0);
			children[0].setSize(width, barHeight);
			children[1].setLocation(0, barHeight);
			children[1].setSize(width, height - 2 * barHeight - 1);
			children[2].setLocation(0, height - barHeight - 1);
			children[2].setSize(width, barHeight);
		});

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
				}
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveLeftKey())
				{
					moveLeftKeyPressed = true;
				}
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveRightKey())
				{
					moveRightKeyPressed = true;
				}
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveDownKey())
				{
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
		initialized = true;
	}

	protected void updateCollectedKeys()
	{

	}

	protected void updateFocussedEnemy(Enemy enemy)
	{
		boolean enemyNotNull = enemy != null;
		enemyName.setVisible(enemyNotNull);
		enemyLevel.setVisible(enemyNotNull);
		enemyHealth.setVisible(enemyNotNull);
		if (enemyNotNull)
		{
			enemyName.setText(enemy.getName());
			enemyLevel.setText("Level" + enemy.getLevel());
			enemyHealth.setMaxValue(enemy.getMaxHealth());
			enemyHealth.setMinValue(0);
			enemyHealth.setValue(enemy.getCurrentHealth());
		}
	}

	protected void updatePlayerExperience()
	{
		if (playerLevel != null)
			playerLevel.setText(String.format(
					LocalizedString.LocalizedString("level_player_level"),
					level.getPlayer().getLevel()));
		if (playerExperience == null)
			return;
		playerExperience.setMaxValue(level.getPlayer().getLevelEndExperience());
		playerExperience.setMinValue(level.getPlayer().getLevelStartExperience());
		playerExperience.setValue(level.getPlayer().getExperience());
	}

	protected void updatePlayerHealth()
	{
		if (playerHealth == null)
			return;
		playerHealth.setMaxValue(level.getPlayer().getMaxHealth());
		playerHealth.setValue(level.getPlayer().getCurrentHealth());

		if (level.getPlayer().isAlive())
			return;
		Runnable r = () ->
		{
			MessageDialog deathNotification = new MessageDialog();
			deathNotification.setMessage("U DIEDED\nRest in pieces");
			deathNotification.setDelegate(new DialogDelegate()
			{
				@Override
				public void dialogDidCancel(final Dialog dialog)
				{

				}

				@Override
				public void dialogDidReturn(final Dialog dialog)
				{
					getNavigationController().pop();
				}
			});
			getNavigationController().push(deathNotification);
		};
		SwingUtilities.invokeLater(r);
	}

	@Override
	protected void updateViews(final double time, final double timeDelta)
	{
		super.updateViews(time, timeDelta);
		if (!initialized)
			return;
		playerController.update(time);

		if (!level.getPlayer().isAlive())
			return;

		for (EnemyController enemyController : enemyControllers)
		{
			enemyController.update(time);
		}

		if (moveUpKeyPressed)
			playerController.moveUp();
		if (moveLeftKeyPressed)
			playerController.moveLeft();
		if (moveRightKeyPressed)
			playerController.moveRight();
		if (moveDownKeyPressed)
			playerController.moveDown();

		Point scrollCenter = level.getPlayer().getBounds().getLocation();
		scrollCenter.translate(-mapView.getWidth() / 2, -mapView.getHeight() / 2);
		mapView.setOffset(scrollCenter);
		mapView.setPointOfVision(level.getPlayer().getCenter());
		playerLabel.setFrame(level.getPlayer().getBounds());
	}
}
