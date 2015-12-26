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

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class Level
{
	private Enemy[] enemies;
	private Map map;
	private Point[] path;
	private Player player;

	public Level(URL url) throws IOException
	{
		Properties properties = new Properties();
		properties.load(url.openStream());

		int width = Integer.parseInt(properties.getProperty("Width"));
		int height = Integer.parseInt(properties.getProperty("Height"));

		int[][] points = new int[width][height];

		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
			{
				String value = properties.getProperty(x + "," + y);
				if (value == null)
					points[x][y] = 0;
				else
					points[x][y] = Integer.parseInt(value) + 1;
			}
		this.map = new Map(points);
		Point start = new Point(0, 32);
		Point end = new Point(499, 235);
		map.removeFinish();
		map.removeStart();
		long time = System.currentTimeMillis();
		Point[] path = map.findPath(start, end);
		long time2 = System.currentTimeMillis();
		if (path != null)
		{
			for (Point p : path)
				map.setPoint(p.x, p.y, -1);
			this.path = path;
		}
	}

	public int getHeight()
	{
		return map.getHeight();
	}

	public Point[] getPath()
	{
		return path;
	}

	public int getPixel(int x, int y)
	{
		return map.getPoint(x, y);
	}

	public int getWidth()
	{
		return map.getWidth();
	}
}
