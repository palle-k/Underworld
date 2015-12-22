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

package project.gui.graphics;

import com.googlecode.lanterna.terminal.Terminal;
import project.gui.components.TBufferedView;

import java.awt.*;
import java.awt.geom.GeneralPath;

public class TGraphics
{
	private class TGraphicsState
	{
		private Color fillBackground;
		private char fillChar;
		private Color fillColor;
		private TGraphicsState parentState;
		private GeneralPath path;
		private Color strokeBackground;
		private char strokeChar;
		private Color strokeColor;

		private TGraphicsState(
				Color fillColor,
				Color strokeColor,
				Color fillBackground,
				Color strokeBackground,
				char fillChar,
				char strokeChar,
				GeneralPath path,
				TGraphicsState parentState)
		{
			this.fillColor = fillColor;
			this.strokeColor = strokeColor;
			this.fillBackground = fillBackground;
			this.strokeBackground = strokeBackground;
			this.path = path;
			this.parentState = parentState;
			this.fillChar = fillChar;
			this.strokeChar = strokeChar;
		}

		private boolean canPop()
		{
			return parentState != null;
		}

		private TGraphicsState pop()
		{
			return parentState;
		}
	}

	private TBufferedView.TChar[][] buffer;
	private TGraphicsState currentState;
	private Rectangle dirtyRect;
	private int height;
	private boolean maskToBounds;
	private int offsetX;
	private int offsetY;
	private TGraphics parent;
	private Terminal target;
	private int width;

	public TGraphics(Terminal target, Rectangle dirtyRect)
	{
		this.target = target;
		offsetX = 0;
		offsetY = 0;
		width = target.getTerminalSize().getColumns();
		height = target.getTerminalSize().getRows();
		maskToBounds = true;
		currentState = new TGraphicsState(Color.BLACK, Color.BLACK, null, null, ' ', ' ', new GeneralPath(), null);
		this.dirtyRect = dirtyRect;
	}

	private TGraphics(TGraphics target, int offsetX, int offsetY, int width, int height, boolean maskToBounds)
	{
		this.parent = target;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.width = width;
		this.height = height;
		this.maskToBounds = maskToBounds;
		currentState = new TGraphicsState(Color.BLACK, Color.BLACK, null, null, ' ', ' ', new GeneralPath(), null);
	}

	public TGraphics(TBufferedView.TChar[][] buffer, Rectangle dirtyRect, int width, int height)
	{
		this.buffer = buffer;
		this.width = width;
		this.height = height;
		currentState = new TGraphicsState(Color.BLACK, Color.BLACK, null, null, ' ', ' ', new GeneralPath(), null);
		this.maskToBounds = true;
		this.dirtyRect = dirtyRect;
	}

	public void closePath()
	{
		currentState.path.closePath();
	}

	public void drawText(String text, int x, int y)
	{
		if (text == null)
			return;
		int baseX = x;
		char[] characters = text.toCharArray();
		for (char c : characters)
		{
			if (c == '\n')
			{
				y++;
				x = baseX;
			} else if (c == '\t')
				x += 4;
			else
			{
				setPoint(x, y, currentState.strokeColor, currentState.strokeBackground, c);
				x++;
			}
		}
	}

	public void fill(char c)
	{
		Rectangle bounds = currentState.path.getBounds();
		for (int y = 0; y < bounds.getMaxY(); y++)
		{
			for (int x = 0; x < bounds.getMaxX(); x++)
			{
				if (currentState.path.contains(x, y))
					setPoint(x, y, currentState.fillColor, currentState.fillBackground, currentState.fillChar);
			}
		}
		currentState.path.reset();
	}

	public void fillRect(Rectangle rect)
	{
		for (int y = (int) rect.getMinY(); y < rect.getMaxY(); y++)
		{
			for (int x = (int) rect.getMinX(); x < rect.getMaxX(); x++)
			{
				setPoint(x, y, currentState.fillColor, currentState.fillBackground, currentState.fillChar);
			}
		}
	}

	public TGraphics getChildContext(Rectangle childRect, boolean maskToBounds)
	{
		return new TGraphics(this, childRect.x, childRect.y, childRect.width, childRect.height, maskToBounds);
	}

	public Color getFillBackground()
	{
		return currentState.fillBackground;
	}

