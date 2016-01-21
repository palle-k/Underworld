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

package project.game.data;


import project.game.data.skills.SkillExecutor;
import project.util.Direction;

import java.awt.Color;
import java.awt.Point;
import java.util.Properties;

/**
 * Konfiguration von Skills
 * Speicherung von Parametern des Skills,
 * einem Executor zum Ausfuehren des Skills
 * und den Ebenen zur Darstellung des Skills
 */
public class SkillConfiguration
{
	/**
	 * Schadensvariation bei einer Basisattacke.
	 * Der tatsaechliche Schaden liegt zwischen targetDamage - attackDamageVariation / 2
	 * und targetDamage + attackDamageVariation / 2
	 */
	protected int attackDamageVariation;

	/**
	 * Farbe der Angriffsprojektile.
	 */
	protected Color attackProjectileColor;

	/**
	 * Maximalradius, den die Basisattacke ueberbruecken kann.
	 */
	protected int attackRange;

	/**
	 * Boolean, welcher angibt, ob die verwendeten Projektile der
	 * Basisattacke abhaengig von der Bewegungsrichtung sind.
	 */
	protected boolean directionDependentProjectiles;

	/**
	 * Icon fuer den Skill
	 */
	protected String icon;

	/**
	 * Animationszeit der Overlays eines Angriffs ueber dem Angreifer
	 */
	protected double overlayAnimationTime;

	/**
	 * Farbe des Overlays eines Angriffs.
	 * Dieses Overlay wird ueber dem Angreifer angezeigt.
	 */
	protected Color overlayColor;

	/**
	 * Ebenen, die ueber dem Aktor bei der Ausfuehrung eines Angriffs angezeigt werden sollen
	 */
	protected String[] overlays;

	/**
	 * Animationszeit von Projektilen (Geschwindigkeit zum Gegner)
	 * Wird mit Distanz multipliziert.
	 */
	protected double projectileAnimationTime;

	/**
	 * Verzoegerung, mit der Projektile aufgeloest werden
	 */
	protected double projectileDissolveDelay;

	/**
	 * Ebenen, welche Projektile darstellen, welche beim Angriff vom angreifenden Aktor
	 * auf den angegriffenen Aktor geschossen werden.
	 * Diese koennen Richtungsabhaengig sein.
	 */
	protected String[] projectiles;

	/**
	 * Projektilebenen pro Richtung
	 */
	protected int projectilesPerDirection;

	/**
	 * Angriffsrate (Wiederholrate fuer Basisangriffe)
	 */
	protected double rate;

	/**
	 * Gibt das Level an, welches mindestens zur ausfuehrung des Skills erforderlich ist.
	 */
	protected int requiredLevel;

	/**
	 * Gibt an, ob ein Gegner bekannt sein muss, damit dieser Skill ausgefuehrt werden kann.
	 */
	protected boolean requiresFocus;

	/**
	 * Klasse, welche den SkillExecutor ausfuehrt
	 */
	protected SkillExecutor skillExecutor;

	/**
	 * Quelle fuer den Sound, welcher bei einem Standardangriff gespielt werden soll.
	 */
	protected String soundSource;

	/**
	 * Schaden, der durch eine Basisattacke erzielt wird.
	 * Der tatsaechliche Schaden kann variieren.
	 */
	protected int targetDamage;

	/**
	 * Animationszeit der Overlays eines Angriffs ueber dem Ziel
	 */
	protected double targetOverlayAnimationTime;

	/**
	 * Farbe der Overlays eines Treffers.
	 * Dieses Overlay wird ueber dem angegriffenen Aktor gezeigt.
	 */
	protected Color targetOverlayColor;

	/**
	 * Overlays, welche bei einem Angriff ueber dem Gegner gezeigt werden.
	 */
	protected String[] targetOverlays;

	/**
	 * Angabe, ob fuer die Darstellung von Standardangriffen
	 * Projektile angezeigt werden sollen.
	 */
	protected boolean useProjectiles;

