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
import project.game.ui.views.MapView;
import project.gui.components.TLabel;

/**
 * Basisklasse zum steuern eines Aktors
 *
 * @param <Actor> Klasse des gesteuerten Aktors
 */
public abstract class ActorController<Actor extends GameActor> implements GameActorDelegate
{
	/**
	 * Label des gesteuerten Aktors auf der MapView
	 */
	protected final TLabel actorLabel;

	/**
	 * Gesteuerter Aktor
	 */
	protected final Actor controlledActor;

	/**
	 * Umgebendes Level
	 */
	protected final Level level;

	/**
	 * MapView, welche das Level anzeigt
	 */
	protected final MapView mapView;

	/**
	 * Erstellt eine neue Aktorsteuerung fuer den angegebenen Aktor im angegebenen Level.
	 * Dieser wird durch das angegebene actorLabel dargestellt, welches sich auf der angegebenen
	 * mapView befindet.
	 *
	 * @param controlledActor zu steuernder Aktor
	 * @param level           umgebendes Level
	 * @param actorLabel      Darstellendes Label des gesteuerten Aktors
	 * @param mapView         Darstellende Komponente des gesamten Levels
	 */
	public ActorController(final Actor controlledActor, final Level level, final TLabel actorLabel, final MapView mapView)
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

	/**
	 * Gibt das darstellende Label des Aktors an
	 * @return Aktor-Label
	 */
	public TLabel getActorLabel()
	{
		return actorLabel;
	}

	/**
	 * Gibt den gesteuerten Aktor an
	 * @return gesteuerter Aktor
	 */
	public Actor getControlledActor()
	{
		return controlledActor;
	}

	/**
	 * Gibt das den Aktor umgebende Level an
	 * @return umgebendes Level
	 */
	public Level getLevel()
	{
		return level;
	}

	/**
	 * Gibt die darstellende Komponente des gesamten Levels an
	 * @return Levelansicht
	 */
	public MapView getMapView()
	{
		return mapView;
	}

	/**
	 * Aktualisiert den Aktor
	 * @param time Zeit der Aktualisierung
	 */
	public abstract void update(double time);

	/**
	 * Lade die Faehigkeiten des Aktors, damit diese
	 * ausgefuehrt werden koenen. Hierzu muessen
	 * level und target sowie eventuell moegliche
	 * alternative Ziele gesetzt werden.
	 * @see project.game.data.skills.SkillExecutor
	 */
	protected void loadSkills()
	{
		controlledActor.getBaseAttack().getSkillExecutor().setLevel(level);
		controlledActor.getBaseAttack().getSkillExecutor().setTarget(getMapView());
	}
}
