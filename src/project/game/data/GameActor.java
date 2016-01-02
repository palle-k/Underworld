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

import project.gui.components.TComponent;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

public class GameActor extends MapObject
{
	private enum ActorState
	{
		RESTING,
		ATTACKING,
		DEFENDING,
		DEAD
	}

	public static final ActorState ATTACKING = ActorState.ATTACKING;
	public static final ActorState DEAD      = ActorState.DEAD;
	public static final ActorState DEFENDING = ActorState.DEFENDING;
	public static final ActorState RESTING   = ActorState.RESTING;
	protected int        attackDamage;
	protected String[]   attackLayers;
	protected String[]   attackProjectiles;
	protected int        attackRate;
	protected String[]   attackStates;
	protected int        currentHealth;
	protected String     deadState;
	protected String[]   defenseStates;
	protected int        healthRegeneration;
	protected int        maxHealth;
	protected String[]   movingStates;
	protected int        speed;
	private   int        attackDamageVariation;
	private   int        attackRange;
	private   ActorState state;
	private   long       stateCounter;
	private   int        visionRange;

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
		maxHealth = Integer.parseInt(properties.getProperty("max_health"));
		healthRegeneration = Integer.parseInt(properties.getProperty("health_regeneration"));

		//TODO load attack states and projectiles
	}

	public void decreaseHealth(int damage)
	{
		currentHealth -= damage;
		if (currentHealth < 0)
			currentHealth = 0;
		if (delegate != null && delegate instanceof GameActorDelegate)
		{
			((GameActorDelegate) delegate).actorDidChangeState(this);
			GameActor self = this;
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						Thread.sleep(200);
					}
					catch (InterruptedException e)
					{
					}
					SwingUtilities.invokeLater(() -> ((GameActorDelegate) delegate).actorDidChangeState(self));
				}
			}).start();
		}
	}

	public int getAttackDamage()
	{
		return attackDamage;
	}

	public int getAttackDamageVariation()
	{
		return attackDamageVariation;
	}

	public String[] getAttackLayers()
	{
		return attackLayers;
	}

	public String[] getAttackProjectiles()
	{
		return attackProjectiles;
	}

	public int getAttackRange()
	{
		return attackRange;
	}

	public int getAttackRate()
	{
		return attackRate;
	}

	public String[] getAttackStates()
	{
		return attackStates;
	}

	public Point getCenter()
	{
		return new Point((int) getBounds().getCenterX(), (int) getBounds().getCenterY());
	}

	public int getCurrentHealth()
	{
		return currentHealth;
	}

	public int getSpeed()
	{
		return speed;
	}

	public ActorState getState()
	{
		return state;
	}

	@Override
	public TComponent getView()
	{
		return null;
	}

	public int getVisionRange()
	{
		return visionRange;
	}

	public boolean isAlive()
	{
		return currentHealth > 0;
	}

	public void regenerateHealth(int additionalHealth)
	{
		currentHealth += additionalHealth;
		if (currentHealth > maxHealth)
			currentHealth = maxHealth;
	}

	public void setCenter(Point newCenter)
	{
		Point     currentCenter = getCenter();
		int       dx            = newCenter.x - currentCenter.x;
		int       dy            = newCenter.y - currentCenter.y;
		Rectangle bounds        = getBounds();
		bounds.translate(dx, dy);
		setBounds(bounds);
	}

	private void makeStateChange(ActorState newState)
	{
		this.state = newState;
		stateCounter++;
		if (state != ActorState.RESTING && delegate instanceof GameActorDelegate)
		{
			long stateCounterCopy = stateCounter;
			((GameActorDelegate) delegate).actorDidChangeState(this);
			GameActor self = this;
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
					state = ActorState.RESTING;
					((GameActorDelegate) delegate).actorDidChangeState(self);
				}
			}).start();
		}
	}
}
