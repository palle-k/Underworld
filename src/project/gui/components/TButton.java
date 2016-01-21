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

import project.gui.event.Selectable;

/**
 * Button:
 * Kann ueber SelectableGroup angewaehlt und aufgerufen werden.
 */
public class TButton extends TLabel implements Selectable
{
	private Runnable actionHandler;
	private String   originalText;
	private boolean  selected;
	private boolean  selectionEnabled;
	private Runnable selectionHandler;

	/**
	 * Erzeugt einen neuen Button
	 */
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

	/**
	 * Runnable-Objekt, welches beim Aufrufen eines Buttons ausgefuehrt wird.
	 * Dies ist der Fall, wenn der Nutzer die Enter-Taste drueckt.
	 * @return Aktionshandler
	 */
	public Runnable getActionHandler()
	{
		return actionHandler;
	}

	/**
	 * Runnable-Objekt, welches beim Auswaehlen eines Buttons ausgefuehrt wird
	 * @return Selektionshandler
	 */
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

	/**
	 * Setzt das Runnable-Objekt, welches beim Druecken der Enter-Taste ausgefuehrt wird
	 * @param actionHandler Aktionshandler
	 */
	public void setActionHandler(final Runnable actionHandler)
	{
		this.actionHandler = actionHandler;
	}

	/**
	 * Setzt, ob die Auswahl des Buttons moeglich ist
	 * @param selectionEnabled true, wenn die Auswahl moeglich sein soll, sonst false
	 */
	public void setSelectionEnabled(final boolean selectionEnabled)
	{
		this.selectionEnabled = selectionEnabled;
	}

	/**
	 * Setzt das Runnable-Objekt, welches bei Auswahl des Buttons ausgefuehrt werden soll
	 * @param selectionHandler Selektionshandler
	 */
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