	/**
	 * Gibt die Abweichung des Schadens des Aktors an.
	 * Der tatsaechliche Schaden liegt zwischen targetDamage - attackDamageVariation / 2
	 * und targetDamage + attackDamageVariation / 2
	 *
	 * @return Schadensvariation.
	 */
	public int getAttackDamageVariation()
	{
		return attackDamageVariation;
	}

	/**
	 * Gibt die Animationszeit von Projektilen an, die auf einen angegriffenen Gegner fliegen
	 *
	 * @param toPoint Position des Gegners
	 * @return Animationszeit fuer Projektile
	 */
	public double getAttackProjectileAnimationTime(Point fromPoint, Point toPoint)
	{
		return fromPoint.distance(toPoint) * projectileAnimationTime;
	}

	/**
	 * Gibt die Farbe der Projektile an, welche waehrend eines Angriffs auf den Gegner geschossen werden sollen.
	 * @return Farbe der Projektile
	 */
	public Color getAttackProjectileColor()
	{
		return attackProjectileColor;
	}

	/**
	 * Gibt die Ebenen fuer ein Projektil an, welches beim Standardangriff geschossen werden soll
	 * @param direction Richtung, in die sicht das Projektil bewegt
	 * @return Ebenen fuer Angriffsdarstellung
	 */
	public String[] getAttackProjectilesForDirection(Direction direction)
	{

		String[] projectiles = new String[projectilesPerDirection];
		System.arraycopy(
				this.projectiles,
				directionDependentProjectiles ? direction.ordinal() * projectilesPerDirection : 0,
				projectiles,
				0,
				projectilesPerDirection);
		return projectiles;
	}

	/**
	 * Gibt die Ebenen fuer ein Projektil an, welches beim Standardangriff geschossen werden soll
	 * @param toPoint Zielpunkt fuer den Angriff
	 * @return Ebenen fuer Angriffsdarstellung
	 */
	public String[] getAttackProjectilesForDirection(Point fromPoint, Point toPoint)
	{
		Direction direction   = Direction.direction(fromPoint, toPoint);
		String[]  projectiles = new String[projectilesPerDirection];
		System.arraycopy(
				this.projectiles,
				directionDependentProjectiles ? direction.ordinal() * projectilesPerDirection : 0,
				projectiles,
				0,
				projectilesPerDirection);
		return projectiles;
	}

	/**
	 * Gibt den Maximalen Angriffsradius eines Standardangriffs an.
	 * Ein Angriff kann nur durchgefuehrt werden, wenn sich der Gegner innerhalb dieses Radius
	 * befindet.
	 * @return maximaler Angriffsradius
	 */
	public int getAttackRange()
	{
		return attackRange;
	}

	/**
	 * Gibt das Icon fuer den Skill an
	 * @return Icon
	 */
	public String getIcon()
	{
		return icon;
	}

	/**
	 * Gibt die Animationszeit an, in welcher Angriffsoverlays animiert werden
	 * @return Animationszeit von Angriffsoverlays
	 */
	public double getOverlayAnimationTime()
	{
		return overlayAnimationTime;
	}

	/**
	 * Gibt die Farbe der Ebenen an, welche waehrend eines Angriffs ueber dem Angreifer gezeigt werden sollen.
	 * @return Farbe der Angriffsoverlays
	 */
	public Color getOverlayColor()
	{
		return overlayColor;
	}

	/**
	 * Gibt die Ebenen an, welche beim Ausfuehren eines Standardangriffs angezeigt werden sollen
	 *
	 * @return Angriffsebenen
	 */
	public String[] getOverlays()
	{
		return overlays;
	}

	/**
	 * Gibt die Verzoegerung an, mit der sich Projektile aufloesen.
	 * Diese Verzoegerung gibt an, wie lange es dauert, bis ein Projektil,
	 * welches an einer bestimmten Kartenposition angezeigt wird,
	 * an dieser Position wieder verschwindet.
	 * @return Verzoegerung der Projektilaufloesung in Sekunden
	 */
	public double getProjectileDissolveDelay()
	{
		return projectileDissolveDelay;
	}

