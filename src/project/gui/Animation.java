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

package project.gui;

public class Animation implements GameloopAction
{
	private double				fromValue;
	private double				toValue;
	private double				duration;
	private double				startTime;
	private int					interpolationMode;
	private AnimationHandler    animationHandler;
	private boolean				completed;
	private CompletionHandler	completionHandler;
	private boolean             running;

	public static final int		ANIMATION_CURVE_LINEAR		= 0;
	public static final int		ANIMATION_CURVE_EASE		= 1;
	public static final int		ANIMATION_CURVE_EASE_IN		= 2;
	public static final int		ANIMATION_CURVE_EASE_OUT	= 3;

	public Animation()
	{

	}

	public Animation(AnimationHandler animationHandler)
	{
		this.animationHandler = animationHandler;
	}

	public Animation(CompletionHandler completionHandler)
	{
		this.completionHandler = completionHandler;
	}

	public Animation(AnimationHandler animationHandler, CompletionHandler completionHandler)
	{
		this.animationHandler = animationHandler;
		this.completionHandler = completionHandler;
	}

	@Override
	public void update(double time, double timeDelta)
	{
		if (!running)
			return;
		if (isFinished(time))
		{
			animationHandler.updateAnimation(toValue);
			return;
		}
		switch (interpolationMode)
		{
			case ANIMATION_CURVE_LINEAR:
				animationHandler.updateAnimation((time - startTime) / duration * (toValue - fromValue) + fromValue);
				break;

			case ANIMATION_CURVE_EASE:
				animationHandler.updateAnimation((Math.cos((time - startTime) / duration * 3.141592654f) * -0.5f + 0.5f) * (toValue - fromValue) + fromValue);
				break;

			case ANIMATION_CURVE_EASE_IN:
				animationHandler.updateAnimation((-Math.cos((time - startTime) / duration * 3.141592654f * 0.5f) + 1.0f) * (toValue - fromValue) + fromValue);
				break;

			case ANIMATION_CURVE_EASE_OUT:
				animationHandler.updateAnimation(Math.sin((time - startTime) / duration * 3.141592654f * 0.5f) * (toValue - fromValue) + fromValue);
				break;
		}
	}

	protected void setStartTime(double time)
	{
		startTime = time;
		running = true;
	}

	public boolean isRunning()
	{
		return running;
	}

	public void setFromValue(double fromValue)
	{
		this.fromValue = fromValue;
	}

	public double getFromValue()
	{
		return fromValue;
	}

	public void setToValue(double toValue)
	{
		this.toValue = toValue;
	}

	public double getToValue()
	{
		return toValue;
	}

	public void setDuration(double duration)
	{
		this.duration = duration;
	}

	public double getDuration()
	{
		return duration;
	}

	public void setInterpolationMode(int interpolationMode)
	{
		this.interpolationMode = interpolationMode;
	}

	public int getInterpolationMode()
	{
		return interpolationMode;
	}

	public boolean isFinished(double time)
	{
		if (!completed && time > startTime + duration)
			if (completionHandler != null)
				callCompletionHandler();
		completed = time > startTime + duration;
		return completed;
	}

	public void setCompletionHandler(CompletionHandler handler)
	{
		this.completionHandler = handler;
	}

	public CompletionHandler getCompletionHandler()
	{
		return completionHandler;
	}

	public void setAnimationHandler(AnimationHandler animationHandler)
	{
		this.animationHandler = animationHandler;
	}

	public AnimationHandler getAnimationHandler()
	{
		return animationHandler;
	}

	private void callCompletionHandler()
	{
		final Animation animation = this;
		completionHandler.animationCompleted(animation);
	}
}
