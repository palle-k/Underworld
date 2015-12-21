/******************************************************************************
 * Copyright (c) 2015 Palle Klewitz.                                          *
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

package project.gui;

import java.awt.*;

public class TBufferedView extends TComponent
{
	static class TChar
	{
		private final char character;
		private final Color color;
		private final Color backgroundColor;

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
				if (character != other.character)
					return false;
				if (color == null && other.color != null)
					return false;
				if (color != null && other.color == null)
					return false;
				if (color != null && other.color != null && !color.equals(other.color))
					return false;
				if (backgroundColor == null && other.backgroundColor != null)
					return false;
				if (backgroundColor != null && other.backgroundColor == null)
					return false;
				if (backgroundColor != null && other.backgroundColor != null && !backgroundColor.equals(other.backgroundColor))
					return false;
				return true;
			}
			return super.equals(obj);
		}
	}

	private TChar[][] backBuffer;
	private TChar[][] frameBuffer;

	public TBufferedView()
	{
		backBuffer = new TChar[0][0];
		frameBuffer = new TChar[0][0];
	}

	@Override
	void dispatchRepaint(TGraphics graphics, Rectangle dirtyRect)
	{
		validateBuffers();
		dirtyRect = dirtyRect.intersection(new Rectangle(new Point(), getSize()));
		for (int x = 0; x < dirtyRect.width; x++)
		{
			for (int y = 0; y < dirtyRect.height; y++)
			{
				backBuffer[x+dirtyRect.x][y+dirtyRect.y] = null;
			}
		}
		TGraphics bufferedGraphics = new TGraphics(backBuffer, getWidth(), getHeight());
		paintComponent(bufferedGraphics);
		resetNeedsDisplay();
		for (TComponent child : getChildren())
		{
			Rectangle r = new Rectangle(dirtyRect);
			if (child.masksToBounds())
				r = r.intersection(child.getFrame());
			r.translate(-child.getLocation().x, -child.getLocation().y);
			child.dispatchRepaint(bufferedGraphics.getChildContext(child.getFrame(), child.masksToBounds()), r);
		}
		swapBuffers(graphics);
	}

	private void validateBuffers()
	{
		if (backBuffer.length != frame.width || backBuffer[0].length != frame.height)
		{
			backBuffer = new TChar[frame.width][frame.height];
			needsDisplay = true;
		}
		if (frameBuffer.length != frame.width || frameBuffer[0].length != frame.height)
		{
			frameBuffer = new TChar[getWidth()][getHeight()];
			needsDisplay = true;
		}
	}

	private void swapBuffers(TGraphics graphics)
	{
		for (int y = 0; y < getHeight(); y++)
		{
			for (int x = 0; x < getWidth(); x++)
			{
				if (backBuffer[x][y] != null && (frameBuffer[x][y] == null || !frameBuffer[x][y].equals(backBuffer[x][y])))
				{
					frameBuffer[x][y] = backBuffer[x][y];
					graphics.setPoint(x, y, backBuffer[x][y].color, backBuffer[x][y].backgroundColor, backBuffer[x][y].character);
				}
				else if (backBuffer[x][y] == null && frameBuffer[x][y] != null)
				{
					frameBuffer[x][y] = backBuffer[x][y];
					graphics.setPoint(x, y, getBackgroundColor(), getBackgroundColor(), ' ');
				}
			}
		}
	}
}
