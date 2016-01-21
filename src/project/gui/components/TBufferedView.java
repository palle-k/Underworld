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

import project.gui.graphics.TGraphics;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * Gepufferte Komponente<br>
 * Erweiterung einer Standardkomponente,
 * bei welcher lediglich geaenderte Punkte in die hoehere Komponente
 * gezeichnet werden<br>
 * WARNUNG:
 * Eine gepufferte Komponente sollte moeglichst lediglich als oberste Komponente
 * in einer Komponentenhierarchie verwendet werden oder vor dem Neuzeichnen
 * ist die Methode clearFramebuffer() aufzurufen.
 */
public class TBufferedView extends TComponent
{
	/**
	 * Pufferzeichen<br>
	 * Ein einzelnes Zeichen mit Farbattributen
	 */
	public static class TChar
	{
		private final Color backgroundColor;
		private final char  character;
		private final Color color;

		/**
		 * Erzeugt ein neues Pufferzeichen
		 * @param character Zeichen
		 * @param color Zeichenfarbe
		 * @param backgroundColor Zeichenhintergrundfarbe
		 */
		public TChar(char character, Color color, Color backgroundColor)
		{
			this.character = character;
			this.color = color;
			this.backgroundColor = backgroundColor;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj == null)
				return false;
			if (obj instanceof TChar)
			{
				TChar other = (TChar) obj;
				return character == other.character && !(color == null && other.color != null) &&
				       !(color != null && other.color == null) && !(color != null && !color.equals(other.color)) &&
				       !(backgroundColor == null && other.backgroundColor != null) &&
				       !(backgroundColor != null && other.backgroundColor == null) &&
				       !(backgroundColor != null && !backgroundColor.equals(other.backgroundColor));
			}
			return super.equals(obj);
		}

		/**
		 * Gibt die Hintergrundfarbe des Zeichens an
		 * @return Zeichenhintergrundfarbe
		 */
		public Color getBackgroundColor()
		{
			return backgroundColor;
		}

		/**
		 * Gibt das Zeichen an
		 * @return Zeichen
		 */
		public char getCharacter()
		{
			return character;
		}

		/**
		 * Gibt die Zeichenfarbe an
		 * @return Zeichenfarbe
		 */
		public Color getColor()
		{
			return color;
		}

		@Override
		public String toString()
		{
			return "TChar: " + character + ", " + color + ", " + backgroundColor;
		}
	}

	private TChar[][] backBuffer;
	private TChar[][] frameBuffer;

	/**
	 * Erzeugt eine neue, gepufferte Komponente
	 */
	public TBufferedView()
	{
		super();
		backBuffer = new TChar[0][0];
		frameBuffer = new TChar[0][0];
	}

	/**
	 * Entleert den Framebuffer.
	 * Nach der Entleerung muessen alle Zeichen neu gerendert werden
	 */
	protected void clearFramebuffer()
	{
		for (int x = 0; x < frameBuffer.length; x++)
			for (int y = 0; y < frameBuffer[x].length; y++)
				frameBuffer[x][y] = null;
	}

	@Override
	void dispatchRepaint(TGraphics graphics, Rectangle dirtyRect)
	{
		boolean redrawAll = validateBuffers();
		if (redrawAll)
			dirtyRect = new Rectangle(new Point(), getSize());
		else
			dirtyRect = dirtyRect.intersection(new Rectangle(new Point(), getSize()));

		TGraphics bufferedGraphics = new TGraphics(backBuffer, dirtyRect, getWidth(), getHeight());
		paintComponent(bufferedGraphics, dirtyRect);
		resetNeedsDisplay();
		paintChildren(bufferedGraphics, dirtyRect);
		updateFramebuffer(graphics, dirtyRect);
	}

	/**
	 * Aktualisiert den Framebuffer und zeichnet geaenderte Punkte in den Grafikkontext
	 * @param graphics Zielkontext fuer geaenderte Zeichen
	 * @param dirtyRect geaenderter Bereich
	 */
	private void updateFramebuffer(TGraphics graphics, Rectangle dirtyRect)
	{
		for (int y = dirtyRect.y; y < dirtyRect.y + dirtyRect.height; y++)
			for (int x = dirtyRect.x; x < dirtyRect.x + dirtyRect.width; x++)
				if (backBuffer[x][y] != null &&
				    (frameBuffer[x][y] == null || !frameBuffer[x][y].equals(backBuffer[x][y])))
				{
					frameBuffer[x][y] = backBuffer[x][y];
					graphics.setPoint(
							x,
							y,
							backBuffer[x][y].color,
							backBuffer[x][y].backgroundColor,
							backBuffer[x][y].character);
				}
				else if (backBuffer[x][y] == null && frameBuffer[x][y] != null)
				{
					frameBuffer[x][y] = backBuffer[x][y];
					graphics.setPoint(x, y, getBackgroundColor(), getBackgroundColor(), ' ');
				}
	}

	/**
	 * Ueberprueft die Puffer und aktualisiert diese, falls noetig
	 * @return true, wenn die Puffer geaendert wurden, sonst false
	 */
	private boolean validateBuffers()
	{
		boolean bufferUpdated = false;
		if (backBuffer == null)
		{
			backBuffer = new TChar[frame.width][frame.height];
			bufferUpdated = true;
		}
		else if (backBuffer.length != frame.width || backBuffer[0].length != frame.height)
		{
			backBuffer = new TChar[frame.width][frame.height];
			bufferUpdated = true;
		}
		if (frameBuffer == null)
		{
			frameBuffer = new TChar[frame.width][frame.height];
			bufferUpdated = true;
		}
		else if (frameBuffer.length != frame.width || frameBuffer[0].length != frame.height)
		{
			frameBuffer = new TChar[getWidth()][getHeight()];
			bufferUpdated = true;
		}
		return bufferUpdated;
	}
}
