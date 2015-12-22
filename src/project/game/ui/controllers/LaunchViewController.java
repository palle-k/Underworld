/******************************************************************************
 * Copyright (c) 2015 Palle Klewitz.                                          *
 * *
 * Permission is hereby granted, free of charge, to any person obtaining      *
 * a copy of this software and associated documentation files                 *
 * (the "Software"), to deal in the Software without restriction,             *
 * including without limitation the rights to use, copy, modify,             *
 * merge, publish, distribute, sublicense, and/or sell copies of             *
 * the Software, and to permit persons to whom the Software                  *
 * is furnished to do so, subject to the following conditions:               *
 * *
 * The above copyright notice and this permission notice shall                *
 * be included in all copies or substantial portions of the Software.         *
 * *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY                         *
 * OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT                        *
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS                     *
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.                             *
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS                        *
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,                      *
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,                      *
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE                            *
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.                    *
 ******************************************************************************/

package project.game.ui.controllers;

import project.gui.components.TLabel;
import project.gui.controller.ViewController;
import project.gui.event.TEvent;
import project.gui.event.TEventHandler;
import project.gui.layout.VerticalFlowLayout;

import java.awt.*;

public class LaunchViewController extends ViewController
{

	@Override
	public void viewDidAppear()
	{
		super.viewDidAppear();

		VerticalFlowLayout layout = new VerticalFlowLayout();
		layout.setSpacing(5);
		getView().setLayoutManager(layout);

		Color backgroundColor = new Color(30, 30, 30);

		TLabel label = new TLabel();
		label.setSize(64, 8);
		label.setText("           )  (           (                 )   (    (    (     \n" +
				"        ( /(  )\\ )        )\\ )  (  (     ( /(   )\\ ) )\\ ) )\\ )  \n" +
				"    (   )\\())(()/(   (   (()/(  )\\))(   ')\\()) (()/((()/((()/(  \n" +
				"    )\\ ((_)\\  /(_))  )\\   /(_))((_)()\\ )((_)\\   /(_))/(_))/(_)) \n" +
				" _ ((_) _((_)(_))_  ((_) (_))  _(())\\_)() ((_) (_)) (_)) (_))_  \n" +
				"| | | || \\| | |   \\ | __|| _ \\ \\ \\((_)/ // _ \\ | _ \\| |   |   \\ \n" +
				"| |_| || .` | | |) || _| |   /  \\ \\/\\/ /| (_) ||   /| |__ | |) |\n" +
				" \\___/ |_|\\_| |___/ |___||_|_\\   \\_/\\_/  \\___/ |_|_\\|____||___/ ");
		getView().add(label);

		TLabel line2 = new TLabel();
		line2.setSize(28, 1);
		line2.setText("Press any key to continue...");
		getView().add(line2);

		TLabel copyright = new TLabel();
		copyright.setSize(31, 1);
		copyright.setText("v1.0.0 - (c) Palle Klewitz 2015");
		copyright.setColor(Color.LIGHT_GRAY);
		getView().add(copyright);

		getView().setAllowsFirstResponder(true);
		getView().setEventHandler(new TEventHandler()
		{
			@Override
			public void keyDown(final TEvent event)
			{
			}

			@Override
			public void keyUp(final TEvent event)
			{
				getNavigationController().push(new MainMenuViewController());
			}
		});
	}

	@Override
	public void viewDidDisappear()
	{
		super.viewDidDisappear();
		getView().removeAll();
	}
}
