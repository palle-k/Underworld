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

import java.awt.Point;
import java.awt.Rectangle;

public class PlayerController
{
	private StepController attackController;
	private Enemy[]        enemies;
	private Map            map;
	private StepController movementController;
	private Player         player;

	public PlayerController(final Player player, final Map map, final Enemy[] enemies)
	{
		this.player = player;
		this.map = map;
		this.enemies = enemies;
		movementController = new StepController(player.getSpeed());
		attackController = new StepController(player.getAttackRate());
		movementController.start();
		attackController.start();
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

	public void moveDown()
	{
		move(0, 1);
	}

	public void moveLeft()
	{
		move(-1, 0);
	}

	public void moveRight()
	{
		move(1, 0);
	}

	public void moveUp()
	{
		move(0, -1);
	}

	public void update(double time)
	{
		movementController.updateTime(time);
		attackController.updateTime(time);
	}

	private Enemy findNearestVisibleEnemy(int maxDist)
	{
		//TODO implement linear search (check visibility and distance)
		return null;
	}

	private void move(int dx, int dy)
	{
		if (movementController.requiresUpdate())
		{
			int       steps        = movementController.getNumberOfSteps();
			Rectangle playerBounds = new Rectangle(player.getBounds());
			for (int i = 0; i < steps; i++)
			{
				playerBounds.translate(dx, dy);
				if (!map.canMoveTo(playerBounds))
				{
					playerBounds.translate(-dx, -dy);
					break;
				}
			}
			if (!playerBounds.equals(player.getBounds()))
				player.setBounds(playerBounds);
		}
	}
}
