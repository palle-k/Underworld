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

package project.gui.controller.dialog;

import project.gui.components.TComponent;
import project.gui.controller.ViewController;
import project.gui.layout.VerticalFlowLayout;

import java.awt.Color;

/**
 * Dialog-Basisklasse
 * Ein Dialog kann als ViewController in einen NavigationController gepusht werden
 * und wird beim Schliessen automatisch wieder gepopt, sofern dies nicht deaktiviert
 * wurde.
 */
public abstract class Dialog extends ViewController
{
	private DialogDelegate delegate;
	private TComponent     dialogView;

	private String message = "";

	private boolean popOnEnd;

	/**
	 * Erstelllt einen neuen Dialog
	 */
	public Dialog()
	{
		super();
		popOnEnd = true;
		setReplacesParentViewController(false);
	}

	/**
	 * Gibt das Delegate-Objekt zurueck, welches ueber Nutzeraktionen
	 * informiert wird
	 * @return DialogDelgate
	 */
	public DialogDelegate getDelegate()
	{
		return delegate;
	}

	/**
	 * Gibt die Nachricht zurueck, die dem Nutzer praesentiert werden soll
	 *
	 * @return zu prasentierende Nachricht
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Gibt an, ob der Dialog nach Bestaetigung durch den Nutzer aus der Navigationshierarchie
	 * durch eine pop-Operation entfernt werden soll.
	 * @return true, wenn der Dialog beim Schliessen aus dem aktuellen NavigationController entfernt werden
	 * soll, sonst false
	 */
	public boolean popsOnEnd()
	{
		return popOnEnd;
	}

	/**
	 * Setzt das Delegate-Objekt, welches ueber Nutzereingaben benachrichtigt wird
	 * @param delegate DialogDelegate
	 */
	public void setDelegate(final DialogDelegate delegate)
	{
		this.delegate = delegate;
	}

	/**
	 * Setzt die Nachricht, die dem Nutzer praesentiert werden soll
	 *
	 * @param message zu praesentierende Nachricht
	 */
	public void setMessage(final String message)
	{
		this.message = message;
	}

	/**
	 * Legt fest, ob der Dialog nach Bestaetigung durch den Nutzer aus der Navigationshierarchie
	 * durch eine pop-Operation entfernt werden soll.
	 * @param popOnEnd true, wenn der Dialog beim Schliessen aus dem aktuellen NavigationController entfernt werden
	 * soll, sonst false
	 */
	public void setPopOnEnd(final boolean popOnEnd)
	{
		this.popOnEnd = popOnEnd;
	}

	/**
	 * Schliesst den Dialog und benachrichtigt das DialogDelegate-Objekt, dass der Dialog abgebrochen wurde.
	 * Wenn popsOnEnd() true ist, wird der Dialog aus der Navigationshierarchie entfernt.
	 */
	protected void cancelDialog()
	{
		if (popOnEnd)
			getNavigationController().pop();
		if (getDelegate() != null)
			getDelegate().dialogDidCancel(this);
	}

	/**
	 * Gibt die Komponente an, die Dialoginhalte zeigt
	 * @return Dialoginhaltskomponente
	 */
	protected TComponent getDialogView()
	{
		return dialogView;
	}

	@Override
	protected void initializeView()
	{
		super.initializeView();
		dialogView = new TComponent();
		dialogView.setSize(50, 8);
		dialogView.setBackgroundColor(Color.LIGHT_GRAY);
		dialogView.setDrawsBackground(true);
		dialogView.setBorderColor(Color.WHITE);
		dialogView.setDrawsBorder(true);
		getView().add(dialogView);

		getView().setLayoutManager(new VerticalFlowLayout());
	}

	/**
	 * Schliesst den Dialog und benachrichtigt das DialogDelegate-Objekt, dass der Dialog bestaetigt wurde.
	 * Wenn popsOnEnd() true ist, wird der Dialog aus der Navigationshierarchie entfernt.
	 */
	protected void returnDialog()
	{
		if (popOnEnd)
			getNavigationController().pop();
		if (getDelegate() != null)
			getDelegate().dialogDidReturn(this);
	}
}
