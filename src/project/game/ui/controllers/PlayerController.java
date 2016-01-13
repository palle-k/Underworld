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
import project.game.data.GameActor;
import project.game.data.Level;
import project.game.data.Map;
import project.game.data.MapObject;
import project.game.data.Player;
import project.game.data.PlayerDelegate;
import project.gui.components.TLabel;
import project.gui.dynamics.StepController;
import project.util.Direction;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Optional;

public class PlayerController implements PlayerDelegate
{
	private final Level               level;
	private       StepController      attackController;
	private       boolean             cancelMovement;
	private       Enemy               currentEnemy;
	private       StepController      healthRegenerationController;
	private       StepController      horizontalMovementController;
	private       LevelViewController mainController;
	private       int                 pathIndex;
	private       Point[]             pathToEnemy;
	private       TLabel              playerLabel;
	private       StepController      verticalMovementController;

	public PlayerController(final Level level, TLabel playerLabel, LevelViewController mainController)
	{
		this.level = level;
		this.playerLabel = playerLabel;
		this.mainController = mainController;
		level.getPlayer().setDelegate(this);
		horizontalMovementController = new StepController(level.getPlayer().getSpeed() * 2);
		verticalMovementController = new StepController(level.getPlayer().getSpeed());
		attackController = new StepController(level.getPlayer().getAttackRate());
		healthRegenerationController = new StepController(level.getPlayer().getHealthRegeneration());
		horizontalMovementController.start();
		verticalMovementController.start();
		attackController.start();
		healthRegenerationController.start();
	}

	@Override
	public void actorDidChangeHealth(final GameActor actor)
	{
		mainController.updatePlayerHealth();
	}

	@Override
	public void actorDidChangeState(final GameActor actor)
	{
		if (actor.getState() == GameActor.RESTING)
			playerLabel.setText(actor.getRestingState());
		else if (actor.getState() == GameActor.DEAD)
			playerLabel.setText(actor.getDeadState());
		else if (actor.getState() == GameActor.ATTACKING)
			playerLabel.setText(actor.getAttackStates()[0]);
		else if (actor.getState() == GameActor.DEFENDING)
			playerLabel.setText(actor.getDefenseStates()[0]);
	}

	public Enemy attackEnemy()
	{
		Enemy enemy = findNearestVisibleEnemy(20);
		currentEnemy = enemy;
		cancelMovement = false;
		mainController.updateFocussedEnemy(enemy);
		return enemy;
	}

	public boolean attackSkill1()
	{
		/*
		TODO Check if attack is reloaded
		If attack needs enemy focus, check for it
		execute skill
		handle attack overlays via animations
		return true if success, false if failure
		*/
		return false;
	}

	public boolean attackSkill2()
	{
		/*
		TODO Check if attack is reloaded
		If attack needs enemy focus, check for it
		execute skill
		handle attack overlays via animations
		return true if success, false if failure
		*/
		return false;
	}

	public boolean attackSkill3()
	{
		/*
		TODO Check if attack is reloaded
		If attack needs enemy focus, check for it
		execute skill
		handle attack overlays via animations
		return true if success, false if failure
		*/
		return false;
	}

	public boolean attackSkill4()
	{
		/*
		TODO Check if attack is reloaded
		If attack needs enemy focus, check for it
		execute skill
		handle attack overlays via animations
		return true if success, false if failure
		*/
		return false;
	}

	@Override
	public void mapObjectDidMove(final MapObject mapObject)
	{

	}

	public void moveDown()
	{
		move(0, 1);
		cancelMovement = true;
	}

	public void moveLeft()
	{
		move(-1, 0);
		cancelMovement = true;
	}

	public void moveRight()
	{
		move(1, 0);
		cancelMovement = true;
	}

	public void moveUp()
	{
		move(0, -1);
		cancelMovement = true;
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
	}

	public boolean takeAttackPotion()
	{
		//TODO Check if attack potion exists and if yes, use it
		return false;
	}

	public boolean takeHealthPotion()
	{
		//TODO Check if health potion exists and if yes, use it
		return false;
	}

