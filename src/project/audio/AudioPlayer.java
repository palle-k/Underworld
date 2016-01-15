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

package project.audio;

import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaPlayer;

import java.awt.Point;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioPlayer
{
	private static Map<String, AudioClip> cachedClips = new HashMap<>();
	private double          delay;
	private Point           location;
	private AudioClip       player;
	private ExecutorService soundExecutor;

	public AudioPlayer(URL source)
	{
		this(source, true);
	}

	public AudioPlayer(URL source, boolean useCache)
	{
		soundExecutor = Executors.newSingleThreadExecutor();
		soundExecutor.execute(() -> {
			if (useCache && cachedClips.containsKey(source.toString()))
				player = cachedClips.get(source.toString());
			{
				player = new AudioClip(source.toString());
				if (useCache)
					cachedClips.put(source.toString(), player);
			}

		});
	}

	public double getDelay()
	{
		return delay;
	}

	public void play()
	{
		soundExecutor.execute(() -> {
			if (delay > 0)
			{
				new Timer().schedule(new TimerTask()
				{
					@Override
					public void run()
					{
						player.play();
					}
				}, (long) (delay * 1000));
			}
			else
				player.play();
		});

	}

	public void setBalance(Point relativeToPoint)
	{
		double dx = location.x - relativeToPoint.x;
		double dy = Math.abs(relativeToPoint.y - location.y);
		soundExecutor.execute(() -> player.setPan(dx / dy));
	}

	public void setBalance(double balance)
	{
		soundExecutor.execute(() -> player.setBalance(balance));
	}

	public void setDelay(final double delay)
	{
		this.delay = delay;
	}

	public void setLocation(final Point location)
	{
		this.location = location;
	}

	public void setRepeats(boolean repeats)
	{
		soundExecutor.execute(() -> {
			if (repeats)
				player.setCycleCount(MediaPlayer.INDEFINITE);
			else
				player.setCycleCount(1);
		});
	}

	public void setVolume(Point relativeToPoint)
	{
		double dist = relativeToPoint.distance(location) + 1.0;
		soundExecutor.execute(() -> player.setVolume(1.0 / dist));
	}

	public void stop()
	{
		//soundExecutor.execute(player::stop);
		soundExecutor.execute(player::stop);
	}
}
