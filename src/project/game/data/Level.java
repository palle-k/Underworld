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

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class Level
{
	private int[][] map;

	public Level(URL url) throws IOException
	{
		Properties properties = new Properties();
		properties.load(url.openStream());

		int width = Integer.parseInt(properties.getProperty("Width"));
		int height = Integer.parseInt(properties.getProperty("Height"));

		map = new int[width][height];

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				String value = properties.getProperty(x + "," + y);
				if (value == null)
				{
					map[x][y] = -1;
				} else
				{
					map[x][y] = Integer.parseInt(value);
				}
			}
		}
	}

	public int getHeight()
	{
		return map[0].length;
	}

	public int getPixel(int x, int y)
	{
		if (x >= map.length)
			return 0;
		else if (y >= map[x].length)
			return 0;
		else if (x < 0)
			return 0;
		else if (y < 0)
			return 0;
		return map[x][y];
	}

	public int getWidth()
	{
		return map.length;
	}
}
