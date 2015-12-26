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

import project.game.data.state.PlayerState;
import project.game.data.state.SavedGameState;
import project.gui.components.TButton;
import project.gui.components.TComponent;
import project.gui.components.TLabel;
import project.gui.controller.ViewController;
import project.gui.event.SelectableGroup;
import project.gui.layout.HorizontalFlowLayout;
import project.gui.layout.VerticalFlowLayout;

import java.awt.event.KeyEvent;

public class ClassChooserController extends ViewController
{
	@Override
	public void initializeView()
	{
		super.initializeView();

		VerticalFlowLayout containerLayout = new VerticalFlowLayout();
		containerLayout.setSpacing(1);

		TComponent classChooserContainer = new TComponent();
		classChooserContainer.setSize(60, 10);
		getView().add(classChooserContainer);

		HorizontalFlowLayout layout = new HorizontalFlowLayout();
		layout.setSpacing(10);
		classChooserContainer.setLayoutManager(layout);

		TLabel sword = new TLabel();
		sword.setText("  /|\n" +
				"  ||\n" +
				"  ||\n" +
				"  ||\n" +
				"  ||\n" +
				"o====o\n" +
				"  )(\n" +
				"  ()");
		sword.setSize(6, 8);

		TButton swordButton = new TButton();
		swordButton.setText("Knight");
		swordButton.setSize(10, 1);
		swordButton.setActionHandler(new Runnable()
		{
			@Override
			public void run()
			{
				SavedGameState.getSavedGameState().getPlayerState().setPlayerClass(PlayerState.KNIGHT);
				getPageController().next();
			}
		});

		TComponent swordContainer = new TComponent();
		swordContainer.setSize(10, 10);
		swordContainer.add(sword);
		swordContainer.add(swordButton);
		swordContainer.setLayoutManager(containerLayout);
		classChooserContainer.add(swordContainer);

		TLabel bow = new TLabel();
		bow.setText("|\\\n" +
				"| \\\n" +
				"|  |\n" +
				"|——)——>\n" +
				"|  |\n" +
				"| /\n" +
				"|/");
		bow.setSize(8, 8);

		TButton bowButton = new TButton();
		bowButton.setText("Hunter");
		bowButton.setSize(10, 1);
		bowButton.setActionHandler(new Runnable()
		{
			@Override
			public void run()
			{
				SavedGameState.getSavedGameState().getPlayerState().setPlayerClass(PlayerState.HUNTER);
				getPageController().next();
			}
		});

		TComponent bowContainer = new TComponent();
		bowContainer.setSize(10, 10);
		bowContainer.add(bow);
		bowContainer.add(bowButton);
		bowContainer.setLayoutManager(containerLayout);
		classChooserContainer.add(bowContainer);

		TLabel wand = new TLabel();
		wand.setText("//^\\\\\n" +
				"|(O)|\n" +
				"\\\\ //\n" +
				" |||\n" +
				" \\|/\n" +
				"  |\n" +
				"  |\n" +
				"  |");
		wand.setSize(5, 8);

		TButton wandButton = new TButton();
		wandButton.setText("Wizard");
		wandButton.setSize(10, 1);
		wandButton.setActionHandler(new Runnable()
		{
			@Override
			public void run()
			{
				SavedGameState.getSavedGameState().getPlayerState().setPlayerClass(PlayerState.WIZARD);
				getPageController().next();
			}
		});

		TComponent wandContainer = new TComponent();
		wandContainer.setSize(10, 10);
		wandContainer.add(wand);
		wandContainer.add(wandButton);
		wandContainer.setLayoutManager(containerLayout);
		classChooserContainer.add(wandContainer);

		SelectableGroup classSelectables = new SelectableGroup();
		classSelectables.addResponder(swordButton);
		classSelectables.addResponder(bowButton);
		classSelectables.addResponder(wandButton);
		classSelectables.setForwardsKey((char) KeyEvent.VK_RIGHT);
		classSelectables.setBackwardsKey((char) KeyEvent.VK_LEFT);

		TButton back = new TButton();
		back.setText("Zur\u00fcck");
		back.setSize(20, 1);
		back.setActionHandler(() -> getNavigationController().pop());
		getView().add(back);

		TLabel classDescription = new TLabel();
		classDescription.setText("");
		classDescription.setSize(60, 4);
		getView().add(classDescription);
		swordButton.setSelectionHandler(() -> classDescription.setText("Powerful in short range."));
		bowButton.setSelectionHandler(() -> classDescription.setText("Fast and high range."));
		wandButton.setSelectionHandler(() -> classDescription.setText("Helpful skills for tenacious battles."));
		back.setSelectionHandler(() -> classDescription.setText(""));

		SelectableGroup selectables = new SelectableGroup();
		selectables.addResponder(classSelectables);
		selectables.addResponder(back);
		getView().addResponder(selectables);

		VerticalFlowLayout mainLayout = new VerticalFlowLayout();
		mainLayout.setSpacing(3);
		mainLayout.setLayoutInsets(10, 0, 0, 0);
		getView().setLayoutManager(mainLayout);

	}
}
