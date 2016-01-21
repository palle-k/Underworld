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

import project.game.data.GameActor;
import project.gui.dynamics.animation.Animation;
import project.gui.dynamics.animation.AnimationHandler;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class JumpBetweenTargetsAttackExecutor extends SkillExecutor
{
	private int    jumpCount;

	private double jumpDuration;

	@Override
	public void executeSkill(final GameActor attackingActor, final GameActor attackTarget)
	{
		List<GameActor> targets = Arrays.stream(getPossibleTargets())
				.filter(GameActor::isAlive)
				.filter(target -> attackingActor.getCenter().distance(target.getCenter()) <=
				                  getConfiguration().getAttackRange())
				.filter(target -> getLevel().getMap().canSee(attackingActor.getCenter(), target.getCenter()))
				.limit(jumpCount)
				.collect(Collectors.toList());

		if (targets.isEmpty())
			return;

		for (int i = 0; i < jumpCount; i++)
		{
			final Point[] path;

			if (i == 0)
				path = getLevel()
						.getMap()
						.findPath(
								attackingActor.getCenter(),
								targets.get(i % targets.size()).getCenter());
			else
				path = getLevel()
						.getMap()
						.findPath(
								targets.get((i - 1) % targets.size()).getCenter(),
								targets.get(i % targets.size()).getCenter());

			if (path == null)
				continue;

			Animation jumpAnimation = new Animation((AnimationHandler) value -> {
				attackingActor.setCenter(path[(int) value]);
			});
			jumpAnimation.setDuration(jumpDuration);
			jumpAnimation.setDelay(jumpDuration * i);
			jumpAnimation.setFromValue(0);
			jumpAnimation.setToValue(path.length - 1);
			final int finalI = i;
			jumpAnimation.setCompletionHandler(animation -> {
				GameActor target = targets.get(finalI % targets.size());
				if (!target.isAlive())
					return;
				target.decreaseHealth(getConfiguration().getRandomizedDamage());
				if (!target.isAlive())
					runKillAction(target);
			});
			getTarget().addAnimation(jumpAnimation);
		}
	}

	@Override
	public void loadAdditionalProperties(final Properties properties, final String prefix)
	{
		super.loadAdditionalProperties(properties, prefix);
		jumpCount = Integer.parseInt(properties.getProperty(prefix + "attack_jump_count", "0"));
		jumpDuration = Double.parseDouble(properties.getProperty("jump_duration", "0"));
	}
}
