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

package project.game.behaviour;

import project.game.data.GameActor;
import project.game.data.Level;
import project.gui.dynamics.StepController;

import java.awt.Rectangle;

public class RandomMovementBehaviour extends Behaviour
{
	private StepController horizontalMovementController;

	private double horizontalSpeed;

	private StepController verticalMovementController;

	private double verticalSpeed;

	public RandomMovementBehaviour(final GameActor controlledActor, final Level level)
	{
		super(controlledActor, level);
		horizontalMovementController = new StepController();
		verticalMovementController = new StepController();
	}

	public double getHorizontalSpeed()
	{
		return horizontalSpeed;
	}

	public double getVerticalSpeed()
	{
		return verticalSpeed;
	}

	public void setHorizontalSpeed(final double horizontalSpeed)
	{
		this.horizontalSpeed = horizontalSpeed;
	}

	public void setVerticalSpeed(final double verticalSpeed)
	{
		this.verticalSpeed = verticalSpeed;
	}

	@Override
	public void startBehaviour()
	{
		if (isExecuting())
			return;
		super.startBehaviour();
		horizontalMovementController.setFrequency(horizontalSpeed);
		verticalMovementController.setFrequency(verticalSpeed);
		horizontalMovementController.start();
		verticalMovementController.start();
	}

	@Override
	public void stopBehaviour()
	{
		super.stopBehaviour();
		horizontalMovementController.stop();
		verticalMovementController.stop();
	}

	@Override
	protected void executeBehaviour(final double time)
	{
		horizontalMovementController.updateTime(time);
		verticalMovementController.updateTime(time);
		Rectangle bounds = new Rectangle(getControlledActor().getBounds());
		for (int i = 0; i < horizontalMovementController.getNumberOfSteps(); i++)
		{
			Rectangle newBounds = new Rectangle(bounds);
			newBounds.translate((int) (Math.random() * 4 - 2), 0);
			getControlledActor().enterMovementState();
			if (!getLevel().getMap().canMoveTo(newBounds))
				break;
			else
				bounds = newBounds;
		}
		for (int i = 0; i < verticalMovementController.getNumberOfSteps(); i++)
		{
			Rectangle newBounds = new Rectangle(bounds);
			newBounds.translate(0, (int) (Math.random() * 4 - 2));
			getControlledActor().enterMovementState();
			if (!getLevel().getMap().canMoveTo(newBounds))
				break;
			else
				bounds = newBounds;
		}
		getControlledActor().setBounds(bounds);
	}

	@Override
	protected void skipExecution(final double time)
	{
		horizontalMovementController.updateTime(time);
		verticalMovementController.updateTime(time);
	}
}
