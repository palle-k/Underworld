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

package project.gui.dynamics.animation;

import project.gui.dynamics.GameloopAction;

public class Animation implements GameloopAction
{
	private enum AnimationCurve
	{
		ANIMATION_CURVE_LINEAR,
		ANIMATION_CURVE_EASE,
		ANIMATION_CURVE_EASE_IN,
		ANIMATION_CURVE_EASE_OUT
	}

	public static final AnimationCurve ANIMATION_CURVE_EASE     = AnimationCurve.ANIMATION_CURVE_EASE;
	public static final AnimationCurve ANIMATION_CURVE_EASE_IN  = AnimationCurve.ANIMATION_CURVE_EASE_IN;
	public static final AnimationCurve ANIMATION_CURVE_EASE_OUT = AnimationCurve.ANIMATION_CURVE_EASE_OUT;
	public static final AnimationCurve ANIMATION_CURVE_LINEAR   = AnimationCurve.ANIMATION_CURVE_LINEAR;
	private AnimationHandler  animationHandler;
	private boolean           completed;
	private CompletionHandler completionHandler;
	private double            delay;
	private double            duration;
	private double            fromValue;
	private AnimationCurve interpolationMode = ANIMATION_CURVE_LINEAR;
	private boolean loops;
	private boolean running;
	private double  startTime;
	private double  toValue;

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

	public AnimationHandler getAnimationHandler()
	{
		return animationHandler;
	}

	public CompletionHandler getCompletionHandler()
	{
		return completionHandler;
	}

	public double getDelay()
	{
		return delay;
	}

	public double getDuration()
	{
		return duration;
	}

	public double getFromValue()
	{
		return fromValue;
	}

	public AnimationCurve getInterpolationMode()
	{
		return interpolationMode;
	}

	public double getToValue()
	{
		return toValue;
	}

	public boolean isFinished(double time)
	{
		if (loops)
			return false;
		if (!completed && time > startTime + duration)
			if (completionHandler != null)
				callCompletionHandler();
		completed = time > startTime + duration;
		return completed;
	}

	public boolean isRunning()
	{
		return running;
	}

	public boolean loops()
	{
		return loops;
	}

	public void setAnimationHandler(AnimationHandler animationHandler)
	{
		this.animationHandler = animationHandler;
	}

	public void setCompletionHandler(CompletionHandler handler)
	{
		this.completionHandler = handler;
	}

	public void setDelay(double delay)
	{
		this.delay = Math.max(delay, 0);
	}

	public void setDuration(double duration)
	{
		this.duration = duration;
	}

	public void setFromValue(double fromValue)
	{
		this.fromValue = fromValue;
	}

	public void setInterpolationMode(AnimationCurve interpolationMode)
	{
		this.interpolationMode = interpolationMode;
	}

	public void setLoops(final boolean loops)
	{
		this.loops = loops;
	}

	public void setStartTime(double time)
	{
		startTime = time + delay;
		running = true;
	}

	public void setToValue(double toValue)
	{
		this.toValue = toValue;
	}

	@Override
	public void update(double time, double timeDelta)
	{
		if (!running || startTime > time)
			return;
		if (isFinished(time))
		{
			animationHandler.updateAnimation(toValue);
			return;
		}
		double animationTime = time - startTime;
		if (loops)
			animationTime %= duration;
		switch (interpolationMode)
		{
			case ANIMATION_CURVE_LINEAR:
				animationHandler.updateAnimation(animationTime / duration * (toValue - fromValue) + fromValue);
				break;

			case ANIMATION_CURVE_EASE:
				animationHandler.updateAnimation(
						(Math.cos(animationTime / duration * 3.141592654f) * -0.5f + 0.5f) * (toValue - fromValue) +
						fromValue);
				break;

			case ANIMATION_CURVE_EASE_IN:
				animationHandler.updateAnimation(
						(-Math.cos(animationTime / duration * 3.141592654f * 0.5f) + 1.0f) * (toValue - fromValue) +
						fromValue);
				break;

			case ANIMATION_CURVE_EASE_OUT:
				animationHandler.updateAnimation(
						Math.sin(animationTime / duration * 3.141592654f * 0.5f) * (toValue - fromValue) + fromValue);
				break;
		}
	}

	private void callCompletionHandler()
	{
		final Animation animation = this;
		completionHandler.animationCompleted(animation);
	}
}
