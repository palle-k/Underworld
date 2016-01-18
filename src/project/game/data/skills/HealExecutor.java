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

import project.audio.AudioPlayer;
import project.game.controllers.SkillCoordinator;
import project.game.data.GameActor;

import java.util.Properties;

/**
 * Implementierung des SkillExecutors fuer einen Heilungsskill
 */
public class HealExecutor extends SkillExecutor
{
	/**
	 * Anzahl wiederhergestellter Lebenspunkte
	 */
	private int restoredHealth;

	/**
	 * Fuehrt eine Heilung des Spielers durch
	 * @param attackingActor Angreifer
	 * @param attackTarget Ziel
	 */
	@Override
	public void executeSkill(final GameActor attackingActor, final GameActor attackTarget)
	{
		String      soundSource  = getConfiguration().getSoundSource();
		AudioPlayer soundPlayer = null;
		if (soundSource != null)
			soundPlayer = new AudioPlayer(AudioPlayer.class.getResource(soundSource));
		SkillCoordinator skillCoordinator = new SkillCoordinator(
				getTarget(),
				getConfiguration().getOverlays(),
				null,
				null,
				getConfiguration().getOverlayColor(),
				null,
				null,
				getConfiguration().getOverlayAnimationTime(),
				0,
				0,
				0,
				soundPlayer,
				null);
		skillCoordinator.visualizeSkill(attackingActor, attackTarget);
		attackingActor.regenerateHealth(restoredHealth);
	}

	@Override
	public void loadAdditionalProperties(final Properties properties, final String prefix)
	{
		restoredHealth = Integer.parseInt(properties.getProperty(prefix + "restored_health", "0"));
	}
}
