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

package project.game.controllers;

import project.game.behaviour.AutoAttackBehaviour;
import project.game.behaviour.FollowBehaviour;
import project.game.data.Enemy;
import project.game.data.GameActor;
import project.game.data.Level;
import project.game.data.Map;
import project.game.data.MapObject;
import project.game.data.Player;
import project.game.data.PlayerDelegate;
import project.game.data.SkillConfiguration;
import project.game.data.skills.KillAction;
import project.game.data.skills.SkillExecutor;
import project.game.ui.controllers.LevelViewController;
import project.game.ui.views.MapView;
import project.game.ui.views.PathView;
import project.gui.components.TLabel;
import project.gui.dynamics.StepController;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Optional;

/**
 * Klasse zur Steuerung eines Spielers
 * Verwaltet Nutzereingaben, die Ausfuehrung von Faehigkeiten
 * und die Verfolgung von Gegnern.
 */
public class PlayerController extends ActorController<Player> implements PlayerDelegate
{
	private StepController attackController;

	private AutoAttackBehaviour autoAttackBehaviour;

	private Enemy          currentEnemy;

	private FollowBehaviour followBehaviour;

	private StepController healthRegenerationController;

	private StepController horizontalMovementController;

	private double lastUpdateTime;

	private LevelViewController mainController;

	private PathView playerPath;

	private double skill1lastExecutionTime;

	private double skill2lastExecutionTime;

	private double skill3lastExecutionTime;

	private double skill4lastExecutionTime;

	private StepController verticalMovementController;

	public PlayerController(final Player controlledActor, final Level level, final TLabel actorLabel, final MapView mapView)
	{
		super(controlledActor, level, actorLabel, mapView);
		horizontalMovementController = new StepController(controlledActor.getSpeed() * 2);
		verticalMovementController = new StepController(controlledActor.getSpeed());
		attackController = new StepController(controlledActor.getBaseAttack().getRate());
		healthRegenerationController = new StepController(controlledActor.getHealthRegeneration());
		horizontalMovementController.start();
		verticalMovementController.start();
		attackController.start();
		healthRegenerationController.start();
		
		followBehaviour = new FollowBehaviour(controlledActor, level);
		followBehaviour.setHorizontalSpeed(controlledActor.getSpeed() * 2);
		followBehaviour.setVerticalSpeed(controlledActor.getSpeed());
		followBehaviour.setEndDistance(controlledActor.getBaseAttack().getAttackRange());
		followBehaviour.setBeginDistance(Integer.MAX_VALUE);
		followBehaviour.setMaxDistance(Integer.MAX_VALUE);

		autoAttackBehaviour = new AutoAttackBehaviour(controlledActor, level);
		autoAttackBehaviour.setConfiguration(controlledActor.getBaseAttack());

		playerPath = new PathView();
		mapView.add(playerPath, 0);
	}

	@Override
	public void actorDidChangeHealth(final GameActor actor)
	{
		mainController.updatePlayerHealth();
	}

	/**
	 * Greift den naechsten Feind an
	 *
	 * @return angegriffener Feind
	 */
	public Enemy attackEnemy()
	{
		Enemy enemy = findNearestVisibleEnemy(60);
		currentEnemy = enemy;
		followBehaviour.setTarget(enemy);
		followBehaviour.startBehaviour();
		return enemy;
	}

