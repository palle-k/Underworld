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

import project.game.data.GameActor;
import project.game.data.GameActorDelegate;
import project.game.data.Level;
import project.gui.components.TComponent;
import project.gui.components.TLabel;

public abstract class ActorController<Actor extends GameActor> implements GameActorDelegate
{
	protected final TLabel actorLabel;

	protected final Actor controlledActor;

	protected final Level level;

	protected final TComponent mapView;

	public ActorController(final Actor controlledActor, final Level level, final TLabel actorLabel, final TComponent mapView)
	{
		this.controlledActor = controlledActor;
		this.controlledActor.setDelegate(this);
		this.level = level;
		this.actorLabel = actorLabel;
		this.mapView = mapView;

		loadSkills();
	}

	@Override
	public void actorDidChangeState(final GameActor actor)
	{
		if (actor.getState() == GameActor.RESTING)
			actorLabel.setText(actor.getRestingState());
		else if (actor.getState() == GameActor.DEAD)
			actorLabel.setText(actor.getDeadState());
		else if (actor.getState() == GameActor.ATTACKING)
			actorLabel.setText(actor.getAttackState());
		else if (actor.getState() == GameActor.DEFENDING)
			actorLabel.setText(actor.getDefenseState());
		else if (actor.getState() == GameActor.MOVING)
			actorLabel.setText(actor.getNextMovementState());
	}

	public TLabel getActorLabel()
	{
		return actorLabel;
	}

	public Actor getControlledActor()
	{
		return controlledActor;
	}

	public Level getLevel()
	{
		return level;
	}

	public TComponent getMapView()
	{
		return mapView;
	}

	public abstract void update(double time);

	protected void loadSkills()
	{
		controlledActor.getBaseAttack().getSkillExecutor().setLevel(level);
		controlledActor.getBaseAttack().getSkillExecutor().setTarget(getMapView());
	}
}
