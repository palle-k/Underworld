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

package project.game.data;

import project.game.data.state.PlayerState;
import project.game.data.state.SavedGameState;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Properties;

/**
 * Verwaltung des Spielers
 */
public class Player extends GameActor implements Serializable
{
	/**
	 * Erstellt einen neuen Spieler
	 * Laed automatisch die entsprechenden Ebenen fuer die gewaehlte Spielerklasse
	 *
	 * @return Spieler-Objekt
	 * @throws IOException
	 */
	public static Player makePlayer() throws IOException
	{
		//Properties  properties  = new Properties();
		PlayerState playerState = SavedGameState.getPlayerState();
		if (playerState.playerClassChosen())
		{
			String configurationFile = "";
			if (playerState.getPlayerClass() == PlayerState.HUNTER)
				configurationFile = "Hunter";
			else if (playerState.getPlayerClass() == PlayerState.KNIGHT)
				configurationFile = "Knight";
			else if (playerState.getPlayerClass() == PlayerState.WIZARD)
				configurationFile = "Wizard";
			configurationFile = "objects/" + configurationFile + ".properties";
			//properties.load(Player.class.getResourceAsStream(configurationFile));
			URL source = Player.class.getResource(configurationFile);
			return new Player(source);
		}
		else
			throw new RuntimeException("Presenting Level before player class was chosen.");
	}

	private transient long experience;

	private transient SkillConfiguration skill1;
	private transient SkillConfiguration skill2;
	private transient SkillConfiguration skill3;
	private transient SkillConfiguration skill4;

	/**
	 * Initialisiert einen neuen Spieler und laed diesen aus den gegebenen Properties
	 * DEPRECATED. Use new Player(URL source) instead
	 *
	 * @param playerProperties - Eigenschaften des Spielers und Ebenen fuer verschiedene Stati
	 */
	@Deprecated
	protected Player(Properties playerProperties)
	{
		super(playerProperties);
		//TODO parse attacks etc...
	}

	/**
	 * Initialisiert einen neuen Spieler und laed diesen aus der Properties-File, welche sich
	 * an der gegebenen Quell-URL befindet.
	 *
	 * @param source Quell-URL
	 * @throws IOException wenn die Quelle nicht existiert oder nicht gelesen werden kann
	 */
	protected Player(final URL source) throws IOException
	{
		super(source);
	}

	/**
	 * Der Spieler die angegebene Anzahl von Erfahrungspunkten
	 * @param exp verdiente Erfahrungspunkte
	 */
	public void earnExperience(int exp)
	{
		boolean levelWillChange = false;
		if ((experience + exp) >= getLevelEndExperience())
			levelWillChange = true;
		experience += exp;
		SavedGameState.getPlayerState().setPlayerExperience(experience);
		if (delegate instanceof PlayerDelegate)
		{
			if (levelWillChange)
				((PlayerDelegate) delegate).playerLevelDidChange(this);
			((PlayerDelegate) delegate).playerDidEarnExperience(this);
		}
	}

	/**
	 * Erhalte die gesamte Erfahrung, welche der Spieler erhalten hat
	 * @return erhaltene Erfahrung
	 */
	public long getExperience()
	{
		return experience;
	}

	@Override
	public double getHealthRegeneration()
	{
		return (int) (super.getHealthRegeneration() * Math.sqrt(getLevel()));
	}

	/**
	 * Gibt das Level des Spielers zurueck.
	 * Dieses wird aus der erhaltenen Erfahrung berechnet.
	 * @return Spielerlevel
	 */
	@Override
	public int getLevel()
	{
		return (int) Math.sqrt(experience * 0.01) + 1;
	}

	/**
	 * Gibt die Anzahl an Erfahrungspunkten an, die noetig ist, um das aktuelle
	 * Spielerlevel abzuschliessen
	 * @return Fuer das naechste Level benoetigte Erfahrung
	 */
	public long getLevelEndExperience()
	{
		long level       = getLevel();
		long levelSquare = level * level;
		return levelSquare * 100;
	}

	/**
	 * Gibt die Anzahl von Erfahrungspunkten an, die noetig waren,
	 * um das vorherige Spielerlevel abzuschliessen und im Level aufzusteigen
	 * @return Anzahl an Erfahrungspunkten, bei denen das aktuelle Level
	 * begonnen hat
	 */
	public long getLevelStartExperience()
	{
		long level       = getLevel() - 1;
		long levelSquare = level * level;
		return levelSquare * 100;
	}

	@Override
	public int getMaxHealth()
	{
		return (int) (super.getMaxHealth() * Math.sqrt(getLevel()));
	}

	/**
	 * Gibt die Konfiguration fuer den ersten Skill an
	 *
	 * @return Konfiguration des ersten Skills
	 * @see SkillConfiguration
	 */
	public SkillConfiguration getSkill1()
	{
		return skill1;
	}

	/**
	 * Gibt die Konfiguration fuer den zweiten Skill an
	 *
	 * @return Konfiguration des zweiten Skills
	 * @see SkillConfiguration
	 */
	public SkillConfiguration getSkill2()
	{
		return skill2;
	}

	/**
	 * Gibt die Konfiguration fuer den dritten Skill an
	 *
	 * @return Konfiguration des dritten Skills
	 * @see SkillConfiguration
	 */
	public SkillConfiguration getSkill3()
	{
		return skill3;
	}

	/**
	 * Gibt die Konfiguration fuer den vierten Skill an
	 *
	 * @return Konfiguration des vierten Skills
	 * @see SkillConfiguration
	 */
	public SkillConfiguration getSkill4()
	{
		return skill4;
	}

	/**
	 * Stellt den Spieler wieder her
	 */
	@Override
	protected void restore()
	{
		super.restore();
		experience = SavedGameState.getPlayerState().getPlayerExperience();
		skill1 = new SkillConfiguration();
		skill1.load(properties, "skill_1_");
		skill2 = new SkillConfiguration();
		skill2.load(properties, "skill_2_");
		skill3 = new SkillConfiguration();
		skill3.load(properties, "skill_3_");
		skill4 = new SkillConfiguration();
		skill4.load(properties, "skill_4_");
	}
}