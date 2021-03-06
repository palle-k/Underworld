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
import project.util.StringUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * Standard-Textkomponente
 * Anzeigen eines Textes, Unterstuetzung von Mehrzeiligem Text,
 * Automatische Groessenanpassung
 */
public class TLabel extends TComponent
{
	private boolean modifiedBounds;
	private String text;
	private Color  textColor;
	private Insets textInsets;

	/**
	 * Erstellt ein neues TLabel
	 */
	public TLabel()
	{
		super();
		setDrawsBackground(false);
		text = "";
		textColor = Appearance.defaultTextColor;
		textInsets = new Insets(0, 0, 0, 0);
		modifiedBounds = false;
	}

	/**
	 * Gibt die Textfarbe an
	 * @return Textfarbe
	 */
	public Color getColor()
	{
		return textColor;
	}

	/**
	 * Gibt den Text an, welcher dargestellt wird
	 * @return dargestellter Text
	 */
	public String getText()
	{
		return text;
	}

	/**
	 * Gibt die Einrueckungen des Textes an
	 * @return Texteinrueckungen
	 */
	public Insets getTextInsets()
	{
		return textInsets;
	}

	/**
	 * Setzt die Textfarbe
	 * @param textColor neue Textfarbe
	 */
	public void setColor(Color textColor)
	{
		this.textColor = textColor;
		setNeedsDisplay();
	}

	@Override
	public void setFrame(final Rectangle frame)
	{
		super.setFrame(frame);
		modifiedBounds = true;
	}

	/**
	 * Setzt den Text, welcher dargestellt werden soll.
	 * Wurden die Begrenzungen der Komponente noch nicht modifiziert,
	 * wird die Groesse des Labels automatisch an die Groesse des Texts angepasst
	 * @param text neuer Text
	 */
	public void setText(String text)
	{
		if (this.text.equals(text))
			return;
		Rectangle previous = new Rectangle(new Point(), StringUtils.getStringDimensions(this.text));
		this.text = text;
		Dimension newSize = StringUtils.getStringDimensions(this.text);
		newSize.width += textInsets.left + textInsets.right;
		newSize.height += textInsets.top + textInsets.bottom;
		if (!modifiedBounds)
		{
			super.setSize(newSize);
			modifiedBounds = false;
		}
		setNeedsDisplay(new Rectangle(new Point(), newSize).union(previous));
		//setNeedsDisplay(new Rectangle(new Point(), getSize()));
	}

	/**
	 * Setzt die Texteinrueckungen
	 * @param textInsets neue Texteinrueckungen
	 */
	public void setTextInsets(Insets textInsets)
	{
		this.textInsets = textInsets;
		Dimension newSize = StringUtils.getStringDimensions(this.text);
		newSize.width += textInsets.left + textInsets.right;
		newSize.height += textInsets.top + textInsets.bottom;
		if (!modifiedBounds)
			super.setSize(newSize);
		setNeedsDisplay();
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
	}

}
