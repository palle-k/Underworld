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

package project.gui.layout;

import project.gui.components.TComponent;

public class VerticalFlowLayout implements TLayoutManager
{
	private enum HorizontalAlignment
	{
		LEFT,
		CENTER,
		RIGHT
	}

	private enum VerticalAlignment
	{
		TOP,
		MIDDLE,
		BOTTOM
	}

	public static final VerticalAlignment BOTTOM = VerticalAlignment.BOTTOM;
	public static final HorizontalAlignment CENTER = HorizontalAlignment.CENTER;
	public static final HorizontalAlignment LEFT = HorizontalAlignment.LEFT;
	public static final VerticalAlignment MIDDLE = VerticalAlignment.MIDDLE;
	public static final HorizontalAlignment RIGHT = HorizontalAlignment.RIGHT;
	public static final VerticalAlignment TOP = VerticalAlignment.TOP;
	private HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;
	private int spacing;
	private VerticalAlignment verticalAlignment = VerticalAlignment.MIDDLE;

	public HorizontalAlignment getHorizontalAlignment()
	{
		return horizontalAlignment;
	}

	public int getSpacing()
	{
		return spacing;
	}

	public VerticalAlignment getVerticalAlignment()
	{
		return verticalAlignment;
	}

	@Override
	public void layoutComponent(final TComponent component)
	{
		int totalHeight = 0;
		for (TComponent child : component.getChildren())
			totalHeight += child.getHeight() + spacing;
		int currentPosY = 0;
		if (verticalAlignment == MIDDLE)
			currentPosY = (component.getHeight() - totalHeight) / 2;
		else if (verticalAlignment == BOTTOM)
			currentPosY = component.getHeight() - totalHeight;
		int componentWidth = component.getWidth();
		for (TComponent child : component.getChildren())
		{
			if (horizontalAlignment == LEFT)
				child.setPosX(0);
			else if (horizontalAlignment == RIGHT)
				child.setPosX(componentWidth - child.getWidth());
			else if (horizontalAlignment == CENTER)
				child.setPosX((componentWidth - child.getWidth()) / 2);
			child.setPosY(currentPosY);
			currentPosY += child.getHeight() + spacing;
		}
	}

	public void setHorizontalAlignment(final HorizontalAlignment horizontalAlignment)
	{
		this.horizontalAlignment = horizontalAlignment;
	}

	public void setSpacing(final int spacing)
	{
		this.spacing = Math.max(spacing, 0);
	}

	public void setVerticalAlignment(final VerticalAlignment verticalAlignment)
	{
		this.verticalAlignment = verticalAlignment;
	}
}
