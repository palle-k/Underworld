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
import project.game.data.SkillConfiguration;
import project.gui.components.TComponent;

import java.util.Properties;

/**
 * Interface, welches Methoden zum Ausfuehren eines Skills vorgibt.
 */
public interface SkillExecutor
{
	/**
	 * Fuehrt den Skill mit angegebenem Aktor und Ziel aus.
	 * Das Ziel kann null sein, wenn
	 * <code>configuration.requiresFocus() == false</code>.
	 * @param attackingActor Angreifer
	 * @param attackTarget Ziel
	 */
	void executeSkill(GameActor attackingActor, GameActor attackTarget);

	/**
	 * Laed zusaetzliche, nicht standardmaessig geladene Properties
	 * zur Ausfuehrung des Skills.
	 * <br>
	 * Methode optional
	 * @param properties Quelle fuer zusaetzliche Properties
	 * @param prefix Prefix, welches vor Properties hinzugefuegt werden muss.
	 */
	default void loadAdditionalProperties(Properties properties, String prefix)
	{
		//Default implementation makes this method optional.
	}

	/**
	 * Setzt die Konfiguration des Skills.
	 * Diese enthaelt saemtliche Informationen zum Skill.
	 * @param configuration Konfiguration des Skills
	 */
	void setConfiguration(SkillConfiguration configuration);

	/**
	 * Setzt das Ziel, auf welchem der Skill dargestellt werden soll.
	 * @param visualizationTarget Ziel fuer die Darstellung
	 */
	void setTarget(TComponent visualizationTarget);
}
