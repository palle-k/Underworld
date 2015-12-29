/******************************************************************************
 * Copyright (c) 2015 Palle Klewitz.                                          *
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

import java.util.Properties;

public class GameActor extends MapObject
{
	protected int      attackDamage;
	protected String[] attackLayers;
	protected int      attackRate;
	protected String[] attackStates;
	protected int      currentHealth;
	protected String   deadState;
	protected String[] defenseStates;
	protected int      healthRegeneration;
	protected int      maxHealth;
	protected String[] movingStates;
	protected int      speed;

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
	}

	public void decreaseHealth(int damage)
	{
		currentHealth -= damage;
		if (currentHealth < 0)
			currentHealth = 0;
	}

	public int getCurrentHealth()
	{
		return currentHealth;
	}

	public int getSpeed()
	{
		return speed;
	}

	@Override
	public TComponent getView()
	{
		return null;
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
}
