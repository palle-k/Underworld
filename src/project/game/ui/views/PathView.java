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

import project.gui.components.TComponent;
import project.gui.graphics.TGraphics;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Pfadansicht<br>
 * Stellt einen Pfad dar, der z.B. von einem Aktor zurueckgelegt wird.
 * Ein Punkt kann mit addPoint(Point) hinzugefuegt werden.
 */
public class PathView extends TComponent
{
	private Color pathBackground;

	private Color pathColor;

	private List<Point> pointList;

	private char        strokeChar;

	/**
	 * Erstellt eine neue Pfadansicht
	 */
	public PathView()
	{
		this.pointList = new ArrayList<>();
		pathColor = new Color(200, 200, 200);
		strokeChar = '.';
	}

	/**
	 * Fuegt einen Punkt als letzten Punkt dem Pfad hinzu
	 * @param point hinzuzufuegender Punkt
	 */
	public void addPoint(Point point)
	{
		if (!pointList.isEmpty() && pointList.get(pointList.size() - 1).equals(point))
			return;
		pointList.add(point);
		if (point.x >= getWidth())
			setWidth(point.x + 1);
		if (point.y >= getHeight())
			setHeight(point.y + 1);
		setNeedsDisplay();
	}

	/**
	 * Gibt die Pfadhintergrundfarbe an
	 *
	 * @return Pfadhintergrundfarbe
	 */
	public Color getPathBackground()
	{
		return pathBackground;
	}

	/**
	 * Gibt die Pfadfarbe an
	 * @return Pfadfarbe
	 */
	public Color getPathColor()
	{
		return pathColor;
	}

	/**
	 * Gibt das Zeichen an, mit welchem der Pfad gezeichnet werden soll
	 *
	 * @return Pfadzeichen
	 */
	public char getStrokeChar()
	{
		return strokeChar;
	}

	/**
	 * Setzt die Pfadhintergrundfarbe
	 *
	 * @param pathBackground neue Pfadhintergrundfarbe
	 */
	public void setPathBackground(final Color pathBackground)
	{
		this.pathBackground = pathBackground;
	}
	
	/**
	 * Setzt die Pfadfarbe
	 * @param pathColor neue Pfadfarbe
	 */
	public void setPathColor(final Color pathColor)
	{
		this.pathColor = pathColor;
	}

	/**
	 * Setzt das Zeichen, mit welchem der Pfad gezeichnet werden soll
	 *
	 * @param strokeChar Pfadzeichen
	 */
	public void setStrokeChar(final char strokeChar)
	{
		this.strokeChar = strokeChar;
	}

	@Override
	protected void paintComponent(final TGraphics graphics, final Rectangle dirtyRect)
	{
		super.paintComponent(graphics, dirtyRect);
		if (pointList.isEmpty())
			return;
		graphics.moveTo(pointList.get(0));
		for (int i = 1; i < pointList.size(); i++)
			graphics.lineTo(pointList.get(i));
		graphics.setStrokeBackground(pathBackground);
		graphics.setStrokeColor(pathColor);
		graphics.setComposite(Composite.MULTIPLY);
		graphics.setStrokeChar(strokeChar);
		graphics.stroke();
	}
}
