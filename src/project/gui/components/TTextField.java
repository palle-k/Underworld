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

import project.gui.event.TEvent;

import java.awt.event.KeyEvent;

public class TTextField extends TLabel
{
	public TTextField()
	{
		super();
		setAllowsFirstResponder(true);
	}

	@Override
	protected void keyUp(final TEvent event)
	{
		if (event.getKey() == '\b')
		{
			if (getText().length() > 0)
				setText(getText().substring(0, getText().length() - 1));
		}
		else if (event.getKey() != '\n' && event.getKey() != KeyEvent.VK_CONTROL && event.getKey() != KeyEvent.VK_ALT &&
		         event.getKey() != KeyEvent.VK_SHIFT)
			setText(getText() + Character.toString(event.getKey()));
		super.keyUp(event);
	}
}
