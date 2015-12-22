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

import project.game.data.Level;
import project.game.ui.views.LevelView;
import project.gui.controller.ViewController;

import java.io.IOException;

public class LevelViewController extends ViewController
{
	@Override
	public void viewDidAppear()
	{
		super.viewDidAppear();
		Level level;
		try
		{
			level = new Level(Level.class.getResource("level.properties"));
		} catch (IOException e)
		{
			e.printStackTrace();
			return;
		}

		LevelView levelView = new LevelView();
		levelView.setSize(level.getWidth() * 4, level.getHeight() * 2);
		levelView.setLevel(level);
		getView().add(levelView);
	}

	@Override
	public void viewDidDisappear()
	{
		super.viewDidDisappear();
		getView().removeAll();
	}
}
