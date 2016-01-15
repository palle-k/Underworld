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
	 * Schaden, der durch eine Basisattacke erzielt wird.
	 * Der tatsaechliche Schaden kann variieren.
	 */
	protected transient int attackDamage;

	/**
	 * Schadensvariation bei einer Basisattacke.
	 * Der tatsaechliche Schaden liegt zwischen attackDamage - attackDamageVariation / 2
	 * und attackDamage + attackDamageVariation / 2
	 */
	protected transient int           attackDamageVariation;
	/**
	 * Animationszeit der Overlays eines Angriffs ueber dem Ziel
	 */
	protected transient double        attackHitOverlayAnimationTime;
	/**
	 * Farbe der Overlays eines Treffers.
	 * Dieses Overlay wird ueber dem angegriffenen Aktor gezeigt.
	 */
	protected transient Color         attackHitOverlayColor;
	/**
	 * Overlays, welche bei einem Angriff ueber dem Gegner gezeigt werden.
	 */
	protected transient String[]      attackHitOverlays;
	/**
	 * Animationszeit der Overlays eines Angriffs ueber dem Angreifer
	 */
	protected transient double        attackOverlayAnimationTime;
	/**
	 * Farbe des Overlays eines Angriffs.
	 * Dieses Overlay wird ueber dem Angreifer angezeigt.
	 */
	protected transient Color         attackOverlayColor;
	/**
	 * Ebenen, die ueber dem Aktor bei der Ausfuehrung eines Angriffs angezeigt werden sollen
	 */
	protected transient String[]      attackOverlays;
	/**
	 * Animationszeit von Projektilen (Geschwindigkeit zum Gegner)
	 * Wird mit Distanz multipliziert.
	 */
	protected transient double        attackProjectileAnimationTime;
	/**
	 * Farbe der Angriffsprojektile.
	 */
	protected transient Color         attackProjectileColor;
	/**
	 * Ebenen, welche Projektile darstellen, welche beim Angriff vom angreifenden Aktor
	 * auf den angegriffenen Aktor geschossen werden.
	 * Diese koennen Richtungsabhaengig sein.
	 */
	protected transient String[]      attackProjectiles;
	/**
	 * Maximalradius, den die Basisattacke ueberbruecken kann.
	 */
	protected transient int           attackRange;
	/**
	 * Angriffsrate (Wiederholrate fuer Basisangriffe)
	 */
	protected transient double        attackRate;
	/**
	 * Quelle fuer den Sound, welcher bei einem Standardangriff gespielt werden soll.
	 */
	protected transient String        attackSoundSource;
	/**
	 * Boolean, welcher angibt, ob die verwendeten Projektile der
	 * Basisattacke abhaengig von der Bewegungsrichtung sind.
	 */
	protected transient boolean       directionDependentProjectiles;
	/**
	 * Projektilebenen pro Richtung
	 */
	protected transient int           projectilesPerDirection;
	/**
	 * Gibt das Level an, welches mindestens zur ausfuehrung des Skills erforderlich ist.
	 */
	protected transient int           requiredLevel;
	/**
	 * Gibt an, ob ein Gegner bekannt sein muss, damit dieser Skill ausgefuehrt werden kann.
	 */
	protected transient boolean       requiresFocus;
	/**
	 * Klasse, welche den SkillExecutor ausfuehrt
	 */
	protected transient SkillExecutor skillExecutor;
	/**
	 * Angabe, ob fuer die Darstellung von Standardangriffen
	 * Projektile angezeigt werden sollen.
	 */
	protected transient boolean       useProjectiles;

	/**
	 * Gibt den Schaden an, welcher der Aktor durchschnittlich erziehlt.
	 *
	 * @return Schaden
	 */
	public int getAttackDamage()
	{
		return attackDamage;
	}

	/**
	 * Gibt die Abweichung des Schadens des Aktors an.
	 * Der tatsaechliche Schaden liegt zwischen attackDamage - attackDamageVariation / 2
	 * und attackDamage + attackDamageVariation / 2
	 * @return Schadensvariation.
	 */
	public int getAttackDamageVariation()
	{
		return attackDamageVariation;
	}

	/**
	 * Gibt die Animationszeit von Overlays an, welche waehrend eines Angriffs ueber
	 * dem angegriffenen Gegner gezeigt werden
	 * @return Animationszeit fuer Hit-Overlays
	 */
	public double getAttackHitOverlayAnimationTime()
	{
		return attackHitOverlayAnimationTime;
	}

	/**
	 * Gibt die Farbe der Overlays an, welche ueber dem getroffenen Aktor bei einem Angriff gezeigt werden sollen.
	 * @return Farbe der Hit-Overlays
	 */
	public Color getAttackHitOverlayColor()
	{
		return attackHitOverlayColor;
	}

	/**
	 * Gibt die Ebenen an, welche ueber dem angegriffenen Gegner bei einem Angriff angezeigt werden sollen.
	 * @return Ebenen als Strings
	 */
	public String[] getAttackHitOverlays()
	{
		return attackHitOverlays;
	}

	/**
	 * Gibt die Animationszeit an, in welcher Angriffsoverlays animiert werden
	 * @return Animationszeit von Angriffsoverlays
	 */
	public double getAttackOverlayAnimationTime()
	{
		return attackOverlayAnimationTime;
	}

	/**
	 * Gibt die Farbe der Ebenen an, welche waehrend eines Angriffs ueber dem Angreifer gezeigt werden sollen.
	 * @return Farbe der Angriffsoverlays
	 */
	public Color getAttackOverlayColor()
	{
		return attackOverlayColor;
	}

	/**
	 * Gibt die Ebenen an, welche beim Ausfuehren eines Standardangriffs angezeigt werden sollen
	 *
	 * @return Angriffsebenen
	 */
	public String[] getAttackOverlays()
	{
		return attackOverlays;
	}

	/**
	 * Gibt die Animationszeit von Projektilen an, die auf einen angegriffenen Gegner fliegen
	 * @param toPoint Position des Gegners
	 * @return Animationszeit fuer Projektile
	 */
	public double getAttackProjectileAnimationTime(Point fromPoint, Point toPoint)
	{
		return fromPoint.distance(toPoint) * attackProjectileAnimationTime;
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
				attackProjectiles,
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
				attackProjectiles,
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
	 * Gibt die Angriffsrate des Aktors an.
	 * Diese gibt die Anzahl von Basisangriffen pro Sekunde an
	 * @return Angriffsrate
	 */
	public double getAttackRate()
	{
		return attackRate;
	}

	/**
	 * Gibt den Pfad der Audiodatei an, welche den Klang der Basisattacke enthaelt.
	 * Der Pfad ist relativ zur Klasse AudioPlayer
	 * @return Basisattackenklang
	 */
	public String getAttackSoundSource()
	{
		return attackSoundSource;
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
	 * Laed einen Angriff (oder ein beliebigen SkillExecutor)
	 * aus den angegebenen Properties. Dabei werden die Schluessel
	 * mit dem angegebenen Prefix versehen.
	 * @param properties Quelle
	 * @param prefix Property-Prefix
	 */
	public void load(Properties properties, String prefix)
	{
		requiredLevel = Integer.parseInt(properties.getProperty(prefix + "attack_required_level", "0"));

		attackRate = Double.parseDouble(properties.getProperty(prefix + "attack_rate", "0"));
		attackDamage = Integer.parseInt(properties.getProperty(prefix + "attack_damage", "0"));
		attackRange = Integer.parseInt(properties.getProperty(prefix + "attack_range", "0"));
		attackDamageVariation = Integer.parseInt(properties.getProperty(prefix + "attack_damage_variation", "0"));

		attackSoundSource = properties.getProperty(prefix + "attack_sound", null);
		useProjectiles = Boolean.parseBoolean(properties.getProperty(prefix + "attack_objects", "false"));

		if (useProjectiles)
		{
			directionDependentProjectiles = Boolean.parseBoolean(properties.getProperty(
					prefix + "attack_direction_dependent",
					"false"));
			projectilesPerDirection = Integer.parseInt(properties.getProperty(
					prefix + "attack_objects_per_direction",
					"0"));

			attackProjectiles = new String[projectilesPerDirection * (directionDependentProjectiles ? 4 : 1)];
			attackProjectileAnimationTime = Double.parseDouble(properties.getProperty(
					prefix + "attack_projectile_duration",
					"0"));
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
					attackProjectiles[i] = upValue;
					attackProjectiles[i + projectilesPerDirection] = leftValue;
					attackProjectiles[i + projectilesPerDirection * 2] = downValue;
					attackProjectiles[i + projectilesPerDirection * 3] = rightValue;
				}
			}
			else
			{
				projectilesPerDirection = Integer.parseInt(properties.getProperty(
						prefix + "attack_object_count",
						"0"));
				attackProjectiles = new String[projectilesPerDirection];
				for (int i = 1; i <= projectilesPerDirection; i++)
					attackProjectiles[i - 1] = properties.getProperty(prefix + "attack_obj_" + i);
			}
		}
		else
		{
			attackProjectiles = new String[0];
			directionDependentProjectiles = false;
			projectilesPerDirection = 0;
		}
		int   base_r    = Integer.parseInt(properties.getProperty(prefix + "attack_color_r", "0"));
		int   base_g    = Integer.parseInt(properties.getProperty(prefix + "attack_color_g", "0"));
		int   base_b    = Integer.parseInt(properties.getProperty(prefix + "attack_color_b", "0"));
		Color baseColor = new Color(base_r, base_g, base_b);

		requiresFocus = Boolean.parseBoolean(properties.getProperty(prefix + "attack_requires_focus", "true"));

		attackOverlayColor = baseColor;
		attackProjectileColor = baseColor;
		attackHitOverlayColor = baseColor;

		int numberOfHitOverlays = Integer.parseInt(properties.getProperty(prefix + "attack_hit_objects", "0"));
		attackHitOverlays = new String[numberOfHitOverlays];
		for (int i = 1; i <= numberOfHitOverlays; i++)
			attackHitOverlays[i - 1] = properties.getProperty(prefix + "attack_hit_" + i, "");

		int numberOfAttackOverlays = Integer.parseInt(properties.getProperty(prefix + "attack_overlays", "0"));
		attackOverlays = new String[numberOfAttackOverlays];
		for (int i = 1; i <= numberOfAttackOverlays; i++)
			attackOverlays[i - 1] = properties.getProperty(prefix + "attack_overlay_" + i);

		attackOverlayAnimationTime = Double.parseDouble(properties.getProperty(
				prefix + "attack_overlay_duration",
				"0.0"));
		attackHitOverlayAnimationTime = Double.parseDouble(properties.getProperty(
				prefix + "attack_hit_duration",
				"0.0"));

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
