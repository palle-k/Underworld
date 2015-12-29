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

import project.gui.components.TComponent;

import java.awt.*;
import java.util.Properties;

public abstract class MapObject
{
	protected Rectangle bounds;
	protected Color     color;
	protected String    restingState;
	private   Runnable  onContact;
	private   Level     owner;

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
		return bounds;
	}

	public abstract TComponent getView();

	public void setBounds(final Rectangle bounds)
	{
		this.bounds = bounds;
	}

	protected Runnable getOnPlayerContact()
	{
		return onContact;
	}

	protected Level getOwner()
	{
		return owner;
	}

	protected void setOnPlayerContact(final Runnable onContact)
	{
		this.onContact = onContact;
	}

	protected void setOwner(final Level owner)
	{
		this.owner = owner;
	}

}
