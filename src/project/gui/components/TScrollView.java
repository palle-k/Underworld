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

import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

public class TScrollView extends TComponent
{
	private TComponent contentView;
	private Point      offset;
	private Insets     scrollInsets;

	public TScrollView()
	{
		offset = new Point();
		contentView = new TBufferedView();
		scrollInsets = new Insets(0, 0, 0, 0);
		add(contentView);
	}

	public TScrollView(TComponent contentView)
	{
		this.contentView = contentView;
		scrollInsets = new Insets(0, 0, 0, 0);
		add(contentView);
	}

	public TComponent getContentView()
	{
		return contentView;
	}

	public Point getOffset()
	{
		return offset;
	}

	public Insets getScrollInsets()
	{
		return scrollInsets;
	}

	public void setOffset(final Point offset)
	{
		if (this.offset.equals(offset))
			return;
		this.offset = offset;
		contentView.setLocation(-offset.x + scrollInsets.left, -offset.y + scrollInsets.top);
		setNeedsDisplay(new Rectangle(new Point(), getSize()));
	}

	public void setScrollInsets(final Insets scrollInsets)
	{
		this.scrollInsets = scrollInsets;
	}

	public void setScrollInsets(int top, int left, int bottom, int right)
	{
		this.scrollInsets = new Insets(top, left, bottom, right);
	}
}
