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

public class PlayerController
{
	private Enemy[] enemies;
	private Map     map;
	private Player  player;

	public PlayerController(final Player player, final Map map, final Enemy[] enemies)
	{
		this.player = player;
		this.map = map;
		this.enemies = enemies;
	}

	public Enemy attackEnemy()
	{
		Enemy enemy = findNearestVisibleEnemy(20);
		if (enemy != null)
		{
			Point enemyCenter = new Point((int) enemy.getBounds().getCenterX(), (int) enemy.getBounds().getCenterY());
			Point playerCenter = new Point(
					(int) player.getBounds().getCenterX(),
					(int) player.getBounds().getCenterY());
			//TODO move player to enemy (with pathfinding)
			//TODO attack enemy with basic attacks when in range
		}
		return enemy;
	}

	public boolean attackSkill1(double time)
	{
		//TODO If attack needs enemy focus, check for it
		//execute skill
		return false;
	}

	public boolean attackSkill2(double time)
	{
		//TODO If attack needs enemy focus, check for it
		//execute skill
		return false;
	}

	public boolean attackSkill3(double time)
	{
		//TODO If attack needs enemy focus, check for it
		//execute skill
		return false;
	}

	public boolean attackSkill4(double time)
	{
		//TODO If attack needs enemy focus, check for it
		//execute skill
		return false;
	}

	public void moveDown(double time)
	{
		//TODO check movement, make movement and cancel attacks
	}

	public void moveLeft(double time)
	{
		//TODO check movement, make movement and cancel attacks
	}

	public void moveRight(double time)
	{
		//TODO check movement, make movement and cancel attacks
	}

	public void moveUp(double time)
	{
		//TODO check movement, make movement and cancel attacks
	}

	public void update(double time)
	{
		//TODO update attacks and movement when focussed on enemy
	}

	private Enemy findNearestVisibleEnemy(int maxDist)
	{
		//TODO implement linear search (check visibility and distance)
		return null;
	}
}
