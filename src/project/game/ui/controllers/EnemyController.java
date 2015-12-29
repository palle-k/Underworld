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

import project.game.data.Enemy;
import project.game.data.Map;
import project.game.data.Player;

import java.awt.*;

public class EnemyController
{
	private Enemy   enemy;
	private boolean following;
	private long    lastFollowTime;
	private Point   lastPathEndpoint;
	private Point   lastPlayerPosition;
	private Map     map;
	private Point[] path;
	private int     pathIndex;
	private Player  player;
	private long    requiredTravelDistance;

	public EnemyController(final Enemy enemy, final Map map, final Player player)
	{
		this.enemy = enemy;
		this.map = map;
		this.player = player;
	}

	public void update(double time)
	{

		//TODO optimize number of path recalculations
		//If path is too long: mark player position and distance and check, if distance was travelled

		Point playerCenter = new Point((int) player.getBounds().getCenterX(), (int) player.getBounds().getCenterY());

		if (lastPlayerPosition.equals(playerCenter) && !following)
			return;
		if (lastPathEndpoint != null && Math.abs(playerCenter.x - lastPathEndpoint.x) +
		                                Math.abs(playerCenter.y - lastPathEndpoint.y) >
		                                requiredTravelDistance +
		                                (following ? enemy.getFollowRange() : enemy.getVisionRange()))
			return;

		Point enemyCenter = new Point((int) enemy.getBounds().getCenterX(), (int) enemy.getBounds().getCenterY());
		int dist =              Math.abs(
				enemyCenter.x -
				playerCenter.x) +
		                        Math.abs(
				           enemyCenter.y -
				           playerCenter.y);

		if (enemy.getSpeed() > 0)
		{
			following = dist <= (following ? enemy.getFollowRange() : enemy.getVisionRange());
			if (following)
			{
				if (!pathContains(playerCenter))
					path = map.findPath(enemyCenter, playerCenter);
				following = path.length <= enemy.getFollowRange();
			}


			if (following)
			{
				long playerIndex = pathIndex(playerCenter);
				if (playerIndex - pathIndex <= enemy.getAttackRange() && map.canSee(enemyCenter, playerCenter))
				{
					//TODO attack player
				}
				else
				{
					if (lastFollowTime == 0)
						lastFollowTime = (long) (time * enemy.getSpeed());
					long followTime = (long) (time * enemy.getSpeed());
					long steps      = followTime - lastFollowTime;
					if (steps > 0)
					{
						pathIndex += steps;
						if (pathIndex >= path.length)
							pathIndex = path.length - 1;
						Rectangle enemyBounds = enemy.getBounds();
						int       dx          = path[pathIndex].x - enemyCenter.x;
						int       dy          = path[pathIndex].y - enemyCenter.y;
						enemyBounds.translate(dx, dy);
						enemy.setBounds(enemyBounds);
					}
				}
			}
			else
			{
				path = null;
				lastFollowTime = 0;
				pathIndex = 0;
			}

			lastPlayerPosition = playerCenter;

		/*
		TODO check if player in vision range, if yes:
			Calculate path and check for distance
			If distance less than vision range:
				Walk to attack range
				Attack
		Only recalculate path if player is not on path anymore
		If path distance is greater than vision range:
			Save Point and distance to travel before checking again
		*/
		}
		else
		{
			long playerIndex = pathIndex(playerCenter);
			if (playerIndex - pathIndex <= enemy.getAttackRange() && map.canSee(enemyCenter, playerCenter))
			{
				//TODO attack player
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
