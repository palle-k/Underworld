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

package project.game.ui.views;

import project.game.data.Level;
import project.gui.components.TComponent;
import project.gui.graphics.TGraphics;

import java.awt.*;

public class LevelView extends TComponent
{
	private Level level;

	public Level getLevel()
	{
		return level;
	}

	public void setLevel(final Level level)
	{
		this.level = level;
	}

	@Override
	protected void paintComponent(final TGraphics graphics)
	{
		super.paintComponent(graphics);
		for (int x = 0; x < getWidth(); x++)
			for (int y = 0; y < getHeight(); y++)
			{
				int pixel = level.getPixel(x / 4, y / 2);
				if (pixel == 0)
					graphics.setPoint(x, y, Color.WHITE, Color.WHITE, ' ');
			}
	}
}
