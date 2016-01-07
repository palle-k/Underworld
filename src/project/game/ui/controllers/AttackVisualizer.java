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

package project.game.ui.controllers;

import project.gui.components.TComponent;
import project.gui.components.TLabel;
import project.gui.dynamics.animation.Animation;
import project.gui.dynamics.animation.AnimationHandler;
import project.util.StringUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

public class AttackVisualizer
{
	public static AttackVisualizer AttackHitVisualizer(final TComponent visualizationTarget, final String[] attackHitOverlays, final Color attackHitColor, final double hitAnimationDuration)
	{
		return new AttackVisualizer(
				visualizationTarget,
				null,
				null,
				attackHitOverlays,
				null,
				null,
				attackHitColor,
				0,
				0,
				hitAnimationDuration);
	}

	public static AttackVisualizer AttackOverlayProjectileVisualizer(final TComponent visualizationTarget, final String[] attackOverlays, final Color attackOverlayColor, final double overlayAnimationDuration, final String[] attackProjectiles, final Color attackProjectileColor, final double projectileAnimationDuration)
	{
		return new AttackVisualizer(
				visualizationTarget,
				attackOverlays,
				attackProjectiles,
				null,
				attackOverlayColor,
				attackProjectileColor,
				null,
				overlayAnimationDuration,
				projectileAnimationDuration,
				0);
	}

	public static AttackVisualizer AttackOverlayVisualizer(final TComponent visualizationTarget, final String[] attackOverlays, final Color attackOverlayColor, final double overlayAnimationDuration)
	{
		return new AttackVisualizer(
				visualizationTarget,
				attackOverlays,
				null,
				null,
				attackOverlayColor,
				null,
				null,
				overlayAnimationDuration,
				0,
				0);
	}

	public static AttackVisualizer AttackProjectileHitVisualizer(final TComponent visualizationTarget, final String[] attackProjectiles, final Color attackProjectileColor, final double projectileAnimationDuration, final String[] attackHitOverlays, final Color attackHitColor, final double hitAnimationDuration)
	{
		return new AttackVisualizer(
				visualizationTarget,
				null,
				attackProjectiles,
				attackHitOverlays,
				null,
				attackProjectileColor,
				attackHitColor,
				0,
				projectileAnimationDuration,
				hitAnimationDuration);
	}

	public static AttackVisualizer AttackProjectileVisualizer(final TComponent visualizationTarget, final String[] attackProjectiles, final Color attackProjectileColor, final double projectileAnimationDuration)
	{
		return new AttackVisualizer(
				visualizationTarget,
				null,
				attackProjectiles,
				null,
				null,
				attackProjectileColor,
				null,
				0,
				projectileAnimationDuration,
				0);
	}

	public static AttackVisualizer AttackVisualizer(final TComponent visualizationTarget, final String[] attackOverlays, final String[] attackProjectiles, final String[] attackHitOverlays, final Color attackOverlayColor, final Color attackProjectileColor, final Color attackHitColor, final double overlayAnimationDuration, final double projectileAnimationDuration, final double hitAnimationDuration)
	{
		return new AttackVisualizer(
				visualizationTarget,
				attackOverlays,
				attackProjectiles,
				attackHitOverlays,
				attackOverlayColor,
				attackProjectileColor,
				attackHitColor,
				overlayAnimationDuration,
				projectileAnimationDuration,
				hitAnimationDuration);
	}

	private Color      attackHitColor;
	private String[]   attackHitOverlays;
	private Color      attackOverlayColor;
	private String[]   attackOverlays;
	private Color      attackProjectileColor;
	private String[]   attackProjectiles;
	private double     hitAnimationDuration;
	private double     overlayAnimationDuration;
	private double     projectileAnimationDuration;
	private TComponent visualizationTarget;

	private AttackVisualizer(final TComponent visualizationTarget, final String[] attackOverlays, final String[] attackProjectiles, final String[] attackHitOverlays, final Color attackOverlayColor, final Color attackProjectileColor, final Color attackHitColor, final double overlayAnimationDuration, final double projectileAnimationDuration, final double hitAnimationDuration)
	{
		this.visualizationTarget = visualizationTarget;
		this.attackOverlays = attackOverlays;
		this.attackProjectiles = attackProjectiles;
		this.attackHitOverlays = attackHitOverlays;
		this.attackOverlayColor = attackOverlayColor;
		this.attackProjectileColor = attackProjectileColor;
		this.attackHitColor = attackHitColor;
		this.overlayAnimationDuration = overlayAnimationDuration;
		this.projectileAnimationDuration = projectileAnimationDuration;
		this.hitAnimationDuration = hitAnimationDuration;
	}

	public void visualizeAttack(Point fromPoint, Point toPoint)
	{
		if (attackOverlays != null)
		{
			TLabel overlayLabel = new TLabel();
			overlayLabel.setColor(attackOverlayColor);
			overlayLabel.setText("");
			overlayLabel.setLocation(fromPoint);
			visualizationTarget.add(overlayLabel);

			Animation overlayAnimation = new Animation((AnimationHandler) value -> {
				String newValue = attackOverlays[(int) value];
				overlayLabel.setText(newValue);
				Dimension newSize = StringUtils.getStringDimensions(newValue);
				overlayLabel.setLocation(fromPoint.x - newSize.width / 2, fromPoint.y - newSize.height / 2);
			});
			overlayAnimation.setCompletionHandler(animation -> visualizationTarget.remove(overlayLabel));
			overlayAnimation.setFromValue(0);
			overlayAnimation.setToValue(attackOverlays.length - 1);
			overlayAnimation.setInterpolationMode(Animation.ANIMATION_CURVE_LINEAR);
			overlayAnimation.setDuration(overlayAnimationDuration);
			overlayLabel.addAnimation(overlayAnimation);
		}

		if (attackProjectiles != null)
		{
			TLabel projectileLabel = new TLabel();
			projectileLabel.setText("");
			projectileLabel.setLocation(fromPoint);
			projectileLabel.setColor(attackProjectileColor);
			visualizationTarget.add(projectileLabel);

			Animation projectileAnimation = new Animation((AnimationHandler) value -> {
				String newValue = attackProjectiles[(int) (value / projectileAnimationDuration *
				                                           (attackProjectiles.length - 1))];
				projectileLabel.setText(newValue);
				Dimension newSize = StringUtils.getStringDimensions(newValue);
				Point     point   = new Point(fromPoint);
				point.x += (toPoint.x - fromPoint.x) / (value / projectileAnimationDuration);
				point.y += (toPoint.y - fromPoint.y) / (value / projectileAnimationDuration);
				point.x -= newSize.width / 2;
				point.y -= newSize.height / 2;
				projectileLabel.setLocation(point);
			});
			projectileAnimation.setFromValue(0);
			projectileAnimation.setToValue(1);
			projectileAnimation.setDuration(projectileAnimationDuration);
			projectileAnimation.setInterpolationMode(Animation.ANIMATION_CURVE_LINEAR);
			projectileLabel.addAnimation(projectileAnimation);
		}

	}
}
