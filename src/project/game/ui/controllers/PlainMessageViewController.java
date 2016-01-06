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
import project.gui.layout.VerticalFlowLayout;
import project.util.StringUtils;

import java.awt.Color;

public class PlainMessageViewController extends ViewController
{
	private TLabel continuationLabel;
	private String message;
	private TLabel messageLabel;

	public String getMessage()
	{
		return message;
	}

	public void setMessage(final String message)
	{
		this.message = message;
		messageLabel.setText(message);
		messageLabel.setSize(StringUtils.getStringDimensions(message));
		getView().setNeedsLayout();
	}

	@Override
	protected void initializeView()
	{
		super.initializeView();

		messageLabel = new TLabel();
		messageLabel.setText(message);
		messageLabel.setSize(StringUtils.getStringDimensions(message));
		getView().add(messageLabel);

		continuationLabel = new TLabel();
		continuationLabel.setText("Press any key to continue");
		continuationLabel.setSize(StringUtils.getStringDimensions("Press any key to continue"));
		continuationLabel.setColor(Color.GRAY);
		getView().add(continuationLabel);

		VerticalFlowLayout layout = new VerticalFlowLayout();
		layout.setSpacing(4);
		getView().setLayoutManager(layout);
	}
}
