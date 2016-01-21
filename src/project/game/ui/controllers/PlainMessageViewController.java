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

import project.gui.components.TLabel;
import project.gui.controller.ViewController;
import project.gui.event.TEvent;
import project.gui.event.TEventHandler;
import project.gui.layout.VerticalFlowLayout;
import project.util.StringUtils;

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

import static project.game.localization.LocalizedString.LocalizedString;

/**
 * ViewController, der eine Einfache Nachricht anzeigt
 */
public class PlainMessageViewController extends ViewController
{
	private String   message;
	private TLabel   messageLabel;
	private Runnable onKeyPress;

	/**
	 * Nachricht, die angezeigt werden soll
	 *
	 * @return Anzuzeigende Nachricht
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Aktion, welche bei Tastendruck ausgefuehrt werden soll
	 * @return Tastendruckation
	 */
	public Runnable getOnKeyPress()
	{
		return onKeyPress;
	}

	/**
	 * Setzt die Nachricht, die dem Nutzer angezeigt werden soll
	 * @param message anzuzeigende Nachricht
	 */
	public void setMessage(final String message)
	{
		this.message = message;
		if (messageLabel == null)
			return;
		messageLabel.setText(message);
		messageLabel.setSize(StringUtils.getStringDimensions(message));
		getView().setNeedsLayout();
	}

	/**
	 * Setzt die Aktion, welche bei Tastendruck ausgefuehrt werden soll
	 * @param onKeyPress Tastendruckation
	 */
	public void setOnKeyPress(final Runnable onKeyPress)
	{
		this.onKeyPress = onKeyPress;
	}

	@Override
	protected void initializeView()
	{
		super.initializeView();

		messageLabel = new TLabel();
		messageLabel.setText(message);
		messageLabel.setSize(StringUtils.getStringDimensions(message));
		getView().add(messageLabel);

		TLabel continuationLabel = new TLabel();
		continuationLabel.setText(LocalizedString("plain_message_continue"));
		continuationLabel.setSize(StringUtils.getStringDimensions(LocalizedString("plain_message_continue")));
		continuationLabel.setColor(Color.GRAY);
		continuationLabel.setVisible(false);
		getView().add(continuationLabel);

		VerticalFlowLayout layout = new VerticalFlowLayout();
		layout.setSpacing(4);
		getView().setLayoutManager(layout);
		getView().setAllowsFirstResponder(true);
		getView().setSingleFirstResponder(true);
		new Timer().schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				continuationLabel.setVisible(true);
				getView().setEventHandler(new TEventHandler()
				{
					@Override
					public void keyDown(final TEvent event)
					{

					}

					@Override
					public void keyUp(final TEvent event)
					{
						if (onKeyPress != null)
							onKeyPress.run();
					}
				});
			}
		}, 1000);
	}
}
