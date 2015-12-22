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

package project;

import project.gui.components.TFrame;
import project.gui.components.TLabel;
import project.gui.dynamics.GameLoop;
import project.gui.layout.VerticalFlowLayout;

import javax.swing.*;
import java.awt.*;

public class Labyrinth
{
	public static void main(String[] args)
	{
		try
		{
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Labyrinth");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e)
		{

		}

		Color backgroundColor = new Color(30, 30, 30);

		TFrame frame = new TFrame();
		frame.setFrame(new Rectangle(0, 0, 140, 40));
		frame.setVisible(true);
		frame.setBackgroundColor(backgroundColor);

		VerticalFlowLayout layout = new VerticalFlowLayout();
		layout.setSpacing(5);
		frame.setLayoutManager(layout);

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
		label.setColor(Color.WHITE);
		label.setBackgroundColor(backgroundColor);
		label.setDrawsBackground(true);
		frame.add(label);

		TLabel line2 = new TLabel();
		line2.setSize(28, 1);
		line2.setText("Press any key to continue...");
		line2.setColor(Color.WHITE);
		line2.setBackgroundColor(backgroundColor);
		line2.setDrawsBackground(true);
		frame.add(line2);

		TLabel copyright = new TLabel();
		copyright.setSize(31, 1);
		copyright.setText("v1.0.0 - (c) Palle Klewitz 2015");
		copyright.setBackgroundColor(backgroundColor);
		copyright.setDrawsBackground(true);
		copyright.setColor(Color.LIGHT_GRAY);
		frame.add(copyright);

		GameLoop loop = new GameLoop();
		loop.addAction((double time, double timeDelta) -> frame.updateAnimations(time, timeDelta));
		loop.start();
	}
}