	/**
	 * Fuehrt den ersten Skill aus
	 * @return true, wenn die Ausfuehrung erfolgreich war, sonst false
	 */
	public boolean attackSkill1()
	{
		SkillConfiguration skill1 = controlledActor.getSkill1();
		if (skill1lastExecutionTime <= 0 || lastUpdateTime - skill1lastExecutionTime >= 1.0 / skill1
				.getRate())
		{
			if (!skill1.requiresFocus() ||
			    (currentEnemy != null && level.getMap().canSee(controlledActor.getCenter(), currentEnemy.getCenter())))
			{
				skill1lastExecutionTime = lastUpdateTime;
				skill1.getSkillExecutor().setTarget(mapView);
				skill1.getSkillExecutor().setPossibleTargets(level.getEnemies());
				skill1.getSkillExecutor().executeSkill(controlledActor, currentEnemy);
				followBehaviour.pause(skill1.getTotalAnimationTime());
				horizontalMovementController.pause(skill1.getTotalAnimationTime());
				verticalMovementController.pause(skill1.getTotalAnimationTime());
				controlledActor.enterAttackState();
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * Fuehrt den zweiten Skill aus
	 * @return true, wenn die Ausfuehrung erfolgreich war, sonst false
	 */
	public boolean attackSkill2()
	{
		SkillConfiguration skill2 = controlledActor.getSkill2();
		if (skill2lastExecutionTime <= 0 || lastUpdateTime - skill2lastExecutionTime >= 1.0 / skill2
				.getRate())
		{
			if (!skill2.requiresFocus() ||
			    (currentEnemy != null && level.getMap().canSee(controlledActor.getCenter(), currentEnemy.getCenter())))
			{
				skill2lastExecutionTime = lastUpdateTime;
				skill2.getSkillExecutor().setTarget(mapView);
				skill2.getSkillExecutor().setPossibleTargets(level.getEnemies());
				skill2.getSkillExecutor().executeSkill(controlledActor, currentEnemy);
				followBehaviour.pause(skill2.getTotalAnimationTime());
				horizontalMovementController.pause(skill2.getTotalAnimationTime());
				verticalMovementController.pause(skill2.getTotalAnimationTime());
				controlledActor.enterAttackState();
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * Fuehrt den dritten Skill aus
	 * @return true, wenn die Ausfuehrung erfolgreich war, sonst false
	 */
	public boolean attackSkill3()
	{
		SkillConfiguration skill3 = controlledActor.getSkill3();
		if (skill3lastExecutionTime <= 0 || lastUpdateTime - skill3lastExecutionTime >= 1.0 / skill3
				.getRate())
		{
			if (!skill3.requiresFocus() ||
			    (currentEnemy != null && level.getMap().canSee(controlledActor.getCenter(), currentEnemy.getCenter())))
			{
				skill3lastExecutionTime = lastUpdateTime;
				skill3.getSkillExecutor().setTarget(mapView);
				skill3.getSkillExecutor().setPossibleTargets(level.getEnemies());
				skill3.getSkillExecutor().executeSkill(controlledActor, currentEnemy);
				followBehaviour.pause(skill3.getTotalAnimationTime());
				horizontalMovementController.pause(skill3.getTotalAnimationTime());
				verticalMovementController.pause(skill3.getTotalAnimationTime());
				controlledActor.enterAttackState();
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * Fuehrt den vierten Skill aus
	 * @return true, wenn die Ausfuehrung erfolgreich war, sonst false
	 */
	public boolean attackSkill4()
	{
		SkillConfiguration skill4 = controlledActor.getSkill4();
		if (skill4lastExecutionTime <= 0 || lastUpdateTime - skill4lastExecutionTime >= 1.0 / skill4
				.getRate())
		{
			if (!skill4.requiresFocus() ||
			    (currentEnemy != null && level.getMap().canSee(controlledActor.getCenter(), currentEnemy.getCenter())))
			{
				skill4lastExecutionTime = lastUpdateTime;
				skill4.getSkillExecutor().setTarget(mapView);
				skill4.getSkillExecutor().setPossibleTargets(level.getEnemies());
				skill4.getSkillExecutor().executeSkill(controlledActor, currentEnemy);
				followBehaviour.pause(skill4.getTotalAnimationTime());
				horizontalMovementController.pause(skill4.getTotalAnimationTime());
				verticalMovementController.pause(skill4.getTotalAnimationTime());
				controlledActor.enterAttackState();
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * Gibt den Hauptcontroller an
	 * @return Hauptcontroller
	 */
	public LevelViewController getMainController()
	{
		return mainController;
	}

	/**
	 * Gibt an, wie weit der erste Skill nachgeladen ist.
	 * @return Wert zwischen 0 und 1: 0 - Skill gerade ausgefuehrt, 1: Skill nachgeladen
	 */
	public double getSkill1Reload()
	{
		return Math.min((lastUpdateTime - skill1lastExecutionTime) * controlledActor.getSkill1().getRate(), 1);
	}

	/**
	 * Gibt an, wann der erste Skill zuletzt ausgefuehrt wurde.
	 * @return Zeitpunkt der Ausfuehrung
	 */
	public double getSkill1lastExecutionTime()
	{
		return skill1lastExecutionTime;
	}

	/**
	 * Gibt an, wie weit der zweite Skill nachgeladen ist.
	 * @return Wert zwischen 0 und 1: 0 - Skill gerade ausgefuehrt, 1: Skill nachgeladen
	 */
	public double getSkill2Reload()
	{
		return Math.min((lastUpdateTime - skill2lastExecutionTime) * controlledActor.getSkill2().getRate(), 1);
	}

	/**
	 * Gibt an, wann der zweite Skill zuletzt ausgefuehrt wurde.
	 * @return Zeitpunkt der Ausfuehrung
	 */
	public double getSkill2lastExecutionTime()
	{
		return skill2lastExecutionTime;
	}

	/**
	 * Gibt an, wie weit der dritte Skill nachgeladen ist.
	 * @return Wert zwischen 0 und 1: 0 - Skill gerade ausgefuehrt, 1: Skill nachgeladen
	 */
	public double getSkill3Reload()
	{
		return Math.min((lastUpdateTime - skill3lastExecutionTime) * controlledActor.getSkill3().getRate(), 1);
	}

	/**
	 * Gibt an, wann der dritte Skill zuletzt ausgefuehrt wurde.
	 * @return Zeitpunkt der Ausfuehrung
	 */
	public double getSkill3lastExecutionTime()
	{
		return skill3lastExecutionTime;
	}

	/**
	 * Gibt an, wie weit der vierte Skill nachgeladen ist.
	 * @return Wert zwischen 0 und 1: 0 - Skill gerade ausgefuehrt, 1: Skill nachgeladen
	 */
	public double getSkill4Reload()
	{
		return Math.min((lastUpdateTime - skill4lastExecutionTime) * controlledActor.getSkill4().getRate(), 1);
	}

	/**
	 * Gibt an, wann der vierte Skill zuletzt ausgefuehrt wurde.
	 * @return Zeitpunkt der Ausfuehrung
	 */
	public double getSkill4lastExecutionTime()
	{
		return skill4lastExecutionTime;
	}

	@Override
	public void mapObjectDidMove(final MapObject mapObject)
	{
		actorLabel.setFrame(level.getPlayer().getBounds());
	}

	/**
	 * Bewege den Spieler nach unten
	 */
	public void moveDown()
	{
		move(0, 1);
		//cancelMovement = true;
		followBehaviour.stopBehaviour();
	}

	/**
	 * Bewege den Spieler nach links
	 */
	public void moveLeft()
	{
		move(-1, 0);
		//cancelMovement = true;
		followBehaviour.stopBehaviour();
	}

	/**
	 * Bewege den Spieler nach rechts
	 */
	public void moveRight()
	{
		move(1, 0);
		//cancelMovement = true;
		followBehaviour.stopBehaviour();
	}

	/**
	 * Bewege den Spieler nach oben
	 */
	public void moveUp()
	{
		move(0, -1);
		//cancelMovement = true;
		followBehaviour.stopBehaviour();
	}

	@Override
	public void playerDidEarnExperience(final Player player)
	{
		mainController.updatePlayerExperience();
	}

	@Override
	public void playerLevelDidChange(final Player player)
	{
		mainController.updatePlayerExperience();
		controlledActor.regenerateHealth(controlledActor.getMaxHealth() - controlledActor.getCurrentHealth());
		healthRegenerationController.stop();
		healthRegenerationController.setFrequency(controlledActor.getHealthRegeneration());
		healthRegenerationController.start();
	}

	public void setMainController(final LevelViewController mainController)
	{
		this.mainController = mainController;
	}

	public boolean takeAttackPotion()
	{
		//TODO implement potions
		//TODO Check if enterAttackState potion exists and if yes, use it
		return false;
	}

	public boolean takeHealthPotion()
	{
		//TODO Check if health potion exists and if yes, use it
		return false;
	}

	@Override
	public void update(double time)
	{
		lastUpdateTime = time;

		/*
		 * Aktualisiere Bewegung
		 */

		horizontalMovementController.updateTime(time);
		verticalMovementController.updateTime(time);
		attackController.updateTime(time);
		healthRegenerationController.updateTime(time);

		/*
		 * Aktualisiere Lebensregeneration
		 */
		if (healthRegenerationController.requiresUpdate())
			controlledActor.regenerateHealth(healthRegenerationController.getNumberOfSteps());

		if (currentEnemy != null && !currentEnemy.isAlive())
		{
			currentEnemy = null;
			followBehaviour.resetTarget();
		}
		mainController.updateFocussedEnemy(currentEnemy);
		Rectangle actorBounds = controlledActor.getBounds();
		playerPath.addPoint(new Point((int) actorBounds.getCenterX(), (int) actorBounds.getMaxY() - 1));

		/*
		 * Aktualisiere Verfolgung
		 */
		followBehaviour.update(time);

		/*
		 * Sammle Schluessel ein
		 */
		Arrays.stream(level.getKeys())
				.filter(key -> !key.isCollected())
				.filter(key -> key.getBounds().intersects(controlledActor.getBounds()))
				.forEach(key -> {
					key.collect();
					mainController.updateCollectedKeys();
				});

		if (currentEnemy != null)
		{
			/*
			 * Aktualisiere Angriff
			 */
			if (Math.abs(currentEnemy.getCenter().x - controlledActor.getCenter().x)
			    + Math.abs(currentEnemy.getCenter().y - controlledActor.getCenter().y) * 2
			    <= controlledActor.getBaseAttack().getAttackRange()
			    && level.getMap().canSee(controlledActor.getCenter(), currentEnemy.getCenter())
			    && attackController.requiresUpdate()
			    && currentEnemy.isAlive())
			{
				double totalAnimationTime = controlledActor.getBaseAttack().getTotalAnimationTime();
				followBehaviour.pause(totalAnimationTime);
				int steps = attackController.getNumberOfSteps();
				for (int i = 0; i < steps; i++)
				{
					SkillExecutor skillExecutor = controlledActor.getBaseAttack().getSkillExecutor();
					skillExecutor.setTarget(mapView);
					skillExecutor.executeSkill(controlledActor, currentEnemy);
				}
				controlledActor.enterAttackState();
			}
		}
	}

	@Override
	protected void loadSkills()
	{
		super.loadSkills();

		KillAction killAction = actor ->
				controlledActor.earnExperience(actor.getEarnedExperience());

		controlledActor.getBaseAttack().getSkillExecutor().setOnKillAction(killAction);

		controlledActor.getSkill1().getSkillExecutor().setTarget(mapView);
		controlledActor.getSkill1().getSkillExecutor().setLevel(level);
		controlledActor.getSkill1().getSkillExecutor().setOnKillAction(killAction);
		controlledActor.getSkill2().getSkillExecutor().setTarget(mapView);
		controlledActor.getSkill2().getSkillExecutor().setLevel(level);
		controlledActor.getSkill2().getSkillExecutor().setOnKillAction(killAction);
		controlledActor.getSkill3().getSkillExecutor().setTarget(mapView);
		controlledActor.getSkill3().getSkillExecutor().setLevel(level);
		controlledActor.getSkill3().getSkillExecutor().setOnKillAction(killAction);
		controlledActor.getSkill4().getSkillExecutor().setTarget(mapView);
		controlledActor.getSkill4().getSkillExecutor().setLevel(level);
		controlledActor.getSkill4().getSkillExecutor().setOnKillAction(killAction);
	}

	/**
	 * Sucht den naechstmoeglichen, sichtbaren Gegner innerhalb des
	 * angegebenen Radius.
	 * @param maxDist maximaler Suchradius
	 * @return naechster, sichtbarer Gegner oder null, falls kein Gegner gefunden
	 */
	private Enemy findNearestVisibleEnemy(int maxDist)
	{
		Point playerCenter = controlledActor.getCenter();
		Map   map          = level.getMap();
		Optional<Enemy> nearestEnemy = Arrays.stream(level.getEnemies())
				.filter(Enemy::isAlive)
				.filter(enemy -> {
					double dx = enemy.getCenter().x - playerCenter.x;
					double dy = enemy.getCenter().y - playerCenter.y;
					dy *= 2;
					return Math.sqrt(dx * dx + dy * dy) <= maxDist;
				})
				.filter(enemy -> map.canSee(enemy.getCenter(), playerCenter))
				.min((o1, o2) -> Double.compare(
						o1.getCenter().distance(playerCenter),
						o2.getCenter().distance(playerCenter)));
		return nearestEnemy.isPresent() ? nearestEnemy.get() : null;
	}

	/**
	 * Bewege den Spieler in die angegebene Richtung
	 * @param dx Translation x
	 * @param dy Translation y
	 */
	private void move(int dx, int dy)
	{
		if (horizontalMovementController.requiresUpdate() || verticalMovementController.requiresUpdate())
		{
			int       horizontalSteps = horizontalMovementController.getNumberOfSteps();
			int       verticalSteps   = verticalMovementController.getNumberOfSteps();
			Rectangle playerBounds    = controlledActor.getBounds();

			for (int i = 0; i < Math.min(horizontalSteps, verticalSteps); i++)
			{
				playerBounds.translate(dx, dy);
				if (!level.getMap().canMoveTo(playerBounds))
				{
					playerBounds.translate(-dx, -dy);
					break;
				}
			}
			if (horizontalSteps > verticalSteps)
			{
				for (int i = 0; i < horizontalSteps - verticalSteps; i++)
				{
					playerBounds.translate(dx, 0);
					if (!level.getMap().canMoveTo(playerBounds))
					{
						playerBounds.translate(-dx, 0);
						break;
					}
				}
			}
			else if (verticalSteps > horizontalSteps)
			{
				for (int i = 0; i < verticalSteps - horizontalSteps; i++)
				{
					playerBounds.translate(0, dy);
					if (!level.getMap().canMoveTo(playerBounds))
					{
						playerBounds.translate(0, -dy);
						break;
					}
				}
			}
			if (!playerBounds.equals(controlledActor.getBounds()))
			{
				Rectangle previousBounds = controlledActor.getBounds();
				controlledActor.setBounds(playerBounds);
				if (level.getEntranceBounds().intersects(playerBounds) &&
				    !level.getEntranceBounds().intersects(previousBounds))
					mainController.playerDidReachEntrance();
				if (Arrays.stream(level.getExitBounds())
						    .filter(rectangle -> rectangle.intersects(playerBounds))
						    .count() >= 1)
					if (level.getKeys().length == level.getCollectedKeyCount())
						mainController.playerDidReachExit();
			}
			controlledActor.enterMovementState();
		}
	}
}
