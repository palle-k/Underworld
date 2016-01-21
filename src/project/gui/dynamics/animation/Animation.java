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

/**
 * Animationsklasse:
 * Darstellung beliebiger Animation mit
 * verschiedenen Animationskurven.
 */
public class Animation implements GameloopAction
{
	private enum AnimationCurve
	{
		ANIMATION_CURVE_LINEAR,
		ANIMATION_CURVE_EASE,
		ANIMATION_CURVE_EASE_IN,
		ANIMATION_CURVE_EASE_OUT
	}

	/**
	 * Animationskurve, die langsam beginnt und langsam endet, dazwischen schneller wird
	 */
	public static final AnimationCurve ANIMATION_CURVE_EASE     = AnimationCurve.ANIMATION_CURVE_EASE;

	/**
	 * Animationskurve, die langsam beginnt und schnell endet
	 */
	public static final AnimationCurve ANIMATION_CURVE_EASE_IN  = AnimationCurve.ANIMATION_CURVE_EASE_IN;

	/**
	 * Animationskurve, die schnell beginnt und langsam endet
	 */
	public static final AnimationCurve ANIMATION_CURVE_EASE_OUT = AnimationCurve.ANIMATION_CURVE_EASE_OUT;

	/**
	 * Animationskure, die mit konstanter Geschwindigkeit verlaueft
	 */
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

	/**
	 * Erstellt eine neue Animation
	 */
	public Animation()
	{

	}

	/**
	 * Erstellt eine neue Animation mit dem angegebenen AnimationHandler
	 * @param animationHandler zu verwendender AnimationHandler
	 * @see AnimationHandler
	 */
	public Animation(AnimationHandler animationHandler)
	{
		this.animationHandler = animationHandler;
	}

	/**
	 * Erstellt eine neue Animation mit dem angegebenen CompletionHandler
	 * @param completionHandler zu verwendender CompletionHandler
	 * @see CompletionHandler
	 */
	public Animation(CompletionHandler completionHandler)
	{
		this.completionHandler = completionHandler;
	}

	/**
	 * Erstellt eine neue Animation mit dem angegebenen AnimationHandler und
	 * CompletionHandler
	 * @param animationHandler zu verwendender AnimationHandler
	 * @param completionHandler zu verwendender CompletionHandler
	 * @see AnimationHandler
	 * @see CompletionHandler
	 */
	public Animation(AnimationHandler animationHandler, CompletionHandler completionHandler)
	{
		this.animationHandler = animationHandler;
		this.completionHandler = completionHandler;
	}

	/**
	 * Gibt den Animationshandler an, der zum Aktualisieren der Animation verwendet wird
	 * @return verwendeter Animationshandler
	 */
	public AnimationHandler getAnimationHandler()
	{
		return animationHandler;
	}

	/**
	 * Gibt den CompletionHandler an, der beim Beenden der Animation aufgerufen wird
	 * @return verwendeter CompletionHandler
	 */
	public CompletionHandler getCompletionHandler()
	{
		return completionHandler;
	}

	/**
	 * Gibt die Verzeogerung an, mit der die Animation ausgefuehrt wird
	 * @return Animationsverzoegerung in Sekunden
	 */
	public double getDelay()
	{
		return delay;
	}

	/**
	 * Gibt die Animationslaenge an
	 * @return Animationslaenge in Sekunden
	 */
	public double getDuration()
	{
		return duration;
	}

	/**
	 * Gibt den Startwert der Animation an
	 * @return Animationsstartwert
	 */
	public double getFromValue()
	{
		return fromValue;
	}

	/**
	 * Gibt die verwendete Animationskurve an
	 * @return verwendete Animationskurve
	 */
	public AnimationCurve getInterpolationMode()
	{
		return interpolationMode;
	}

	/**
	 * Gibt den Endwert der Animation an
	 * @return Endwert der Animation
	 */
	public double getToValue()
	{
		return toValue;
	}

