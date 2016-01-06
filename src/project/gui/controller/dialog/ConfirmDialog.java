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

import project.gui.components.TButton;
import project.gui.components.TLabel;
import project.gui.event.SelectableGroup;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import static project.game.localization.LocalizedString.LocalizedString;

public class ConfirmDialog extends Dialog
{
	private String message = "";
	private TLabel messageLabel;
	
	public String getMessage()
	{
		return message;
	}

	public void setMessage(final String message)
	{
		this.message = message;
		if (messageLabel != null)
			messageLabel.setText(message);
	}

	@Override
	protected void initializeView()
	{
		super.initializeView();
		Dialog self = this;

		messageLabel = new TLabel();
		messageLabel.setFrame(new Rectangle(2, 2, 46, 2));
		messageLabel.setColor(Color.BLACK);
		messageLabel.setBackgroundColor(Color.LIGHT_GRAY);
		messageLabel.setText(getMessage());
		getDialogView().add(messageLabel);

		TButton confirmButton = new TButton();
		confirmButton.setText(LocalizedString("confirm_dialog_confirm"));
		confirmButton.setFrame(new Rectangle(2, 5, 22, 1));
		confirmButton.setActionHandler(() -> returnDialog());
		confirmButton.setBackgroundColor(Color.LIGHT_GRAY);
		confirmButton.setDrawsBackground(true);
		confirmButton.setColor(Color.BLACK);
		getDialogView().add(confirmButton);

		TButton cancelButton = new TButton();
		cancelButton.setText(LocalizedString("confirm_dialog_decline"));
		cancelButton.setFrame(new Rectangle(26, 5, 22, 1));
		cancelButton.setActionHandler(() -> cancelDialog());
		cancelButton.setBackgroundColor(Color.LIGHT_GRAY);
		cancelButton.setDrawsBackground(true);
		cancelButton.setColor(Color.BLACK);
		getDialogView().add(cancelButton);

		SelectableGroup buttonGroup = new SelectableGroup();
		buttonGroup.addResponder(confirmButton);
		buttonGroup.addResponder(cancelButton);
		buttonGroup.setBackwardsKey((char) KeyEvent.VK_LEFT);
		buttonGroup.setForwardsKey((char) KeyEvent.VK_RIGHT);
		getDialogView().addResponder(buttonGroup);
		buttonGroup.setSingleFirstResponder(true);
	}
}
