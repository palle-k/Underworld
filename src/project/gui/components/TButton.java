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

package project.gui.components;

import project.gui.event.Selectable;

public class TButton extends TLabel implements Selectable
{
	private enum ButtonStyle
	{
		LIGHT_LEFT_INDICATOR,
		BACKGROUND_INDICATOR,
		BORDER_INDICATOR,
		BACKGROUND_BORDER_INDICATOR
	}

	public static final ButtonStyle BACKGROUND_BORDER_INDICATOR = ButtonStyle.BACKGROUND_BORDER_INDICATOR;
	public static final ButtonStyle BACKGROUND_INDICATOR = ButtonStyle.BACKGROUND_INDICATOR;
	public static final ButtonStyle BORDER_INDICATOR = ButtonStyle.BORDER_INDICATOR;
	public static final ButtonStyle LIGHT_LEFT_INDICATOR = ButtonStyle.LIGHT_LEFT_INDICATOR;
	private Runnable actionHandler;
	private String originalText;
	private boolean selected;
	private boolean selectionEnabled;
	private Runnable selectionHandler;

	public TButton()
	{
		super();
		selectionEnabled = true;
	}

	@Override
	public void deselect()
	{
		selected = false;
		setText(originalText);
	}

	public Runnable getActionHandler()
	{
		return actionHandler;
	}

	public Runnable getSelectionHandler()
	{
		return selectionHandler;
	}

	@Override
	public boolean isSelected()
	{
		return selected;
	}

	@Override
	public void performAction()
	{
		if (actionHandler != null)
			actionHandler.run();
	}

	@Override
	public void select()
	{
		selected = true;
		setText(originalText);
		if (selectionHandler != null)
			selectionHandler.run();
	}

	@Override
	public boolean selectionEnabled()
	{
		return selectionEnabled;
	}

	public void setActionHandler(final Runnable actionHandler)
	{
		this.actionHandler = actionHandler;
	}

	public void setSelectionEnabled(final boolean selectionEnabled)
	{
		this.selectionEnabled = selectionEnabled;
	}

	public void setSelectionHandler(final Runnable selectionHandler)
	{
		this.selectionHandler = selectionHandler;
	}

	@Override
	public void setText(final String text)
	{
		originalText = text;
		super.setText((isSelected() ? "> " : "  ") + text);
	}
}
