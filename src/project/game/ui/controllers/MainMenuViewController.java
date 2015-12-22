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

import project.gui.components.TButton;
import project.gui.components.TLabel;
import project.gui.controller.ViewController;
import project.gui.event.SelectableGroup;
import project.gui.layout.VerticalFlowLayout;

public class MainMenuViewController extends ViewController
{
	@Override
	public void viewDidAppear()
	{
		super.viewDidAppear();

		TLabel label = new TLabel();
		label.setSize(64, 10);
		label.setText("           )  (           (                 )   (    (    (     \n" +
				"        ( /(  )\\ )        )\\ )  (  (     ( /(   )\\ ) )\\ ) )\\ )  \n" +
				"    (   )\\())(()/(   (   (()/(  )\\))(   ')\\()) (()/((()/((()/(  \n" +
				"    )\\ ((_)\\  /(_))  )\\   /(_))((_)()\\ )((_)\\   /(_))/(_))/(_)) \n" +
				" _ ((_) _((_)(_))_  ((_) (_))  _(())\\_)() ((_) (_)) (_)) (_))_  \n" +
				"| | | || \\| | |   \\ | __|| _ \\ \\ \\((_)/ // _ \\ | _ \\| |   |   \\ \n" +
				"| |_| || .` | | |) || _| |   /  \\ \\/\\/ /| (_) ||   /| |__ | |) |\n" +
				" \\___/ |_|\\_| |___/ |___||_|_\\   \\_/\\_/  \\___/ |_|_\\|____||___/ ");
		label.setVisible(true);
		getView().add(label);

		TButton play = new TButton();
		play.setSize(6, 1);
		play.setText("Play");
		play.setActionHandler(() -> getNavigationController().push(new LevelViewController()));
		getView().add(play);

		TButton introduction = new TButton();
		introduction.setSize(10, 1);
		introduction.setText("Tutorial");
		getView().add(introduction);

		TButton settings = new TButton();
		settings.setSize(10, 1);
		settings.setText("Settings");
		settings.setActionHandler(() -> getNavigationController().push(new SettingsViewController()));
		getView().add(settings);

		TButton quit = new TButton();
		quit.setSize(6, 1);
		quit.setText("Quit");
		quit.setActionHandler(() -> getNavigationController().getView().setVisible(false));
		getView().add(quit);

		SelectableGroup buttonGroup = new SelectableGroup();
		buttonGroup.addSelectable(play);
		buttonGroup.addSelectable(introduction);
		buttonGroup.addSelectable(settings);
		buttonGroup.addSelectable(quit);
		getView().addResponder(buttonGroup);

		VerticalFlowLayout layout = new VerticalFlowLayout();
		layout.setSpacing(2);
		layout.setHorizontalAlignment(VerticalFlowLayout.LEFT);
		layout.setVerticalAlignment(VerticalFlowLayout.TOP);
		layout.setLayoutInsets(3, 5, 0, 0);
		getView().setLayoutManager(layout);
	}

	@Override
	public void viewDidDisappear()
	{
		super.viewDidDisappear();
		getView().removeAll();
	}
}
