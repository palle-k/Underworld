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

/**
 * Controller fuer die Darstellung mehrerer Level
 * Der LevelCoordinator uebernimmt die Auswahl des naechsten Levels.
 */
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

	private Level currentLevel;

	private boolean didExit;

	private boolean didPlayTutorial;

	private int     levelIndex;

	private boolean shownLevel;

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
		return true;
	}

	@Override
	public boolean hasPrevious()
	{
		return false;
	}

	@Override
	protected ViewController getNextPage()
	{
		currentLevel = null;

		if (!SavedGameState.getPlayerState().playerClassChosen())
		{
			ClassChooserController classChooser = new ClassChooserController();
			classChooser.setOnCancel(() -> getNavigationController().pop());
			classChooser.setOnClassChoose(this::next);
			return classChooser;
		}

//		try
//		{
//			Level debugLevel = new Level(Level.class.getResource("levels/final_boss.properties"), "GiantHogweed", "DarkGuardian");
//			LevelViewController levelViewController = new LevelViewController(debugLevel);
//			return levelViewController;
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}


		if (!askedForTutorial && !didPlayTutorial)
		{
			ConfirmDialog tutorialDialog = new ConfirmDialog();
			tutorialDialog.setMessage(LocalizedString("confirm_play_tutorial"));
			tutorialDialog.setDelegate(new DialogDelegate()
			{
				@Override
				public void dialogDidCancel(final Dialog dialog)
				{
					didPlayTutorial = true;
					askedForTutorial = true;
					next();
				}

				@Override
				public void dialogDidReturn(final Dialog dialog)
				{
					askedForTutorial = true;
					next();
				}
			});
			tutorialDialog.setPopOnEnd(false);
			return tutorialDialog;
		}

		String propertiesPrefix = null;

		Level nextLevel = SavedGameState.getLevelState().getSavedLevel();
		if (nextLevel != null)
		{
			SavedGameState.getLevelState().setSavedLevel(null);
			if (didPlayTutorial)
			{
				propertiesPrefix = "level_" + levelIndex + "_";
			}
			else
			{
				propertiesPrefix = "tutorial_level_" + tutorialIndex + "_";
			}

			shownLevel = true;
			shownPreLevelText = true;
			shownPostLevelText = levelProperties.getProperty(propertiesPrefix + "_post_text") == null;
		}
		else
		{
			if (!didPlayTutorial)
			{
				if (shownPreLevelText && shownPostLevelText && shownLevel)
				{
					tutorialIndex++;

					if (tutorialIndex > Integer.parseInt(levelProperties.getProperty("tutorial_level_count")))
						didPlayTutorial = true;

					shownPreLevelText = levelProperties.getProperty("tutorial_level_" + tutorialIndex + "_pre_text") ==
					                    null;
					shownPostLevelText =
							levelProperties.getProperty("tutorial_level_" + tutorialIndex + "_post_text") == null;
					shownLevel = false;
				}
				propertiesPrefix = "tutorial_level_" + tutorialIndex + "_";
			}

			if (didPlayTutorial)
			{
				System.out.printf(
						"Shown pre text? %b, level? %b, post text? %b\n",
						shownPreLevelText,
						shownLevel,
						shownPostLevelText);
				if (shownPreLevelText && shownPostLevelText && shownLevel)
				{
					System.out.printf("Incrementing Level index\n");
					levelIndex++;

					if (levelIndex > Integer.parseInt(levelProperties.getProperty("level_count")))
					{
						PlainMessageViewController finishedMessage = new PlainMessageViewController();
						finishedMessage.setMessage(LocalizedString("game_finished_message"));
						finishedMessage.setOnKeyPress(() -> getNavigationController().pop());
						return finishedMessage;
					}

					shownPreLevelText = levelProperties.getProperty("level_" + levelIndex + "_pre_text") == null;
					shownPostLevelText = levelProperties.getProperty("level_" + levelIndex + "_post_text") == null;
					shownLevel = false;
				}
				propertiesPrefix = "level_" + levelIndex + "_";
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
			else if (!shownLevel)
			{
				shownLevel = true;
			}
			else if (!shownPostLevelText)
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
			else
				throw new IllegalStateException("already shown all states of the current level");

			shownPreLevelText = true;
			shownLevel = true;

			try
			{
				nextLevel = loadLevel(propertiesPrefix);
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
						getNavigationController().pop();
					}

					@Override
					public void dialogDidReturn(final Dialog dialog)
					{
						getNavigationController().pop();
					}
				});
				dialog.setPopOnEnd(false);
				return dialog;
			}
		}

		currentLevel = nextLevel;

		LevelViewController levelVC = new LevelViewController(nextLevel);
