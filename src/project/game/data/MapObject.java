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

import project.gui.components.TComponent;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Properties;

public abstract class MapObject implements Serializable
{
	protected Rectangle         bounds;
	protected Color             color;
	protected transient MapObjectDelegate delegate;
	protected String            restingState;

	protected MapObject(Properties properties)
	{
		restingState = properties.getProperty("resting");

		int r = Integer.parseInt(properties.getProperty("color_r"));
		int g = Integer.parseInt(properties.getProperty("color_g"));
		int b = Integer.parseInt(properties.getProperty("color_b"));

		color = new Color(r, g, b);

		bounds = new Rectangle();
	}

	public Rectangle getBounds()
	{
		return new Rectangle(bounds);
	}

	public Color getColor()
	{
		return color;
	}

	public MapObjectDelegate getDelegate()
	{
		return delegate;
	}

	public String getRestingState()
	{
		return restingState;
	}

	public abstract TComponent getView();

	public void setBounds(final Rectangle bounds)
	{
		if (this.bounds.equals(bounds))
			return;
		this.bounds = bounds;
		if (delegate != null)
			delegate.mapObjectDidMove(this);
	}

	public void setDelegate(final MapObjectDelegate delegate)
	{
		this.delegate = delegate;
	}
}
