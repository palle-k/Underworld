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

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

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
		MOVING,
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
	 * Bewegungsstatus fuer Aktor:
	 * Der Aktor hat sich vor kurzer Zeit bewegt.
	 */
	public static final ActorState MOVING = ActorState.MOVING;
	/**
	 * Wartestatus fuer Aktor:
	 * Der Aktor hat seit einiger Zeit keine Aktion durchgefuehrt.
	 */
	public static final ActorState RESTING   = ActorState.RESTING;
	/**
	 * Ebenen, die den Aktor waehrend eines Angriffs darstellen.
	 */
	protected transient String attackState;

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
	protected transient String defenseState;

	/**
	 * Verdiente Erfarhung beim Toeten dieses Aktors
	 */
	protected transient int earnedExperience;

	/**
	 * Wert, der die Regeneration von Leben angibt.
	 * (Wiederhergestellte Lebenspunkte pro Sekunde)
	 */
	protected transient double healthRegeneration;

	/**
	 * Maximal erreichbare Lebenspunkte
	 */
	protected transient int maxHealth;

	/**
	 * Ebenen, welche den Aktor in Bewegung darstellen.
	 */
	protected transient String[] movementStates;

	/**
	 * Geschwindigkeit, mit der sich der Aktor in einem Level
	 * bewegen kann
	 */
	protected transient double speed;
	/**
	 * Basisattacke (Hat jeder Aktor)
	 */
	private transient SkillConfiguration baseAttack;
	private int movementStateCounter;
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
	private transient Timer stateTimer;

	/**
	 * Erzeugt einen neuen GameActor mit den Angaben in den angegebenen Properties
	 * DEPRECATED. Use GameActor(URL source) instead.
	 * @param properties Quelle fuer Ebenen, Werte, usw...
	 */
	@Deprecated
	protected GameActor(Properties properties)
	{
		super(properties);
		this.properties = properties;
		restore();
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
	 * Der Aktor wurde angegriffen und verliert die angegebene Anzahl
	 * an Lebenspunkten
	 * @param damage Erhaltener Schaden
	 */
	public void decreaseHealth(int damage)
	{
		currentHealth -= damage;
		if (currentHealth < 0)
			currentHealth = 0;
		enterState(ActorState.DEFENDING);
		if (delegate instanceof GameActorDelegate)
			((GameActorDelegate) delegate).actorDidChangeHealth(this);
	}

	/**
	 * Setzt den Status des GameActors auf Angriff
	 */
	public void enterAttackState()
	{
		enterState(ActorState.ATTACKING);
	}

	/**
	 * Setzt den Status des GameAcotrs auf Bewegen
	 */
	public void enterMovementState()
	{
		enterState(ActorState.MOVING);
	}

	/**
	 * Gibt die Ebenen an, welche den Aktor waehrend der Durchfuherung eines Angriffs zeigen
	 * @return Angriffsstatusebenen
	 */
	public String getAttackState()
	{
		return attackState;
	}

	/**
	 * Gibt den Standardangriff zurueck, welchen der Aktor ausfuehren kann.
	 * @return Standardangriff
	 */
	public SkillConfiguration getBaseAttack()
	{
		return baseAttack;
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
	public String getDefenseState()
	{
		return defenseState;
	}

	/**
	 * Gibt die Erfahrung an, die beim Besiegen des Gegners erlangt wird.
	 * @return Erlangte Erfahrung durch Besiegen
	 */
	public int getEarnedExperience()
	{
		return earnedExperience;
	}

	/**
	 * Gibt die Anzahl an Lebenspunkten an, die pro Sekunde regeneriert werden sollen.
	 *
	 * @return Lebenspunkteregeneration pro Sekunde
	 */
	public double getHealthRegeneration()
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
	 * Gibt die Bewegungsebenen des Aktors an.
	 * @return Bewegungsebenen
	 */
	public String[] getMovementStates()
	{
		return movementStates;
	}

	public String getNextMovementState()
	{
		movementStateCounter++;
		movementStateCounter %= movementStates.length;
		return movementStates[movementStateCounter];
	}

	/**
	 * Gibt die Geschwindigkeit an, mit der sich der Aktor bewegen kann.
	 * @return Geschwindigkeit des Aktors
	 */
	public double getSpeed()
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
	 * Stellt den Aktor wieder her.
	 */
	@Override
	protected void restore()
	{
		super.restore();

		speed = Double.parseDouble(properties.getProperty("speed", "0"));

		defenseState = properties.getProperty("defend");
		deadState = properties.getProperty("dead", "");

		int movement_state_count = Integer.parseInt(properties.getProperty("movement_states"));
		movementStates = new String[movement_state_count];

		for (int i = 0; i < movement_state_count; i++)
			movementStates[i] = properties.getProperty("move_" + (i + 1));

		maxHealth = Integer.parseInt(properties.getProperty("max_health"));
		currentHealth = maxHealth;
		healthRegeneration = Double.parseDouble(properties.getProperty("health_regeneration", "0"));

		earnedExperience = Integer.parseInt(properties.getProperty("earned_experience", "0"));

		attackState = properties.getProperty("attack");

		baseAttack = new SkillConfiguration();
		baseAttack.load(properties, "base_");
	}

	/**
	 * Fuehrt eine Statusaenderung des Aktors durch.
	 * Ist der neue Status nicht DEAD oder RESTING,
	 * wird der RESTING-Status nach 200ms automatisch wiederhergestellt.
	 * @param newState neuer Status des Aktors
	 */
	private void enterState(ActorState newState)
	{
		setState(newState);
		stateCounter++;
		if (delegate instanceof  GameActorDelegate)
			((GameActorDelegate) delegate).actorDidChangeState(this);
		if (state != ActorState.RESTING && state != ActorState.DEAD)
		{
			long      stateCounterCopy = stateCounter;
			GameActor self             = this;
			if (stateTimer == null)
				stateTimer = new Timer();
			stateTimer.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					if (stateCounterCopy == stateCounter)
					{
						setState(ActorState.RESTING);
						if (delegate instanceof GameActorDelegate)
							((GameActorDelegate) delegate).actorDidChangeState(self);
					}
				}
			}, 200);
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
		//Rectangle newBounds = getBounds();
//		if (state == ActorState.RESTING)
//			newBounds.setSize(StringUtils.getStringDimensions(getRestingState()));
//		else if (state == ActorState.ATTACKING)
//			newBounds.setSize(StringUtils.getStringDimensions(getAttackState()));
//		else if (state == ActorState.DEFENDING)
//			newBounds.setSize(StringUtils.getStringDimensions(getDefenseState()));
//		else if (state == ActorState.DEAD)
//			newBounds.setSize(StringUtils.getStringDimensions(getDeadState()));
		//setBounds(newBounds);
	}
}
