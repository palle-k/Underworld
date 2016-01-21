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

package project.game.controllers;

import project.audio.AudioPlayer;
import project.game.data.GameActor;
import project.gui.components.TComponent;
import project.gui.components.TLabel;
import project.gui.dynamics.animation.Animation;
import project.gui.dynamics.animation.AnimationHandler;
import project.util.StringUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasse zur Darstellung einer Faehigkeit
 * auf der Benutzeroberflaeche
 */
public class SkillVisualizationController
{
	private double      hitAnimationDuration;
	private AudioPlayer hitPlayer;

	private Runnable onHit;

	private double      overlayAnimationDuration;

	private double      projectileAnimationDuration;

	private double projectileDissolveDelay;

	private AudioPlayer shootPlayer;

	private Color       skillHitColor;

	private String[]    skillHitOverlays;

	private Color       skillOverlayColor;

	private String[]    skillOverlays;

	private Color       skillProjectileColor;

	private String[]    skillProjectiles;

	private TComponent  visualizationTarget;

	/**
	 * Erstellt eine neue Klasse zur Darstellung einer Faehigkeit mit den angegebenen Parametern
	 *
	 * @param visualizationTarget         Zielkomponente zur Darstellung
	 * @param skillOverlays               Ebenen, welche ueber dem Angreifer dargestellt werden sollen
	 * @param skillProjectiles            Ebenen, welche zwischen Angreifer und Ziel dargestellt werden sollen
	 * @param skillHitOverlays            Ebenen, welche ueber dem Ziel dargestellt werden sollen
	 * @param skillOverlayColor           Farbe der Ebenen, welche ueber dem Angreifer dargestellt werden sollen
	 * @param skillProjectileColor        Farbe der Ebenen, welche zwischen Angreifer und Ziel dargestellt werden sollen
	 * @param skillHitColor               Farbe der Ebenen, welche ueber dem Ziel dargestellt werden sollen
	 * @param overlayAnimationDuration    Animationsdauer der Ebenen ueber dem Angreifer
	 * @param projectileAnimationDuration Animationsdauer der Ebenen zwischen Angreifer und Ziel
	 * @param projectileDissolveDelay     Verzoegerung, mit welcher sich Projektile aufloesen
	 * @param hitAnimationDuration        Animationsdauer der Ebenen ueber dem Ziel
	 */
	public SkillVisualizationController(
			final TComponent visualizationTarget,
			final String[] skillOverlays,
			final String[] skillProjectiles,
			final String[] skillHitOverlays,
			final Color skillOverlayColor,
			final Color skillProjectileColor,
			final Color skillHitColor,
			final double overlayAnimationDuration,
			final double projectileAnimationDuration,
			final double projectileDissolveDelay,
			final double hitAnimationDuration)
	{
		this.visualizationTarget = visualizationTarget;
		this.skillOverlays = skillOverlays;
		this.skillProjectiles = skillProjectiles;
		this.skillHitOverlays = skillHitOverlays;
		this.skillOverlayColor = skillOverlayColor;
		this.skillProjectileColor = skillProjectileColor;
		this.skillHitColor = skillHitColor;
		this.overlayAnimationDuration = overlayAnimationDuration;
		this.projectileAnimationDuration = projectileAnimationDuration;
		this.projectileDissolveDelay = projectileDissolveDelay;
		this.hitAnimationDuration = hitAnimationDuration;
	}

