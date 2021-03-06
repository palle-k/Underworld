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

package project.game.ui.controllers;

import project.gui.controller.NavigationController;
import project.gui.controller.dialog.Dialog;
import project.gui.controller.dialog.DialogDelegate;
import project.gui.controller.dialog.SingleKeyInputDialog;

import static project.game.localization.LocalizedString.LocalizedString;

/**
 * Klasse zur Darstellung des Dialogs fuer eine Tastenzuweisung
 */
public class ControlSettingsChange implements DialogDelegate, Runnable
{
	private NavigationController  navigationController;
	private ControlSettingsUpdate onReturn;

	/**
	 * Erstellt einen neuen Controller zur Darstellung eines Dialogs zur Wahl einer Taste
	 *
	 * @param navigationController NavigationController, auf welchen der Dialog gepusht werden soll
	 * @param onReturn             Aktion bei erfolgreicher Wahl einer Tastenzuweisung
	 */
	public ControlSettingsChange(NavigationController navigationController, final ControlSettingsUpdate onReturn)
	{
		this.navigationController = navigationController;
		this.onReturn = onReturn;
	}

	@Override
	public void dialogDidCancel(final Dialog dialog)
	{

	}

	@Override
	public void dialogDidReturn(final Dialog dialog)
	{
		if (onReturn == null)
			return;
		onReturn.updateKey(((SingleKeyInputDialog) dialog).getChosenKey());
	}

	@Override
	public void run()
	{
		SingleKeyInputDialog dialog = new SingleKeyInputDialog();
		dialog.setInputMessage(LocalizedString("single_key_input_message"));
		dialog.setDelegate(this);
		navigationController.push(dialog);
	}
}
