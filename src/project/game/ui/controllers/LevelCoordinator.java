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
import project.game.data.Level;
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
	private boolean askedForTutorial;
	private boolean gameFinished;
	private int levelIndex = 1;
	private Properties levelProperties;
	private boolean    shouldPlayTutorial;
	private boolean    shownPostLevelText;
	private boolean    shownPreLevelText;
	private boolean    tutorialFinished;
	private int tutorialIndex = 1;

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
		if (!SavedGameState.getSavedGameState().getPlayerState().playerClassChosen())
		{
			ClassChooserController classChooser = new ClassChooserController();
			classChooser.setOnCancel(() -> getNavigationController().pop());
			classChooser.setOnClassChoose(this::next);
			return classChooser;
		}
		if (!askedForTutorial && !SavedGameState.getSavedGameState().getLevelState().tutorialWasPlayed())
		{
			askedForTutorial = true;
			ConfirmDialog tutorialDialog = new ConfirmDialog();
			tutorialDialog.setMessage(LocalizedString("confirm_play_tutorial"));
			tutorialDialog.setDelegate(new DialogDelegate()
			{
				@Override
				public void dialogDidCancel(final Dialog dialog)
				{
					SavedGameState.getSavedGameState().getLevelState().setTutorialPlayed(true);
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
			return null;
		}

		String propertiesPrefix;

		if (askedForTutorial && shouldPlayTutorial && !tutorialFinished)
		{
			if (shownPostLevelText)
			{
				int tutorialLevelCount = Integer.parseInt(getLevelProperties().getProperty("tutorial_level_count"));
				shownPreLevelText = false;
				shownPostLevelText = false;
				tutorialIndex++;
				if (tutorialIndex >= tutorialLevelCount)
					tutorialFinished = true;
			}
			propertiesPrefix = "tutorial_level_" + tutorialIndex + "_";
		}
		else
		{
			if (shownPostLevelText)
			{
				int levelCount = Integer.parseInt(getLevelProperties().getProperty("level_count"));
				shownPreLevelText = false;
				shownPostLevelText = false;
				levelIndex++;
				if (levelIndex >= levelCount - 1)
					gameFinished = true;
				//TODO handle game end
			}
			//FIXME level prefix calculation
			propertiesPrefix = "level_1_";
		}

		if (!shownPreLevelText)
		{
			shownPreLevelText = true;
			String preText = getLevelProperties().getProperty(propertiesPrefix + "pre_text");
			if (preText != null)
			{
				PlainMessageViewController plainMessageController = new PlainMessageViewController();
				plainMessageController.setMessage(LocalizedString(preText));
				plainMessageController.setOnKeyPress(this::next);
				return plainMessageController;
			}
		}
		if (!shownPostLevelText)
		{
			shownPostLevelText = true;
			String postText = getLevelProperties().getProperty(propertiesPrefix + "post_text");
			if (postText != null)
			{
				PlainMessageViewController plainMessageController = new PlainMessageViewController();
				plainMessageController.setMessage(LocalizedString(postText));
				plainMessageController.setOnKeyPress(this::next);
				return plainMessageController;
			}
		}
		try
		{
			Level               level   = new Level(Level.class.getResource(
					"levels/" + getLevelProperties().getProperty(propertiesPrefix + "filename") + ".properties"));
			LevelViewController levelVC = new LevelViewController(level);
			levelVC.setAttacksEnabled(!Boolean.parseBoolean(getLevelProperties().getProperty(
					propertiesPrefix + "attacks_disabled", "false")));
			levelVC.setSkillsEnabled(!Boolean.parseBoolean(getLevelProperties().getProperty(
					propertiesPrefix + "skills_disabled", "false")));
			levelVC.setDamageEnabled(!Boolean.parseBoolean(getLevelProperties().getProperty(
					propertiesPrefix + "damage_disabled", "false")));
			levelVC.setOnLevelCancel(() -> {
				//TODO save game state
				getNavigationController().pop();
			});
			levelVC.setOnLevelFailure(() -> getNavigationController().pop());
			levelVC.setOnLevelFinish(this::next);
			return levelVC;
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
			return null;
		}
	}

	@Override
	protected ViewController getPreviousPage()
	{
		throw new RuntimeException("Cannot go backwards in story yet.");
	}

	@Override
	protected void initializeView()
	{
		super.initializeView();
	}

	private Properties getLevelProperties()
	{
		if (levelProperties == null)
		{
			levelProperties = new Properties();
			try
			{
				levelProperties.load(Underworld.class.getResourceAsStream("data/configuration/Configuration.properties"));
			}
			catch (IOException e)
			{
				//do nothing
				e.printStackTrace();
			}
		}
		return levelProperties;
	}
}
