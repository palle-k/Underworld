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

import project.util.Direction;
import project.util.StringUtils;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Properties;

/**
 * Basisklasse fuer saemtliche Dinge/Personen, die im Spiel handeln koennen.
 * Speicherung eines Status, sowie Strings zur Darstellung des Aktors in jedem Zustand
 */
public abstract class GameActor extends MapObject implements Serializable
{
	/*
	Enum nach aussen unsichtbar
	 */
	private enum ActorState
	{
		RESTING,
		ATTACKING,
		DEFENDING,
		DEAD
	}

	/**
	 * Attackstatus fuer Aktor:
	 * Der Aktor hat vor kurzer Zeit einen Angriff durchgefuehrt
	 */
	public static final ActorState ATTACKING = ActorState.ATTACKING;

	/**
	 * Todstatus fuer Aktor:
	 * Der Aktor hat kein Leben mehr (isAlive() returns false)
	 */
	public static final ActorState DEAD      = ActorState.DEAD;

	/**
	 * Verteidigungsstatus fuer Aktor:
	 * Der Aktor wurde vor kurzer Zeit (von einem anderen Aktoren) angegriffen.
	 */
	public static final ActorState DEFENDING = ActorState.DEFENDING;

	/**
	 * Wartestatus fuer Aktor:
	 * Der Aktor hat seit einiger Zeit keine Aktion durchgefuehrt.
	 */
	public static final ActorState RESTING   = ActorState.RESTING;

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
	protected transient int attackDamageVariation;

	/**
	 * Ebenen, die ueber dem Aktor bei der Ausfuehrung eines Angriffs angezeigt werden sollen
	 */
	protected transient String[] attackLayers;

	/**
	 * Ebenen, welche Projektile darstellen, welche beim Angriff vom angreifenden Aktor
	 * auf den angegriffenen Aktor geschossen werden.
	 * Diese koennen Richtungsabhaengig sein.
	 */
	protected transient String[] attackProjectiles;

	/**
	 * Maximalradius, den die Basisattacke ueberbruecken kann.
	 */
	protected transient int attackRange;

	/**
	 * Angriffsrate (Wiederholrate fuer Basisangriffe)
	 */
	protected transient int attackRate;

	/**
	 * Ebenen, die den Aktor waehrend eines Angriffs darstellen.
	 */
	protected transient String[] attackStates;

	/**
	 * Aktuell verfuegbare Lebenspunkte
	 */
	protected int currentHealth;

	/**
	 * Ebene, die den Aktor darstellt, falls sein Leben 0 erreicht.
	 */
	protected transient String deadState;

	/**
	 * Ebenen, welche den Aktor in Verteidigungsposition darstellen.
	 */
	protected transient String[] defenseStates;

	/**
	 * Boolean, welcher angibt, ob die verwendeten Projektile der
	 * Basisattacke abhaengig von der Bewegungsrichtung sind.
	 */
	protected transient boolean directionDependentProjectiles;

	/**
	 * Wert, der die Regeneration von Leben angibt.
	 * (Wiederhergestellte Lebenspunkte pro Sekunde)
	 */
	protected transient int healthRegeneration;

	/**
	 * Maximal erreichbare Lebenspunkte
	 */
	protected transient int maxHealth;

	/**
	 * Ebenen, welche den Aktor in Bewegung darstellen.
	 */
	protected transient String[] movingStates;

	/**
	 * Projektilebenen pro Richtung
	 */
	protected transient int projectilesPerDirection;

	/**
	 * Geschwindigkeit, mit der sich der Aktor in einem Level
	 * bewegen kann
	 */
	protected transient int speed;

	/**
	 * Angabe, ob fuer die Darstellung von Standardangriffen
	 * Projektile angezeigt werden sollen.
	 */
	protected transient boolean useProjectiles;

	/**
	 * Aktueller Status des Aktors.
	 *
	 * @see ActorState
	 */
	private ActorState state;

	/*
	 * Statuszaehler (Verhindert fehlerhafte Aenderungen des Aktorstatus)
	 */
	private transient long stateCounter;

