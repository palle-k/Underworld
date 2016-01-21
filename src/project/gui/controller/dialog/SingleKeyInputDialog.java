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

import project.gui.components.TLabel;
import project.gui.event.TEvent;
import project.gui.event.TEventHandler;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

/**
 * Dialog zur Wahl einer Taste
 */
public class SingleKeyInputDialog extends Dialog
{
	private int chosenKey;
	private String inputMessage = "";
	private TLabel messageLabel;

	public int getChosenKey()
	{
		return chosenKey;
	}

	public String getInputMessage()
	{
		return inputMessage;
	}

	public void setInputMessage(final String inputMessage)
	{
		this.inputMessage = inputMessage;
		if (messageLabel != null)
			messageLabel.setText(inputMessage);
	}

	@Override
	protected void initializeView()
	{
		super.initializeView();

		messageLabel = new TLabel();
		messageLabel.setText(inputMessage);
		messageLabel.setFrame(new Rectangle(2, 2, 46, 4));
		messageLabel.setBackgroundColor(Color.LIGHT_GRAY);
		getDialogView().add(messageLabel);

		getDialogView().setAllowsFirstResponder(true);
		getDialogView().requestFirstResponder();
		getDialogView().setSingleFirstResponder(true);
		getDialogView().setEventHandler(new TEventHandler()
		{
			@Override
			public void keyDown(final TEvent event)
			{

			}

			@Override
			public void keyUp(final TEvent event)
			{
				if (event.getKey() == KeyEvent.VK_ESCAPE)
					cancelDialog();
				else if (event.getKey() != KeyEvent.VK_ENTER && event.getKey() >= 0x20 && event.getKey() < 128)
				{
					chosenKey = event.getKey();
					returnDialog();
				}
			}
		});
	}
}
