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
import java.util.Properties;

public class Enemy extends GameActor implements Serializable
{
	public static Enemy createDynamic() throws IOException
	{
		Properties properties = new Properties();
		properties.load(Enemy.class.getResourceAsStream("objects/DynamicEnemy.properties"));
		return new Enemy(properties);
	}

	public static Enemy createStatic() throws IOException
	{
		Properties properties = new Properties();
		properties.load(Enemy.class.getResourceAsStream("objects/StaticEnemy.properties"));
		return new Enemy(properties);
	}
	private int    earnedExperience; //Earned experience when killing the enemy
	private int    follow_range; //Maximum distance to continue following the player
	private int    level;
	private String name;
	private int    visionRange;

	protected Enemy(Properties properties)
	{
		super(properties);
		earnedExperience = Integer.parseInt(properties.getProperty("earned_experience"));
		follow_range = Integer.parseInt(properties.getProperty("follow_range"));
		visionRange = Integer.parseInt(properties.getProperty("vision_range"));
		name = properties.getProperty("name");
	}

	public int getEarnedExperience()
	{
		return earnedExperience;
	}

	public int getFollowRange()
	{
		return follow_range;
	}

	@Override
	public int getLevel()
	{
		return level;
	}

	public String getName()
	{
		return name;
	}

	public int getVisionRange()
	{
		return visionRange;
	}
}
