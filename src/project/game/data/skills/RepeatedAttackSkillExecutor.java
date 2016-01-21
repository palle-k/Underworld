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
import project.game.controllers.SkillVisualizationController;
import project.game.data.GameActor;

import java.util.Properties;

public class RepeatedAttackSkillExecutor extends SkillExecutor
{
	private double repeatDelay;

	private int repetitions;

	@Override
	public void executeSkill(final GameActor attackingActor, final GameActor attackTarget)
	{
		if (!attackTarget.isAlive())
			return;
		String      soundSource  = getConfiguration().getSoundSource();
		AudioPlayer attackPlayer = null;
		if (soundSource != null)
			attackPlayer = new AudioPlayer(AudioPlayer.class.getResource(soundSource));
		SkillVisualizationController coordinator = new SkillVisualizationController(
				getTarget(),
				getConfiguration().getOverlays(),
				getConfiguration().getAttackProjectilesForDirection(
						attackingActor.getCenter(),
						attackTarget.getCenter()),
				getConfiguration().getTargetOverlays(),
				getConfiguration().getOverlayColor(),
				getConfiguration().getAttackProjectileColor(),
				getConfiguration().getTargetOverlayColor(),
				getConfiguration().getOverlayAnimationTime(),
				getConfiguration().getAttackProjectileAnimationTime(
						attackingActor.getCenter(),
						attackTarget.getCenter()),
				getConfiguration().getProjectileDissolveDelay(),
				getConfiguration().getTargetOverlayAnimationTime(),
				attackPlayer,
				null
		);
		coordinator.setOnHitAction(() -> {
			if (!attackTarget.isAlive())
				return;
			attackTarget.decreaseHealth(getConfiguration().getRandomizedDamage());
			if (!attackTarget.isAlive())
				runKillAction(attackTarget);
		});
		for (int i = 0; i < repetitions; i++)
			coordinator.visualizeSkill(attackingActor, attackTarget, i * repeatDelay);
	}

	@Override
	public void loadAdditionalProperties(final Properties properties, final String prefix)
	{
		super.loadAdditionalProperties(properties, prefix);
		repetitions = Integer.parseInt(properties.getProperty(prefix + "repetitions", "1"));
		repeatDelay = Double.parseDouble(properties.getProperty(prefix + "repeat_delay", "0"));
	}
}
