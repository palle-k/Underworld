/******************************************************************************
 * Copyright (c) 2015 Palle Klewitz.                                          *
 * *
 * Permission is hereby granted, free of charge, to any person obtaining      *
 * a copy of this software and associated documentation files                 *
 * (the "Software"), to deal in the Software without restriction,             *
 * including without limitation the rights to use, copy, modify,             *
 * merge, publish, distribute, sublicense, and/or sell copies of             *
 * the Software, and to permit persons to whom the Software                  *
 * is furnished to do so, subject to the following conditions:               *
 * *
 * The above copyright notice and this permission notice shall                *
 * be included in all copies or substantial portions of the Software.         *
 * *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY                         *
 * OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT                        *
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS                     *
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.                             *
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS                        *
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,                      *
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,                      *
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE                            *
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.                    *
 ******************************************************************************/

package project.game.data.state;

import java.util.prefs.Preferences;

public class PlayerState
{
	private enum PlayerClass
	{
		KNIGHT,
		HUNTER,
		WIZARD
	}

	public static final PlayerClass HUNTER = PlayerClass.HUNTER;
	public static final PlayerClass KNIGHT = PlayerClass.KNIGHT;
	public static final PlayerClass WIZARD = PlayerClass.WIZARD;

	private PlayerClass playerClass;

	protected PlayerState(Preferences preferences)
	{
		int playerClassOrdinal = preferences.getInt("player_class_ordinal", -1);
		if (playerClassOrdinal != -1)
			playerClass = PlayerClass.values()[playerClassOrdinal];
	}

	public PlayerClass getPlayerClass()
	{
		return playerClass;
	}

	public boolean playerClassChosen()
	{
		return playerClass != null;
	}

	public void setPlayerClass(final PlayerClass playerClass)
	{
		this.playerClass = playerClass;
	}

	protected void save(Preferences preferences)
	{
		if (playerClass != null)
			preferences.putInt("player_class_ordinal", playerClass.ordinal());
	}
}
