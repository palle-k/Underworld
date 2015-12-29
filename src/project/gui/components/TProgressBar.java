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

package project.gui.components;

import project.gui.graphics.Appearance;
import project.gui.graphics.TGraphics;

import java.awt.*;

public class TProgressBar extends TComponent
{
	private Color color;
	private double maxValue;
	private double value;

	public TProgressBar()
	{
		value = 0;
		maxValue = 1;
		color = Appearance.defaultTextColor;
	}

	public Color getColor()
	{
		return color;
	}

	public double getMaxValue()
	{
		return maxValue;
	}

	public double getValue()
	{
		return value;
	}

	public void setColor(final Color color)
	{
		this.color = color;
		setNeedsDisplay(new Rectangle(new Point(), getSize()));
	}

	public void setMaxValue(final double maxValue)
	{
		this.maxValue = maxValue;
		setNeedsDisplay(new Rectangle(new Point(), getSize()));
	}

	public void setValue(final double value)
	{
		this.value = value;
		setNeedsDisplay(new Rectangle(new Point(), getSize()));
	}

	@Override
	protected void paintComponent(final TGraphics graphics, Rectangle dirtyRect)
	{
		super.paintComponent(graphics, dirtyRect);
		for (int y = 0; y < getHeight(); y++)
		{
			graphics.setPoint(0, y, getColor(), getBackgroundColor(), '[');
			graphics.setPoint(getWidth() - 1, y, getColor(), getBackgroundColor(), ']');

			for (int x = 1; x < value / maxValue * (getWidth() - 3); x++)
			{
				graphics.setPoint(x, y, getColor(), getBackgroundColor(), '#');
			}
		}
	}
}
