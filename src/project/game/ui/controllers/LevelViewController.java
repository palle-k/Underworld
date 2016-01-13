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
import java.util.Arrays;
import java.util.HashMap;

import static project.game.localization.LocalizedString.LocalizedString;

public class LevelViewController extends ViewController
{
	private boolean                        attacksEnabled;
	private TLabel                         collectedKeys;
	private boolean                        damageEnabled;
	private EnemyController[]              enemyControllers;
	private TProgressBar                   enemyHealth;
	private TLabel                         enemyLevel;
	private TLabel                         enemyName;
	private boolean                        finished;
	private boolean                        initialized;
	private java.util.Map<Key, TComponent> keyViews;
	private Level                          level;
	private MapView                        mapView;
	private boolean                        moveDownKeyPressed;
	private boolean                        moveLeftKeyPressed;
	private boolean                        moveRightKeyPressed;
	private boolean                        moveUpKeyPressed;
	private Runnable                       onLevelCancel;
	private Runnable                       onLevelFailure;
	private Runnable                       onLevelFinish;
	private PlayerController               playerController;
	private TProgressBar                   playerExperience;
	private TProgressBar                   playerHealth;
	private TLabel                         playerLabel;
	private TLabel                         playerLevel;
	private boolean                        skillsEnabled;

	public LevelViewController(Level level)
	{
		this.level = level;
		attacksEnabled = true;
		skillsEnabled = true;
		damageEnabled = true;
	}

	public boolean attacksEnabled()
	{
		return attacksEnabled;
	}

	public boolean damageEnabled()
	{
		return damageEnabled;
	}

	public Runnable getOnLevelCancel()
	{
		return onLevelCancel;
	}

	public Runnable getOnLevelFailure()
	{
		return onLevelFailure;
	}

	public Runnable getOnLevelFinish()
	{
		return onLevelFinish;
	}

	public void setAttacksEnabled(final boolean attacksEnabled)
	{
		this.attacksEnabled = attacksEnabled;
	}

	public void setDamageEnabled(final boolean damageEnabled)
	{
		this.damageEnabled = damageEnabled;
	}

	public void setOnLevelCancel(final Runnable onLevelCancel)
	{
		this.onLevelCancel = onLevelCancel;
	}

	public void setOnLevelFailure(final Runnable onLevelFailure)
	{
		this.onLevelFailure = onLevelFailure;
	}

	public void setOnLevelFinish(final Runnable onLevelFinish)
	{
		this.onLevelFinish = onLevelFinish;
	}

	public void setSkillsEnabled(final boolean skillsEnabled)
	{
		this.skillsEnabled = skillsEnabled;
	}

	public boolean skillsEnabled()
	{
		return skillsEnabled;
	}

