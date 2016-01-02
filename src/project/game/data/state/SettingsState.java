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

public class SettingsState
{
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

	protected SettingsState(Preferences preferences)
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

	public int getAttackPotionKey()
	{
		return attackPotionKey;
	}

	public int getBaseAttackKey()
	{
		return baseAttackKey;
	}

	public int getHealthPotionKey()
	{
		return healthPotionKey;
	}

	public int getMoveDownKey()
	{
		return moveDownKey;
	}

	public int getMoveLeftKey()
	{
		return moveLeftKey;
	}

	public int getMoveRightKey()
	{
		return moveRightKey;
	}

	public int getMoveUpKey()
	{
		return moveUpKey;
	}

	public int getSkill1Key()
	{
		return skill1Key;
	}

	public int getSkill2Key()
	{
		return skill2Key;
	}

	public int getSkill3Key()
	{
		return skill3Key;
	}

	public int getSkill4Key()
	{
		return skill4Key;
	}

	public void setAttackPotionKey(final int attackPotionKey)
	{
		this.attackPotionKey = attackPotionKey;
	}

	public void setBaseAttackKey(final int baseAttackKey)
	{
		this.baseAttackKey = baseAttackKey;
	}

	public void setHealthPotionKey(final int healthPotionKey)
	{
		this.healthPotionKey = healthPotionKey;
	}

	public void setMoveDownKey(final int moveDownKey)
	{
		this.moveDownKey = moveDownKey;
	}

	public void setMoveLeftKey(final int moveLeftKey)
	{
		this.moveLeftKey = moveLeftKey;
	}

	public void setMoveRightKey(final int moveRightKey)
	{
		this.moveRightKey = moveRightKey;
	}

	public void setMoveUpKey(final int moveUpKey)
	{
		this.moveUpKey = moveUpKey;
	}

	public void setSkill1Key(final int skill1Key)
	{
		this.skill1Key = skill1Key;
	}

	public void setSkill2Key(final int skill2Key)
	{
		this.skill2Key = skill2Key;
	}

	public void setSkill3Key(final int skill3Key)
	{
		this.skill3Key = skill3Key;
	}

	public void setSkill4Key(final int skill4Key)
	{
		this.skill4Key = skill4Key;
	}

	protected void save(Preferences preferences)
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
}
