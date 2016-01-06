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

package project.gui.layout;

import project.gui.components.TComponent;

import java.awt.Insets;

public class HorizontalFlowLayout implements TLayoutManager
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

	public static final VerticalAlignment   BOTTOM              = VerticalAlignment.BOTTOM;
	public static final HorizontalAlignment CENTER              = HorizontalAlignment.CENTER;
	public static final HorizontalAlignment LEFT                = HorizontalAlignment.LEFT;
	public static final VerticalAlignment   MIDDLE              = VerticalAlignment.MIDDLE;
	public static final HorizontalAlignment RIGHT               = HorizontalAlignment.RIGHT;
	public static final VerticalAlignment   TOP                 = VerticalAlignment.TOP;
	private             HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;
	private             Insets              layoutInsets        = new Insets(0, 0, 0, 0);
	private int spacing;
	private VerticalAlignment verticalAlignment = VerticalAlignment.MIDDLE;

	public HorizontalAlignment getHorizontalAlignment()
	{
		return horizontalAlignment;
	}

	public Insets getLayoutInsets()
	{
		return layoutInsets;
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
		int totalWidth = layoutInsets.left + layoutInsets.right;

		for (TComponent child : component.getChildren())
			totalWidth += child.getWidth() + spacing;
		int currenPosX = layoutInsets.left;
		if (horizontalAlignment == CENTER)
			currenPosX = (component.getWidth() - totalWidth) / 2 + layoutInsets.left;
		else if (horizontalAlignment == RIGHT)
			currenPosX = totalWidth - component.getWidth() - layoutInsets.left;
		int componentHeight = component.getHeight() - layoutInsets.top - layoutInsets.bottom;
		for (TComponent child : component.getChildren())
		{
			if (verticalAlignment == TOP)
				child.setPosY(layoutInsets.top);
			else if (verticalAlignment == BOTTOM)
				child.setPosY(componentHeight - layoutInsets.bottom - child.getHeight());
			else if (verticalAlignment == MIDDLE)
				child.setPosY((componentHeight - child.getHeight()) / 2 + layoutInsets.top);
			child.setPosX(currenPosX);
			currenPosX += child.getWidth() + spacing;
		}
	}

	public void setHorizontalAlignment(final HorizontalAlignment horizontalAlignment)
	{
		this.horizontalAlignment = horizontalAlignment;
	}

	public void setLayoutInsets(final Insets layoutInsets)
	{
		this.layoutInsets = layoutInsets;
	}

	public void setLayoutInsets(int top, int left, int bottom, int right)
	{
		layoutInsets.top = top;
		layoutInsets.left = left;
		layoutInsets.right = right;
		layoutInsets.bottom = bottom;
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
