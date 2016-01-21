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
import project.game.data.GameActor;
import project.gui.dynamics.animation.Animation;
import project.gui.dynamics.animation.AnimationHandler;

import java.awt.Point;
import java.util.Properties;

/**
 * SkillExecutor, welcher den Angreifer zum Ziel hinspringen laesst
 */
public class JumpToTargetSkillExecutor extends SkillExecutor
{
	/**
	 * Dauer des Sprungs
	 */
	private double jumpDuration;

	/**
	 * Distanz, auf welche sich der Angreifer dem Ziel naehert
	 */
	private int targetDistance;

	@Override
	public void executeSkill(final GameActor attackingActor, final GameActor attackTarget)
	{
		Point[] pathToTarget = getLevel()
				.getMap()
				.findPath(
						attackingActor.getCenter(),
						attackTarget.getCenter(),
						attackingActor.getBounds().width,
						attackingActor.getBounds().height);
		if (pathToTarget != null)
		{
			//attackingActor.setCenter(pathToTarget[pathToTarget.length - 1 - targetDistance]);
			Animation pathAnimation = new Animation((AnimationHandler) value ->
					attackingActor.setCenter(pathToTarget[(int) value]));
			pathAnimation.setFromValue(0);
			pathAnimation.setToValue(Math.max(pathToTarget.length - 1 - targetDistance, 0));
			pathAnimation.setDuration(jumpDuration);
			pathAnimation.setInterpolationMode(Animation.ANIMATION_CURVE_EASE);
			getTarget().addAnimation(pathAnimation);
			String soundSource = getConfiguration().getSoundSource();
			if (soundSource != null)
				new AudioPlayer(AudioPlayer.class.getResource(soundSource)).play();
		}
	}

	@Override
	public void loadAdditionalProperties(final Properties properties, final String prefix)
	{
		super.loadAdditionalProperties(properties, prefix);
		targetDistance = Integer.parseInt(properties.getProperty(prefix + "target_distance", "0"));
		jumpDuration = Double.parseDouble(properties.getProperty(prefix + "jump_duration", "0"));
	}
}
