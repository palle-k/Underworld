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
import project.game.data.skills.SkillExecutor;
import project.gui.components.TComponent;
import project.gui.components.TLabel;
import project.gui.dynamics.StepController;
import project.util.Direction;
import project.util.PathUtil;

import java.awt.Point;
import java.awt.Rectangle;

public class EnemyController implements GameActorDelegate
{
	private StepController attackController;
	private Enemy          enemy;
	private TLabel         enemyLabel;
	private StepController healthRegenerationController;
	private StepController horizontalMovementController;
	private Point          lastCheckPoint;
	/*
	private Point   lastPathEndpoint;
	private Point   lastPlayerPosition;*/
	private Map            map;
	private TComponent     mapView;
	private Point[]        path;
	private int            pathIndex;
	private Player         player;
	private long           requiredDistance;
	private StepController verticalMovementController;
	//private long    requiredTravelDistance;

	public EnemyController(final Enemy enemy, final Map map, final Player player, final TLabel enemyLabel, TComponent mapView)
	{
		this.enemy = enemy;
		this.map = map;
		this.player = player;
		this.enemyLabel = enemyLabel;
		this.mapView = mapView;
		enemy.setDelegate(this);
		horizontalMovementController = new StepController(enemy.getSpeed());
		verticalMovementController = new StepController(enemy.getSpeed() / 2);
		attackController = new StepController(enemy.getBaseAttack().getAttackRate());
		healthRegenerationController = new StepController(enemy.getHealthRegeneration());
		horizontalMovementController.start();
		verticalMovementController.start();
		attackController.start();
		healthRegenerationController.start();
	}

	@Override
	public void actorDidChangeHealth(final GameActor actor)
	{
		if (!actor.isAlive())
			enemyLabel.removeFromSuperview();
	}

	@Override
	public void actorDidChangeState(final GameActor actor)
	{
		if (actor.getState() == GameActor.RESTING)
			enemyLabel.setText(actor.getRestingState());
		else if (actor.getState() == GameActor.DEAD)
			enemyLabel.setText(actor.getDeadState());
		else if (actor.getState() == GameActor.ATTACKING)
			enemyLabel.setText(actor.getAttackState());
		else if (actor.getState() == GameActor.DEFENDING)
			enemyLabel.setText(actor.getDefenseState());
		else if (actor.getState() == GameActor.MOVING)
			enemyLabel.setText(actor.getNextMovementState());
	}

	@Override
	public void mapObjectDidMove(final MapObject mapObject)
	{
		enemyLabel.setFrame(mapObject.getBounds());
	}

	public void update(double time)
	{
		if (!enemy.isAlive())
		{
			enemyLabel.removeFromSuperview();
			return;
		}
		healthRegenerationController.updateTime(time);
		if (healthRegenerationController.requiresUpdate())
			enemy.regenerateHealth(healthRegenerationController.getNumberOfSteps());

		Point playerCenter = player.getCenter();
		Point enemyCenter  = enemy.getCenter();

		int playerDist = Math.abs(enemyCenter.x - playerCenter.x) + Math.abs(enemyCenter.y - playerCenter.y) * 2;

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
					    Math.abs(playerCenter.x - lastCheckPoint.x) + Math.abs(playerCenter.y - lastCheckPoint.y) * 2 >=
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
				else if (!PathUtil.pathContains(path, playerCenter))
					recalculateFlag = true;
//
//				if (playerDist == 0)
//					recalculateFlag = false;

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
					int playerIndex = PathUtil.pathIndex(path, playerCenter);
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
					if (PathUtil.pathLength(path, pathIndex, playerIndex, 1, 2) >
					    enemy.getBaseAttack().getAttackRange() ||
					    !map.canSee(enemyCenter, playerCenter))
					{

						if (pathIndex < playerIndex)
						{
							pathIndex += Math.min(horizontalSteps, verticalSteps);
							if (pathIndex >= path.length - 1)
								pathIndex = path.length - 2;
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
						if (pathIndex < 0)
							pathIndex = 0;
						else if (pathIndex >= path.length)
							pathIndex = path.length - 1;
					}

					enemy.setCenter(path[pathIndex]);
					enemy.enterMovementState();
				}
				else
				{
					Rectangle bounds = new Rectangle(enemy.getBounds());
					for (int i = 0; i < horizontalMovementController.getNumberOfSteps(); i++)
					{
						Rectangle newBounds = new Rectangle(bounds);
						newBounds.translate((int) (Math.random() * 4 - 2), 0);
						enemy.enterMovementState();
						if (!map.canMoveTo(newBounds))
							break;
						else
							bounds = newBounds;
						requiredDistance--;
					}
					for (int i = 0; i < verticalMovementController.getNumberOfSteps(); i++)
					{
						Rectangle newBounds = new Rectangle(bounds);
						newBounds.translate(0, (int) (Math.random() * 4 - 2));
						enemy.enterMovementState();
						if (!map.canMoveTo(newBounds))
							break;
						else
							bounds = newBounds;
						requiredDistance--;
					}
					enemy.setBounds(bounds);
				}
			}
		}
		attackController.updateTime(time);
		if (playerDist <= enemy.getBaseAttack().getAttackRange() && attackController.requiresUpdate() &&
		    map.canSee(enemyCenter, playerCenter))
			for (int i = 0; i < attackController.getNumberOfSteps(); i++)
				attackPlayer();
	}

	private void attackPlayer()
	{
		int damage = enemy.getBaseAttack().getAttackDamage() +
		             (int) (Math.random() * enemy.getBaseAttack().getAttackDamageVariation() -
		                    0.5 * enemy.getBaseAttack().getAttackDamageVariation());
		enemy.enterAttackState();
//		player.decreaseHealth(damage);

//		Point playerLocation = player.getCenter();
//		Point enemyLocation = enemy.getCenter();

//		String[] projectiles = enemy.getBaseAttack().getAttackProjectilesForDirection(Direction.direction(enemyLocation, playerLocation));

		SkillExecutor skillExecutor = enemy.getBaseAttack().getSkillExecutor();
		skillExecutor.setTarget(mapView);
		skillExecutor.executeSkill(enemy, player);

//		SkillCoordinator skillCoordinator = new SkillCoordinator(
//				mapView,
//				enemy.getBaseAttack().getAttackOverlays(),
//				projectiles,
//				enemy.getBaseAttack().getAttackHitOverlays(),
//				enemy.getBaseAttack().getAttackOverlayColor(),
//				enemy.getBaseAttack().getAttackProjectileColor(),
//				enemy.getBaseAttack().getAttackHitOverlayColor(),
//				enemy.getBaseAttack().getAttackOverlayAnimationTime(),
//				enemy.getBaseAttack().getAttackProjectileAnimationTime(enemyLocation, playerLocation),
//				enemy.getBaseAttack().getAttackHitOverlayAnimationTime(), damage);
//		skillCoordinator.visualizeSkill(enemy, player);
	}
}
