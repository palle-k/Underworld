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
import java.util.Properties;

public class Player extends GameActor implements Serializable
{
	public static Player makePlayer() throws IOException
	{
		Properties  properties  = new Properties();
		PlayerState playerState = SavedGameState.getSavedGameState().getPlayerState();
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
			properties.load(Player.class.getResourceAsStream(configurationFile));

			return new Player(properties);
		}
		else
			return null;
		//throw new RuntimeException("Presenting Level before player class was chosen.");

	}

	private int  autohit_attack_range;
	private int  autohit_damage;
	private int  autohit_damage_variation;
	private long experience;
	private int  health;
	private int  speed;

	protected Player(Properties playerProperties) throws IOException
	{
		super(playerProperties);
		//TODO parse attacks etc...

	}

	public void earnExperience(int exp)
	{
		boolean levelWillChange = false;
		if ((experience + exp) >= getLevelEndExperience())
			levelWillChange = true;
		experience += exp;
		if (delegate instanceof PlayerDelegate)
		{
			if (levelWillChange)
				((PlayerDelegate) delegate).playerLevelDidChange(this);
			((PlayerDelegate) delegate).playerDidEarnExperience(this);
		}
	}

	public long getExperience()
	{
		return experience;
	}

	@Override
	public int getLevel()
	{
		return (int) Math.sqrt(experience * 0.01) + 1;
	}

	public long getLevelEndExperience()
	{
		long level       = getLevel();
		long levelSquare = level * level;
		return levelSquare * 100;
	}

	public long getLevelStartExperience()
	{
		long level       = getLevel() - 1;
		long levelSquare = level * level;
		return levelSquare * 100;
	}
}
