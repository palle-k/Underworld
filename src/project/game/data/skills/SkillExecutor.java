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

package project.game.data.skills;

import project.game.data.GameActor;
import project.game.data.Level;
import project.game.data.SkillConfiguration;
import project.game.ui.views.MapView;

import java.util.Properties;

/**
 * Abstrakte Klasse, welche Methoden zum Ausfuehren eines Skills vorgibt.
 */
public abstract class SkillExecutor
{
	/**
	 * Konfiguration des Skills
	 */
	private SkillConfiguration configuration;

	/**
	 * Level, welches die Umgebung bildet.
	 * Ermoeglicht Reaktion auf Umgebung
	 */
	private Level level;

	/**
	 * Aktion, die beim Besiegen eines Gegners
	 * ausgefuehrt wird
	 */
	private KillAction onKillAction;

	/**
	 * Moegliche Ziele fuer Skill
	 * Ermoeglicht Flaechenskills
	 */
	private GameActor[] possibleTargets;

	/**
	 * Ziel fuer die Darstellung
	 */
	private MapView target;

	/**
	 * Fuehrt den Skill mit angegebenem Aktor und Ziel aus.
	 * Das Ziel kann null sein, wenn
	 * <code>configuration.requiresFocus() == false</code>.
	 * @param attackingActor Angreifer
	 * @param attackTarget Ziel
	 */
	public abstract void executeSkill(GameActor attackingActor, GameActor attackTarget);

	/**
	 * Gibt die Konfiguration des Skills an
	 * @return Skillkonfiguration
	 */
	public SkillConfiguration getConfiguration()
	{
		return configuration;
	}

	/**
	 * Gibt das Level an, in welchem der Skill ausgefuert werden soll
	 * @return Level
	 */
	public Level getLevel()
	{
		return level;
	}

	/**
	 * Gibt die Aktion an, die beim besiegen eines Gegners ausgefuehrt wird
	 * @return Aktion bei Besiegen
	 */
	public KillAction getOnKillAction()
	{
		return onKillAction;
	}

	/**
	 * Gibt die moeglichen Angriffsziele an.
	 * Dies ist fuer Flaechenskills erforderlich.
	 * <br>
	 * Anmerkung: attackTarget in executeSkill may be contained
	 * in nearby targets.
	 * @return moegliche Angriffsziele
	 */
	public GameActor[] getPossibleTargets()
	{
		return possibleTargets;
	}

	/**
	 * Gibt das Ziel an, auf welchem der Skill angezeigt werden soll
	 * @return Ziel
	 */
	public MapView getTarget()
	{
		return target;
	}

	/**
	 * Laed zusaetzliche, nicht standardmaessig geladene Properties
	 * zur Ausfuehrung des Skills.
	 * <br>
	 * Methode optional
	 * @param properties Quelle fuer zusaetzliche Properties
	 * @param prefix Prefix, welches vor Properties hinzugefuegt werden muss.
	 */
	public void loadAdditionalProperties(Properties properties, String prefix)
	{
		//Default implementation makes this method optional.
	}

	/**
	 * Setzt die Konfiguration des Skills.
	 * Diese enthaelt saemtliche Informationen zum Skill.
	 * @param configuration Konfiguration des Skills
	 */
	public void setConfiguration(SkillConfiguration configuration)
	{
		this.configuration = configuration;
	}

	/**
	 * Setzt das level, in welchem der Skill ausgefuehrt werden soll
	 * @param level Level
	 */
	public void setLevel(final Level level)
	{
		this.level = level;
	}

	/**
	 * Setzt die Aktion, die beim besiegen eines Gegners ausgefuehrt werden soll
	 * @param onKillAction Aktion beim Besiegen
	 */
	public void setOnKillAction(final KillAction onKillAction)
	{
		this.onKillAction = onKillAction;
	}

	/**
	 * Setzt andere moegliche Angriffsziele, um Flaechenskills
	 * zu ermoeglichen.
	 * @param possibleTargets moegliche Angriffsziele
	 */
	public void setPossibleTargets(final GameActor[] possibleTargets)
	{
		this.possibleTargets = possibleTargets;
	}

	/**
	 * Setzt das Ziel, auf welchem der Skill dargestellt werden soll.
	 * @param visualizationTarget Ziel fuer die Darstellung
	 */
	public void setTarget(MapView visualizationTarget)
	{
		this.target = visualizationTarget;
	}

	/**
	 * Fuehre die Aktion beim Besiegen eines Gegners aus
	 */
	protected void runKillAction(GameActor killedActor)
	{
		if (onKillAction != null)
			onKillAction.actorKilled(killedActor);
	}
}
