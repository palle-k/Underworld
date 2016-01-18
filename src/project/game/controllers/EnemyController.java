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

import project.game.behaviour.FollowBehaviour;
import project.game.behaviour.RandomMovementBehaviour;
import project.game.data.Enemy;
import project.game.data.GameActor;
import project.game.data.GameActorDelegate;
import project.game.data.Level;
import project.game.data.MapObject;
import project.game.data.Player;
import project.game.data.skills.SkillExecutor;
import project.gui.components.TComponent;
import project.gui.components.TLabel;
import project.gui.dynamics.StepController;

import java.awt.Point;

public class EnemyController extends ActorController<Enemy> implements GameActorDelegate
{
	private StepController attackController;

	private FollowBehaviour followBehaviour;

	//private Enemy          enemy;
	//private TLabel         enemyLabel;
	private StepController healthRegenerationController;

	//	private StepController horizontalMovementController;
// private Point          lastCheckPoint;
	/*
	private Point   lastPathEndpoint;
	private Point   lastPlayerPosition;*/
//	private Level level;
//	private TComponent     mapView;
//	private Point[]        path;
//	private int            pathIndex;
	private Player player;

	private RandomMovementBehaviour randomMovementBehaviour;
//	private long           requiredDistance;
//	private StepController verticalMovementController;
//	private long    requiredTravelDistance;

	public EnemyController(final Enemy controlledActor, final Level level, final TLabel actorLabel, final TComponent mapView)
	{
		super(controlledActor, level, actorLabel, mapView);
		//controlledActor.setDelegate(this);
		this.player = level.getPlayer();
		attackController = new StepController(controlledActor.getBaseAttack().getRate());
		healthRegenerationController = new StepController(controlledActor.getHealthRegeneration());
		attackController.start();
		healthRegenerationController.start();
		followBehaviour = new FollowBehaviour(controlledActor, level);
		followBehaviour.setTarget(player);
		followBehaviour.setBeginDistance(controlledActor.getVisionRange());
		followBehaviour.setEndDistance(controlledActor.getBaseAttack().getAttackRange());
		followBehaviour.setHorizontalSpeed(controlledActor.getSpeed() * 2);
		followBehaviour.setVerticalSpeed(controlledActor.getSpeed());
		followBehaviour.startBehaviour();

		randomMovementBehaviour = new RandomMovementBehaviour(controlledActor, level);
		randomMovementBehaviour.setHorizontalSpeed(controlledActor.getSpeed() * 2);
		randomMovementBehaviour.setVerticalSpeed(controlledActor.getSpeed());
		//FIXME Predicate not interrupting randomMovementBehaviour
		randomMovementBehaviour.setCondition(behaviour -> !followBehaviour.isFollowing());
		randomMovementBehaviour.startBehaviour();
	}

	//public EnemyController(final Enemy enemy, final Level level, final Player player, final TLabel enemyLabel, TComponent mapView)
//	{
//		this.enemy = enemy;
//		this.level = level;
//		this.player = player;
//		this.enemyLabel = enemyLabel;
//		this.mapView = mapView;
//		enemy.setDelegate(this);
//		horizontalMovementController = new StepController(enemy.getSpeed());
//		verticalMovementController = new StepController(enemy.getSpeed() / 2);
//		attackController = new StepController(enemy.getBaseAttack().getRate());
//		healthRegenerationController = new StepController(enemy.getHealthRegeneration());
//		horizontalMovementController.start();
//		verticalMovementController.start();
//		attackController.start();
//		healthRegenerationController.start();

//		followBehaviour = new FollowBehaviour(enemy, level);
//		followBehaviour.setTarget(player);
//		followBehaviour.setBeginDistance(enemy.getVisionRange());
//		followBehaviour.setEndDistance(enemy.getBaseAttack().getAttackRange());
//		followBehaviour.setHorizontalSpeed(enemy.getSpeed() * 2);
//		followBehaviour.setVerticalSpeed(enemy.getSpeed());
//		followBehaviour.startBehaviour();

//		randomMovementBehaviour = new RandomMovementBehaviour(enemy, level);
//		randomMovementBehaviour.setHorizontalSpeed(enemy.getSpeed() * 2);
//		randomMovementBehaviour.setVerticalSpeed(enemy.getSpeed());
//		randomMovementBehaviour.setCondition(behaviour -> !followBehaviour.isFollowing());
//		randomMovementBehaviour.startBehaviour();
//	}