	/**
	 * Gibt an, ob die Animation zur angegebenen Zeit beendet ist
	 * @param time zu pruefende Zeit
	 * @return true, wenn die Animation zur gegebenen Zeit beendet ist, sonst false
	 */
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

	/**
	 * Gibt an, ob die Animation laeuft
	 * @return true, wenn die Animation laeuft, sonst false
	 */
	public boolean isRunning()
	{
		return running;
	}

	/**
	 * Gibt an, ob sich die Animation automatisch wiederholt
	 * @return true, wenn sich die Animation wiederholt, sonst false
	 */
	public boolean loops()
	{
		return loops;
	}

	/**
	 * Setzt den AnimationHandler, der fuer das Anwenden des Animationswertes verantwortlich ist
	 * @param animationHandler zu verwendender Animationshandler
	 */
	public void setAnimationHandler(AnimationHandler animationHandler)
	{
		this.animationHandler = animationHandler;
	}

	/**
	 * Setzt den CompletionHandler, der beim Beenden der Animation aufgerufen wird
	 * @param handler zu verwendender CompletionHandler
	 */
	public void setCompletionHandler(CompletionHandler handler)
	{
		this.completionHandler = handler;
	}

	/**
	 * Setzt die Verzoegerung, mit der die Animation ausgefuehrt wird.
	 * Der Aufruf dieser Methode muss vor dem Start der Animation erfolgen
	 * @param delay Verzoegerung in Sekunden
	 */
	public void setDelay(double delay)
	{
		this.delay = Math.max(delay, 0);
	}

	/**
	 * Setzt die Laenge der Animation
	 * @param duration Animationslaenge in Sekunden
	 */
	public void setDuration(double duration)
	{
		this.duration = duration;
	}

	/**
	 * Setzt den Startwert der Animation
	 * @param fromValue Startwert der Animation
	 */
	public void setFromValue(double fromValue)
	{
		this.fromValue = fromValue;
	}

	/**
	 * Setzt die verwendete Animationskurve der Animation
	 * @param interpolationMode zu verwendende Animationskurve
	 */
	public void setInterpolationMode(AnimationCurve interpolationMode)
	{
		this.interpolationMode = interpolationMode;
	}

	/**
	 * Legt fest, ob sich die Animation automatisch wiederholt
	 * @param loops true, wenn sich die Animation wiederholen soll, sonst false
	 */
	public void setLoops(final boolean loops)
	{
		this.loops = loops;
	}

	/**
	 * Setzt den Startzeitpunkt der Animation
	 * @param time Startzeitpunkt in Sekunden
	 */
	public void setStartTime(double time)
	{
		startTime = time + delay;
		running = true;
	}

	/**
	 * Setzt den Endwert der Animation
	 * @param toValue Animationsendwert
	 */
	public void setToValue(double toValue)
	{
		this.toValue = toValue;
	}

	@Override
	public void update(double time)
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

		//Berechnung der Animationskurve

		switch (interpolationMode)
		{
			case ANIMATION_CURVE_LINEAR:
				//value = time / duration * (toValue - fromValue) + fromValue
				animationHandler.updateAnimation(animationTime / duration * (toValue - fromValue) + fromValue);
				break;

			case ANIMATION_CURVE_EASE:
				//value = cos(time / duration * PI) * -0.5 + 0.5 * (toValue - fromValue) + fromValue
				animationHandler.updateAnimation(
						(Math.cos(animationTime / duration * 3.141592654f) * -0.5f + 0.5f) * (toValue - fromValue) +
						fromValue);
				break;

			case ANIMATION_CURVE_EASE_IN:
				//value = -cos(time / duration * PI * 0.5) + 1.0 * (toValue - fromValue) + fromValue
				animationHandler.updateAnimation(
						(-Math.cos(animationTime / duration * 3.141592654f * 0.5f) + 1.0f) * (toValue - fromValue) +
						fromValue);
				break;

			case ANIMATION_CURVE_EASE_OUT:
				//value = sin(time / duration * PI * 0.5) * (toValue - fromValue) + fromValue
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
