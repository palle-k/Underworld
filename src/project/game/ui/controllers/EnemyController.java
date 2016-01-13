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
import project.game.data.GameActorDelegate;
import project.game.data.Map;
import project.game.data.MapObject;
import project.game.data.Player;
import project.gui.components.TLabel;
import project.gui.dynamics.StepController;
import project.util.Direction;

import java.awt.Point;

public class EnemyController implements GameActorDelegate
{
	private StepController attackController;
	private Enemy          enemy;
	private TLabel         enemyLabel;
	private boolean        following;
	private StepController healthRegenerationController;
	private StepController horizontalMovementController;
	private Point          lastCheckPoint;
	/*
	private Point   lastPathEndpoint;
	private Point   lastPlayerPosition;*/
	private Map            map;
	private Point[]        path;
	private int            pathIndex;
	private Player         player;
	private long           requiredDistance;
	private StepController verticalMovementController;
	//private long    requiredTravelDistance;

	public EnemyController(final Enemy enemy, final Map map, final Player player, final TLabel enemyLabel)
	{
		this.enemy = enemy;
		this.map = map;
		this.player = player;
		this.enemyLabel = enemyLabel;
		enemy.setDelegate(this);
		horizontalMovementController = new StepController(enemy.getSpeed());
		verticalMovementController = new StepController(enemy.getSpeed() / 2);
		attackController = new StepController(enemy.getAttackRate());
		healthRegenerationController = new StepController(enemy.getHealthRegeneration());
		horizontalMovementController.start();
		verticalMovementController.start();
		attackController.start();
		healthRegenerationController.start();
	}

	@Override
	public void actorDidChangeHealth(final GameActor actor)
	{

	}

	@Override
	public void actorDidChangeState(final GameActor actor)
	{
		if (actor.getState() == GameActor.RESTING)
			enemyLabel.setText(actor.getRestingState());
		else if (actor.getState() == GameActor.DEAD)
			enemyLabel.setText(actor.getDeadState());
		else if (actor.getState() == GameActor.ATTACKING)
			enemyLabel.setText(actor.getAttackStates()[0]);
		else if (actor.getState() == GameActor.DEFENDING)
			enemyLabel.setText(actor.getDefenseStates()[0]);
	}

	@Override
	public void mapObjectDidMove(final MapObject mapObject)
	{
		enemyLabel.setFrame(mapObject.getBounds());
	}

	public void update(double time)
	{
		if (!enemy.isAlive())
			return;

		healthRegenerationController.updateTime(time);
		if (healthRegenerationController.requiresUpdate())
			enemy.regenerateHealth(healthRegenerationController.getNumberOfSteps());

		Point playerCenter = player.getCenter();
		Point enemyCenter  = enemy.getCenter();

		int   playerDist   = Math.abs(enemyCenter.x - playerCenter.x) + Math.abs(enemyCenter.y - playerCenter.y);

		if (enemy.getSpeed() > 0)
		{
			horizontalMovementController.updateTime(time);
			verticalMovementController.updateTime(time);

			if (horizontalMovementController.requiresUpdate() || verticalMovementController.requiresUpdate())
			{
				boolean recalculateFlag = false;
				if (path == null)
				{
					if (lastCheckPoint == null ||
					    Math.abs(playerCenter.x - lastCheckPoint.x) + Math.abs(playerCenter.y - lastCheckPoint.y) >=
					    requiredDistance)
					{
						if (playerDist > enemy.getVisionRange())
						{
							lastCheckPoint = playerCenter;
							requiredDistance = playerDist - enemy.getVisionRange();
						}
						else
							recalculateFlag = true;
					}
				}
				else if (!Map.pathContains(path, playerCenter))
					recalculateFlag = true;

				if (playerDist == 0)
					recalculateFlag = false;

				if (recalculateFlag)
				{
					Point[] newPath = map.findPath(
							enemyCenter,
							playerCenter,
							enemy.getBounds().width,
							enemy.getBounds().height);
					if (newPath != null && ((path != null && newPath.length > enemy.getFollowRange()) ||
					    (path == null && newPath.length > enemy.getVisionRange())))
					{
						lastCheckPoint = playerCenter;
						requiredDistance = newPath.length - enemy.getVisionRange();
						path = null;
					}
					else
					{
						path = newPath;
						pathIndex = 0;
					}
				}

				if (path != null)
				{
					int playerIndex = Map.pathIndex(path, playerCenter);
					/*if (Math.abs(playerIndex - pathIndex) > enemy.getAttackRange() ||
					    !map.canSee(enemyCenter, playerCenter))
					{
						if (playerIndex > pathIndex)
						{
							pathIndex += movementController.getNumberOfSteps();
							if (pathIndex > playerIndex)
								pathIndex = playerIndex;
						}
						else if (playerIndex < pathIndex)
						{
							pathIndex -= movementController.getNumberOfSteps();
							if (pathIndex < playerIndex)
								pathIndex = playerIndex;
						}

					}*/
					int horizontalSteps = horizontalMovementController.getNumberOfSteps();
					int verticalSteps = verticalMovementController.getNumberOfSteps();
					if (Math.abs(playerIndex - pathIndex) > enemy.getAttackRange() ||
					    !map.canSee(enemyCenter, playerCenter))
					{
						if (pathIndex < playerIndex)
						{
							pathIndex += Math.min(horizontalSteps, verticalSteps);
							if (horizontalSteps > verticalSteps)
							{
								loop: for (int i = 0; i < horizontalSteps - verticalSteps; i++)
									switch (Direction.direction(path[pathIndex], path[pathIndex + 1]))
									{
										case LEFT:
										case RIGHT:
											pathIndex++;
											break;
										default:
											break loop;
									}
							}
							else if (verticalSteps > horizontalSteps)
							{
								loop: for (int i = 0; i < verticalSteps - horizontalSteps; i++)
									switch (Direction.direction(path[pathIndex], path[pathIndex + 1]))
									{
										case UP:
										case DOWN:
											pathIndex++;
											break;
										default:
											break loop;
									}
							}
						}

					}

					enemy.setCenter(path[pathIndex]);
				}

			}
		}
		attackController.updateTime(time);
		if (playerDist <= enemy.getAttackRange() && attackController.requiresUpdate() &&
		    map.canSee(enemyCenter, playerCenter))
			for (int i = 0; i < attackController.getNumberOfSteps(); i++)
				attackPlayer();
	}

	private void attackPlayer()
	{
		int damage = enemy.getAttackDamage() +
		             (int) (Math.random() * enemy.getAttackDamageVariation() -
		                    0.5 * enemy.getAttackDamageVariation());
		enemy.attack();
		player.decreaseHealth(damage);

		if (enemy.usesProjectiles())
		{
			Point playerLocation = player.getCenter();
			Point enemyLocation = enemy.getCenter();

			String[] projectiles = enemy.getAttackProjectilesForDirection(Direction.direction(enemyLocation, playerLocation));

		}
	}
}
