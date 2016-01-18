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

public class StepController
{
	private long    absoluteValue;
	private boolean active;
	private double  frequency;
	private long    lastUpdateTime;
	private long    maxValue;
	private long    offsetValue;
	private long    updateTime;

	public StepController()
	{

	}

	public StepController(final double frequency)
	{
		this.frequency = frequency;
	}

	public long getAbsoluteValue()
	{
		return active ? (absoluteValue < maxValue ? absoluteValue : maxValue) : 0;
	}

	public double getFrequency()
	{
		return frequency;
	}

	public long getMaxValue()
	{
		return maxValue;
	}

	public int getNumberOfSteps()
	{
		return active ? (int) (updateTime - lastUpdateTime) : 0;
	}

	public long getOffsetValue()
	{
		return offsetValue;
	}

	public boolean isFinished()
	{
		return absoluteValue >= maxValue;
	}

	public boolean requiresUpdate()
	{
		return active && updateTime != lastUpdateTime;
	}

	public void setFrequency(final double frequency)
	{
		if (active)
			throw new IllegalStateException("Frequency of StepController must not be updated while active. Stop first.");
		else if (frequency < 0)
			throw new IllegalArgumentException("Frequency must be greater than or equal to 0");
		this.frequency = frequency;
	}

	public void setMaxValue(final long maxValue)
	{
		this.maxValue = maxValue;
	}

	public void setOffsetValue(final long offsetValue)
	{
		this.offsetValue = offsetValue;
		absoluteValue = offsetValue;
	}

	public void start()
	{
		active = true;
	}

	public void stop()
	{
		active = false;
		lastUpdateTime = -1;
		absoluteValue = 0;
	}

	public void updateTime(double time)
	{
		if (!active)
			return;

		long stepTime = (long) (time * frequency);
		if (lastUpdateTime < 0)
		{
			lastUpdateTime = stepTime;
			updateTime = stepTime;
		}
		lastUpdateTime = updateTime;
		updateTime = stepTime;
		absoluteValue += getNumberOfSteps();
	}
}
