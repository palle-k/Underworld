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
import project.gui.components.TLabel;
import project.gui.dynamics.animation.Animation;
import project.gui.dynamics.animation.AnimationHandler;
import project.util.StringUtils;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * SkillExecutor fuer die Ausfuehrung einer gerichteten Attacke
 * Saemtliche Gegner auf dem Weg des Angriffs erleiden Schaden.
 */
public class DirectedAttackSkillExecutor extends SkillExecutor
{
	/**
	 * Radius, in welchem Gegner Schaden erleiden.
	 * Dieser wird von den dargestellten Projektilen aus gemessen
	 */
	private double projectileDamageRange;

	/**
	 * Distanz, ueber welche der Angriff ueber das Ziel hinausgeht.
	 * 1: Der Angriff erreicht das Ziel
	 * < 1: Der Angriff endet vor dem Ziel
	 * > 1: Gegner hinter dem Ziel erleiden Schaden
	 */
	private double rangeExtension;

	@Override
	public void executeSkill(final GameActor attackingActor, final GameActor attackTarget)
	{
		Point startPoint = attackingActor.getCenter();
		Point endPoint   = attackTarget.getCenter();

		double dx = endPoint.x - startPoint.x;
		double dy = endPoint.y - startPoint.y;

		List<GameActor> targets = Arrays.stream(getPossibleTargets())
				.filter(GameActor::isAlive)
				.filter(actor -> {
					Point center = actor.getCenter();
					return Math.abs(center.x - startPoint.x) + Math.abs(center.y - startPoint.y)
					       <= getConfiguration().getAttackRange() * rangeExtension;
				}).collect(Collectors.toList());

		List<TLabel> projectileLabels   = new ArrayList<>();
		double       projectileDuration = getConfiguration().getAttackProjectileAnimationTime(startPoint, endPoint);

		String[] projectiles = getConfiguration().getAttackProjectilesForDirection(startPoint, endPoint);

		Animation projectileAnimation = new Animation((AnimationHandler) value -> {
			TLabel    projectile       = new TLabel();
			String    attackProjectile = projectiles[(int) (value * (projectiles.length - 1) / rangeExtension)];
			Dimension projectileSize   = StringUtils.getStringDimensions(attackProjectile);
			projectile.setText(attackProjectile);
			projectileLabels.add(projectile);

			Point location = new Point(startPoint.x + (int) (dx * value), startPoint.y + (int) (dy * value));

			for (int i = 0; i < targets.size(); i++)
			{
				GameActor target = targets.get(i);
				if (!target.isAlive() || target.getCenter().distance(location) > projectileDamageRange)
					continue;
				int damage = getConfiguration().getRandomizedDamage();
				target.decreaseHealth(damage);
				if (!target.isAlive())
					runKillAction(target);
				targets.remove(target);
				i--;
			}

			location.translate(-projectileSize.width / 2, -projectileSize.height / 2);
			projectile.setColor(getConfiguration().getAttackProjectileColor());
			projectile.setSize(projectileSize);
			projectile.setLocation(location);

			getTarget().add(projectile);
		});
		projectileAnimation.setFromValue(0);
		projectileAnimation.setToValue(rangeExtension);
		projectileAnimation.setDuration(projectileDuration);
		projectileAnimation.setInterpolationMode(Animation.ANIMATION_CURVE_LINEAR);
		getTarget().addAnimation(projectileAnimation);

		Animation projectileDissolveAnimation = new Animation((AnimationHandler) value -> {
			if (!projectileLabels.isEmpty())
				projectileLabels.remove(0).removeFromSuperview();
		});
		projectileDissolveAnimation.setFromValue(0);
		projectileDissolveAnimation.setToValue(1);
		projectileDissolveAnimation.setDuration(projectileDuration);
		projectileDissolveAnimation.setInterpolationMode(Animation.ANIMATION_CURVE_LINEAR);
		projectileDissolveAnimation.setDelay(getConfiguration().getProjectileDissolveDelay());
		projectileDissolveAnimation.setCompletionHandler(animation -> projectileLabels.forEach(TLabel::removeFromSuperview));
		getTarget().addAnimation(projectileDissolveAnimation);

		String      soundSource  = getConfiguration().getSoundSource();
		AudioPlayer attackPlayer = null;
		if (soundSource != null)
		{
			attackPlayer = new AudioPlayer(AudioPlayer.class.getResource(soundSource));
			attackPlayer.play();
		}
	}

	@Override
	public void loadAdditionalProperties(final Properties properties, final String prefix)
	{
		super.loadAdditionalProperties(properties, prefix);
		rangeExtension = Double.parseDouble(properties.getProperty(prefix + "attack_range_extension", "0"));
		projectileDamageRange = Double.parseDouble(properties.getProperty(prefix + "attack_damage_range", "0"));
	}
}
