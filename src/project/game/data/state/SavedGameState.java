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

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class SavedGameState
{
	private static SavedGameState state;

	public static SavedGameState getSavedGameState()
	{
		if (state == null)
			loadState();
		return state;
	}

	public static void loadState()
	{
		Preferences preferences = Preferences.userRoot().node("com.palleklewitz.Underworld");
		state = new SavedGameState(preferences);
	}

	private LevelState    levelState;
	private PlayerState   playerState;
	private SettingsState settingsState;

	public SavedGameState(Preferences preferences)
	{
		levelState = new LevelState(preferences);
		playerState = new PlayerState(preferences);
		settingsState = new SettingsState(preferences);
	}

	public LevelState getLevelState()
	{
		return levelState;
	}

	public PlayerState getPlayerState()
	{
		return playerState;
	}

	public SettingsState getSettingsState()
	{
		return settingsState;
	}

	public void reset()
	{
		try
		{
			Preferences.userRoot().node("com.palleklewitz.Underworld").removeNode();
		}
		catch (BackingStoreException e)
		{
			e.printStackTrace();
		}
		Preferences preferences = Preferences.userRoot().node("com.palleklewitz.Underworld");
		levelState = new LevelState(preferences);
		playerState = new PlayerState(preferences);
		settingsState = new SettingsState(preferences);
	}

	public void save()
	{
		Preferences preferences = Preferences.userRoot().node("com.palleklewitz.Underworld");
		levelState.save(preferences);
		playerState.save(preferences);
		settingsState.save(preferences);
	}
}
