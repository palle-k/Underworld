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
import project.game.data.Map;

import java.awt.Point;
import java.util.Arrays;
import java.util.Properties;

/**
 * SkillExecutor zum Ausfuehren einer Attacke um ein Ziel herum.
 * Saemtliche Gegner im Angriffsradius um das Ziel herum erleiden Schaden
 */
public class AreaAttackSkillExecutor extends SkillExecutor
{
	/**
	 * Radius, in welchem der Schaden erfolgt
	 */
	private double damageRange;

	/**
	 * Gibt an, ob der Angriff Waende zerstoert
	 */
	private boolean destroysWalls;

	@Override
	public void executeSkill(final GameActor attackingActor, final GameActor attackTarget)
	{
		if (!attackTarget.isAlive())
			return;
		String      soundSource  = getConfiguration().getSoundSource();
		AudioPlayer attackPlayer = null;
		if (soundSource != null)
			attackPlayer = new AudioPlayer(AudioPlayer.class.getResource(soundSource));
		SkillVisualizationController skillVisualizationController = new SkillVisualizationController(
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
				null);
		skillVisualizationController.setOnHitAction(() -> {
			Arrays
					.stream(getPossibleTargets())
				.filter(GameActor::isAlive)
				.filter(actor -> actor.getCenter().distance(attackTarget.getCenter()) <= damageRange)
				.forEach(actor -> {
					if (!actor.isAlive())
						return;
					actor.decreaseHealth(getConfiguration().getRandomizedDamage());
					if (!actor.isAlive())
						runKillAction(actor);
				});
			if (destroysWalls)
			{
				Point damageCenter = attackTarget.getCenter();
				for (int x = damageCenter.x - (int) damageRange; x < damageCenter.x + (int) damageRange; x++)
					for (int y = damageCenter.y - (int) damageRange / 2;
					     y < damageCenter.y + (int) damageRange / 2; y++)
					{
						int dx = x - damageCenter.x;
						int dy = (y - damageCenter.y) * 2;
						if (Math.sqrt(dx * dx + dy * dy) <= damageRange)
							if (getLevel().getMap().getPoint(x, y) == Map.WALL)
								getLevel().getMap().setPoint(x, y, Map.WATER);
					}
				getTarget().invalidateVisiblity();
				getTarget().setNeedsDisplay();
			}
		});
		skillVisualizationController.visualizeSkill(attackingActor, attackTarget);
	}

	@Override
	public void loadAdditionalProperties(final Properties properties, final String prefix)
	{
		super.loadAdditionalProperties(properties, prefix);
		damageRange = Double.parseDouble(properties.getProperty(prefix + "attack_damage_range", "0"));
		destroysWalls = Boolean.parseBoolean(properties.getProperty(prefix + "attack_destroys_walls", "false"));
	}
}
