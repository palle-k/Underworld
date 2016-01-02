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

import java.awt.*;

public class Dialog extends ViewController
{
	private DialogDelegate delegate;
	private TComponent     dialogView;

	public Dialog()
	{
		super();
		setReplacesParentViewController(false);
	}

	public DialogDelegate getDelegate()
	{
		return delegate;
	}

	@Override
	public void initializeView()
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

	public void setDelegate(final DialogDelegate delegate)
	{
		this.delegate = delegate;
	}

	protected TComponent getDialogView()
	{
		return dialogView;
	}
}