	/**
	 * Erstellt eine neue Klasse zur Darstellung einer Faehigkeit mit den angegebenen Parametern
	 * @param visualizationTarget Zielkomponente zur Darstellung
	 * @param skillOverlays Ebenen, welche ueber dem Angreifer dargestellt werden sollen
	 * @param skillProjectiles Ebenen, welche zwischen Angreifer und Ziel dargestellt werden sollen
	 * @param skillHitOverlays Ebenen, welche ueber dem Ziel dargestellt werden sollen
	 * @param skillOverlayColor Farbe der Ebenen, welche ueber dem Angreifer dargestellt werden sollen
	 * @param skillProjectileColor Farbe der Ebenen, welche zwischen Angreifer und Ziel dargestellt werden sollen
	 * @param skillHitColor Farbe der Ebenen, welche ueber dem Ziel dargestellt werden sollen
	 * @param overlayAnimationDuration Animationsdauer der Ebenen ueber dem Angreifer
	 * @param projectileAnimationDuration Animationsdauer der Ebenen zwischen Angreifer und Ziel
	 * @param projectileDissolveDelay Verzoegerung, mit welcher sich Projektile aufloesen
	 * @param hitAnimationDuration Animationsdauer der Ebenen zwischen Angreifer und Ziel
	 * @param hitPlayer AudioPlayer fuer den Klang beim Treffen des Ziels
	 * @param shootPlayer AudioPlayer fuer den Klang beim Ausloesen des Skills
	 */
	public SkillVisualizationController(
			final TComponent visualizationTarget,
			final String[] skillOverlays,
			final String[] skillProjectiles,
			final String[] skillHitOverlays,
			final Color skillOverlayColor,
			final Color skillProjectileColor,
			final Color skillHitColor,
			final double overlayAnimationDuration,
			final double projectileAnimationDuration,
			final double projectileDissolveDelay,
			final double hitAnimationDuration,
			final AudioPlayer hitPlayer,
			final AudioPlayer shootPlayer)
	{
		this.skillHitColor = skillHitColor;
		this.skillHitOverlays = skillHitOverlays;
		this.skillOverlayColor = skillOverlayColor;
		this.skillOverlays = skillOverlays;
		this.skillProjectileColor = skillProjectileColor;
		this.skillProjectiles = skillProjectiles;
		this.hitAnimationDuration = hitAnimationDuration;
		this.overlayAnimationDuration = overlayAnimationDuration;
		this.projectileAnimationDuration = projectileAnimationDuration;
		this.projectileDissolveDelay = projectileDissolveDelay;
		this.visualizationTarget = visualizationTarget;
		this.hitPlayer = hitPlayer;
		this.shootPlayer = shootPlayer;
	}

	/**
	 * Gibt die Aktion zurueck, die beim Treffen des Gegners ausgefuehrt wird
	 * @return Trefferaktion
	 */
	public Runnable getOnHitAction()
	{
		return onHit;
	}

	/**
	 * Setzt die Aktion, die beim Treffen des Gegners ausgefuehrt wird
	 * @param onHit Trefferaktion
	 */
	public void setOnHitAction(final Runnable onHit)
	{
		this.onHit = onHit;
	}

	/**
	 * Stellt den Skill zwischen Angreifer und Ziel dar.
	 * @param from Angreifer
	 * @param to Ziel
	 */
	public void visualizeSkill(GameActor from, GameActor to)
	{
		visualizeSkill(from, to, 0);
	}

