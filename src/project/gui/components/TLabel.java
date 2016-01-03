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
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

public class TLabel extends TComponent
{
	private String text;
	private Color  textColor;
	private Insets textInsets;

	public TLabel()
	{
		super();
		setDrawsBackground(true);
		text = "";
		textColor = Appearance.defaultTextColor;
		textInsets = new Insets(0, 0, 0, 0);
	}

	public Color getColor()
	{
		return textColor;
	}

	public String getText()
	{
		return text;
	}

	public Insets getTextInsets()
	{
		return textInsets;
	}

	public void setColor(Color textColor)
	{
		this.textColor = textColor;
	}

	public void setText(String text)
	{
		if (this.text.equals(text))
			return;
		this.text = text;
		setNeedsDisplay(new Rectangle(new Point(), getSize()));
	}

	public void setTextInsets(Insets textInsets)
	{
		this.textInsets = textInsets;
		setNeedsDisplay(new Rectangle(new Point(), getSize()));
	}

	@Override
	protected void paintComponent(TGraphics graphics, Rectangle dirtyRect)
	{
		super.paintComponent(graphics, dirtyRect);
		graphics.setStrokeColor(getColor());
		graphics.setStrokeBackground(drawsBackground() ? getBackgroundColor() : null);
		graphics.drawText(
				getText(),
				(drawsBorder() ? 1 : 0) + getTextInsets().left,
				(drawsBorder() ? 1 : 0) + getTextInsets().top);
		//TODO advanced text layout
		//TODO automatic size calculation
	}
}