	@Override
	public void actorDidChangeHealth(final GameActor actor)
	{
		if (!actor.isAlive())
			actorLabel.removeFromSuperview();
	}

	@Override
	public void mapObjectDidMove(final MapObject mapObject)
	{
		actorLabel.setFrame(mapObject.getBounds());
	}

	@Override
	public void update(double time)
	{
		if (!controlledActor.isAlive())
		{
			actorLabel.removeFromSuperview();
			return;
		}
		healthRegenerationController.updateTime(time);
		if (healthRegenerationController.requiresUpdate())
			controlledActor.regenerateHealth(healthRegenerationController.getNumberOfSteps());

		Point playerCenter = player.getCenter();
		Point enemyCenter  = controlledActor.getCenter();

		if (controlledActor.getSpeed() > 0)
		{
//			horizontalMovementController.updateTime(time);
//			verticalMovementController.updateTime(time);
//
//			if (horizontalMovementController.requiresUpdate() || verticalMovementController.requiresUpdate())
//			{
//				boolean recalculateFlag = false;
//				if (path == null)
//				{
//					if (lastCheckPoint == null ||
//					    Math.abs(playerCenter.x - lastCheckPoint.x) + Math.abs(playerCenter.y - lastCheckPoint.y) * 2 >=
//					    requiredDistance)
//					{
//						if (playerDist > enemy.getVisionRange())
//						{
//							lastCheckPoint = playerCenter;
//							requiredDistance = playerDist - enemy.getVisionRange();
//						}
//						else
//							recalculateFlag = true;
//					}
//				}
//				else if (!PathUtil.pathContains(path, playerCenter))
//					recalculateFlag = true;
////
////				if (playerDist == 0)
////					recalculateFlag = false;
//
//				if (recalculateFlag)
//				{
//					Point[] newPath = map.findPath(
//							enemyCenter,
//							playerCenter,
//							enemy.getBounds().width,
//							enemy.getBounds().height);
//					if (newPath != null && ((path != null && newPath.length > enemy.getFollowRange()) ||
//					    (path == null && newPath.length > enemy.getVisionRange())))
//					{
//						lastCheckPoint = playerCenter;
//						requiredDistance = newPath.length - enemy.getVisionRange();
//						path = null;
//					}
//					else
//					{
//						path = newPath;
//						pathIndex = 0;
//					}
//				}
//
//				if (path != null)
//				{
//					int playerIndex = PathUtil.pathIndex(path, playerCenter);
//					/*if (Math.abs(playerIndex - pathIndex) > enemy.getAttackRange() ||
//					    !map.canSee(enemyCenter, playerCenter))
//					{
//						if (playerIndex > pathIndex)
//						{
//							pathIndex += movementController.getNumberOfSteps();
//							if (pathIndex > playerIndex)
//								pathIndex = playerIndex;
//						}
//						else if (playerIndex < pathIndex)
//						{
//							pathIndex -= movementController.getNumberOfSteps();
//							if (pathIndex < playerIndex)
//								pathIndex = playerIndex;
//						}
//
//					}*/
//					int horizontalSteps = horizontalMovementController.getNumberOfSteps();
//					int verticalSteps = verticalMovementController.getNumberOfSteps();
//					if (PathUtil.pathLength(path, pathIndex, playerIndex, 1, 2) >
//					    enemy.getBaseAttack().getAttackRange() ||
//					    !map.canSee(enemyCenter, playerCenter))
//					{
//
//						if (pathIndex < playerIndex)
//						{
//							pathIndex += Math.min(horizontalSteps, verticalSteps);
//							if (pathIndex >= path.length - 1)
//								pathIndex = path.length - 2;
//							if (horizontalSteps > verticalSteps)
//							{
//								loop: for (int i = 0; i < horizontalSteps - verticalSteps; i++)
//									switch (Direction.direction(path[pathIndex], path[pathIndex + 1]))
//									{
//										case LEFT:
//										case RIGHT:
//											pathIndex++;
//											break;
//										default:
//											break loop;
//									}
//							}
//							else if (verticalSteps > horizontalSteps)
//							{
//								loop: for (int i = 0; i < verticalSteps - horizontalSteps; i++)
//									switch (Direction.direction(path[pathIndex], path[pathIndex + 1]))
//									{
//										case UP:
//										case DOWN:
//											pathIndex++;
//											break;
//										default:
//											break loop;
//									}
//							}
//						}
//						if (pathIndex < 0)
//							pathIndex = 0;
//						else if (pathIndex >= path.length)
//							pathIndex = path.length - 1;
//					}
//
//					enemy.setCenter(path[pathIndex]);
//					enemy.enterMovementState();
//				}
//				else
//				{
//					Rectangle bounds = new Rectangle(enemy.getBounds());
//					for (int i = 0; i < horizontalMovementController.getNumberOfSteps(); i++)
//					{
//						Rectangle newBounds = new Rectangle(bounds);
//						newBounds.translate((int) (Math.random() * 4 - 2), 0);
//						enemy.enterMovementState();
//						if (!map.canMoveTo(newBounds))
//							break;
//						else
//							bounds = newBounds;
//						requiredDistance--;
//					}
//					for (int i = 0; i < verticalMovementController.getNumberOfSteps(); i++)
//					{
//						Rectangle newBounds = new Rectangle(bounds);
//						newBounds.translate(0, (int) (Math.random() * 4 - 2));
//						enemy.enterMovementState();
//						if (!map.canMoveTo(newBounds))
//							break;
//						else
//							bounds = newBounds;
//						requiredDistance--;
//					}
//					enemy.setBounds(bounds);
//				}
//			}
			followBehaviour.update(time);
			randomMovementBehaviour.update(time);
		}

		int playerDist = Math.abs(enemyCenter.x - playerCenter.x) + Math.abs(enemyCenter.y - playerCenter.y) * 2;

		attackController.updateTime(time);
		if (playerDist <= controlledActor.getBaseAttack().getAttackRange() && attackController.requiresUpdate() &&
		    level.getMap().canSee(enemyCenter, playerCenter))
			for (int i = 0; i < attackController.getNumberOfSteps(); i++)
				attackPlayer();
	}

