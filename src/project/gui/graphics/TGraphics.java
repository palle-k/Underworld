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

package project.gui.graphics;

import com.googlecode.lanterna.terminal.Terminal;
import project.gui.components.TBufferedView;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

public class TGraphics
{
	private class TGraphicsState
	{
		private Color          fillBackground;
		private char           fillChar;
		private Color          fillColor;
		private TGraphicsState parentState;
		private GeneralPath    path;
		private Color          strokeBackground;
		private char           strokeChar;
		private Color          strokeColor;

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
	private TGraphicsState          currentState;
	private Rectangle               dirtyRect;
	private int                     height;
	private boolean[][] mask;
	private boolean                 maskToBounds;
	private int                     offsetX;
	private int                     offsetY;
	private TGraphics               parent;
	private Terminal                target;
	private int                     width;

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
		int    baseX      = x;
		char[] characters = text.toCharArray();
		for (char c : characters)
		{
			if (c == '\n')
			{
				y++;
				x = baseX;
			}
			else if (c == '\t')
				x += 4 - (x % 4);
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

	public boolean[][] getMask()
	{
		return mask;
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

	public TBufferedView.TChar queryPoint(int x, int y)
	{
		if (buffer != null)
			return buffer[x][y];
		if (parent != null)
			return parent.queryPoint(x + offsetX, y + offsetY);
		return null;
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

	public void setMask(final boolean[][] mask)
	{
		this.mask = mask;
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
					target.applyBackgroundColor(
							backgroundColor.getRed(),
							backgroundColor.getGreen(),
							backgroundColor.getBlue());
				else
					target.applyBackgroundColor(Terminal.Color.DEFAULT);
				target.putCharacter(c);
			}
			if (buffer != null)
			{
				TBufferedView.TChar current = queryPoint(x, y);
				if (backgroundColor == null)
					backgroundColor = current.getBackgroundColor();
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
		/*Rectangle bounds = currentState.path.getBounds();
		boolean previous = false;
		for (int y = (int) bounds.getMinY(); y < bounds.getMaxY(); y++)
		{
			for (int x = (int) bounds.getMinX(); x < bounds.getMaxX(); x++)
			{
				boolean   inside = currentState.path.contains(x, y);
				if (previous ^ inside)
					setPoint(
							x - (inside ? 0 : 1),
							y,
							currentState.strokeColor,
							currentState.strokeBackground,
							currentState.strokeChar);
				previous = inside;
			}
		}*/
		double[] previousCords = null;
		double[] firstCords = null;
		for (PathIterator iterator = currentState.path.getPathIterator(null); !iterator.isDone(); iterator.next())
		{
			double[] cords = new double[6];
			int      type  = iterator.currentSegment(cords);
			switch (type)
			{
				case PathIterator.SEG_MOVETO:
					previousCords = cords;
					if (firstCords == null)
						firstCords = cords;
					break;
				case PathIterator.SEG_LINETO:
					if (previousCords != null)
					{
						double dx   = previousCords[0] - cords[0];
						double dy   = previousCords[1] - cords[1];
						double dist = Math.sqrt(dx * dx + dy * dy);
						dx /= dist;
						dy /= dist;
						for (int i = 0; i <= dist; i++)
						{
							int x = (int) (dx * i + cords[0]);
							int y = (int) (dy * i + cords[1]);
							setPoint(
									x,
									y,
									currentState.strokeColor,
									currentState.strokeBackground,
									currentState.strokeChar);
						}
					}
					previousCords = cords;
					if (firstCords == null)
						firstCords = cords;
					break;
				case PathIterator.SEG_CUBICTO:
					throw new RuntimeException("Cubic bezier curves not yet implemented");
				case PathIterator.SEG_QUADTO:
					throw new RuntimeException("quad bezier curves not yet implemented.");
				case PathIterator.SEG_CLOSE:
					if (previousCords != null)
					{
						double dx   = previousCords[0] - firstCords[0];
						double dy   = previousCords[1] - firstCords[1];
						double dist = Math.sqrt(dx * dx + dy * dy);
						dx /= dist;
						dy /= dist;
						for (int i = 0; i <= dist; i++)
						{
							int x = (int) (dx * i + firstCords[0]);
							int y = (int) (dy * i + firstCords[1]);
							setPoint(
									x,
									y,
									currentState.strokeColor,
									currentState.strokeBackground,
									currentState.strokeChar);
						}
					}
					firstCords = null;
					previousCords = null;
					break;
				default:
					break;
			}
		}
		currentState.path.reset();
	}

	public void strokeRect(Rectangle rect)
	{
		for (int x = (int) rect.getMinX(); x < rect.getMaxX(); x++)
		{
			setPoint(
					x,
					(int) rect.getMinY(),
					currentState.strokeColor,
					currentState.strokeBackground,
					currentState.strokeChar);
			setPoint(
					x,
					(int) rect.getMaxY() - 1,
					currentState.strokeColor,
					currentState.strokeBackground,
					currentState.strokeChar);
		}
		for (int y = (int) rect.getMinY(); y < rect.getMaxY(); y++)
		{
			setPoint(
					(int) rect.getMinX(),
					y,
					currentState.strokeColor,
					currentState.strokeBackground,
					currentState.strokeChar);
			setPoint(
					(int) rect.getMaxX() - 1,
					y,
					currentState.strokeColor,
					currentState.strokeBackground,
					currentState.strokeChar);
		}
	}

	private boolean canDrawAtPoint(int x, int y)
	{
		return !(dirtyRect != null && !dirtyRect.contains(x, y)) &&
		       (!maskToBounds || x >= 0 && x < width && y >= 0 && y < height) && (mask == null || mask.length <= x || mask[x].length <= y || mask[x][y]);
	}
}
