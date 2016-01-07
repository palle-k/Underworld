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
import project.game.data.MapObject;
import project.game.data.Player;
import project.game.data.PlayerDelegate;
import project.gui.components.TLabel;
import project.gui.dynamics.StepController;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;

public class PlayerController implements PlayerDelegate
{
	private final Level level;
	private StepController attackController;
	private StepController      horizontalMovementController;
	private LevelViewController mainController;
	private TLabel              playerLabel;
	private StepController      verticalMovementController;

	public PlayerController(final Level level, TLabel playerLabel, LevelViewController mainController)
	{
		this.level = level;
		this.playerLabel = playerLabel;
		this.mainController = mainController;
		level.getPlayer().setDelegate(this);
		horizontalMovementController = new StepController(level.getPlayer().getSpeed() * 2);
		verticalMovementController = new StepController(level.getPlayer().getSpeed());
		attackController = new StepController(level.getPlayer().getAttackRate());
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
					(int) level.getPlayer().getBounds().getCenterX(),
					(int) level.getPlayer().getBounds().getCenterY());
			//TODO move player to enemy (with pathfinding)
			//attack enemy with basic attacks when in range
		}
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
