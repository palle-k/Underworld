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

package project.gui.dynamics;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class GameLoop implements Runnable
{
	private List<GameloopAction> actionList;
	private long baseTime;
	private Thread gameloopThread;
	private long time;

	public GameLoop()
	{
		actionList = new ArrayList<>();
		Class<GameLoop> clazz = GameLoop.class;
	}

	public synchronized void addAction(GameloopAction action)
	{
		actionList.add(action);
	}

	public synchronized void removeAction(GameloopAction action)
	{
		actionList.remove(action);
	}

	@Override
	public void run()
	{
		baseTime = System.currentTimeMillis();
		while (true)
		{
			long newTime = System.currentTimeMillis() - baseTime;
			long timeDelta = newTime - time;
			time = newTime;
			double updateTime = time * 0.001;
			double updateTimeDelta = timeDelta * 0.001;
			try
			{
				SwingUtilities.invokeAndWait(() -> invokeActions(updateTime, updateTimeDelta));
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			} catch (InvocationTargetException e)
			{
				e.printStackTrace();
			}
			long executionTime = System.currentTimeMillis() - baseTime - time;
			if (executionTime < 16)
				try
				{
					Thread.sleep(16 - executionTime);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
		}
	}

	public void start()
	{
		gameloopThread = new Thread(this);
		gameloopThread.start();
	}

	public void stop()
	{
		gameloopThread.interrupt();
	}

	private synchronized void invokeActions(double time, double timeDelta)
	{
		for (GameloopAction action : actionList)
			action.update(time, timeDelta);
	}
}
