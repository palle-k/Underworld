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

import java.awt.event.KeyEvent;
import java.util.prefs.Preferences;

public class SettingsState implements StateSaving
{
	/*
	Tastenbelegungen
	 */
	private int attackPotionKey = KeyEvent.VK_W;
	private int baseAttackKey   = KeyEvent.VK_SPACE;
	private int healthPotionKey = KeyEvent.VK_Q;
	private int moveDownKey     = KeyEvent.VK_DOWN;
	private int moveLeftKey     = KeyEvent.VK_LEFT;
	private int moveRightKey    = KeyEvent.VK_RIGHT;
	private int moveUpKey       = KeyEvent.VK_UP;
	private int skill1Key       = KeyEvent.VK_1;
	private int skill2Key       = KeyEvent.VK_2;
	private int skill3Key       = KeyEvent.VK_3;
	private int skill4Key       = KeyEvent.VK_4;

	/**
	 * Erstellt einen neuen Einstellungsstatus und laed diesen aus den
	 * angegebenen Preferences
	 * @param preferences quelle
	 */
	protected SettingsState(Preferences preferences)
	{
		load(preferences);
	}

	/**
	 * Gibt den Keycode fuer das Einnehmen eines Angriffstranks zurueck.
	 * @return Keycode - Angriffstrank
	 */
	public int getAttackPotionKey()
	{
		return attackPotionKey;
	}

	/**
	 * Gibt den Keycode fuer das Angreifen eines Gegners zurueck.
	 * @return Keycode - Angriffstaste
	 */
	public int getBaseAttackKey()
	{
		return baseAttackKey;
	}

	/**
	 * Gibt den Keycode fuer das Einnehmen eines Lebenstrank zurueck.
	 * @return Keycode - Lebenstrank
	 */
	public int getHealthPotionKey()
	{
		return healthPotionKey;
	}

	/**
	 * Gibt den Keycode fuer das Bewegen in positive y-Richtung zurueck.
	 * @return Keycode - Bewegung nach unten
	 */
	public int getMoveDownKey()
	{
		return moveDownKey;
	}

	/**
	 * Gibt den Keycode fuer das Bewegen in negative x-Richtung zurueck.
	 * @return Keycode - Bewegung nach links
	 */
	public int getMoveLeftKey()
	{
		return moveLeftKey;
	}

	/**
	 * Gibt den Keycode fuer das Bewegen in positive x-Richtung zurueck.
	 * @return Keycode - Bewegung nach rechts
	 */
	public int getMoveRightKey()
	{
		return moveRightKey;
	}

	/**
	 * Gibt den Keycode fuer das Bewegen in negative y-Richtung zurueck.
	 * @return Keycode - Bewegung nach oben
	 */
	public int getMoveUpKey()
	{
		return moveUpKey;
	}

	/**
	 * Gibt den Keycode fuer die Ausloesung der ersten Attacke zurueck
	 * @return Keycode - Erste Attacke
	 */
	public int getSkill1Key()
	{
		return skill1Key;
	}

	/**
	 * Gibt den Keycode fuer die Ausloesung der zweiten Attacke zurueck
	 * @return Keycode - Erste Attacke
	 */
	public int getSkill2Key()
	{
		return skill2Key;
	}

	/**
	 * Gibt den Keycode fuer die Ausloesung der dritten Attacke zurueck
	 * @return Keycode - Erste Attacke
	 */
	public int getSkill3Key()
	{
		return skill3Key;
	}

	/**
	 * Gibt den Keycode fuer die Ausloesung der vierten Attacke zurueck
	 * @return Keycode - Erste Attacke
	 */
	public int getSkill4Key()
	{
		return skill4Key;
	}

	/**
	 * Laed die Einstellungen aus den angegebenen Preferences. Wenn keine Werte
	 * gespeichert werden, werden folgende Standartbelegungen gewaehlt:
	 * <ul>
	 *     <li>moveUpKey: ARROW UP</li>
	 *     <li>moveLeftKey: ARROW_LEFT</li>
	 *     <li>moveRightKey: ARROW_RIGHT</li>
	 *     <li>moveDownKey: ARROW DOWN</li>
	 *     <li>baseAttackKey: SPACE</li>
	 *     <li>skill1Key: 1</li>
	 *     <li>skill2Key: 2</li>
	 *     <li>skill3Key: 3</li>
	 *     <li>skill4Key: 4</li>
	 *     <li>healthPotionKey: Q</li>
	 *     <li>attackPotionKey: W</li>
	 * </ul>
	 * @param preferences Quelle
	 */
	@Override
	public void load(final Preferences preferences)
	{
		moveUpKey = preferences.getInt("settings_move_up", KeyEvent.VK_UP);
		moveLeftKey = preferences.getInt("settings_move_left", KeyEvent.VK_LEFT);
		moveRightKey = preferences.getInt("settings_move_right", KeyEvent.VK_RIGHT);
		moveDownKey = preferences.getInt("settings_move_down", KeyEvent.VK_DOWN);

		baseAttackKey = preferences.getInt("settings_base_attack", KeyEvent.VK_SPACE);

		skill1Key = preferences.getInt("settings_skill1", KeyEvent.VK_1);
		skill2Key = preferences.getInt("settings_skill2", KeyEvent.VK_2);
		skill3Key = preferences.getInt("settings_skill3", KeyEvent.VK_3);
		skill4Key = preferences.getInt("settings_skill4", KeyEvent.VK_4);

		healthPotionKey = preferences.getInt("settings_health_potion", KeyEvent.VK_Q);
		attackPotionKey = preferences.getInt("settings_attack_potion", KeyEvent.VK_W);
	}