	@Override
	protected void initializeView()
	{
		super.initializeView();

		TComponent topBar = new TComponent();
		getView().add(topBar);

		TLabel playerHealthLabel = new TLabel();
		playerHealthLabel.setSize(10, 1);
		playerHealthLabel.setText(LocalizedString("level_view_current_health"));
		topBar.add(playerHealthLabel);

		playerHealth = new TProgressBar();
		playerHealth.setLocation(10, 0);
		playerHealth.setSize(40, 1);
		playerHealth.setValue(level.getPlayer().getCurrentHealth());
		playerHealth.setMaxValue(level.getPlayer().getMaxHealth());
		playerHealth.setColor(Color.RED);
		topBar.add(playerHealth);

		TLabel playerExperienceLabel = new TLabel();
		playerExperienceLabel.setSize(10, 1);
		playerExperienceLabel.setLocation(0, 1);
		playerExperienceLabel.setText(LocalizedString("level_view_current_exp"));
		topBar.add(playerExperienceLabel);

		playerExperience = new TProgressBar();
		playerExperience.setLocation(10, 1);
		playerExperience.setSize(40, 1);
		playerExperience.setMaxValue(level.getPlayer().getLevelEndExperience());
		playerExperience.setMinValue(level.getPlayer().getLevelStartExperience());
		playerExperience.setValue(level.getPlayer().getExperience());
		playerExperience.setColor(Color.CYAN);
		topBar.add(playerExperience);

		collectedKeys = new TLabel();
		collectedKeys.setLocation(52, 0);
		collectedKeys.setSize(30, 1);
		collectedKeys.setText(String.format(
				LocalizedString("level_view_collected_keys"),
				level.getCollectedKeyCount(),
				level.getKeys().length));
		topBar.add(collectedKeys);

		playerLevel = new TLabel();
		playerLevel.setLocation(52, 1);
		playerLevel.setSize(20, 1);
		playerLevel.setText(String.format(LocalizedString("level_view_current_level"), level.getPlayer().getLevel()));
		topBar.add(playerLevel);

		enemyName = new TLabel();
		enemyName.setFrame(new Rectangle(76, 1, 20, 1));
		enemyName.setText("Enemy Name");
		enemyName.setVisible(false);
		topBar.add(enemyName);

		enemyLevel = new TLabel();
		enemyLevel.setText("Level 3");
		enemyLevel.setFrame(new Rectangle(98, 1, 20, 1));
		enemyLevel.setVisible(false);
		topBar.add(enemyLevel);

		enemyHealth = new TProgressBar();
		enemyHealth.setMinValue(0);
		enemyHealth.setMaxValue(42);
		enemyHealth.setValue(13.37);
		enemyHealth.setColor(Color.RED);
		enemyHealth.setFrame(new Rectangle(76, 0, 40, 1));
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
		keyViews = new HashMap<>();
		Arrays.stream(keys)
				.filter(key -> !key.isCollected())
				.forEach((key) -> {
					TLabel keyLabel = new TLabel();
					keyLabel.setLocation(key.getBounds().getLocation());
					keyLabel.setSize(key.getBounds().getSize());
					keyLabel.setColor(key.getColor());
					keyLabel.setText(key.getRestingState());
					mapView.add(keyLabel);
					keyViews.put(key, keyLabel);
				});
		/*
		for (Key key : keys)
		{
			TLabel keyLabel = new TLabel();
			keyLabel.setLocation(key.getBounds().getLocation());
			keyLabel.setSize(key.getBounds().getSize());
			keyLabel.setColor(key.getColor());
			keyLabel.setText(key.getRestingState());
			mapView.add(keyLabel);
			keyViews.put(key, keyLabel);
		}
		*/

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

		playerController = new PlayerController(level, playerLabel, this);

		TComponent bottomBar = new TComponent();
		getView().add(bottomBar);

		getView().setLayoutManager(component ->
		                           {
			                           TComponent[] children = component.getChildren();
			                           if (children.length != 3)
				                           throw new RuntimeException(
						                           "View managed by level view controller layout must have 3 children.");
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
				{
					if (onLevelCancel != null)
						onLevelCancel.run();
				}
				else if (event.getKey() == SavedGameState.getSettingsState().getMoveUpKey())
					moveUpKeyPressed = true;
				else if (event.getKey() == SavedGameState.getSettingsState().getMoveLeftKey())
					moveLeftKeyPressed = true;
				else if (event.getKey() == SavedGameState.getSettingsState().getMoveRightKey())
					moveRightKeyPressed = true;
				else if (event.getKey() == SavedGameState.getSettingsState().getMoveDownKey())
					moveDownKeyPressed = true;
				else if (attacksEnabled &&
				         event.getKey() == SavedGameState.getSettingsState().getBaseAttackKey())
					playerController.attackEnemy();
				else if (skillsEnabled)
					if (event.getKey() == SavedGameState.getSettingsState().getSkill1Key())
						playerController.attackSkill1();
					else if (event.getKey() == SavedGameState.getSettingsState().getSkill2Key())
						playerController.attackSkill2();
					else if (event.getKey() == SavedGameState.getSettingsState().getSkill3Key())
						playerController.attackSkill3();
					else if (event.getKey() == SavedGameState.getSettingsState().getSkill4Key())
						playerController.attackSkill4();

			}

			@Override
			public void keyUp(final TEvent event)
			{
				if (event.getKey() == SavedGameState.getSettingsState().getMoveUpKey())
					moveUpKeyPressed = false;
				else if (event.getKey() == SavedGameState.getSettingsState().getMoveLeftKey())
					moveLeftKeyPressed = false;
				else if (event.getKey() == SavedGameState.getSettingsState().getMoveRightKey())
					moveRightKeyPressed = false;
				else if (event.getKey() == SavedGameState.getSettingsState().getMoveDownKey())
					moveDownKeyPressed = false;
			}
		});
		getView().requestFirstResponder();
		initialized = true;
	}

	protected void playerDidReachEntrance()
	{
		//TODO implement going to previous level
	}

	protected void playerDidReachExit()
	{
		if (onLevelFinish == null)
			return;
		//prevent comodification of getView().updateAnimations()
		//by scheduling the execution of the message dialog on
		//the swing thread.
		SwingUtilities.invokeLater(onLevelFinish);
	}

	protected void updateCollectedKeys()
	{
		collectedKeys.setText(String.format(
				LocalizedString("level_view_collected_keys"),
				level.getCollectedKeyCount(),
				level.getKeys().length));
		if (level.getCollectedKeyCount() == level.getKeys().length)
			collectedKeys.setColor(Color.GREEN);
		else
			collectedKeys.setColor(Color.WHITE);
		Arrays.stream(level.getKeys())
				.filter(Key::isCollected)
				.filter(key -> keyViews.containsKey(key))
				.forEach(key ->
				         {
					         mapView.remove(keyViews.get(key));
					         keyViews.remove(key);
				         });
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
					LocalizedString("level_view_current_level"),
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
			deathNotification.setMessage(LocalizedString("level_view_death_notification"));
			deathNotification.setDelegate(new DialogDelegate()
			{
				@Override
				public void dialogDidCancel(final Dialog dialog)
				{

				}

				@Override
				public void dialogDidReturn(final Dialog dialog)
				{
					if (onLevelFailure != null)
						onLevelFailure.run();
				}
			});
			getNavigationController().push(deathNotification);
		};
		//prevent comodification of getView().updateAnimations()
		//by scheduling the execution of the message dialog on
		//the swing thread.
		SwingUtilities.invokeLater(r);
	}

	@Override
	protected void updateViews(final double time, final double timeDelta)
	{
		super.updateViews(time, timeDelta);
		if (!initialized || finished)
			return;

		if (!level.getPlayer().isAlive())
			return;

		playerController.update(time);

		if (damageEnabled)
			for (EnemyController enemyController : enemyControllers)
				enemyController.update(time);

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
