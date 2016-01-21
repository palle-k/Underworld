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

package project.gui.dynamics;

import java.util.ArrayList;
import java.util.List;

/**
 * GameLoop: Zeitsteuerung von Animationen
 */
public class GameLoop implements Runnable
{
	private List<GameloopAction> actionList;
	private long numberOfUpdates;
	private boolean              running;
	private long                 time;
	private long totalUpdateTime;

	private long updateInterval;

	private long updateTimeDelta;

	/**
	 * Erstellt eine neue GameLoop
	 */
	public GameLoop()
	{
		actionList = new ArrayList<>();
		updateInterval = 16;
	}

	/**
	 * Fuegt eine neue Aktion hinzu, die beim Aktualisieren der Gameloop ausgefuehrt wird
	 * @param action hinzuzufuegende Aktion
	 */
	public synchronized void addAction(GameloopAction action)
	{
		actionList.add(action);
	}

	/**
	 * Anzahl der durchgefuehrten Aktualisierungen
	 * Verwendet fuer Performance Metrics
	 * @return Anzahl Aktualisierungen
	 */
	public long getNumberOfUpdates()
	{
		return numberOfUpdates;
	}

	/**
	 * Zeit, die fuer die Durchfuehrung von Aktualisierungen verwendet wurde.
	 * Verwendet fuer Performance Metrics
	 * @return Aktualisierungszeit in ms
	 */
	public long getTotalUpdateTime()
	{
		return totalUpdateTime;
	}

	/**
	 * Gibt das Aktualisierungsintervall zurueck, in welchem
	 * die Gameloop alle Aktionen ausfuehrt.
	 * Standardmaessig betraegt dieses 33ms (30fps).
	 *
	 * @return Aktualisierungsintervall in ms
	 */
	public long getUpdateInterval()
	{
		return updateInterval;
	}

	/**
	 * Zeit, die fuer die letzte Aktualisierung verwendet wurde.
	 * Verwendet fuer Performance Metrics
	 * @return Aktualisierungszeit in ms
	 */
	public long getUpdateTimeDelta()
	{
		return updateTimeDelta;
	}

	/**
	 * Entfernt die Aktion aus der Gameloop.
	 * Diese Aktion wird beim Aktualisieren nicht mehr ausgefuehrt.
	 * @param action zu entfernende Aktion
	 */
	public synchronized void removeAction(GameloopAction action)
	{
		actionList.remove(action);
	}

	/**
	 * Aktualisiert die Gameloop in regelmaessigen Abstaenden von getUpdateInterval() ms.
	 */
	@Override
	public void run()
	{
		long baseTime = System.currentTimeMillis();
		while (running)
		{
			long newTime   = System.currentTimeMillis() - baseTime;
			//long timeDelta = newTime - time;
			//totalUpdateTime += updateTimeDelta;
			numberOfUpdates++;
			time = newTime;
			double updateTime = time * 0.001;
			//double updateTimeDelta = timeDelta * 0.001;
			//SwingUtilities.invokeAndWait(() -> invokeActions(updateTime, updateTimeDelta));
			invokeActions(updateTime);
			updateTimeDelta = System.currentTimeMillis() - baseTime - time;
			totalUpdateTime += updateTimeDelta;
			if (updateTimeDelta < updateInterval)
				try
				{
					Thread.sleep(updateInterval - updateTimeDelta);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
		}
	}

	/**
	 * Setzt das Aktualisierungsintervall, in welchem die Gameloop alle Aktionen
	 * ausfuehrt. Standardmaessig betraegt dieses 33ms (30fps).
	 *
	 * @param updateInterval Aktualisierungsintervall in ms
	 */
	public void setUpdateInterval(final long updateInterval)
	{
		this.updateInterval = updateInterval;
	}

	/**
	 * Startet die Ausfuehrung der Gameloop in einem neuen Thread.
	 */
	public void start()
	{
		running = true;
		Thread gameloopThread = new Thread(this);
		gameloopThread.start();
	}

	/**
	 * Beendet die Ausfuehrung der Gameloop. Die tatsaechliche
	 * Beendigung der Gameloop findet verzoegert statt.
	 */
	public void stop()
	{
		running = false;
	}

	/**
	 * Fuehrt die Gameloop-Aktionen durch
	 * @param time Aktualisierungszeitpunkt in Sekunden
	 *
	 */
	private void invokeActions(double time)
	{
		for (GameloopAction action : actionList)
			action.update(time);
	}
}
