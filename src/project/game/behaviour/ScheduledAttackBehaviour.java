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
import project.game.data.SkillConfiguration;

/**
 * Verhalten zum Ausloesen eines Skills sobald das Ziel in Reichweite ist
 */
public class ScheduledAttackBehaviour extends TargetedBehaviour
{
	private SkillConfiguration configuration;

	private boolean            executed;

	/**
	 * Erstellt ein neues Verhalten zum Ausloesen eines Skills sobald das Ziel in
	 * Reichweite ist
	 *
	 * @param controlledActor gesteuerter Aktor
	 * @param level           umgebendes Level
	 */
	public ScheduledAttackBehaviour(final GameActor controlledActor, final Level level)
	{
		super(controlledActor, level);
	}

	/**
	 * Gibt die Angriffskonfiguration an
	 * @return Angriffskonfiguration
	 */
	public SkillConfiguration getConfiguration()
	{
		return configuration;
	}

	/**
	 * Setzt die Ausloesung zurueck.
	 * Notwendig, wenn der Skill ein weiteres mal ausgeloest werden soll.
	 */
	public void resetExecution()
	{
		executed = false;
	}

	/**
	 * Setzt den Skill, welcher vom Verhalten ausgefuehrt werden soll
	 * @param configuration Skill
	 */
	public void setConfiguration(final SkillConfiguration configuration)
	{
		this.configuration = configuration;
	}

	@Override
	protected void executeBehaviour(final double time)
	{
		if (!executed
		    && Math.abs(getTarget().getCenter().x - getControlledActor().getCenter().x)
		       + Math.abs(getTarget().getCenter().y - getControlledActor().getCenter().y) * 2
		       <= configuration.getAttackRange()
		    && getLevel().getMap().canSee(getControlledActor().getCenter(), getTarget().getCenter()))
		{
			executed = true;

		}
	}

	@Override
	protected void skipExecution(final double time)
	{

	}
}