	/**
	 * Gibt zufaellig einen moeglichen Schadenswert zurueck.
	 * @return zufaelliger Schadenswert
	 */
	public int getRandomizedDamage()
	{
		return targetDamage + (int) ((Math.random() - 0.5) * attackDamageVariation);
	}

	/**
	 * Gibt die Angriffsrate des Aktors an.
	 * Diese gibt die Anzahl von Basisangriffen pro Sekunde an
	 * @return Angriffsrate
	 */
	public double getRate()
	{
		return rate;
	}

	/**
	 * Gibt das Level an, welches mindestens zur ausfuehrung des Skills erforderlich ist.
	 * @return erforderliches Level
	 */
	public int getRequiredLevel()
	{
		return requiredLevel;
	}

	/**
	 * Gibt ein Objekt der Klasse zurueck, welche den Skill ausfuehrt.
	 * @return Executor fuer den Skill
	 */
	public SkillExecutor getSkillExecutor()
	{
		return skillExecutor;
	}

	/**
	 * Gibt den Pfad der Audiodatei an, welche den Klang der Basisattacke enthaelt.
	 * Der Pfad ist relativ zur Klasse AudioPlayer
	 * @return Basisattackenklang
	 */
	public String getSoundSource()
	{
		return soundSource;
	}

	/**
	 * Gibt den Schaden an, welcher der Aktor durchschnittlich erziehlt.
	 *
	 * @return Schaden
	 */
	public int getTargetDamage()
	{
		return targetDamage;
	}

	/**
	 * Gibt die Animationszeit von Overlays an, welche waehrend eines Angriffs ueber
	 * dem angegriffenen Gegner gezeigt werden
	 * @return Animationszeit fuer Hit-Overlays
	 */
	public double getTargetOverlayAnimationTime()
	{
		return targetOverlayAnimationTime;
	}

	/**
	 * Gibt die Farbe der Overlays an, welche ueber dem getroffenen Aktor bei einem Angriff gezeigt werden sollen.
	 * @return Farbe der Hit-Overlays
	 */
	public Color getTargetOverlayColor()
	{
		return targetOverlayColor;
	}

	/**
	 * Gibt die Ebenen an, welche ueber dem angegriffenen Gegner bei einem Angriff angezeigt werden sollen.
	 *
	 * @return Ebenen als Strings
	 */
	public String[] getTargetOverlays()
	{
		return targetOverlays;
	}

	/**
	 * Gibt die Gesamtangriffszeit an, die der Angriff benoetigt.
	 * @return Gesamtangriffszeit
	 */
	public double getTotalAnimationTime()
	{
		return Math.max(overlayAnimationTime, targetOverlayAnimationTime);
	}

