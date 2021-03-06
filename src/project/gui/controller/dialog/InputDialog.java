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
import project.gui.components.TTextField;
import project.gui.event.TEvent;
import project.gui.event.TEventHandler;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

/**
 * Dialog zur Eingabe von Text
 */
public class InputDialog extends Dialog
{
	private TLabel inputMessageLabel;
	private String response;

	public String getResponse()
	{
		return response;
	}

	@Override
	public void setMessage(final String message)
	{
		super.setMessage(message);
		if (inputMessageLabel != null)
			inputMessageLabel.setText(message);
	}

	@Override
	protected void initializeView()
	{
		super.initializeView();

		inputMessageLabel = new TLabel();
		inputMessageLabel.setFrame(new Rectangle(2, 2, 46, 2));
		inputMessageLabel.setColor(Color.BLACK);
		inputMessageLabel.setBackgroundColor(Color.LIGHT_GRAY);
		inputMessageLabel.setText(getMessage());
		getDialogView().add(inputMessageLabel);

		TTextField inputField = new TTextField();
		inputField.setFrame(new Rectangle(2, 5, 46, 1));
		inputField.setBackgroundColor(Color.WHITE);
		inputField.setDrawsBackground(true);
		inputField.setColor(Color.BLACK);
		inputField.setSingleFirstResponder(true);

		inputField.setEventHandler(new TEventHandler()
		{
			@Override
			public void keyDown(final TEvent event)
			{

			}

			@Override
			public void keyUp(final TEvent event)
			{
				if (event.getKey() == KeyEvent.VK_ENTER)
				{
					response = inputField.getText();
					returnDialog();
				}
				else if (event.getKey() == KeyEvent.VK_ESCAPE)
					cancelDialog();
			}
		});
		getDialogView().add(inputField);
		inputField.requestFirstResponder();
	}
}
