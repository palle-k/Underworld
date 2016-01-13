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
import project.game.data.state.LevelState;
import project.game.data.state.SavedGameState;
import project.gui.components.TComponent;
import project.gui.controller.PageController;
import project.gui.controller.ViewController;
import project.gui.controller.dialog.ConfirmDialog;
import project.gui.controller.dialog.Dialog;
import project.gui.controller.dialog.DialogDelegate;
import project.gui.controller.dialog.MessageDialog;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import static project.game.localization.LocalizedString.LocalizedString;

public class LevelCoordinator extends PageController
{
	private static Properties levelProperties;

	static
	{
		levelProperties = new Properties();
		try
		{
			levelProperties.load(Underworld.class.getResourceAsStream("data/configuration/Configuration.properties"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private boolean askedForTutorial;
	private Level   currentLevel;
	private boolean didExit;
	private boolean gameFinished;
	private int     levelIndex;
	//private Properties levelProperties;
	private boolean shouldPlayTutorial;
	private boolean shownPostLevelText;
	private boolean shownPreLevelText;
	private int     tutorialIndex;

	public LevelCoordinator(final ViewController parent, final TComponent view)
	{
		super(parent, view);
		init();
	}

	public LevelCoordinator(final TComponent view)
	{
		super(view);
		init();
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
		if (!SavedGameState.getPlayerState().playerClassChosen())
		{
			ClassChooserController classChooser = new ClassChooserController();
			classChooser.setOnCancel(() -> getNavigationController().pop());
			classChooser.setOnClassChoose(this::next);
			return classChooser;
		}

		if (gameFinished)
		{
			getNavigationController().pop();
			return null;
		}

		try
		{
			return new LevelViewController(new Level(Level.class.getResource("levels/level.properties")));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		Level savedLevel = SavedGameState.getLevelState().getSavedLevel();
		if (savedLevel != null)
		{
			SavedGameState.getLevelState().setSavedLevel(null);
			shownPreLevelText = true;
			shownPostLevelText = false;
		}
		else if (!askedForTutorial && !shouldPlayTutorial && !SavedGameState.getLevelState().tutorialWasPlayed())
		{
			askedForTutorial = true;
			ConfirmDialog tutorialDialog = new ConfirmDialog();
			tutorialDialog.setMessage(LocalizedString("confirm_play_tutorial"));
			tutorialDialog.setDelegate(new DialogDelegate()
			{
				@Override
				public void dialogDidCancel(final Dialog dialog)
				{
					SavedGameState.getLevelState().setTutorialPlayed(true);
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

		String propertiesPrefix = null;

		if (savedLevel == null)
		{
			if (askedForTutorial && shouldPlayTutorial && !SavedGameState.getLevelState().tutorialWasPlayed())
			{
				if (shownPostLevelText)
				{
					int tutorialLevelCount = Integer.parseInt(levelProperties.getProperty("tutorial_level_count"));
					shownPreLevelText = false;
					shownPostLevelText = false;
					tutorialIndex++;
					if (tutorialIndex > tutorialLevelCount)
					{
						SavedGameState.getLevelState().setTutorialPlayed(true);
						shouldPlayTutorial = false;
					}
				}
				propertiesPrefix = "tutorial_level_" + tutorialIndex + "_";
			}
			else
			{
				if (shownPostLevelText)
				{
					int levelCount = Integer.parseInt(levelProperties.getProperty("level_count"));
					shownPreLevelText = false;
					shownPostLevelText = false;
					levelIndex++;
					if (levelIndex > levelCount)
						gameFinished = true;
				}
				propertiesPrefix = "level_" + levelIndex + "_";
			}
		}

		if (!shownPreLevelText)
		{
			shownPreLevelText = true;
			String preText = levelProperties.getProperty(propertiesPrefix + "pre_text");
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
			String postText = levelProperties.getProperty(propertiesPrefix + "post_text");
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
			if (savedLevel == null)
			{
				URL levelURL = Level.class.getResource(
						"levels/" + levelProperties.getProperty(propertiesPrefix + "filename") + ".properties");
				System.out.printf(
						"Level URL: %s, filename: %s\n",
						levelURL,
						levelProperties.getProperty(propertiesPrefix + "filename"));
				currentLevel = new Level(levelURL);
			}
			else
				currentLevel = savedLevel;
			LevelViewController levelVC = new LevelViewController(currentLevel);
			levelVC.setAttacksEnabled(!Boolean.parseBoolean(levelProperties.getProperty(
					propertiesPrefix + "attacks_disabled", "false")));
			levelVC.setSkillsEnabled(!Boolean.parseBoolean(levelProperties.getProperty(
					propertiesPrefix + "skills_disabled", "false")));
			levelVC.setDamageEnabled(!Boolean.parseBoolean(levelProperties.getProperty(
					propertiesPrefix + "damage_disabled", "false")));
			levelVC.setOnLevelCancel(() -> {
				save();
				getNavigationController().pop();
				didExit = true;
			});
			levelVC.setOnLevelFailure(() ->
			                          {
				                          currentLevel = null;
				                          save();
				                          getNavigationController().pop();
			                          });
			levelVC.setOnLevelFinish(this::next);
			return levelVC;
		}
		catch (Throwable e)
		{
			e.printStackTrace();
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

	private void init()
	{
		LevelState levelState = SavedGameState.getLevelState();
		levelIndex = levelState.getLevelIndex();
		tutorialIndex = levelState.getTutorialIndex();
		shouldPlayTutorial = levelState.isPlayingTutorial();

		Runtime.getRuntime().addShutdownHook(new Thread(this::save));
	}

	private void save()
	{
		if (didExit)
			return;

		LevelState levelState = SavedGameState.getLevelState();
		levelState.setLevelIndex(levelIndex);
		levelState.setTutorialIndex(tutorialIndex);
		levelState.setPlayingTutorial(shouldPlayTutorial);
		levelState.setSavedLevel(currentLevel);

		SavedGameState.save();
	}
/*
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
	}*/
}
