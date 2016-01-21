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
import project.game.data.Map;
import project.gui.components.TScrollView;
import project.gui.graphics.TGraphics;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * Levelansicht fuer das Spiel
 * Stellt die Karte mit Waenden und Sichtbarkeitsbereichen dar.
 * Aktoren und andere Objekte koennen als TComponent-Objekte hinzugefuegt werden.
 * Die Verschiebung der Karte kann mit setOffset(Point) gesetzt werden
 */
public class MapView extends TScrollView
{
	private Level       level;
	private boolean[][] visibility;
	private Point visionPoint;

	/**
	 * Gibt das dargestellte Level an
	 *
	 * @return dargestelltes Level
	 */
	public Level getLevel()
	{
		return level;
	}

	/**
	 * Setzt die Sichtbarkeitsdaten zurueck und erzwingt eine Neuberechnung
	 */
	public void invalidateVisiblity()
	{
		if (visionPoint == null)
			return;
		visibility = level.getMap()
				.getVisiblePoints(
						new Rectangle(getOffset().x, getOffset().y, getWidth(), getHeight()),
						visionPoint);
		setNeedsDisplay();
	}

	@Override
	public void setFrame(final Rectangle frame)
	{
		super.setFrame(frame);
		invalidateVisiblity();
	}

	/**
	 * Setzt das darzustellende Level
	 * @param level darzustellendes Level
	 */
	public void setLevel(final Level level)
	{
		this.level = level;
		setNeedsDisplay();
	}

	/**
	 * Setzt den Punkt, von welchem aus das Level betrachtet wird.
	 * Ausgehend von diesem Punkt wird berechnet, ob andere Punkte sichtbar sind.
	 * @param point Sichtpunkt
	 */
	public void setPointOfVision(Point point)
	{
		if (point != null && (visionPoint == null || !visionPoint.equals(point)))
		{
			visionPoint = point;
			invalidateVisiblity();
		}
	}

	@Override
	protected void paintChildren(final TGraphics graphics, final Rectangle dirtyRect)
	{
		graphics.setMask(visibility);
		super.paintChildren(graphics, dirtyRect);
	}

	@Override
	protected void paintComponent(final TGraphics graphics, Rectangle dirtyRect)
	{
		boolean showPaths = System.getProperty("com.palleklewitz.underworld.map.showpaths", "false").equalsIgnoreCase("true");
		super.paintComponent(graphics, dirtyRect);
		for (int x = 0; x < getWidth(); x++)
			for (int y = 0; y < getHeight(); y++)
			{
				int offsetX = x + getOffset().x;
				int offsetY = y + getOffset().y;
				if (!level.getMap().isOutOfBounds(new Point(offsetX, offsetY)))
				{
					int pixel = level.getPixel(offsetX, offsetY);
					if (visibility == null
					    || (visibility.length > x
					        && visibility[x].length > y))
					{
						if (visibility == null || visibility[x][y])
						{
							if (pixel <= 0)
							{
								Color color = null;
								if (showPaths && pixel == -1)
									graphics.setPoint(x, y, null, Color.GREEN, ' ');
								else if (pixel == Map.EMPTY)
								{
									if (visionPoint != null)
									{
										int dx = visionPoint.x - (offsetX);
										int dy = visionPoint.y - (offsetY);
										int value = Math.max(
												255 - (int) (Math.sqrt(dx * dx / 2 + dy * dy * 9) * 6),
												20);
										color = Color.getHSBColor(0.1f, 0.5f, value / 255.0f);
									}
									else
									{
										color = Color.BLACK;
									}

								}
								else if (pixel == Map.WATER)
								{
									if (visionPoint != null)
									{
										int dx = visionPoint.x - (offsetX);
										int dy = visionPoint.y - (offsetY);
										int value = Math.max(
												255 - (int) (Math.sqrt(dx * dx / 2 + dy * dy * 9) * 6),
												20);
										color = Color.getHSBColor(
												0.5f + (float) Math.random() * 0.02f,
												0.8f,
												value / 255.0f);
									}
									else
									{
										color = Color.BLACK;
									}
								}
								graphics.setPoint(x, y, null, color, ' ');
							}
							else if (visionPoint != null)
							{
								int   dx = visionPoint.x - offsetX;
								int   dy = visionPoint.y - offsetY;
								int   value = Math.max(255 - (int) (Math.sqrt(dx * dx / 2 + dy * dy * 9) * 4), 0);
								Color color = Color.getHSBColor(0.08f, 0.3f, value / 255.0f);
								graphics.setPoint(x, y, null, color, ' ');
							}
						}
						else if (visibility != null)
						{
							boolean visible = false;
							visible |= offsetX > 0
							           && x > 0
							           && level.getPixel(offsetX - 1, offsetY) <= 0
							           && visibility[x - 1][y];
							visible |= offsetX > 1
							           && x > 1
							           && level.getPixel(offsetX - 2, offsetY) <= 0
							           && visibility[x - 2][y];
							visible |= offsetY > 0
							           && y > 0
							           && level.getPixel(offsetX, offsetY - 1) <= 0
							           && visibility[x][y - 1];
							visible |= offsetX < level.getWidth()-2
							           && x < visibility.length - 1
							           && level.getPixel(offsetX + 1, offsetY) <= 0
							           && visibility[x + 1][y];
							visible |= offsetX < level.getWidth() - 3
							           && x < visibility.length - 2
							           && level.getPixel(offsetX + 2, offsetY) <= 0
							           && visibility[x + 2][y];
							visible |= offsetY < level.getHeight()-2
							           && y < visibility[x].length - 1
							           && level.getPixel(offsetX, offsetY + 1) <= 0
							           && visibility[x][y + 1];

							if (visible)
							{
								int dx = visionPoint.x - offsetX;
								int dy = visionPoint.y - offsetY;
								Color color;
								if (pixel == Map.EMPTY)
								{
									int value = Math.max(220 - (int) (Math.sqrt(dx * dx / 2 + dy * dy * 9) * 6), 10);
									color = Color.getHSBColor(0.1f, 0.5f, value / 255.0f);
								}
								else if (pixel == Map.WATER)
								{
									int value = Math.max(
											220 - (int) (Math.sqrt(dx * dx / 2 + dy * dy * 9) * 6),
											10);
									color = Color.getHSBColor(
											0.5f + (float) Math.random() * 0.02f,
											0.8f,
											value / 255.0f);
								}
								else
								{
									int value = Math.max(255 - (int) (Math.sqrt(dx * dx / 2 + dy * dy * 9) * 4), 0);
									color = Color.getHSBColor(0.08f, 0.3f, value / 255.0f);
								}
								graphics.setPoint(x, y, null, color, ' ');
							}

						}
					}
				}
			}
	}
}
