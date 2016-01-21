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

import java.util.function.Predicate;

/**
 * Abstrakte Klasse zur Implementierung von Verhalten
 * von Aktoren.
 */
public abstract class Behaviour
{
	/**
	 * Der durch das Verhalten kontrollierte Aktor
	 */
	private final GameActor controlledActor;

	/**
	 * Levelumgebung des Aktors
	 */
	private final Level level;

	/**
	 * Gibt eine Bedingung an, die die Ausfuehrung des
	 * Verhaltens unterbinden oder erlauben kann.
	 */
	private Predicate<Behaviour> condition;

	/**
	 * Gibt an, ob das Verhalten aktiv ist
	 */
	private boolean executing;

	/**
	 * Zeitpunkt der letzten Aktualisierung
	 */
	private double lastUpdateTime;

	/**
	 * Erstellt ein neues Verhalten fuer den
	 * angegebenen Aktor im angegebenen Level
	 * @param controlledActor gesteuerter Aktor
	 * @param level Umgebung
	 */
	public Behaviour(GameActor controlledActor, final Level level)
	{
		this.controlledActor = controlledActor;
		this.level = level;
	}

	/**
	 * Gibt die Bedingung an, die das Verhalten unterbinden kann
	 * @return Verhaltensbedingung
	 */
	public Predicate<Behaviour> getCondition()
	{
		return condition;
	}

	/**
	 * Gibt den gesteuerten Aktor an
	 * @return gesteuerter Aktor
	 */
	public GameActor getControlledActor()
	{
		return controlledActor;
	}

	/**
	 * Gibt das Level an, in welchem sich der Aktor befindet
	 * @return Umgebungs-Level
	 */
	public Level getLevel()
	{
		return level;
	}

	/**
	 * Gibt an, ob das Verhalten ausgefuehrt wird
	 * @return true, wenn das Verhalten ausgefuehrt wird, sonst false
	 */
	public boolean isExecuting()
	{
		return executing;
	}

	/**
	 * Pausiert das Verhalten fuer die angegebene Anzahl von Sekunden.
	 * Die Tatsaechliche Pausierungsdauer kann variieren.
	 * @param duration Dauer der Pausierung in Sekunden
	 */
	public void pause(double duration)
	{
		lastUpdateTime += duration;
	}

	/**
	 * Setzt eine Bedingung, die das Verhalten unterbinden kann
	 * @param condition Verhaltensbedingung
	 */
	public void setCondition(final Predicate<Behaviour> condition)
	{
		this.condition = condition;
	}

	/**
	 * Startet das Verhalten<br>
	 * Folgende Aufrufe von update(time) aktualisieren
	 * den Aktor dem Verhalten entsprechend.
	 */
	public void startBehaviour()
	{
		executing = true;
	}

	/**
	 * Beendet das Verhalten<br>
	 * Folgende Aufrufe von update(time) werden ignoriert.
	 */
	public void stopBehaviour()
	{
		executing = false;
	}

	/**
	 * Aktualisiert das Verhalten.
	 * Ist das Verhalten gestoppt, wird der Aktor nicht
	 * veraendert.
	 * @param time Updatezeit
	 */
	public void update(double time)
	{
		if (!executing)
			return;
		if ((condition == null || condition.test(this)) && lastUpdateTime < time)
			executeBehaviour(time);
		else
			skipExecution(time);
		lastUpdateTime = time;
	}

	/**
	 * Eine Implementierung dieser Methode fuehrt das
	 * Verhalten auf dem gesteuerten Aktor aus.
	 * @param time Updatezeit
	 */
	protected abstract void executeBehaviour(double time);

	/**
	 * Eine Implementierung dieser Methode aktualisiert
	 * den Status des Verhaltens ohne dass der Aktor
	 * veraendert wird
	 * @param time Updatezeit
	 */
	protected abstract void skipExecution(double time);
}
