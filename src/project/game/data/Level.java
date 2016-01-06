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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Properties;

public class Level implements Serializable
{
	private Enemy[] enemies;
	private transient Rectangle entranceBounds;
	private transient Rectangle[] exitBounds;
	private Key[]   keys;
	private transient Map     map;
	private Player  player;
	private URL     source;

	public Level(URL url) throws IOException
	{
		source = url;
		load();
	}

	public Enemy[] getEnemies()
	{
		return enemies;
	}

	public Rectangle getEntranceBounds()
	{
		return entranceBounds;
	}

	public Rectangle[] getExitBounds()
	{
		return exitBounds;
	}

	public int getHeight()
	{
		return map.getHeight();
	}

	public Key[] getKeys()
	{
		return keys;
	}

	public Map getMap()
	{
		if (map == null)
			try
			{
				load();
			}
			catch (IOException e)
			{
				throw new RuntimeException("Map could not be loaded.", e);
			}
		return map;
	}

	public int getPixel(int x, int y)
	{
		return map.getPoint(x, y);
	}

	public Player getPlayer()
	{
		return player;
	}

	public int getWidth()
	{
		return map.getWidth();
	}

	private void load() throws IOException
	{
		if (source == null)
			throw new NullPointerException("No Source Specified.");
		Properties properties = new Properties();
		properties.load(source.openStream());

		int width  = Integer.parseInt(properties.getProperty("Width")) * 8;
		int height = Integer.parseInt(properties.getProperty("Height")) * 4;

		int[][] points = new int[width][height];

		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
			{
				String value = properties.getProperty(x / 8 + "," + y / 4);
				if (value == null)
					points[x][y] = 0;
				else
					points[x][y] = Integer.parseInt(value) + 1;
			}
		this.map = new Map(points, 8, 4);
		Rectangle start = map.getStart();
		Rectangle end   = map.getFinish()[0];
		//map.removeFinish();
		//map.removeStart();
		//Point[] path = map.findPath(start.getLocation(), end.getLocation(), 1, 1);
		//if (path != null)
		//{
		//	for (Point p : path)
		//		map.setPoint(p.x, p.y, -1);
		//}

		if (player == null)
		{
			player = Player.makePlayer();
			if (player != null)
				player.setBounds(new Rectangle(start.getLocation(), new Dimension(8, 4)));
		}
		if (enemies == null)
		{
			Rectangle[] staticEnemyBounds = map.getStaticEnemies();
			Rectangle[] dynamicEnemyBounds = map.getDynamicEnemies();

			enemies = new Enemy[staticEnemyBounds.length + dynamicEnemyBounds.length];

			for (int i = 0, staticEnemyBoundsLength = staticEnemyBounds.length; i < staticEnemyBoundsLength; i++)
			{
				Rectangle enemyBounds = staticEnemyBounds[i];
				Enemy enemy = Enemy.createStatic();
				enemy.setBounds(enemyBounds);
				enemies[i] = enemy;
			}

			for (int i = 0, dynamicEnemyBoundsLength = dynamicEnemyBounds.length; i < dynamicEnemyBoundsLength; i++)
			{
				Rectangle enemyBounds = dynamicEnemyBounds[i];
				Enemy enemy = Enemy.createDynamic();
				enemy.setBounds(enemyBounds);
				enemies[i + staticEnemyBounds.length] = enemy;
			}
		}
		if (keys == null)
		{
			Rectangle[] keyBounds = map.getKeys();
			keys = new Key[keyBounds.length];
			for (int i = 0, keyBoundsLength = keyBounds.length; i < keyBoundsLength; i++)
			{
				Rectangle bounds = keyBounds[i];
				Key       key    = Key.makeKey();
				key.setBounds(bounds);
				keys[i] = key;
			}
		}
		entranceBounds = map.getStart();
		exitBounds = map.getFinish();
		map.removeFeatures();
	}
}
