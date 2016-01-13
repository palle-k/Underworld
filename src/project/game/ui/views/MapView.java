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

package project.game.ui.views;

import project.game.data.Level;
import project.gui.components.TScrollView;
import project.gui.graphics.TGraphics;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

public class MapView extends TScrollView
{
	private Level       level;
	private boolean[][] visibility;
	private Point visionPoint;

	public Level getLevel()
	{
		return level;
	}

	public void setLevel(final Level level)
	{
		this.level = level;
		setNeedsDisplay();
	}

	public void setPointOfVision(Point point)
	{
		if (point != null)
		{
			visibility = level.getMap()
					.getVisiblePoints(
							new Rectangle(getOffset().x, getOffset().y, getWidth(), getHeight()),
							point);
			visionPoint = point;
		}
	}

	@Override
	protected void paintChildren(final TGraphics graphics, final Rectangle dirtyRect)
	{
		graphics.setMask(visibility);
		super.paintChildren(graphics, dirtyRect);
		graphics.setMask(null);
	}

	@Override
	protected void paintComponent(final TGraphics graphics, Rectangle dirtyRect)
	{
		boolean showPaths = System.getProperty("com.palleklewitz.underworld.map.showpaths", "false").equalsIgnoreCase("true");
		super.paintComponent(graphics, dirtyRect);
		for (int x = 0; x < getWidth(); x++)
			for (int y = 0; y < getHeight(); y++)
			{
				if (x + getOffset().x >= 0 && x + getOffset().x < level.getWidth() && y + getOffset().y >= 0 &&
				    y + getOffset().y < level.getHeight())
				{
					int pixel = level.getPixel(x + getOffset().x, y + getOffset().y);
					if (pixel < 1 &&
					    (visibility == null || (visibility.length > x && visibility[x].length > y && visibility[x][y])))
					{
						if (showPaths && pixel == -1)
							graphics.setPoint(x, y, null, Color.GREEN, ' ');
						else
						{
							Color color;
							if (visionPoint != null)
							{
								int dx = visionPoint.x - (x + getOffset().x);
								int dy = visionPoint.y - (y + getOffset().y);
								int value = Math.max(255 - (int) (Math.sqrt(dx * dx / 2 + dy * dy * 9) * 6), 0);
								color = Color.getHSBColor(0.18f, 0.35f, value / 255.0f);
							}
							else
							{
								color = Color.BLACK;
							}

							graphics.setPoint(x, y, null, color, ' ');
						}
					}
				}
				/*else
				{
					//graphics.setPoint(x, y, null, getBackgroundColor(), ' ');
				}*/
			}
	}
}
