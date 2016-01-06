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

import project.game.Underworld;
import project.game.data.state.SavedGameState;
import project.gui.components.TComponent;
import project.gui.controller.PageController;
import project.gui.controller.ViewController;
import project.gui.controller.dialog.ConfirmDialog;
import project.gui.controller.dialog.Dialog;
import project.gui.controller.dialog.DialogDelegate;
import project.gui.controller.dialog.MessageDialog;

import java.io.IOException;
import java.util.Properties;

import static project.game.localization.LocalizedString.LocalizedString;

public class LevelCoordinator extends PageController
{
	private int        levelIndex;
	private Properties levelProperties;
	private boolean    playingStory;
	private boolean    playingTutorial;
	private boolean    shouldPlayTutorial;
	private int        tutorialIndex;

	public LevelCoordinator(final ViewController parent, final TComponent view)
	{
		super(parent, view);
	}

	public LevelCoordinator(final TComponent view)
	{
		super(view);
	}

	@Override
	public boolean hasNext()
	{
		return super.hasNext();
	}

	@Override
	public boolean hasPrevious()
	{
		return false;
	}

	@Override
	protected ViewController getNextPage()
	{
		int currentPageIndex = 0;
		if (currentPageIndex == 0 && !SavedGameState.getSavedGameState().getLevelState().tutorialWasPlayed())
		{
			ConfirmDialog tutorialDialog = new ConfirmDialog();
			tutorialDialog.setMessage(LocalizedString("confirm_play_tutorial"));
			tutorialDialog.setDelegate(new DialogDelegate()
			{
				@Override
				public void dialogDidCancel(final Dialog dialog)
				{
					next();
				}

				@Override
				public void dialogDidReturn(final Dialog dialog)
				{
					shouldPlayTutorial = true;
					next();
				}
			});
			getNavigationController().push(tutorialDialog);
		}
		else
		{
			try
			{
				if (shouldPlayTutorial &&
				    tutorialIndex < Integer.parseInt(getLevelProperties().getProperty("tutorial_level_count")))
				{
					tutorialIndex++;
				}
			}
			catch (Throwable e)
			{
				MessageDialog dialog = new MessageDialog();
				dialog.setMessage(
						LocalizedString("level_configuration_loading_fault") + "\n" + e.getLocalizedMessage());
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
		}
		return null;
	}

	@Override
	protected ViewController getPreviousPage()
	{
		throw new RuntimeException("Cannot go backwards in story.");
	}

	@Override
	protected void initializeView()
	{
		super.initializeView();
	}

	private Properties getLevelProperties() throws IOException, NullPointerException
	{
		if (levelProperties == null)
		{
			levelProperties = new Properties();
			levelProperties.load(Underworld.class.getResourceAsStream("data/configuration/Configuration.properties"));
		}
		return levelProperties;
	}
}