	public Color getFillColor()
	{
		return currentState.fillColor;
	}

	public Color getStrokeBackground()
	{
		return currentState.strokeBackground;
	}

	public Color getStrokeColor()
	{
		return currentState.strokeColor;
	}

	public void lineTo(int x, int y)
	{
		currentState.path.lineTo(x, y);
	}

	public void moveTo(int x, int y)
	{
		currentState.path.moveTo(x, y);
	}

	public void popState()
	{
		if (currentState.canPop())
			currentState = currentState.pop();
		else
			resetState();
	}

	public void pushState()
	{
		currentState = new TGraphicsState(
				currentState.fillColor,
				currentState.strokeColor,
				currentState.fillBackground,
				currentState.strokeBackground,
				currentState.fillChar,
				currentState.strokeChar,
				currentState.path,
				currentState);
	}

	public void resetState()
	{
		currentState.path.reset();
		currentState.fillColor = Color.BLACK;
		currentState.strokeColor = Color.BLACK;
		currentState.fillBackground = Color.BLACK;
		currentState.strokeBackground = Color.BLACK;
	}

	public void setFillBackground(Color fillBackground)
	{
		currentState.fillBackground = fillBackground;
	}

	public void setFillChar(char c)
	{
		currentState.fillChar = c;
	}

	public void setFillColor(Color fillColor)
	{
		this.currentState.fillColor = fillColor;
	}

	public void setPoint(int x, int y, Color color, Color backgroundColor, char c)
	{
		if (canDrawAtPoint(x, y))
		{
			if (parent != null)
				parent.setPoint(x + offsetX, y + offsetY, color, backgroundColor, c);
			if (target != null)
			{
				target.moveCursor(x, y);
				if (color != null)
					target.applyForegroundColor(color.getRed(), color.getGreen(), color.getBlue());
				else
					target.applyForegroundColor(Terminal.Color.DEFAULT);
				if (backgroundColor != null)
					target.applyBackgroundColor(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());
				else
					target.applyBackgroundColor(Terminal.Color.DEFAULT);
				target.putCharacter(c);
			}
			if (buffer != null)
			{
				buffer[x][y] = new TBufferedView.TChar(c, color, backgroundColor);
			}
		}
	}

	public void setStrokeBackground(Color strokeBackground)
	{
		currentState.strokeBackground = strokeBackground;
	}

	public void setStrokeChar(char c)
	{
		currentState.strokeChar = c;
	}

	public void setStrokeColor(Color strokeColor)
	{
		this.currentState.strokeColor = strokeColor;
	}

	public void stroke(char c)
	{
		Rectangle bounds = currentState.path.getBounds();
		boolean inside = false;
		for (int y = (int) bounds.getMinY(); y < bounds.getMaxY(); y++)
		{
			for (int x = (int) bounds.getMinX(); x < bounds.getMaxX(); x++)
			{
				boolean previous = inside;
				if (currentState.path.contains(x, y))
					inside = true;
				if (previous ^ inside)
					setPoint(x - (inside ? 0 : 1), y, currentState.strokeColor, currentState.strokeBackground, currentState.strokeChar);
			}
		}
		currentState.path.reset();
	}

	public void strokeRect(Rectangle rect)
	{
		for (int x = (int) rect.getMinX(); x < rect.getMaxX(); x++)
		{
			setPoint(x, (int) rect.getMinY(), currentState.strokeColor, currentState.strokeBackground, currentState.strokeChar);
			setPoint(x, (int) rect.getMaxY() - 1, currentState.strokeColor, currentState.strokeBackground, currentState.strokeChar);
		}
		for (int y = (int) rect.getMinY(); y < rect.getMaxY(); y++)
		{
			setPoint((int) rect.getMinX(), y, currentState.strokeColor, currentState.strokeBackground, currentState.strokeChar);
			setPoint((int) rect.getMaxX() - 1, y, currentState.strokeColor, currentState.strokeBackground, currentState.strokeChar);
		}
	}

	private boolean canDrawAtPoint(int x, int y)
	{
		if (dirtyRect != null && !dirtyRect.contains(x, y))
			return false;
		if (!maskToBounds)
			return true;
		else
		{
			return x >= 0 && x < width &&
					y >= 0 && y < height;
		}
	}
}