	/**
	 * Erzeugt einen neuen GameActor mit den Angaben in den angegebenen Properties
	 * DEPRECATED. Use GameActor(URL source) instead.
	 * @param properties Quelle fuer Ebenen, Werte, usw...
	 */
	@Deprecated
	protected GameActor(Properties properties)
	{
		super(properties);

		speed = Integer.parseInt(properties.getProperty("speed"));

		attackStates = new String[]{ properties.getProperty("attack") };
		defenseStates = new String[]{ properties.getProperty("defend") };
		deadState = properties.getProperty("dead", "");

		int movement_state_count = Integer.parseInt(properties.getProperty("movement_states"));
		movingStates = new String[movement_state_count];

		for (int i = 0; i < movement_state_count; i++)
			movingStates[i] = properties.getProperty("move_" + (i + 1));

		attackRate = Integer.parseInt(properties.getProperty("attack_rate"));
		attackDamage = Integer.parseInt(properties.getProperty("attack_damage"));
		attackRange = Integer.parseInt(properties.getProperty("attack_range"));
		maxHealth = Integer.parseInt(properties.getProperty("max_health"));
		currentHealth = maxHealth;
		healthRegeneration = Integer.parseInt(properties.getProperty("health_regeneration"));
		attackDamageVariation = Integer.parseInt(properties.getProperty("attack_damage_variation"));
		
		useProjectiles = Boolean.parseBoolean(properties.getProperty("base_attack_objects", "false"));
		if (useProjectiles)
		{
			directionDependentProjectiles = Boolean.parseBoolean(properties.getProperty(
					"base_attack_direction_dependent",
					"false"));
			projectilesPerDirection = Integer.parseInt(properties.getProperty(
					"base_attack_objects_per_direction",
					"1"));

			attackProjectiles = new String[projectilesPerDirection * (directionDependentProjectiles ? 4 : 1)];

			if (directionDependentProjectiles)
			{
				for (int i = 0; i < projectilesPerDirection; i++)
				{
					String upName     = "base_attack_obj_up_" + (i + 1);
					String leftName   = "base_attack_obj_left_" + (i + 1);
					String downName   = "base_attack_obj_down_" + (i + 1);
					String rightName  = "base_attack_obj_right_" + (i + 1);
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
				for (int i = 0; i < projectilesPerDirection; i++)
				{
					String pName  = "base_attack_obj_" + (i + 1);
					String pValue = properties.getProperty(pName);
					attackProjectiles[i] = pValue;
				}
			}
		}
		else
		{
			attackProjectiles = new String[0];
			directionDependentProjectiles = false;
			projectilesPerDirection = 0;
		}

		//TODO load attack states and projectiles
		//FIXME Important: not loading attack projectiles
		attackLayers = new String[0];
	}

	/**
	 * Erzeugt einen neuen GameActor mit den Angaben,
	 * welche sich in der Properties-File am angegebenen Ort befinden.
	 *
	 * @param source Ort der Properties-Quelldatei
	 * @throws IOException Wenn die Datei nicht existiert oder nicht gelesen werden konnte.
	 */
	protected GameActor(final URL source) throws IOException
	{
		super(source);
	}

	/**
	 * Aendert den Status des GameActors auf Angriff
	 * DEPRECATED. Use attack() instead.
	 * @param actor angegriffener Gegner
	 */
	@Deprecated
	public void attack(GameActor actor)
	{
		makeStateChange(ActorState.ATTACKING);
	}

	/**
	 * Setzt den Status des GameActors auf Angriff
	 */
	public void attack()
	{
		makeStateChange(ActorState.ATTACKING);
	}

	/**
	 * Der Aktor wurde angegriffen und verliert die angegebene Anzahl
	 * an Lebenspunkten
	 * @param damage Erhaltener Schaden
	 */
	public void decreaseHealth(int damage)
	{
		currentHealth -= damage;
		if (currentHealth < 0)
			currentHealth = 0;
		makeStateChange(ActorState.DEFENDING);
		if (delegate instanceof GameActorDelegate)
			((GameActorDelegate) delegate).actorDidChangeHealth(this);
	}

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
	 * Gibt die Ebenen an, welche beim Ausfuehren eines Standardangriffs angezeigt werden sollen
	 *
	 * @return Angriffsebenen
	 */
	public String[] getAttackLayers()
	{
		return attackLayers;
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
	public int getAttackRate()
	{
		return attackRate;
	}

	/**
	 * Gibt die Ebenen an, welche den Aktor waehrend der Durchfuherung eines Angriffs zeigen
	 * @return Angriffsstatusebenen
	 */
	public String[] getAttackStates()
	{
		return attackStates;
	}

	/**
	 * Gibt den Mittelpunkt des Aktors an.
	 *
	 * @return Mittelpunkt des Aktors
	 */
	public Point getCenter()
	{
		return new Point((int) getBounds().getCenterX(), (int) getBounds().getCenterY());
	}

	/**
	 * Gibt die Anzahl verfuegbarer Lebenspunkte an.
	 * Diese befindet sich zwischen 0 und getMaxHealth()
	 * @return verfuegbare Lebenspunkte
	 */
	public int getCurrentHealth()
	{
		return currentHealth;
	}

	/**
	 * Gibt die Ebene an, welche den Aktoren darstellt, sobald dieser ueber kein Leben mehr verfuegt.
	 * @return Tot-Status
	 */
	public String getDeadState()
	{
		return deadState;
	}

	/**
	 * Gibt Ebenen an, welche den Aktoren bei der Verteidigung eines Angriffs anzeigen
	 *
	 * @return Verteidigungsebenen
	 */
	public String[] getDefenseStates()
	{
		return defenseStates;
	}

	/**
	 * Gibt die Anzahl an Lebenspunkten an, die pro Sekunde regeneriert werden sollen.
	 *
	 * @return Lebenspunkteregeneration pro Sekunde
	 */
	public int getHealthRegeneration()
	{
		return healthRegeneration;
	}

	/**
	 * Gibt das Level des Aktors zurueck.
	 * Dies ist abhaengig davon, worum es sich bei dem Aktor handelt.
	 * (z.B. Spieler oder Gegner), weswegen es sich um eine abstrakte Methode handelt.
	 * @return Aktorlevel
	 */
	public abstract int getLevel();

	/**
	 * Gibt die maximale Anzahl verfuegbarer Lebenspunkte an.
	 * @return maximal verfuegbare Lebenspunkte
	 */
	public int getMaxHealth()
	{
		return maxHealth;
	}

	/**
	 * Gibt die Geschwindigkeit an, mit der sich der Aktor bewegen kann.
	 * @return Geschwindigkeit des Aktors
	 */
	public int getSpeed()
	{
		return speed;
	}

	/**
	 * Gibt den Status des Aktors an.
	 *
	 * @return Status des Aktors
	 * @see ActorState
	 */
	public ActorState getState()
	{
		return state;
	}

	/**
	 * Gibt an, ob der Aktor lebendig ist.
	 * Dies ist der Fall, wenn getCurrentHealth() > 0.
	 *
	 * @return ob der Aktor lebendig ist.
	 */
	public boolean isAlive()
	{
		return currentHealth > 0;
	}

	/**
	 * Regeneriert die angegebene Anzahl an Lebenspunkten.
	 * Erreichen die verfuegbaren Lebenspunkte maxHealth, koennen
	 * die verfuegbaren Lebenspunkte nicht weiter steigen.
	 * @param additionalHealth dazugewonnene Lebenspunkte
	 */
	public void regenerateHealth(int additionalHealth)
	{
		currentHealth += additionalHealth;
		if (currentHealth > getMaxHealth())
			currentHealth = getMaxHealth();
		if (delegate instanceof GameActorDelegate)
			((GameActorDelegate) delegate).actorDidChangeHealth(this);
	}

	/**
	 * Setzt den Mittelpunkt des Aktors.
	 * Hierbei findet eine Translation des gesamten Aktors statt.
	 * @param newCenter Neuer Mittelpunkt des Aktors
	 */
	public void setCenter(Point newCenter)
	{
		Point     currentCenter = getCenter();
		int       dx            = newCenter.x - currentCenter.x;
		int       dy            = newCenter.y - currentCenter.y;
		Rectangle bounds        = getBounds();
		bounds.translate(dx, dy);
		setBounds(bounds);
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

	/**
	 * Stellt den Aktor wieder her.
	 */
	@Override
	protected void restore()
	{
		super.restore();

		speed = Integer.parseInt(properties.getProperty("speed"));

		attackStates = new String[]{ properties.getProperty("attack") };
		defenseStates = new String[]{ properties.getProperty("defend") };
		deadState = properties.getProperty("dead", "");

		int movement_state_count = Integer.parseInt(properties.getProperty("movement_states"));
		movingStates = new String[movement_state_count];

		for (int i = 0; i < movement_state_count; i++)
			movingStates[i] = properties.getProperty("move_" + (i + 1));

		attackRate = Integer.parseInt(properties.getProperty("attack_rate"));
		attackDamage = Integer.parseInt(properties.getProperty("attack_damage"));
		attackRange = Integer.parseInt(properties.getProperty("attack_range"));
		maxHealth = Integer.parseInt(properties.getProperty("max_health"));
		currentHealth = maxHealth;
		healthRegeneration = Integer.parseInt(properties.getProperty("health_regeneration"));
		attackDamageVariation = Integer.parseInt(properties.getProperty("attack_damage_variation"));

		useProjectiles = Boolean.parseBoolean(properties.getProperty("base_attack_objects", "false"));
		if (useProjectiles)
		{
			directionDependentProjectiles = Boolean.parseBoolean(properties.getProperty(
					"base_attack_direction_dependent",
					"false"));
			projectilesPerDirection = Integer.parseInt(properties.getProperty(
					"base_attack_objects_per_direction",
					"1"));

			attackProjectiles = new String[projectilesPerDirection * (directionDependentProjectiles ? 4 : 1)];

			if (directionDependentProjectiles)
			{
				for (int i = 0; i < projectilesPerDirection; i++)
				{
					String upName     = "base_attack_obj_up_" + (i + 1);
					String leftName   = "base_attack_obj_left_" + (i + 1);
					String downName   = "base_attack_obj_down_" + (i + 1);
					String rightName  = "base_attack_obj_right_" + (i + 1);
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
				for (int i = 0; i < projectilesPerDirection; i++)
				{
					String pName  = "base_attack_obj_" + (i + 1);
					String pValue = properties.getProperty(pName);
					attackProjectiles[i] = pValue;
				}
			}
		}
		else
		{
			attackProjectiles = new String[0];
			directionDependentProjectiles = false;
			projectilesPerDirection = 0;
		}

		//TODO load attack states and projectiles
		//FIXME Important: not loading attack projectiles
		attackLayers = new String[0];
	}

	/**
	 * Fuehrt eine Statusaenderung des Aktors durch.
	 * Ist der neue Status nicht DEAD oder RESTING,
	 * wird der RESTING-Status nach 200ms automatisch wiederhergestellt.
	 * @param newState neuer Status des Aktors
	 */
	private void makeStateChange(ActorState newState)
	{
		setState(newState);
		stateCounter++;
		if (delegate instanceof  GameActorDelegate)
			((GameActorDelegate) delegate).actorDidChangeState(this);
		if (state != ActorState.RESTING && state != ActorState.DEAD)
		{
			long      stateCounterCopy = stateCounter;
			GameActor self             = this;
			new Thread(() -> {
				try
				{
					Thread.sleep(200);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				if (stateCounterCopy == stateCounter)
				{
					setState(ActorState.RESTING);
					if (delegate instanceof GameActorDelegate)
						((GameActorDelegate) delegate).actorDidChangeState(self);
				}
			}).start();
		}
	}

	/**
	 * Setzt den Status des Aktors
	 * Hierbei wird die Groesse des Aktors aktualisiert und dem neuen Status angepasst.
	 *
	 * @param state neuer Status
	 */
	private void setState(ActorState state)
	{
		this.state = state;
		Rectangle newBounds = new Rectangle(bounds);
		if (state == ActorState.RESTING)
			newBounds.setSize(StringUtils.getStringDimensions(getRestingState()));
		else if (state == ActorState.ATTACKING)
			newBounds.setSize(StringUtils.getStringDimensions(getAttackStates()[0]));
		else if (state == ActorState.DEFENDING)
			newBounds.setSize(StringUtils.getStringDimensions(getDefenseStates()[0]));
		else if (state == ActorState.DEAD)
			newBounds.setSize(StringUtils.getStringDimensions(getDeadState()));
		setBounds(newBounds);
	}
}
