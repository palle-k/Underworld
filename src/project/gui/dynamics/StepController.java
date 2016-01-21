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

/**
 * Controller zur Steuerung von diskreten Schritten in zeitlicher Abhaengigkeit
 */
public class StepController
{
	private long    absoluteValue;
	private boolean active;
	private double  frequency;
	private long    lastUpdateTime;
	private long    maxValue;
	private long    offsetValue;

	private double updateSeconds;

	private long    updateTime;

	/**
	 * Erstellt einen neuen StepController
	 */
	public StepController()
	{

	}

	/**
	 * Erstellt einen neuen StepController mit angegebener Schrittfrequenz in Hz
	 * @param frequency Schrittfrequenz in Hz
	 */
	public StepController(final double frequency)
	{
		this.frequency = frequency;
	}

	/**
	 * Gibt den Wert an Schritten an, die insgesamt zurueckgelegt wurden
	 * @return Gesamtschrittanzahl
	 */
	public long getAbsoluteValue()
	{
		return active ? (absoluteValue < maxValue ? absoluteValue : maxValue) : 0;
	}

	/**
	 * Gibt die Aktualisierungsfrequenz an
	 * @return Aktualisierungsfrequenz in Hz
	 */
	public double getFrequency()
	{
		return frequency;
	}

	/**
	 * Gibt den maxilen Absolutwert an
	 * @return maximaler Absolutwert
	 */
	public long getMaxValue()
	{
		return maxValue;
	}

	/**
	 * Gibt die Anzahl erforderlicher Schritte seit der letzten Aktualisierung zurueck
	 * @return Erforderliche Anzahl von Schritten
	 */
	public int getNumberOfSteps()
	{
		return active ? (int) (updateTime - lastUpdateTime) : 0;
	}

	/**
	 * Gibt die Gesamtzahl von Schritten zurueck
	 * @return Gesamtschrittzahl
	 */
	public long getOffsetValue()
	{
		return offsetValue;
	}

	/**
	 * Gibt an, ob die Gesamtzahl von Schritten den Maximalwert erreicht hat
	 * @return true, wenn absoluteValue >= maxValue
	 */
	public boolean isFinished()
	{
		return absoluteValue >= maxValue;
	}

	public void pause(double seconds)
	{
		updateSeconds += seconds;
	}

	/**
	 * Gibt an, ob eine Aktualisierung noetig ist, da die erforderliche Anzahl
	 * von Schritten groesser als null ist.
	 * @return true, wenn eine Aktualisierung erforderlich ist, sonst false
	 */
	public boolean requiresUpdate()
	{
		return active && updateTime != lastUpdateTime;
	}

	/**
	 * Setzt die Aktualisierungsfrequenz.
	 * Das Veraendern der Frequenz ist nur erlaubt, wenn der StepController inaktiv ist.
	 * @param frequency Aktualisierungsfrequenz in Hz
	 */
	public void setFrequency(final double frequency)
	{
		if (active)
			throw new IllegalStateException("Frequency of StepController must not be updated while active. Stop first.");
		else if (frequency < 0)
			throw new IllegalArgumentException("Frequency must be greater than or equal to 0");
		this.frequency = frequency;
	}

	/**
	 * Setzt den Maximalwert an Schritten, die zurueckgelegt werden koennen
	 * @param maxValue Maximalschrittanzahl
	 */
	public void setMaxValue(final long maxValue)
	{
		this.maxValue = maxValue;
	}

	/**
	 * Setzt die Gesamtzahl an Schritten, die zurueckgelegt wurden
	 * @param offsetValue Gesamtschrittzahl
	 */
	public void setOffsetValue(final long offsetValue)
	{
		this.offsetValue = offsetValue;
		absoluteValue = offsetValue;
	}

	/**
	 * Startet den StepController.
	 * Das Veraendern der Frequenz ist erst nach dem Stoppen wieder moeglich.
	 */
	public void start()
	{
		active = true;
	}

	/**
	 * Stoppt den StepController.
	 * Das Veraendern der Frequenz ist jetzt moeglich.
	 */
	public void stop()
	{
		active = false;
		lastUpdateTime = -1;
		absoluteValue = 0;
	}

	/**
	 * Aktualisiert die Zeit des StepControllers.
	 * @param time neuer Zeitwert in Sekunden
	 */
	public void updateTime(double time)
	{
		if (!active)
			return;

		long stepTime = (long) (time * frequency);

		if (updateSeconds >= time)
		{
			lastUpdateTime = stepTime;
			updateTime = stepTime;
			return;
		}
		if (lastUpdateTime < 0)
		{
			lastUpdateTime = stepTime;
			updateTime = stepTime;
		}
		lastUpdateTime = updateTime;
		updateTime = stepTime;
		absoluteValue += getNumberOfSteps();
		updateSeconds = time;
	}
}
