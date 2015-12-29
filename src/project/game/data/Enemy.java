/******************************************************************************
 * Copyright (c) 2015 Palle Klewitz.                                          *
 * *
 * Permission is hereby granted, free of charge, to any person obtaining      *
 * a copy of this software and associated documentation files                 *
 * (the "Software"), to deal in the Software without restriction,             *
 * including without limitation the rights to use, copy, modify,             *
 * merge, publish, distribute, sublicense, and/or sell copies of             *
 * the Software, and to permit persons to whom the Software                  *
 * is furnished to do so, subject to the following conditions:               *
 * *
 * The above copyright notice and this permission notice shall                *
 * be included in all copies or substantial portions of the Software.         *
 * *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY                         *
 * OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT                        *
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS                     *
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.                             *
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS                        *
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,                      *
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,                      *
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE                            *
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.                    *
 ******************************************************************************/

package project.game.data;

public class Enemy extends GameActor
{
	private int attack_range; //Maximum distance for attacking the player
	private int damage; //Average damage
	private int damage_variation; //Range of variation of damage
	private int earnedExperience; //Earned experience when killing the enemy
	private int follow_range; //Maximum distance to continue following the player
	private int speed; //Speed of the enemy (0 for static enemy)
	private int vision_range; //Maximum distance to begin following the player

	protected Enemy()
	{
		super(null);
	}

	public int getAttackRange()
	{
		return attack_range;
	}

	public int getDamage()
	{
		return damage;
	}

	public int getDamageVariation()
	{
		return damage_variation;
	}

	public int getEarnedExperience()
	{
		return earnedExperience;
	}

	public int getFollowRange()
	{
		return follow_range;
	}

	public int getVisionRange()
	{
		return vision_range;
	}

}
