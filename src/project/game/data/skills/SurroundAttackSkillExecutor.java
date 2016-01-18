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
import project.game.data.Enemy;
import project.game.data.GameActor;
import project.game.data.Player;

import java.util.Properties;

public class SurroundAttackSkillExecutor extends SkillExecutor
{
	double damageRange;

	@Override
	public void executeSkill(final GameActor attackingActor, final GameActor attackTarget)
	{
		String      soundSource  = getConfiguration().getSoundSource();
		AudioPlayer attackPlayer = null;
		if (soundSource != null)
			attackPlayer = new AudioPlayer(AudioPlayer.class.getResource(soundSource));
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
				attackPlayer,
				null);
		skillCoordinator.visualizeSkill(attackingActor, attackTarget);

//		String[] overlays = getConfiguration().getOverlays();
//
//		TLabel overlayLabel = new TLabel();
//		overlayLabel.setColor(getConfiguration().getOverlayColor());
//		overlayLabel.setText("");
//		overlayLabel.setLocation(attackingActor.getCenter());
//		getTarget().add(overlayLabel);
//
//		Point location = attackingActor.getCenter();
//
//		Animation overlayAnimation = new Animation((AnimationHandler) value -> {
//			String newValue = overlays[(int) value];
//			if (newValue == null)
//				return;
//			overlayLabel.setText(newValue);
//			Dimension newSize = StringUtils.getStringDimensions(newValue);
//			overlayLabel.setLocation(
//					location.x - newSize.width / 2,
//					location.y - newSize.height / 2);
//			overlayLabel.setSize(newSize);
//		});
//		overlayAnimation.setCompletionHandler(animation -> overlayLabel.removeFromSuperview());
//		overlayAnimation.setFromValue(0);
//		overlayAnimation.setToValue(overlays.length - 1);
//		overlayAnimation.setInterpolationMode(Animation.ANIMATION_CURVE_LINEAR);
//		overlayAnimation.setDuration(getConfiguration().getOverlayAnimationTime());
//		overlayLabel.addAnimation(overlayAnimation);

//		Arrays.stream(getPossibleTargets())
//				.filter(actor -> actor.getCenter().distance(attackingActor.getCenter()) <= damageRange)
//				.peek(actor -> actor.decreaseHealth(getConfiguration().getRandomizedDamage()))
//				.filter(actor -> attackingActor instanceof Player)
//				.forEach();

		for (GameActor actor : getPossibleTargets())
		{
			if (actor.isAlive() && actor.getCenter().distance(attackingActor.getCenter()) <= damageRange)
			{
				actor.decreaseHealth(getConfiguration().getRandomizedDamage());
				//TODO implement this in an OnKill-Interface
				if (!actor.isAlive() && actor instanceof Enemy && attackingActor instanceof Player)
					((Player) attackingActor).earnExperience(((Enemy) actor).getEarnedExperience());
			}
		}
	}

	@Override
	public void loadAdditionalProperties(final Properties properties, final String prefix)
	{
		super.loadAdditionalProperties(properties, prefix);
		damageRange = Double.parseDouble(properties.getProperty(prefix + "attack_damage_range", "0"));
	}
}
