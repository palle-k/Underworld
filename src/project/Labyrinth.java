/******************************************************************************
 * Copyright (c) 2015 Palle Klewitz.                                          *
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

package project;

import project.gui.dynamics.animation.Animation;
import project.gui.components.TFrame;
import project.gui.components.TLabel;
import project.gui.dynamics.GameLoop;
import project.gui.dynamics.GameloopAction;

import javax.swing.*;
import java.awt.*;

public class Labyrinth
{
	public static void main(String[] args)
	{
		try
		{
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{

		}
		TFrame frame = new TFrame();
		frame.setFrame(new Rectangle(0,0,80,25));
		frame.setVisible(true);
		frame.setBackgroundColor(Color.WHITE);

		TLabel label = new TLabel();
		label.setFrame(new Rectangle(1,1,40,2));
		label.setText("Hello World\tWhats up?\nTrolololo?");
		label.setColor(Color.RED);
		label.setBackgroundColor(Color.WHITE);
		label.setDrawsBackground(true);
		frame.add(label);

		GameLoop loop = new GameLoop();
		loop.addAction(new GameloopAction()
		{
			@Override
			public void update(double time, double timeDelta)
			{
				frame.updateAnimations(time, timeDelta);
			}
		});
		loop.start();

		Animation labelAnimation = new Animation((double value) -> label.setPosX((int) value));
		labelAnimation.setFromValue(1);
		labelAnimation.setToValue(50);
		labelAnimation.setDuration(2);
		labelAnimation.setInterpolationMode(Animation.ANIMATION_CURVE_EASE);
		label.addAnimation(labelAnimation);
	}
}
