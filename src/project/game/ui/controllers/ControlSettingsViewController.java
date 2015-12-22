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

public class ControlSettingsViewController extends ViewController
{
	@Override
	public void viewDidAppear()
	{
		super.viewDidAppear();

		TLabel label = new TLabel();
		label.setSize(54, 10);
		label.setText("  ___ ___  _  _ _____ ___  ___  _    ___ \n" +
				" / __/ _ \\| \\| |_   _| _ \\/ _ \\| |  / __|\n" +
				"| (_| (_) | .` | | | |   / (_) | |__\\__ \\\n" +
				" \\___\\___/|_|\\_| |_| |_|_\\\\___/|____|___/\n");
		getView().add(label);

		TButton back = new TButton();
		back.setSize(6, 1);
		back.setText("Back");
		back.setActionHandler(() -> getNavigationController().pop());
		getView().add(back);

		SelectableGroup buttonGroup = new SelectableGroup();
		buttonGroup.addSelectable(back);
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
