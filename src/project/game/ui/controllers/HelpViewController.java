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
import project.gui.components.TScrollView;
import project.gui.controller.ViewController;
import project.gui.event.TEvent;
import project.gui.event.TEventHandler;
import project.gui.layout.FullSizeSubviewLayout;

import java.awt.Point;
import java.awt.event.KeyEvent;

import static project.game.localization.LocalizedString.LocalizedString;

public class HelpViewController extends ViewController
{
	@Override
	protected void initializeView()
	{
		super.initializeView();
		TScrollView scrollView = new TScrollView();
		getView().add(scrollView);

		FullSizeSubviewLayout layout = new FullSizeSubviewLayout();
		layout.setInsets(1, 2, 1, 2);
		getView().setLayoutManager(layout);

		TLabel helpLabel = new TLabel();
		helpLabel.setText(LocalizedString("help_view_help_text"));
		scrollView.add(helpLabel);

		scrollView.setAllowsFirstResponder(true);
		scrollView.setEventHandler(new TEventHandler()
		{
			@Override
			public void keyDown(final TEvent event)
			{
				if (event.getKey() == KeyEvent.VK_DOWN)
				{
					Point offset = new Point(scrollView.getOffset());
					offset.translate(0, 1);
					if (offset.y > scrollView.getContentViewSize().height - scrollView.getHeight())
						offset.y = scrollView.getContentViewSize().height - scrollView.getHeight();
					scrollView.setOffset(offset);
				}
				else if (event.getKey() == KeyEvent.VK_UP)
				{
					Point offset = new Point(scrollView.getOffset());
					offset.translate(0, -1);
					if (offset.y < 0)
						offset.y = 0;
					scrollView.setOffset(offset);
				}
			}

			@Override
			public void keyUp(final TEvent event)
			{
				if (event.getKey() == KeyEvent.VK_ESCAPE)
					getNavigationController().pop();
			}
		});
		scrollView.requestFirstResponder();
	}
}
