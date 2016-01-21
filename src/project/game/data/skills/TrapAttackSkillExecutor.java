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
import project.game.data.Map;
import project.gui.components.TLabel;
import project.gui.dynamics.GameloopAction;
import project.gui.dynamics.animation.Animation;
import project.gui.dynamics.animation.AnimationHandler;
import project.util.StringUtils;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Arrays;
import java.util.Properties;

public class TrapAttackSkillExecutor extends SkillExecutor
{
	/**
	 * Radius, in welchem der Schaden erfolgt
	 */
	double damageRange;

	/**
	 * Radius, in welchem die Falle ausloest
	 */
	double triggerRange;

	/**
	 * Gibt an, ob der Angriff Waende zerstoert
	 */
	private boolean destroysWalls;

	@Override
	public void executeSkill(final GameActor attackingActor, final GameActor attackTarget)
	{
		TLabel trapLabel = new TLabel();
		trapLabel.setColor(getConfiguration().getOverlayColor());
		trapLabel.setText(getConfiguration().getOverlays()[0]);
		Dimension textSize = StringUtils.getStringDimensions(getConfiguration().getOverlays()[0]);
		trapLabel.setSize(textSize);

		Point trapLocation = attackingActor.getCenter();

		trapLabel.setLocation(
				trapLocation.x - textSize.width / 2,
				trapLocation.y - textSize.height / 2);
		getTarget().add(trapLabel, 1);


		GameloopAction updateAction = new GameloopAction()
		{
			@Override
			public void update(final double time)
			{
				boolean trigger = false;
				for (GameActor gameActor : TrapAttackSkillExecutor.this.getPossibleTargets())
					if (gameActor.isAlive() && gameActor.getCenter().distance(trapLocation) <= triggerRange)
					{
						trigger = true;
						break;
					}
				if (trigger)
				{
					TLabel attackHitLabel = new TLabel();
					attackHitLabel.setText("");
					attackHitLabel.setLocation(trapLocation);
					attackHitLabel.setColor(TrapAttackSkillExecutor.this.getConfiguration().getTargetOverlayColor());
					getTarget().add(attackHitLabel);

					Animation attackHitAnimation = new Animation((AnimationHandler) value -> {
						String newValue = TrapAttackSkillExecutor.this.getConfiguration()
								.getTargetOverlays()[(int) value];
						if (newValue == null)
							return;
						attackHitLabel.setText(newValue);
						Dimension newSize = StringUtils.getStringDimensions(newValue);
						Point     point   = new Point(trapLocation);
						point.x -= newSize.width / 2;
						point.y -= newSize.height / 2;
						attackHitLabel.setLocation(point);
						attackHitLabel.setSize(newSize);
					});
					attackHitAnimation.setFromValue(0);
					attackHitAnimation.setToValue(
							TrapAttackSkillExecutor.this.getConfiguration().getTargetOverlays().length - 1);
					attackHitAnimation.setDuration(TrapAttackSkillExecutor.this.getConfiguration()
							                               .getTargetOverlayAnimationTime());
					attackHitAnimation.setInterpolationMode(Animation.ANIMATION_CURVE_LINEAR);
					attackHitAnimation.setCompletionHandler(animation -> attackHitLabel.removeFromSuperview());
					attackHitLabel.addAnimation(attackHitAnimation);
					trapLabel.removeFromSuperview();
					getTarget().removeOnAnimationUpdate(this);

					if (destroysWalls)
					{
						for (int x = trapLocation.x - (int) damageRange; x < trapLocation.x + (int) damageRange; x++)
							for (int y = trapLocation.y - (int) damageRange / 2;
							     y < trapLocation.y + (int) damageRange / 2; y++)
							{
								int dx = x - trapLocation.x;
								int dy = (y - trapLocation.y) * 2;
								if (Math.sqrt(dx * dx + dy * dy) <= damageRange)
									if (getLevel().getMap().getPoint(x, y) == Map.WALL)
										getLevel().getMap().setPoint(x, y, Map.WATER);
							}
						getTarget().invalidateVisiblity();
						getTarget().setNeedsDisplay();
					}

					Arrays.stream(getPossibleTargets())
							.filter(GameActor::isAlive)
							.filter(target -> target.getCenter().distance(trapLocation) <= damageRange)
							.forEach(target -> {
								target.decreaseHealth(getConfiguration().getRandomizedDamage());
//								if (!target.isAlive() && target instanceof Enemy && attackingActor instanceof Player)
//									((Player) attackingActor).earnExperience(((Enemy) target).getEarnedExperience());
								if (!target.isAlive())
									runKillAction(target);
							});
				}
			}
		};
		getTarget().addOnAnimationUpdate(updateAction);
	}

	@Override
	public void loadAdditionalProperties(final Properties properties, final String prefix)
	{
		super.loadAdditionalProperties(properties, prefix);
		damageRange = Double.parseDouble(properties.getProperty(prefix + "attack_damage_range", "0"));
		triggerRange = Double.parseDouble(properties.getProperty(prefix + "attack_trigger_range", "0"));
		destroysWalls = Boolean.parseBoolean(properties.getProperty(prefix + "attack_destroys_walls", "false"));
	}
}
