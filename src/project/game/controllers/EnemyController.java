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
import project.game.ui.views.MapView;
import project.gui.components.TLabel;
import project.gui.dynamics.StepController;

import java.awt.Point;

/**
 * Steuerungsklasse fuer Gegner:
 * Verfolgung des Spielers, zufaellige Bewegung und Angriff
 */
public class EnemyController extends ActorController<Enemy> implements GameActorDelegate
{
	private StepController attackController;
	private FollowBehaviour followBehaviour;
	private StepController healthRegenerationController;
	private Player player;
	private RandomMovementBehaviour randomMovementBehaviour;

	public EnemyController(final Enemy controlledActor, final Level level, final TLabel actorLabel, final MapView mapView)
	{
		super(controlledActor, level, actorLabel, mapView);
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
		randomMovementBehaviour.setCondition(behaviour -> !followBehaviour.isFollowing());
		randomMovementBehaviour.startBehaviour();
	}

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

	@Override
	protected void loadSkills()
	{
		super.loadSkills();
		controlledActor.getBaseAttack().getSkillExecutor().setPossibleTargets(new GameActor[]{ level.getPlayer() });
	}

	/**
	 * Greift den Spieler an.
	 */
	private void attackPlayer()
	{
		controlledActor.enterAttackState();

		SkillExecutor skillExecutor = controlledActor.getBaseAttack().getSkillExecutor();
		skillExecutor.executeSkill(controlledActor, player);

		followBehaviour.pause(controlledActor.getBaseAttack().getTotalAnimationTime());
	}
}
