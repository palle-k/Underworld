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

import project.game.controllers.EnemyController;
import project.game.controllers.PlayerController;
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
import project.gui.layout.HorizontalFlowLayout;
import project.util.StringUtils;

import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashMap;

import static project.game.localization.LocalizedString.LocalizedString;

/**
 * ViewController zur Darstellung und Steuerung eines Levels
 * Verwaltung von Controllern fuer Gegner und Spieler
 * Annahme und Weitergabe von Nutzereingaben zur Steuerung des Spiels
 */
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

	private TLabel skill1Label;

	private TLabel skill2Label;

	private TLabel skill3Label;

	private TLabel skill4Label;

	private boolean                        skillsEnabled;

	/**
	 * Erstellt einen neuen LevelViewController, welcher
	 * das spezifizierte Level darstellt.
	 *
	 * @param level Zu praesentierendes Level
	 */
	public LevelViewController(Level level)
	{
		this.level = level;
		attacksEnabled = true;
		skillsEnabled = true;
		damageEnabled = true;
	}

	/**
	 * Gibt an, ob der Spieler in der Lage ist,
	 * Angriffe durchzufuehren.
	 * Kann mit setAttacksEnabled(boolean) modifiziert werden
	 * @return true, wenn Angriffe aktiv sind, sonst false
	 */
	public boolean attacksEnabled()
	{
		return attacksEnabled;
	}

	/**
	 * Gibt an, ob der Spieler angegriffen werden kann.
	 * Kann mit setDamageEnabled(boolean) modifiziert werden.
	 * @return true, wenn Schaden aktiv ist, sonst false
	 */
	public boolean damageEnabled()
	{
		return damageEnabled;
	}

	/**
	 * Gibt das Runnable-Objekt zurueck, welches beim manuellen
	 * Beenden des Levels durch druecken der ESC-Taste ausgefuehrt wird.
	 * Kann mit setOnLevelCancel(Runnable) gesetzt werden.
	 * @return Runnable zum manuellen Beenden des Levels
	 */
	public Runnable getOnLevelCancel()
	{
		return onLevelCancel;
	}

	/**
	 * Gibt das Runnable-Objekt zurueck, welches ausgefuehrt wird,
	 * falls der Spieler stirbt.
	 * Kann mit setOnLevelFailure(Runnable) gesetzt werden.
	 * @return Runnable, welches beim Tod des Spielers ausgefuehrt wird
	 */
	public Runnable getOnLevelFailure()
	{
		return onLevelFailure;
	}

	/**
	 * Gibt das Runnable-Objekt zurueck, welches ausgefuehrt wird,
	 * fallse der Spielers das Level beendet.
	 * Kann mit setOnLevelFinish(Runnable) modifiziert werden.
	 * @return Runnable, welches beim Beenden des Levels ausgefuehrt wird.
	 */
	public Runnable getOnLevelFinish()
	{
		return onLevelFinish;
	}

	/**
	 * Rueckmeldungsmethode fuer den PlayerController, um dem Hauptcontroller zu aktualisieren
	 * Wird aufgerufen, falls der Spieler den Eingang erreicht
	 */
	public void playerDidReachEntrance()
	{
		//TODO put this into a PlayerControllerDelegate-Interface
		//TODO implement going to previous level
	}

	/**
	 * Rueckmeldungsmethode fuer den PlayerController, um den Hauptcontroller zu aktualisieren.
	 * Wird aufgerufen, wenn der Spieler den Ausgang erreicht
	 */
	public void playerDidReachExit()
	{
		if (onLevelFinish == null)
			return;
		//prevent comodification of getView().updateAnimations()
		//by scheduling the execution of the message dialog on
		//the swing thread.
		SwingUtilities.invokeLater(onLevelFinish);
	}

	/**
	 * Aktiviert oder deaktiviert Angriffe des Spielers
	 *
	 * @param attacksEnabled gibt an, ob Angriffe aktiv oder inaktiv sein sollen
	 */
	public void setAttacksEnabled(final boolean attacksEnabled)
	{
		this.attacksEnabled = attacksEnabled;
	}

	/**
	 * Aktiviert oder deaktiviert den Erhalt von Schaden durch Angriffe von Gegnern
	 *
	 * @param damageEnabled gibt an, ob Schaden aktiviert sein soll
	 */
	public void setDamageEnabled(final boolean damageEnabled)
	{
		this.damageEnabled = damageEnabled;
	}

	/**
	 * Setzt das Runnable-Objekt, welches beim manuellen Beenden des Levels
	 * durch den Nutzer ausgefuehrt werden soll
	 * @param onLevelCancel Runnable, welches beim Abbruch des Levels ausgefuehrt wird
	 */
	public void setOnLevelCancel(final Runnable onLevelCancel)
	{
		this.onLevelCancel = onLevelCancel;
	}

	/**
	 * Setzt das Runnable-Objekt, welches beim Scheitern des Levels ausgefuehrt werden soll.
	 * Das Level ist gescheitert, sobald der Spieler stirbt.
	 * @param onLevelFailure Runnable, welches beim Scheitern ausgefuehrt wird
	 */
	public void setOnLevelFailure(final Runnable onLevelFailure)
	{
		this.onLevelFailure = onLevelFailure;
	}

	/**
	 * Setzt das Runnable-Objekt, welches beim Beenden des Levels ausgefuehrt werden soll.
	 * Das Level ist beendet, wenn der Spieler saemtliche Schluessel eingesammelt hat und
	 * einen Ausgang erreicht hat.
	 * @param onLevelFinish Runnable, welches beim Beenden des Levels ausgefuehrt wird
	 */
	public void setOnLevelFinish(final Runnable onLevelFinish)
	{
		this.onLevelFinish = onLevelFinish;
	}

	/**
	 * Setzt, ob besondere Attacken und Faehigkeiten des Spielers erlaubt sind.
	 * @param skillsEnabled true, wenn Faehigkeiten aktiviert werden sollen, sonst false
	 */
	public void setSkillsEnabled(final boolean skillsEnabled)
	{
		this.skillsEnabled = skillsEnabled;
	}

	/**
	 * Gibt an, ob besondere Attacken und Faehigkeiten des Spielers erlaubt sind.
	 * @return true, wenn besondere Attacken erlaubt sind, sonst false.
	 */
	public boolean skillsEnabled()
	{
		return skillsEnabled;
	}

	/**
	 * Rueckmeldungsmethode fuer den PlayerController, um den Hauptcontroller zu aktualisieren.
	 * Wird aufgerufen, wenn der Spieler einen Schluessel eingesammelt hat
	 */
	public void updateCollectedKeys()
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

	/**
	 * Rueckmeldungsmethode fuer den PlayerController, um den Hauptcontroller zu aktualisieren.
	 * Wird aufgerufen, falls der Spieler einen neuen Gegner anvisiert hat.
	 * @param enemy Neu anvisierter Gegner oder null, wenn kein Gegner anvisiert ist.
	 */
	public void updateFocussedEnemy(Enemy enemy)
	{
		boolean enemyNotNull = enemy != null;
		enemyName.setVisible(enemyNotNull);
		enemyLevel.setVisible(enemyNotNull);
		enemyHealth.setVisible(enemyNotNull);
		if (enemyNotNull)
		{
			enemyName.setText(enemy.getName());
			enemyLevel.setText(String.format(LocalizedString("level_view_current_level"), enemy.getLevel()));
			enemyHealth.setMaxValue(enemy.getMaxHealth());
			enemyHealth.setMinValue(0);
			enemyHealth.setValue(enemy.getCurrentHealth());
		}
	}

	/**
	 * Rueckmeldungsmethode fuer den PlayerController, um den Hauptcontroller zu aktualisieren.
	 * Wird aufgerufen, fals die Erfahrung des Spielers geaendert wurde
	 */
	public void updatePlayerExperience()
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

		skill1Label.setVisible(level.getPlayer().getSkill1().getRequiredLevel() <= level.getPlayer().getLevel());
		skill2Label.setVisible(level.getPlayer().getSkill2().getRequiredLevel() <= level.getPlayer().getLevel());
		skill3Label.setVisible(level.getPlayer().getSkill3().getRequiredLevel() <= level.getPlayer().getLevel());
		skill4Label.setVisible(level.getPlayer().getSkill4().getRequiredLevel() <= level.getPlayer().getLevel());
	}

	/**
	 * Rueckmeldungsmethode fuer den PlayerController, um den Hauptcontroller zu aktualisieren.
	 * Wird aufgerufen, wenn der Spieler Lebenspunkte verloren oder gewonnen hat.
	 */
	public void updatePlayerHealth()
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
	protected void initializeView()
	{
		super.initializeView();

		//Erstelle obere Leiste (Spielerinformationen, Gegnerinformationen)

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

		//Erstelle Levelansicht

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
		//enemyControllers = new EnemyController[enemies.length];
		/*for (int i = 0, enemiesLength = enemies.length; i < enemiesLength; i++)
		{
			Enemy  enemy      = enemies[i];
			if (enemy.isAlive())
			{
				TLabel enemyLabel = new TLabel();
				enemyLabel.setText(enemy.getRestingState());
				enemyLabel.setLocation(enemy.getBounds().getLocation());
				enemyLabel.setSize(StringUtils.getStringDimensions(enemy.getRestingState()));
				enemyLabel.setColor(enemy.getColor());
				enemyLabel.setMaskToBounds(false);
				mapView.add(enemyLabel);
				enemyControllers[i] = new EnemyController(enemy, level.getMap(), level.getPlayer(), enemyLabel, mapView);
			}
		}*/

		//Initialisiere Gegnersteuerung

		enemyControllers =
				Arrays.stream(enemies)
						.filter(Enemy::isAlive)
						.map(enemy -> {
							TLabel enemyLabel = new TLabel();
							enemyLabel.setText(enemy.getRestingState());
							enemyLabel.setLocation(enemy.getBounds().getLocation());
							enemyLabel.setSize(StringUtils.getStringDimensions(enemy.getRestingState()));
							enemyLabel.setColor(enemy.getColor());
							enemyLabel.setMaskToBounds(false);
							mapView.add(enemyLabel);
							return new EnemyController(enemy, level, enemyLabel, mapView);
						})
						.toArray(size -> new EnemyController[size]);

		playerLabel = new TLabel();
		playerLabel.setLocation(level.getPlayer().getBounds().getLocation());
		playerLabel.setSize(StringUtils.getStringDimensions(level.getPlayer().getRestingState()));
		playerLabel.setText(level.getPlayer().getRestingState());
		playerLabel.setColor(level.getPlayer().getColor());
		mapView.add(playerLabel);

		//Initialisiere Spielersteuerung

		playerController = new PlayerController(level.getPlayer(), level, playerLabel, mapView);
		playerController.setMainController(this);

		//Initialisiere untere Leiste (Faehigkeitsinformationen, Traenke)

		TComponent           bottomBar       = new TComponent();
		HorizontalFlowLayout bottomBarLayout = new HorizontalFlowLayout();
		bottomBarLayout.setHorizontalAlignment(HorizontalFlowLayout.LEFT);
		bottomBarLayout.setVerticalAlignment(HorizontalFlowLayout.TOP);
		bottomBarLayout.setSpacing(4);
		bottomBar.setLayoutManager(bottomBarLayout);
		getView().add(bottomBar);

		skill1Label = new TLabel();
		skill1Label.setText(level.getPlayer().getSkill1().getIcon());
		skill1Label.setSize(4, 2);
		skill1Label.setVisible(level.getPlayer().getSkill1().getRequiredLevel() <= level.getPlayer().getLevel());
		skill1Label.setColor(level.getPlayer().getSkill1().getOverlayColor());
		bottomBar.add(skill1Label);

		skill2Label = new TLabel();
		skill2Label.setText(level.getPlayer().getSkill2().getIcon());
		skill2Label.setSize(4, 2);
		skill2Label.setVisible(level.getPlayer().getSkill2().getRequiredLevel() <= level.getPlayer().getLevel());
		skill2Label.setColor(level.getPlayer().getSkill2().getOverlayColor());
		bottomBar.add(skill2Label);

		skill3Label = new TLabel();
		skill3Label.setText(level.getPlayer().getSkill3().getIcon());
		skill3Label.setSize(4, 2);
		skill3Label.setVisible(level.getPlayer().getSkill3().getRequiredLevel() <= level.getPlayer().getLevel());
		skill3Label.setColor(level.getPlayer().getSkill3().getOverlayColor());
		bottomBar.add(skill3Label);

		skill4Label = new TLabel();
		skill4Label.setText(level.getPlayer().getSkill4().getIcon());
		skill4Label.setSize(4, 2);
		skill4Label.setVisible(level.getPlayer().getSkill4().getRequiredLevel() <= level.getPlayer().getLevel());
		skill4Label.setColor(level.getPlayer().getSkill4().getOverlayColor());
		bottomBar.add(skill4Label);

		//Initialisiere Layout

		getView().setLayoutManager(component -> {
			TComponent[] children = component.getChildren();
			if (children.length != 3)
				throw new IllegalStateException("View managed by level view controller layout must have 3 children.");
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

		//Initialisiere Key-Event-System

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
					if (event.getKey() == SavedGameState.getSettingsState().getSkill1Key()
					    && level.getPlayer().getSkill1().getRequiredLevel() <= level.getPlayer().getLevel())
						playerController.attackSkill1();
					else if (event.getKey() == SavedGameState.getSettingsState().getSkill2Key()
					         && level.getPlayer().getSkill2().getRequiredLevel() <= level.getPlayer().getLevel())
						playerController.attackSkill2();
					else if (event.getKey() == SavedGameState.getSettingsState().getSkill3Key()
					         && level.getPlayer().getSkill3().getRequiredLevel() <= level.getPlayer().getLevel())
						playerController.attackSkill3();
					else if (event.getKey() == SavedGameState.getSettingsState().getSkill4Key()
					         && level.getPlayer().getSkill4().getRequiredLevel() <= level.getPlayer().getLevel())
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

	@Override
	protected void updateViews(final double time, final double timeDelta)
	{
		super.updateViews(time, timeDelta);
		if (!initialized || finished)
			return;
		if (!level.getPlayer().isAlive())
			return;

		//Aktualisiere Controller

		playerController.update(time);

		if (damageEnabled)
			for (EnemyController enemyController : enemyControllers)
				enemyController.update(time);

		//Bewegung des Spielers

		if (moveUpKeyPressed)
			playerController.moveUp();
		if (moveLeftKeyPressed)
			playerController.moveLeft();
		if (moveRightKeyPressed)
			playerController.moveRight();
		if (moveDownKeyPressed)
			playerController.moveDown();

		//UI Updates

		updateSkillLabels();

		Point scrollCenter = level.getPlayer().getBounds().getLocation();
		scrollCenter.translate(-mapView.getWidth() / 2, -mapView.getHeight() / 2);
		mapView.setOffset(scrollCenter);
		mapView.setPointOfVision(level.getPlayer().getCenter());
		playerLabel.setFrame(level.getPlayer().getBounds());
	}

	private String getReloadString(double reloadPercentage)
	{
		return LocalizedString("skill_reload_" + (int) (reloadPercentage * 7));
	}

	private void updateSkillLabels()
	{
		double skill1Reload = playerController.getSkill1Reload();
		double skill2Reload = playerController.getSkill2Reload();
		double skill3Reload = playerController.getSkill3Reload();
		double skill4Reload = playerController.getSkill4Reload();

		if (skill1Reload == 1 || playerController.getSkill1lastExecutionTime() == 0)
			skill1Label.setText(level.getPlayer().getSkill1().getIcon());
		else
			skill1Label.setText(getReloadString(skill1Reload));

		if (skill2Reload == 1 || playerController.getSkill2lastExecutionTime() == 0)
			skill2Label.setText(level.getPlayer().getSkill2().getIcon());
		else
			skill2Label.setText(getReloadString(skill2Reload));

		if (skill3Reload == 1 || playerController.getSkill3lastExecutionTime() == 0)
			skill3Label.setText(level.getPlayer().getSkill3().getIcon());
		else
			skill3Label.setText(getReloadString(skill3Reload));

		if (skill4Reload == 1 || playerController.getSkill4lastExecutionTime() == 0)
			skill4Label.setText(level.getPlayer().getSkill4().getIcon());
		else
			skill4Label.setText(getReloadString(skill4Reload));
	}

}
