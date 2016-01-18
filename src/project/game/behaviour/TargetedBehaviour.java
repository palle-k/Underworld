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

package project.game.behaviour;

import project.game.data.GameActor;
import project.game.data.Level;

/**
 * Abstrakte Klasse zur Implementierung eines Verhaltens,
 * dass den kontrollierten Aktor abhaengig vom ausgewaehlten Ziel
 * steuert.
 */
public abstract class TargetedBehaviour extends Behaviour
{
	/**
	 * Ziel des Verhaltens
	 */
	private GameActor target;

	public TargetedBehaviour(final GameActor controlledActor, Level level)
	{
		super(controlledActor, level);
	}

	/**
	 * Gibt das Ziel an, gegenueber welchem das Verhalten ausgerichtet wird
	 * @return Ziel fuer das Verhalten
	 */
	public GameActor getTarget()
	{
		return target;
	}

	/**
	 * Setzt das Ziel fuer das Verhalten zurueck.
	 * Das Verhalten wird hiermit unterbunden.
	 * Entspricht dem aufruf von setTarget(null)
	 */
	public void resetTarget()
	{
		setTarget(null);
	}

	/**
	 * Setzt das Ziel fuer das Verhalten
	 * @param actor Ziel fuer das Verhalten
	 */
	public void setTarget(GameActor actor)
	{
		this.target = actor;
	}

	/**
	 * Aktualisiert das Verhalten.
	 * Ist das Verhalten gestoppt oder kein Ziel angegeben,
	 * wird der Aktor nicht veraendert.
	 * @param time Updatezeit
	 */
	@Override
	public void update(final double time)
	{
		if (target != null)
			super.update(time);
		else
			skipExecution(time);
	}
}
