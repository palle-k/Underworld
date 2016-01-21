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

import static project.game.localization.LocalizedString.LocalizedString;

/**
 * Controller, welcher zur Auswahl einer Spielerklasse dient
 */
public class ClassChooserController extends ViewController
{
	private Runnable onCancel;
	private Runnable onClassChoose;

	/**
	 * Aktion, die beim Abbrechen der Auswahl ausgefuehrt werden soll
	 *
	 * @return auszufuehrende Aktion
	 */
	public Runnable getOnCancel()
	{
		return onCancel;
	}

	/**
	 * Aktion, die bei Wahl einer Klasse ausgefuehrt werden soll
	 * @return auszufuehrende Aktion
	 */
	public Runnable getOnClassChoose()
	{
		return onClassChoose;
	}

	/**
	 * Setzt die Aktion, die beim Abbrechen der Auswahl ausgefuehrt werden soll
	 * @param onCancel Abbruchaktion
	 */
	public void setOnCancel(final Runnable onCancel)
	{
		this.onCancel = onCancel;
	}

	/**
	 * Setzt die Aktion, die bei Wahl einer Klasse ausgefuehrt werden soll
	 * @param onClassChoose Wahlaktion
	 */
	public void setOnClassChoose(final Runnable onClassChoose)
	{
		this.onClassChoose = onClassChoose;
	}

	@Override
	protected void initializeView()
	{
		super.initializeView();

		VerticalFlowLayout containerLayout = new VerticalFlowLayout();
		containerLayout.setSpacing(1);

		TComponent classChooserContainer = new TComponent();
		classChooserContainer.setSize(60, 10);
		getView().add(classChooserContainer);

		HorizontalFlowLayout layout = new HorizontalFlowLayout();
		layout.setSpacing(0);
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
		swordButton.setText(LocalizedString("class_chooser_knight"));
		swordButton.setActionHandler(() -> {
			SavedGameState.getPlayerState().setPlayerClass(PlayerState.KNIGHT);
			if (onClassChoose != null)
				onClassChoose.run();
		});

		TComponent swordContainer = new TComponent();
		swordContainer.setSize(20, 10);
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
		bowButton.setText(LocalizedString("class_chooser_hunter"));
		bowButton.setActionHandler(() -> {
			SavedGameState.getPlayerState().setPlayerClass(PlayerState.HUNTER);
			if (onClassChoose != null)
				onClassChoose.run();
		});

		TComponent bowContainer = new TComponent();
		bowContainer.add(bow);
		bowContainer.add(bowButton);
		bowContainer.setSize(20, 10);
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
		wandButton.setText(LocalizedString("class_chooser_wizard"));
		wandButton.setActionHandler(() -> {
			SavedGameState.getPlayerState().setPlayerClass(PlayerState.WIZARD);
			if (onClassChoose != null)
				onClassChoose.run();
		});

		TComponent wandContainer = new TComponent();
		wandContainer.setSize(20, 10);
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
		back.setText(LocalizedString("class_chooser_back"));
		back.setSize(20, 1);
		back.setActionHandler(() -> {
			if (onCancel != null) onCancel.run();
		});
		getView().add(back);

		TLabel classDescription = new TLabel();
		classDescription.setText("");
		classDescription.setSize(60, 4);
		getView().add(classDescription);
		swordButton.setSelectionHandler(() -> classDescription.setText(LocalizedString("class_chooser_knight_description")));
		bowButton.setSelectionHandler(() -> classDescription.setText(LocalizedString("class_chooser_hunter_description")));
		wandButton.setSelectionHandler(() -> classDescription.setText(LocalizedString("class_chooser_wizard_description")));
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