	/**
	 * Stellt den Skill zwischen Angreifer und Ziel dar.
	 * @param from Angreifer
	 * @param to Ziel
	 * @param delay Verzoegerung
	 */
	public void visualizeSkill(GameActor from, GameActor to, double delay)
	{
		if (skillOverlays != null && skillOverlays.length > 0)
		{
			TLabel overlayLabel = new TLabel();
			overlayLabel.setColor(skillOverlayColor);
			overlayLabel.setText("");
			overlayLabel.setLocation(from.getCenter());
			visualizationTarget.add(overlayLabel);

			Animation overlayAnimation = new Animation((AnimationHandler) value -> {
				String newValue = skillOverlays[(int) value];
				if (newValue == null)
					return;
				overlayLabel.setText(newValue);
				Dimension newSize = StringUtils.getStringDimensions(newValue);
				overlayLabel.setLocation(
						from.getCenter().x - newSize.width / 2,
						from.getCenter().y - newSize.height / 2);
				overlayLabel.setSize(newSize);
			});
			overlayAnimation.setCompletionHandler(animation -> overlayLabel.removeFromSuperview());
			overlayAnimation.setFromValue(0);
			overlayAnimation.setToValue(skillOverlays.length - 1);
			overlayAnimation.setInterpolationMode(Animation.ANIMATION_CURVE_LINEAR);
			overlayAnimation.setDuration(overlayAnimationDuration);
			overlayAnimation.setDelay(delay);
			overlayLabel.addAnimation(overlayAnimation);

			if (shootPlayer != null)
				shootPlayer.play();
		}

		if (skillProjectiles != null && skillProjectiles.length > 0)
		{
//			TLabel projectileLabel = new TLabel();
//			projectileLabel.setText("");
//			projectileLabel.setLocation(from.getCenter());
//			projectileLabel.setColor(skillProjectileColor);
//			visualizationTarget.add(projectileLabel);
			List<TLabel> projectileLabels = new ArrayList<>();
			Animation projectileAnimation = new Animation((AnimationHandler) value -> {
				TLabel projectileLabel = new TLabel();
				String newValue = skillProjectiles[(int) (value * (skillProjectiles.length - 1))];
				if (newValue == null)
					return;
				projectileLabel.setText(newValue);
				Dimension newSize = StringUtils.getStringDimensions(newValue);
				Point     point   = new Point();
				point.x = from.getCenter().x + (int) ((to.getCenter().x - from.getCenter().x) * value);
				point.y = from.getCenter().y + (int) ((to.getCenter().y - from.getCenter().y) * value);
				point.x -= newSize.width / 2;
				point.y -= newSize.height / 2;
				projectileLabel.setColor(skillProjectileColor);
				projectileLabel.setLocation(point);
				projectileLabel.setSize(newSize);
				visualizationTarget.add(projectileLabel);
				projectileLabels.add(projectileLabel);
			});
			projectileAnimation.setFromValue(0);
			projectileAnimation.setToValue(1);
			projectileAnimation.setDuration(projectileAnimationDuration);
			projectileAnimation.setInterpolationMode(Animation.ANIMATION_CURVE_LINEAR);
			projectileAnimation.setDelay(delay);
			projectileAnimation.setCompletionHandler(animation -> {
				//projectileLabel.removeFromSuperview();
				if (hitPlayer != null)
					hitPlayer.play();
				if (onHit != null)
					onHit.run();
			});
			//projectileLabel.addAnimation(projectileAnimation);

			Animation dissolveAnimation = new Animation((AnimationHandler) value -> {
				if (!projectileLabels.isEmpty())
					projectileLabels.remove(0).removeFromSuperview();
			});
			dissolveAnimation.setFromValue(0);
			dissolveAnimation.setToValue(1);
			dissolveAnimation.setDuration(projectileAnimationDuration);
			dissolveAnimation.setInterpolationMode(Animation.ANIMATION_CURVE_LINEAR);
			dissolveAnimation.setDelay(delay + projectileDissolveDelay);
			dissolveAnimation.setCompletionHandler(animation -> projectileLabels.forEach(TComponent::removeFromSuperview));

			visualizationTarget.addAnimation(projectileAnimation);
			visualizationTarget.addAnimation(dissolveAnimation);
		}
		else
		{
			if (hitPlayer != null)
				hitPlayer.play();
			if (onHit != null)
				onHit.run();
		}

		if (skillHitOverlays != null && skillHitOverlays.length > 0)
		{
			TLabel attackHitLabel = new TLabel();
			attackHitLabel.setText("");
			attackHitLabel.setLocation(to.getCenter());
			attackHitLabel.setColor(skillHitColor);
			visualizationTarget.add(attackHitLabel);

			Animation attackHitAnimation = new Animation((AnimationHandler) value -> {
				String newValue = skillHitOverlays[(int) value];
				if (newValue == null)
					return;
				attackHitLabel.setText(newValue);
				Dimension newSize = StringUtils.getStringDimensions(newValue);
				Point     point   = new Point(to.getCenter());
				point.x -= newSize.width / 2;
				point.y -= newSize.height / 2;
				attackHitLabel.setLocation(point);
				attackHitLabel.setSize(newSize);
			});
			attackHitAnimation.setFromValue(0);
			attackHitAnimation.setToValue(skillHitOverlays.length - 1);
			attackHitAnimation.setDuration(hitAnimationDuration);
			attackHitAnimation.setInterpolationMode(Animation.ANIMATION_CURVE_LINEAR);
			attackHitAnimation.setCompletionHandler(animation -> attackHitLabel.removeFromSuperview());
			if (skillProjectiles != null)
				attackHitAnimation.setDelay(projectileAnimationDuration + delay);
			else
				attackHitAnimation.setDelay(delay);
			attackHitLabel.addAnimation(attackHitAnimation);
		}
	}
}
