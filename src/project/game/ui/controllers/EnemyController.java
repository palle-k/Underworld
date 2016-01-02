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
import project.game.data.Map;
import project.game.data.Player;
import project.gui.dynamics.StepController;

import java.awt.*;

public class EnemyController
{
	private StepController attackController;
	private Enemy          enemy;
	private boolean        following;
	private Point          lastCheckPoint;
	/*
	private Point   lastPathEndpoint;
	private Point   lastPlayerPosition;*/
	private Map            map;
	private StepController movementController;
	private Point[]        path;
	private int            pathIndex;
	private Player         player;
	private long           requiredDistance;
	//private long    requiredTravelDistance;

	public EnemyController(final Enemy enemy, final Map map, final Player player)
	{
		this.enemy = enemy;
		this.map = map;
		this.player = player;
		movementController = new StepController(enemy.getSpeed());
		attackController = new StepController(enemy.getAttackRate());
	}

	public void update(double time)
	{
		Point playerCenter = player.getCenter();
		Point enemyCenter  = enemy.getCenter();
		int   playerDist   = Math.abs(enemyCenter.x - playerCenter.x) + Math.abs(enemyCenter.y - playerCenter.y);

		if (enemy.getSpeed() > 0)
		{
			movementController.updateTime(time);

			if (movementController.requiresUpdate())
			{
				boolean recalculateFlag = false;
				if (path == null)
				{
					int travelled = Math.abs(playerCenter.x - lastCheckPoint.x) +
					                Math.abs(playerCenter.y - lastCheckPoint.y);
					if (travelled >= requiredDistance)
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
				else if (!pathContains(playerCenter))
					recalculateFlag = true;

				if (recalculateFlag)
				{
					Point[] newPath = map.findPath(
							enemyCenter,
							playerCenter,
							enemy.getBounds().width,
							enemy.getBounds().height);
					if ((path != null && newPath.length > enemy.getFollowRange()) ||
					    (path == null && newPath.length > enemy.getVisionRange()))
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
					int playerIndex = pathIndex(playerCenter);
					if (playerIndex > enemy.getAttackRange() || !map.canSee(enemyCenter, playerCenter))
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
					}
				}

			}
		}
		attackController.updateTime(time);
		if (playerDist <= enemy.getAttackRange())
		{
			if (attackController.requiresUpdate())
				for (int i = 0; i < attackController.getNumberOfSteps(); i++)
				{
					int damage = enemy.getAttackDamage() +
					             (int) (Math.random() * enemy.getAttackDamageVariation() -
					                    0.5 * enemy.getAttackDamageVariation());
					player.decreaseHealth(damage);
				}
		}
	}

	private boolean pathContains(Point point)
	{
		if (path == null)
			return false;
		for (Point p : path)
			if (p.equals(point))
				return true;
		return false;
	}

	private int pathIndex(Point point)
	{
		if (path == null)
			return -1;
		for (int i = path.length - 1; i >= 0; i--)
			if (point.equals(path[i]))
				return i;
		return -1;
	}
}
