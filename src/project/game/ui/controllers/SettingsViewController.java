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

import project.game.data.state.SavedGameState;
import project.gui.components.TButton;
import project.gui.components.TLabel;
import project.gui.controller.ViewController;
import project.gui.controller.dialog.ConfirmDialog;
import project.gui.controller.dialog.Dialog;
import project.gui.controller.dialog.DialogDelegate;
import project.gui.controller.dialog.MessageDialog;
import project.gui.event.SelectableGroup;
import project.gui.layout.VerticalFlowLayout;

import static project.game.localization.LocalizedString.LocalizedString;

public class SettingsViewController extends ViewController
{
	@Override
	protected void initializeView()
	{
		super.initializeView();

		TLabel label = new TLabel();
		label.setSize(54, 8);
		label.setText(LocalizedString("settings_menu_title"));
		getView().add(label);

		TButton setControls = new TButton();
		setControls.setSize(30, 1);
		setControls.setText(LocalizedString("settings_menu_set_controls"));
		setControls.setActionHandler(() -> getNavigationController().push(new ControlSettingsViewController()));
		getView().add(setControls);

		TButton resetState = new TButton();
		resetState.setSize(30, 1);
		resetState.setText(LocalizedString("settings_menu_reset_game_state"));
		resetState.setActionHandler(() ->
		                            {
			                            ConfirmDialog resetConfirmDialog = new ConfirmDialog();
			                            resetConfirmDialog.setMessage(LocalizedString(
					                            "settings_menu_reset_game_state_confirmation"));
			                            resetConfirmDialog.setDelegate(new DialogDelegate()
			                            {
				                            @Override
				                            public void dialogDidCancel(final Dialog dialog)
				                            {

				                            }

				                            @Override
				                            public void dialogDidReturn(final Dialog dialog)
				                            {
					                            SavedGameState.reset();
					                            getNavigationController().pop();
				                            }
			                            });
			                            getNavigationController().push(resetConfirmDialog);
		                            });
		getView().add(resetState);

		TButton importSettings = new TButton();
		importSettings.setSize(30, 1);
		importSettings.setText(LocalizedString("settings_menu_import"));
		importSettings.setActionHandler(() -> {
			try
			{
				SavedGameState.importState(null);
			}
			catch (Throwable e)
			{
				e.printStackTrace();
				MessageDialog dialog = new MessageDialog();
				dialog.setMessage(
						LocalizedString("settings_export_fault") + "\n" + e.getLocalizedMessage());
				dialog.setDelegate(new DialogDelegate()
				{
					@Override
					public void dialogDidCancel(final Dialog dialog)
					{

					}

					@Override
					public void dialogDidReturn(final Dialog dialog)
					{
						getNavigationController().getView().setVisible(false);
					}
				});
				getNavigationController().push(dialog);
			}
		});
		getView().add(importSettings);

		TButton exportSettings = new TButton();
		exportSettings.setSize(30, 1);
		exportSettings.setText(LocalizedString("settings_menu_export"));
		exportSettings.setActionHandler(() -> {
			try
			{
				SavedGameState.exportState(null);
			}
			catch (Throwable e)
			{
				e.printStackTrace();
				MessageDialog dialog = new MessageDialog();
				dialog.setMessage(
						LocalizedString("settings_import_fault") + "\n" + e.getLocalizedMessage());
				dialog.setDelegate(new DialogDelegate()
				{
					@Override
					public void dialogDidCancel(final Dialog dialog)
					{
					}

					@Override
					public void dialogDidReturn(final Dialog dialog)
					{
						getNavigationController().getView().setVisible(false);
					}
				});
				getNavigationController().push(dialog);
			}
		});
		getView().add(exportSettings);

		TButton back = new TButton();
		back.setSize(30, 1);
		back.setText(LocalizedString("settings_menu_back"));
		back.setActionHandler(() -> getNavigationController().pop());
		getView().add(back);

		SelectableGroup buttonGroup = new SelectableGroup();
		buttonGroup.addResponder(setControls);
		buttonGroup.addResponder(resetState);
		buttonGroup.addResponder(importSettings);
		buttonGroup.addResponder(exportSettings);
		buttonGroup.addResponder(back);
		getView().addResponder(buttonGroup);

		VerticalFlowLayout layout = new VerticalFlowLayout();
		layout.setSpacing(2);
		layout.setHorizontalAlignment(VerticalFlowLayout.CENTER);
		layout.setLayoutInsets(3, 5, 0, 0);
		getView().setLayoutManager(layout);
	}
}
