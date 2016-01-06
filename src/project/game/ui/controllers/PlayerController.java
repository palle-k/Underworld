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
import project.game.data.Map;
import project.game.data.MapObject;
import project.game.data.Player;
import project.game.data.PlayerDelegate;
import project.gui.components.TLabel;
import project.gui.dynamics.StepController;

import java.awt.Point;
import java.awt.Rectangle;

public class PlayerController implements PlayerDelegate
{
	private StepController      attackController;
	private Enemy[]             enemies;
	private StepController      horizontalMovementController;
	private LevelViewController mainController;
	private Map                 map;
	private Player              player;
	private TLabel              playerLabel;
	private StepController      verticalMovementController;

	public PlayerController(final Player player, final Map map, final Enemy[] enemies, TLabel playerLabel, LevelViewController mainController)
	{
		this.player = player;
		this.map = map;
		this.enemies = enemies;
		this.playerLabel = playerLabel;
		this.mainController = mainController;
		this.player.setDelegate(this);
		horizontalMovementController = new StepController(player.getSpeed() * 2);
		verticalMovementController = new StepController(player.getSpeed());
		attackController = new StepController(player.getAttackRate());
		horizontalMovementController.start();
		verticalMovementController.start();
		attackController.start();
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

	@Override
	public void mapObjectDidMove(final MapObject mapObject)
	{

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

	@Override
	public void playerDidEarnExperience(final Player player)
	{

	}

	@Override
	public void playerDidFocusOnEnemy(final Player player, final Enemy enemy)
	{

	}

	@Override
	public void playerLevelDidChange(final Player player)
	{

	}

	@Override
	public void playerShouldShowAttackPotionOverlay(final Player player)
	{

	}

	@Override
	public void playerShouldShowHealthPotionOverlay(final Player player)
	{

	}

	@Override
	public void playerShouldShowSkill1Overlay(final Player player, final GameActor target)
	{

	}

	@Override
	public void playerShouldShowSkill1State(final Player player)
	{

	}

	@Override
	public void playerShouldShowSkill2Overlay(final Player player, final GameActor target)
	{

	}

	@Override
	public void playerShouldShowSkill2State(final Player player)
	{

	}

	@Override
	public void playerShouldShowSkill3Overlay(final Player player, final GameActor target)
	{

	}

	@Override
	public void playerShouldShowSkill3State(final Player player)
	{

	}

	@Override
	public void playerShouldShowSkill4Overlay(final Player player, final GameActor target)
	{

	}

	@Override
	public void playerShouldShowSkill4State(final Player player)
	{

	}

	public void update(double time)
	{
		horizontalMovementController.updateTime(time);
		verticalMovementController.updateTime(time);
		attackController.updateTime(time);
	}

	private Enemy findNearestVisibleEnemy(int maxDist)
	{
		//TODO implement linear search (check visibility and distance)
		return null;
	}

	private void move(int dx, int dy)
	{
		if (horizontalMovementController.requiresUpdate() || verticalMovementController.requiresUpdate())
		{
			int       horizontalSteps = horizontalMovementController.getNumberOfSteps();
			int       verticalSteps   = verticalMovementController.getNumberOfSteps();
			Rectangle playerBounds    = new Rectangle(player.getBounds());

			for (int i = 0; i < Math.min(horizontalSteps, verticalSteps); i++)
			{
				playerBounds.translate(dx, dy);
				if (!map.canMoveTo(playerBounds))
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
					if (!map.canMoveTo(playerBounds))
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
					if (!map.canMoveTo(playerBounds))
					{
						playerBounds.translate(0, -dy);
						break;
					}
				}
			}
			if (!playerBounds.equals(player.getBounds()))
				player.setBounds(playerBounds);
		}
	}
}
