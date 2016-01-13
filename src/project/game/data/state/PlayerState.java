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

package project.game.data.state;

import java.util.prefs.Preferences;

/**
 * Status des Spielers
 * Sicherung der gewaehlten Spielerklasse
 * Sicherung der gesammelten Erfahrung des Spielers
 */
public class PlayerState implements StateSaving
{
	/**
	 * Moegliche Spielerklassen.
	 * Werte sind:
	 * <ul>
	 * <li>KNIGHT: Ritter</li>
	 * <li>HUNTER: Jaeger</li>
	 * <li>WIZARD: Magier</li>
	 * </ul>
	 */
	private enum PlayerClass
	{
		KNIGHT,
		HUNTER,
		WIZARD
	}

	/*
	Oeffentlich sichtbare Spielerklassen
	Verhindert den Zugriff auf das PlayerClass-enum
	 */

	/**
	 * Spielerklasse Jaeger:
	 * Hohe Lauf- und Angriffgeschwindigkeit,
	 * wenig Leben, Fernangriff
	 */
	public static final PlayerClass HUNTER = PlayerClass.HUNTER;

	/**
	 * Spielerklasse Ritter:
	 * Geringe Lauf- und Angriffgeschwindigkeit,
	 * viel Leben, Nahkampf
	 */
	public static final PlayerClass KNIGHT = PlayerClass.KNIGHT;

	/**
	 * Spielerklasse Magier:
	 * Geringe Lauf- und mittlere Angriffgeschwindigkeit
	 * Mittleres Leben, Fernkampf, Unterstuetzungsskills
	 */
	public static final PlayerClass WIZARD = PlayerClass.WIZARD;
	/**
	 * Gewaehlte Spielerklasse (Jaeger, Krieger oder Magier)
	 */
	private PlayerClass playerClass;
	/**
	 * Vom Spieler gesammelte Erfahrung
	 */
	private long        playerExperience;

	/**
	 * Erstellt einen neuen Spielerstatus und laed diesen aus den
	 * angegebenen Preferences
	 * @param preferences Preferences, aus denen der Spielerstatus geladen wird
	 */
	protected PlayerState(Preferences preferences)
	{
		load(preferences);
	}

	/**
	 * Gibt die Spielerklasse oder null, wenn keine Spielerklasse gewaehlt wurde zurueck.
	 * @return Spielerklasse
	 */
	public PlayerClass getPlayerClass()
	{
		return playerClass;
	}

	/**
	 * Gibt die vom Spieler gesammelte Erfahrung an.
	 * Erfahrung kann durch das Besiegen von Monstern gesammelt werden.
	 *
	 * @return Spielererfahrung
	 */
	public long getPlayerExperience()
	{
		return playerExperience;
	}

	/**
	 * Laed den Spielerstatus aus den angegebenen Einstellungen
	 * Ist die Spielerklasse nicht gewaehlt, ist diese null.
	 * Ist die gesammelte Erfharung nicht gespeichert, ist diese 0.
	 *
	 * @param preferences Quelle
	 */
	@Override
	public void load(final Preferences preferences)
	{
		int playerClassOrdinal = preferences.getInt("player_class_ordinal", -1);
		if (playerClassOrdinal != -1)
			playerClass = PlayerClass.values()[playerClassOrdinal];
		playerExperience = preferences.getLong("player_experience", 0);
	}

	/**
	 * Gibt zurueck, ob die Spielerklasse gewaehlt wurde.
	 * Aequivalent zu getPlayerClass() != null
	 * @return true, wenn die Spielerklasse gewaehlt wurde, sonst false
	 */
	public boolean playerClassChosen()
	{
		return playerClass != null;
	}

	/**
	 * Sichert den Spielerstatus in den angegebenen Preferences.
	 * @param preferences Ziel
	 */
	public void save(Preferences preferences)
	{
		if (playerClass != null)
			preferences.putInt("player_class_ordinal", playerClass.ordinal());
		preferences.putLong("player_experience", playerExperience);
	}

	/**
	 * Setzt die Spielerklasse des Spielers.
	 *
	 * @param playerClass neue Spielerklasse
	 */
	public void setPlayerClass(final PlayerClass playerClass)
	{
		this.playerClass = playerClass;
	}

	/**
	 * Setzt die vom Spieler gesammelte Erfahrung.
	 * Erfahrung kann durch das Besiegen von Monstern gesammelt werden.
	 *
	 * @param playerExperience gesammelte Spielererfahrung
	 */
	public void setPlayerExperience(final long playerExperience)
	{
		this.playerExperience = playerExperience;
	}
}