	public void update(double time)
	{
		horizontalMovementController.updateTime(time);
		verticalMovementController.updateTime(time);
		attackController.updateTime(time);
		healthRegenerationController.updateTime(time);

		if (healthRegenerationController.requiresUpdate())
			level.getPlayer().regenerateHealth(healthRegenerationController.getNumberOfSteps());

		if (currentEnemy != null)
		{
			boolean recalculate = false;

			if (!cancelMovement)
			{
				if (pathToEnemy == null)
					recalculate = true;
				else if (!Map.pathContains(pathToEnemy, currentEnemy.getCenter()))
					recalculate = true;
				else if (!Map.pathContains(pathToEnemy, level.getPlayer().getCenter()))
					recalculate = true;
			}

			if (recalculate)
			{
				pathIndex = 0;
				pathToEnemy = level.getMap().findPath(level.getPlayer().getCenter(), currentEnemy.getCenter());
			}

			int enemyIndex = Map.pathIndex(pathToEnemy, currentEnemy.getCenter());

			if (!cancelMovement)
				if (Math.abs(enemyIndex - pathIndex) > level.getPlayer().getAttackRange())
				{
					int horizontalSteps = horizontalMovementController.getNumberOfSteps();
					int verticalSteps   = verticalMovementController.getNumberOfSteps();
					int steps           = Math.min(horizontalSteps, verticalSteps);
					if (enemyIndex - pathIndex > 0)
						pathIndex += steps;
					else
						pathIndex -= steps;

					if (horizontalSteps > verticalSteps)
						for (int i = 0; i < horizontalSteps - verticalSteps; i++)
						{
							if (enemyIndex - pathIndex > 0 && pathIndex + 1 >= pathToEnemy.length)
								break;
							else if (enemyIndex - pathIndex < 0 && pathIndex < 1)
								break;
							Direction dir = Direction.direction(
									pathToEnemy[pathIndex],
									pathToEnemy[pathIndex +
									            ((enemyIndex - pathIndex) > 0 ? 1 : -1)]);
							if (dir == Direction.LEFT || dir == Direction.RIGHT)
								if (enemyIndex - pathIndex > 0)
									pathIndex++;
								else
									pathIndex--;
						}
					else if (verticalSteps > horizontalSteps)
						for (int i = 0; i < verticalSteps - horizontalSteps; i++)
						{
							if (enemyIndex - pathIndex > 0 && pathIndex + 1 >= pathToEnemy.length)
								break;
							else if (enemyIndex - pathIndex < 0 && pathIndex < 1)
								break;
							Direction dir = Direction.direction(
									pathToEnemy[pathIndex],
									pathToEnemy[pathIndex +
									            ((enemyIndex - pathIndex) > 0 ? 1 : -1)]);
							if (dir == Direction.UP || dir == Direction.DOWN)
								if (enemyIndex - pathIndex > 0)
									pathIndex++;
								else
									pathIndex--;
						}
					level.getPlayer().setCenter(pathToEnemy[pathIndex]);
				}

			if (Math.abs(enemyIndex - pathIndex) <= level.getPlayer().getAttackRange())
				if (attackController.requiresUpdate())
				{
					int steps = attackController.getNumberOfSteps();
					for (int i = 0; i < steps; i++)
					{
						int damage = level.getPlayer().getAttackDamage() +
						             (int) (Math.random() * level.getPlayer().getAttackDamageVariation() -
						                    0.5 * level.getPlayer().getAttackDamageVariation());
						currentEnemy.decreaseHealth(damage);
					}
					level.getPlayer().attack();
					if (!currentEnemy.isAlive())
					{
						level.getPlayer().earnExperience(currentEnemy.getEarnedExperience());
						currentEnemy = null;
					}
					mainController.updateFocussedEnemy(currentEnemy);
				}
		}
	}

	private Enemy findNearestVisibleEnemy(int maxDist)
	{
		Point playerCenter = level.getPlayer().getCenter();
		Map   map          = level.getMap();
		Optional<Enemy> nearestEnemy = Arrays.stream(level.getEnemies())
				.filter(Enemy::isAlive)
				.filter(enemy -> playerCenter.distance(enemy.getCenter()) <= maxDist)
				.filter(enemy -> map.canSee(enemy.getCenter(), playerCenter))
				//.sorted((o1, o2) -> Double.compare(o1.getCenter().distance(playerCenter), o2.getCenter().distance(playerCenter)))
				//.findFirst();
				.min((o1, o2) -> Double.compare(
						o1.getCenter().distance(playerCenter),
						o2.getCenter().distance(playerCenter)));
		return nearestEnemy.isPresent() ? nearestEnemy.get() : null;
	}

	private void move(int dx, int dy)
	{
		if (horizontalMovementController.requiresUpdate() || verticalMovementController.requiresUpdate())
		{
			int       horizontalSteps = horizontalMovementController.getNumberOfSteps();
			int       verticalSteps   = verticalMovementController.getNumberOfSteps();
			Rectangle playerBounds    = new Rectangle(level.getPlayer().getBounds());

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
			if (!playerBounds.equals(level.getPlayer().getBounds()))
			{
				Rectangle previousBounds = level.getPlayer().getBounds();
				level.getPlayer().setBounds(playerBounds);
				if (level.getEntranceBounds().intersects(playerBounds) && !level.getEntranceBounds().intersects(previousBounds))
					mainController.playerDidReachEntrance();
				if (Arrays.stream(level.getExitBounds()).filter(rectangle -> rectangle.intersects(playerBounds)).count() >= 1)
					if (level.getKeys().length == level.getCollectedKeyCount())
						mainController.playerDidReachExit();
				Arrays.stream(level.getKeys())
						.filter(key -> !key.isCollected())
						.filter(key -> key.getBounds().intersects(playerBounds))
						.forEach(key ->
						{
							key.collect();
							mainController.updateCollectedKeys();
						});
			}
		}
	}
}
