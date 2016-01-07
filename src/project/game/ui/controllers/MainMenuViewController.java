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

import project.gui.components.TButton;
import project.gui.components.TComponent;
import project.gui.components.TLabel;
import project.gui.controller.ViewController;
import project.gui.dynamics.animation.Animation;
import project.gui.dynamics.animation.AnimationHandler;
import project.gui.event.SelectableGroup;
import project.gui.layout.VerticalFlowLayout;

import java.awt.Color;

import static project.game.localization.LocalizedString.LocalizedString;

public class MainMenuViewController extends ViewController
{
	@Override
	protected void initializeView()
	{
		super.initializeView();

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
		label.setVisible(true);
		label.setColor(new Color(255, 100, 0));
		getView().add(label);

		final Animation fireAnimation = new Animation((AnimationHandler) value -> {
			if (value < 0.5)
				label.setText("           )  (           (                 )   (    (    (     \n" +
				              "        ( /(  )\\ )        )\\ )  (  (     ( /(   )\\ ) )\\ ) )\\ )  \n" +
				              "    (   )\\())(()/(   (   (()/(  )\\))(   ')\\()) (()/((()/((()/(  \n" +
				              "    )\\ ((_)\\  /(_))  )\\   /(_))((_)()\\ )((_)\\   /(_))/(_))/(_)) \n" +
				              " _ ((_) _((_)(_))_  ((_) (_))  _(())\\_)() ((_) (_)) (_)) (_))_  \n" +
				              "| | | || \\| | |   \\ | __|| _ \\ \\ \\((_)/ // _ \\ | _ \\| |   |   \\ \n" +
				              "| |_| || .` | | |) || _| |   /  \\ \\/\\/ /| (_) ||   /| |__ | |) |\n" +
				              " \\___/ |_|\\_| |___/ |___||_|_\\   \\_/\\_/  \\___/ |_|_\\|____||___/ ");
			else
				label.setText("           (  )           )                 (   )    )    )     \n" +
				              "        ) \\)  (/ (        (/ (  )  )     ) \\)   (/ ( (/ ( (/ (  \n" +
				              "    )   (/)(())(\\)   )   ))(\\)  (/(()   '(/)(( ))(\\)))(\\)))(\\)  \n" +
				              "    (/ ))_(/  \\)_((  (/   \\)_(())_()(/ ())_(/   \\)_((\\)_((\\)_(( \n" +
				              " _ ))_( _))_()_((_  ))_( )_((  _))((/_()( ))_( )_(( )_(( )_((_  \n" +
				              "| | | || \\| | |   \\ | __|| _ \\ \\ \\((_)/ // _ \\ | _ \\| |   |   \\ \n" +
				              "| |_| || .` | | |) || _| |   /  \\ \\/\\/ /| (_) ||   /| |__ | |) |\n" +
				              " \\___/ |_|\\_| |___/ |___||_|_\\   \\_/\\_/  \\___/ |_|_\\|____||___/ ");
		});
		fireAnimation.setDuration(0.5);
		fireAnimation.setFromValue(0);
		fireAnimation.setToValue(1);
		fireAnimation.setLoops(true);
		label.addAnimation(fireAnimation);

		TButton play = new TButton();
		play.setSize(20, 1);
		play.setText(LocalizedString("main_menu_play"));
		play.setActionHandler(() -> {
			//PageController gamePages = new PageController(new TComponent());
			//if (!SavedGameState.getSavedGameState().getPlayerState().playerClassChosen())
			//	gamePages.addController(new ClassChooserController());
			//if (!SavedGameState.getSavedGameState().getLevelState().tutorialWasPlayed())
			//	gamePages.addController(new TutorialViewController());
			//gamePages.addController(new LevelViewController());
			//getNavigationController().push(gamePages);
			LevelCoordinator coordinator = new LevelCoordinator(new TComponent());
			getNavigationController().push(coordinator);
		});
		getView().add(play);

		TButton introduction = new TButton();
		introduction.setSize(20, 1);
		introduction.setText(LocalizedString("main_menu_tutorial"));
		getView().add(introduction);

		TButton showProgress = new TButton();
		showProgress.setSize(20, 1);
		showProgress.setText(LocalizedString("settings_menu_show_progress"));
		getView().add(showProgress);

		TButton settings = new TButton();
		settings.setSize(20, 1);
		settings.setText(LocalizedString("main_menu_settings"));
		settings.setActionHandler(() -> getNavigationController().push(new SettingsViewController()));
		getView().add(settings);

		TButton quit = new TButton();
		quit.setSize(20, 1);
		quit.setText(LocalizedString("main_menu_quit"));
		quit.setActionHandler(() -> getNavigationController().getView().setVisible(false));
		getView().add(quit);

		TLabel navigationHelpLabel = new TLabel();
		navigationHelpLabel.setSize(50, 2);
		navigationHelpLabel.setText(LocalizedString("main_menu_navigation_instruction"));
		navigationHelpLabel.setColor(Color.GRAY);
		getView().add(navigationHelpLabel);

		SelectableGroup buttonGroup = new SelectableGroup();
		buttonGroup.addResponder(play);
		buttonGroup.addResponder(introduction);
		buttonGroup.addResponder(showProgress);
		buttonGroup.addResponder(settings);
		buttonGroup.addResponder(quit);
		getView().addResponder(buttonGroup);

		VerticalFlowLayout layout = new VerticalFlowLayout();
		layout.setSpacing(3);
		layout.setHorizontalAlignment(VerticalFlowLayout.CENTER);
		layout.setLayoutInsets(3, 5, 0, 0);
		getView().setLayoutManager(layout);
	}
}
