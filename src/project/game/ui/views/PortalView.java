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
import java.awt.Dimension;
import java.awt.Rectangle;

public class PortalView extends TComponent
{
	private float baseHue;

	private float hueFactor;

	private double time;

	private float timeFactor;

	public PortalView()
	{
		super();
		addOnAnimationUpdate(time -> {
			this.time = time;
			setNeedsDisplay();
		});
	}

	public float getBaseHue()
	{
		return baseHue;
	}

	public float getHueFactor()
	{
		return hueFactor;
	}

	public float getTimeFactor()
	{
		return timeFactor;
	}

	public void setBaseHue(final float baseHue)
	{
		this.baseHue = baseHue;
	}

	public void setHueFactor(final float hueFactor)
	{
		this.hueFactor = hueFactor;
	}

	public void setTimeFactor(final float timeFactor)
	{
		this.timeFactor = timeFactor;
	}

	@Override
	protected void paintComponent(final TGraphics graphics, final Rectangle dirtyRect)
	{
		Dimension size    = getSize();
		double    centerX = (size.width - 1) * 0.5;
		double    centerY = (size.height - 1) * 0.5;
		super.paintComponent(graphics, dirtyRect);
		for (int x = dirtyRect.x; x < dirtyRect.x + dirtyRect.width; x++)
		{
			for (int y = dirtyRect.y; y < dirtyRect.y + dirtyRect.height; y++)
			{
				double dx = x - centerX;
				double dy = y - centerY;
				dy *= 2;
				float hue = (float) Math.sin(Math.sqrt(dx * dx + dy * dy) + timeFactor * time) * hueFactor + baseHue;
				graphics.setPoint(x, y, null, Color.getHSBColor(hue, 1.0f, 1.0f), ' ');
			}
		}
	}
}