	private void attackPlayer()
	{
//		int damage = enemy.getBaseAttack().getTargetDamage() +
//		             (int) (Math.random() * enemy.getBaseAttack().getAttackDamageVariation() -
//		                    0.5 * enemy.getBaseAttack().getAttackDamageVariation());
		controlledActor.enterAttackState();
//		player.decreaseHealth(damage);

//		Point playerLocation = player.getCenter();
//		Point enemyLocation = enemy.getCenter();

//		String[] projectiles = enemy.getBaseAttack().getAttackProjectilesForDirection(Direction.direction(enemyLocation, playerLocation));

		SkillExecutor skillExecutor = controlledActor.getBaseAttack().getSkillExecutor();
		skillExecutor.executeSkill(controlledActor, player);

//		SkillCoordinator skillCoordinator = new SkillCoordinator(
//				mapView,
//				enemy.getBaseAttack().getOverlays(),
//				projectiles,
//				enemy.getBaseAttack().getTargetOverlays(),
//				enemy.getBaseAttack().getOverlayColor(),
//				enemy.getBaseAttack().getAttackProjectileColor(),
//				enemy.getBaseAttack().getTargetOverlayColor(),
//				enemy.getBaseAttack().getOverlayAnimationTime(),
//				enemy.getBaseAttack().getAttackProjectileAnimationTime(enemyLocation, playerLocation),
//				enemy.getBaseAttack().getTargetOverlayAnimationTime(), damage);
//		skillCoordinator.visualizeSkill(enemy, player);
	}
}
