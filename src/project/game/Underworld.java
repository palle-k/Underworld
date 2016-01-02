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

package project.game;

import project.game.data.state.SavedGameState;
import project.game.localization.LocalizedString;
import project.game.ui.controllers.LaunchViewController;
import project.gui.components.TFrame;
import project.gui.controller.NavigationController;
import project.gui.graphics.Appearance;

import java.awt.*;

public class Underworld
{
	public static void main(String[] args)
	{
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Underworld");

		LocalizedString.InitializeLocalizedStrings();

		//Appearance.defaultBackgroundColor = new Color(30, 30, 30);
		Appearance.defaultBackgroundColor = Color.BLACK;
		Appearance.defaultTextColor = Color.WHITE;
		Appearance.defaultBorderColor = Color.WHITE;

		TFrame frame = new TFrame();
		frame.setFrame(new Rectangle(0, 0, 140, 40));
		frame.setVisible(true);
		frame.setTitle("Underworld");

		NavigationController navigationController = new NavigationController(frame);
		navigationController.push(new LaunchViewController());

		SavedGameState.loadState();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> SavedGameState.getSavedGameState().save()));
	}
}
