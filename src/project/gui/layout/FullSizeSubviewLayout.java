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

import java.awt.Dimension;
import java.awt.Insets;

public class FullSizeSubviewLayout implements TLayoutManager
{
	private Insets insets = new Insets(0, 0, 0, 0);

	public Insets getInsets()
	{
		return insets;
	}

	@Override
	public void layoutComponent(final TComponent component)
	{
		Dimension size = new Dimension(component.getSize());
		size.setSize(size.width - insets.left - insets.right, size.height - insets.top - insets.bottom);
		for (TComponent child : component.getChildren())
		{
			child.setLocation(insets.left, insets.top);
			child.setSize(size);
		}
	}

	public void setInsets(final Insets insets)
	{
		this.insets = insets;
	}

	public void setInsets(int top, int left, int bottom, int right)
	{
		setInsets(new Insets(top, left, bottom, right));
	}
}