	/**
	 * Laed einen Angriff (oder ein beliebigen SkillExecutor)
	 * aus den angegebenen Properties. Dabei werden die Schluessel
	 * mit dem angegebenen Prefix versehen.
	 * @param properties Quelle
	 * @param prefix Property-Prefix
	 */
	public void load(Properties properties, String prefix)
	{
		requiredLevel = Integer.parseInt(properties.getProperty(prefix + "attack_required_level", "0"));

		rate = Double.parseDouble(properties.getProperty(prefix + "attack_rate", "0"));
		targetDamage = Integer.parseInt(properties.getProperty(prefix + "attack_damage", "0"));
		attackRange = Integer.parseInt(properties.getProperty(prefix + "attack_range", "0"));
		attackDamageVariation = Integer.parseInt(properties.getProperty(prefix + "attack_damage_variation", "0"));

		soundSource = properties.getProperty(prefix + "attack_sound", null);
		useProjectiles = Boolean.parseBoolean(properties.getProperty(prefix + "attack_objects", "false"));

		if (useProjectiles)
		{
			directionDependentProjectiles = Boolean.parseBoolean(properties.getProperty(
					prefix + "attack_direction_dependent",
					"false"));
			projectilesPerDirection = Integer.parseInt(properties.getProperty(
					prefix + "attack_objects_per_direction",
					"0"));

			projectiles = new String[projectilesPerDirection * (directionDependentProjectiles ? 4 : 1)];
			projectileAnimationTime = Double.parseDouble(properties.getProperty(
					prefix + "attack_projectile_duration",
					"0"));
			projectileDissolveDelay = Double.parseDouble(properties.getProperty(prefix + "attack_dissolve_delay", "0"));
			if (directionDependentProjectiles)
			{
				for (int i = 0; i < projectilesPerDirection; i++)
				{
					String upName     = prefix + "attack_obj_up_" + (i + 1);
					String leftName   = prefix + "attack_obj_left_" + (i + 1);
					String downName   = prefix + "attack_obj_down_" + (i + 1);
					String rightName  = prefix + "attack_obj_right_" + (i + 1);
					String upValue    = properties.getProperty(upName);
					String leftValue  = properties.getProperty(leftName);
					String downValue  = properties.getProperty(downName);
					String rightValue = properties.getProperty(rightName);
					projectiles[i] = upValue;
					projectiles[i + projectilesPerDirection] = leftValue;
					projectiles[i + projectilesPerDirection * 2] = downValue;
					projectiles[i + projectilesPerDirection * 3] = rightValue;
				}
			}
			else
			{
				projectilesPerDirection = Integer.parseInt(properties.getProperty(
						prefix + "attack_object_count",
						"0"));
				projectiles = new String[projectilesPerDirection];
				for (int i = 1; i <= projectilesPerDirection; i++)
					projectiles[i - 1] = properties.getProperty(prefix + "attack_obj_" + i);
			}
		}
		else
		{
			projectiles = new String[0];
			directionDependentProjectiles = false;
			projectilesPerDirection = 0;
		}
		int   base_r    = Integer.parseInt(properties.getProperty(prefix + "attack_color_r", "0"));
		int   base_g    = Integer.parseInt(properties.getProperty(prefix + "attack_color_g", "0"));
		int   base_b    = Integer.parseInt(properties.getProperty(prefix + "attack_color_b", "0"));
		Color baseColor = new Color(base_r, base_g, base_b);

		requiresFocus = Boolean.parseBoolean(properties.getProperty(prefix + "attack_requires_focus", "true"));

		overlayColor = baseColor;
		attackProjectileColor = baseColor;
		targetOverlayColor = baseColor;

		int numberOfHitOverlays = Integer.parseInt(properties.getProperty(prefix + "attack_hit_objects", "0"));
		targetOverlays = new String[numberOfHitOverlays];
		for (int i = 1; i <= numberOfHitOverlays; i++)
			targetOverlays[i - 1] = properties.getProperty(prefix + "attack_hit_" + i, "");

		int numberOfAttackOverlays = Integer.parseInt(properties.getProperty(prefix + "attack_overlays", "0"));
		overlays = new String[numberOfAttackOverlays];
		for (int i = 1; i <= numberOfAttackOverlays; i++)
			overlays[i - 1] = properties.getProperty(prefix + "attack_overlay_" + i);

		overlayAnimationTime = Double.parseDouble(properties.getProperty(
				prefix + "attack_overlay_duration",
				"0.0"));
		targetOverlayAnimationTime = Double.parseDouble(properties.getProperty(
				prefix + "attack_hit_duration",
				"0.0"));

		icon = properties.getProperty(prefix + "attack_icon", "");

		try
		{
			skillExecutor = (SkillExecutor) Class.forName(properties.getProperty(
					prefix + "attack_class",
					"project.game.data.skills.AttackSkillExecutor"))
					.newInstance();
			skillExecutor.setConfiguration(this);
			skillExecutor.loadAdditionalProperties(properties, prefix);
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Gibt an, ob der Skill nur ausgefuehrt werden kann, wenn ein Ziel gewaehlt ist
	 * @return true, wenn ein Ziel erforderlich ist, sonst false
	 */
	public boolean requiresFocus()
	{
		return requiresFocus;
	}

	/**
	 * Gibt an, ob der Aktor fuer Basisangriffe Projektile verwendet.
	 *
	 * @return true, wenn Projektile verwendet werden, sonst false.
	 */
	public boolean usesProjectiles()
	{
		return useProjectiles;
	}
}
