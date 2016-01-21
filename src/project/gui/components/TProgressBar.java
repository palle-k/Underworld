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

package project.gui.components;

import project.gui.graphics.Appearance;
import project.gui.graphics.TGraphics;

import java.awt.Color;
import java.awt.Rectangle;

/**
 * Komponente zur Darstellung eines Fortschritts
 */
public class TProgressBar extends TComponent
{
	private Color  color;
	private double maxValue;
	private double minValue;
	private double value;

	/**
	 * Erstellt eine neue Fortschrittsleiste
	 */
	public TProgressBar()
	{
		value = 0;
		maxValue = 1;
		color = Appearance.defaultTextColor;
	}

	/**
	 * Gibt die Farbe der Fortschrittsleiste an
	 * @return Farbe der Leiste
	 */
	public Color getColor()
	{
		return color;
	}

	/**
	 * Gibt den Maximalwert an, der angezeigt werden kann.
	 * Ist der aktuelle Wert gleich gross wie der Maximalwert,
	 * ist die Fortschrittsleiste komplett gefuellt.
	 * @return Maximalwert der Fortschrittsleiste
	 */
	public double getMaxValue()
	{
		return maxValue;
	}

	/**
	 * Gibt den Minimalwert der Forschrittsleiste an.
	 * Ist der aktuelle Wert gleich gross wie der Minimalwert,
	 * ist die Fortschrittsleiste komplett leer.
	 * @return Minimalwert der Fortschrittsleiste
	 */
	public double getMinValue()
	{
		return minValue;
	}

	/**
	 * Gibt den Fortschrittswert an, der aktuell dargestellt wird.
	 * Dieser fuellt die Fortschrittsleiste zu einem Teil von
	 * <code>
	 *   (getValue() - getMinValue()) / (getMaxValue() - getMinValue())
	 * </code>
	 * @return dargestellter Fortschrittswert
	 */
	public double getValue()
	{
		return value;
	}

	/**
	 * Setzt die Farbe der Fortschrittsleiste
	 * @param color neue Farbe
	 */
	public void setColor(final Color color)
	{
		this.color = color;
		setNeedsDisplay();
	}

	/**
	 * Setzt den Maximalwert der Fortschrittsleiste.
	 * Ist der aktuelle Wert gleich gross wie der Maximalwert, ist die
	 * Fortschrittsleiste komplett gefuellt.
	 * @param maxValue neuer Maximalwert
	 */
	public void setMaxValue(final double maxValue)
	{
		if (this.maxValue == maxValue)
			return;
		this.maxValue = maxValue;
		setNeedsDisplay();
	}

	/**
	 * Setzt den Minimalwert der Fortschrittsleiste.
	 * Ist der aktuelle Wert gleich gross wie der Minimalwert, ist die
	 * Fortschrittsleiste komplett leer.
	 * @param minValue neuer Minimalwert
	 */
	public void setMinValue(final double minValue)
	{
		if (this.minValue == minValue)
			return;
		this.minValue = minValue;
		setNeedsDisplay();
	}

	/**
	 * Setzt den aktuell dargestellten Wert der Fortschrittsleiste.
	 * Dieser fuellt die Fortschrittsleiste zu einem Teil von
	 * <code>
	 *   (value - getMinValue()) / (getMaxValue() - getMinValue())
	 * </code>
	 * @param value neuer Fortschrittswert
	 */
	public void setValue(final double value)
	{
		if (this.value == value)
			return;
		this.value = value;
		setNeedsDisplay();
	}

	@Override
	protected void paintComponent(final TGraphics graphics, Rectangle dirtyRect)
	{
		super.paintComponent(graphics, dirtyRect);
		for (int y = 0; y < getHeight(); y++)
		{
			graphics.setPoint(0, y, getColor(), getBackgroundColor(), '[');
			graphics.setPoint(getWidth() - 1, y, getColor(), getBackgroundColor(), ']');

			for (int x = 1; x < (value - minValue) / (maxValue - minValue) * (getWidth() - 1); x++)
			{
				graphics.setPoint(x, y, getColor(), getBackgroundColor(), '#');
			}
		}
	}
}