	/**
	 * Speichert den Einstellungsstatus im angegebenen Preferences-Objekt
	 * @param preferences Ziel
	 */
	public void save(Preferences preferences)
	{
		preferences.putInt("settings_move_up", moveUpKey);
		preferences.putInt("settings_move_left", moveLeftKey);
		preferences.putInt("settings_move_right", moveRightKey);
		preferences.putInt("settings_move_down", moveDownKey);

		preferences.putInt("settings_base_attack", baseAttackKey);

		preferences.putInt("settings_skill1", skill1Key);
		preferences.putInt("settings_skill2", skill2Key);
		preferences.putInt("settings_skill3", skill3Key);
		preferences.putInt("settings_skill4", skill4Key);

		preferences.putInt("settings_health_potion", healthPotionKey);
		preferences.putInt("settings_attack_potion", attackPotionKey);
	}

	/**
	 * Setzt den Keycode fuer die Verwendung eines Angriffstranks
	 * @param attackPotionKey Keycode - Angriffstrank
	 */
	public void setAttackPotionKey(final int attackPotionKey)
	{
		this.attackPotionKey = attackPotionKey;
	}

	/**
	 * Setzt den Keycode fuer die Ausloesung einer Standardattacke
	 * @param baseAttackKey Keycode - Standardattacke
	 */
	public void setBaseAttackKey(final int baseAttackKey)
	{
		this.baseAttackKey = baseAttackKey;
	}

	/**
	 * Setzt den Keycode fuer die Verwendung eines Lebenstranks
	 * @param healthPotionKey Keycode - Lebenstrank
	 */
	public void setHealthPotionKey(final int healthPotionKey)
	{
		this.healthPotionKey = healthPotionKey;
	}

	/**
	 * Setzt den Keycode fuer die Bewegung in positive y-Richtung
	 * @param moveDownKey Keycode - Bewegung nach unten
	 */
	public void setMoveDownKey(final int moveDownKey)
	{
		this.moveDownKey = moveDownKey;
	}

	/**
	 * Setzt den Keycode fuer die Bewegung in negative x-Richtung
	 * @param moveLeftKey Keycode - Bewegung nach links
	 */
	public void setMoveLeftKey(final int moveLeftKey)
	{
		this.moveLeftKey = moveLeftKey;
	}

	/**
	 * Setzt den Keycode fuer die Bewegung in positive x-Richtung
	 * @param moveRightKey Keycode - Bewegung nach rechts
	 */
	public void setMoveRightKey(final int moveRightKey)
	{
		this.moveRightKey = moveRightKey;
	}

	/**
	 * Setzt den Keycode fuer die Bewegung in negative y-Richtung
	 * @param moveUpKey Keycode - Bewegung nach oben
	 */
	public void setMoveUpKey(final int moveUpKey)
	{
		this.moveUpKey = moveUpKey;
	}

	/**
	 * Setzt den Keycode fuer die Ausloesung der ersten Attacke
	 * @param skill1Key Keycode - erste Attacke
	 */
	public void setSkill1Key(final int skill1Key)
	{
		this.skill1Key = skill1Key;
	}

	/**
	 * Setzt den Keycode fuer die Ausloesung der zweiten Attacke
	 * @param skill2Key Keycode - zweite Attacke
	 */
	public void setSkill2Key(final int skill2Key)
	{
		this.skill2Key = skill2Key;
	}

	/**
	 * Setzt den Keycode fuer die Ausloesung der dritten Attacke
	 * @param skill3Key Keycode - dritte Attacke
	 */
	public void setSkill3Key(final int skill3Key)
	{
		this.skill3Key = skill3Key;
	}

	/**
	 * Setzt den Keycode fuer die Ausloesung der vierten Attacke
	 * @param skill4Key Keycode - vierte Attacke
	 */
	public void setSkill4Key(final int skill4Key)
	{
		this.skill4Key = skill4Key;
	}
}