//		levelVC.setAttacksEnabled(!Boolean.parseBoolean(levelProperties.getProperty(
//				propertiesPrefix + "attacks_disabled", "false")));
//		levelVC.setSkillsEnabled(!Boolean.parseBoolean(levelProperties.getProperty(
//				propertiesPrefix + "skills_disabled", "false")));
//		levelVC.setDamageEnabled(!Boolean.parseBoolean(levelProperties.getProperty(
//				propertiesPrefix + "damage_disabled", "false")));
		levelVC.setOnLevelCancel(() -> {
			save();
			getNavigationController().pop();
			didExit = true;
		});
		levelVC.setOnLevelFailure(() -> {
			currentLevel = null;
			save();
			getNavigationController().pop();
		});
		levelVC.setOnLevelFinish(this::next);
		levelVC.setOnReachEntrance(this::previous);
		return levelVC;
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

	/**
	 * Initialisiert den LevelCoordinator und laed den Spielstand
	 */
	private void init()
	{
		LevelState levelState = SavedGameState.getLevelState();
		levelIndex = levelState.getLevelIndex();
		tutorialIndex = levelState.getTutorialIndex();
		askedForTutorial = levelState.askedForTutorial();
		shownPreLevelText = levelState.didShowPreLevelText();
		shownPostLevelText = levelState.didShowPostLevelText();
		didPlayTutorial = levelState.tutorialWasPlayed();
		shownLevel = levelState.levelPlayed();

		String propertiesPrefix;

		if (didPlayTutorial)
		{
			propertiesPrefix = "level_" + levelIndex;
		}
		else
		{
			propertiesPrefix = "tutorial_level_" + tutorialIndex;
		}
		shownPreLevelText |= levelProperties.getProperty(propertiesPrefix + "_pre_text") == null;
		shownPostLevelText |= levelProperties.getProperty(propertiesPrefix + "_post_text") == null;
		shownLevel |= levelState.getSavedLevel() != null;

		Runtime.getRuntime().addShutdownHook(new Thread(this::save));
	}

	/**
	 * Laed ein neues Level durch die Konfiguration mit dem angegebenen Praefix
	 *
	 * @param propertiesPrefix Praefix der Level-Properties
	 * @return Geladenes Level
	 * @throws IOException wenn das Level nicht existiert oder nicht geladen werden kann
	 */
	private Level loadLevel(String propertiesPrefix) throws IOException
	{
		URL    levelURL           = Level.class.getResource(
				"levels/" + levelProperties.getProperty(propertiesPrefix + "filename") + ".properties");
		String staticEnemySource  = levelProperties.getProperty(propertiesPrefix + "static_enemy_name");
		String dynamicEnemySource = levelProperties.getProperty(propertiesPrefix + "dynamic_enemy_name");
		System.out.printf(
				"Static enemy (prefix: %s): %s; Dynamic enemy: %s\n",
				propertiesPrefix,
				staticEnemySource,
				dynamicEnemySource);
		return new Level(levelURL, staticEnemySource, dynamicEnemySource);
	}

	/**
	 * Sichert den Spielstand
	 */
	private void save()
	{
		if (didExit)
			return;

		LevelState levelState = SavedGameState.getLevelState();
		levelState.setLevelIndex(levelIndex);
		levelState.setTutorialIndex(tutorialIndex);
		levelState.setAskedForTutorial(askedForTutorial);
		levelState.setSavedLevel(currentLevel);
		levelState.setLevelPlayed(shownLevel);
		levelState.setDidShowPreLevelText(shownPreLevelText);
		levelState.setDidShowPostLevelText(shownPostLevelText);
		SavedGameState.save();
	}
}
