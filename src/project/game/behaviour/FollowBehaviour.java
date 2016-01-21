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
import project.util.Direction;
import project.util.PathUtil;

import java.awt.Point;

/**
 * Zielgerichtetes Verfolgungsverhalten<br>
 * Laesst den gesteuerten Aktor ein spezifiziertes Ziel verfolgen.
 * Hierbei beginnt die Verfolgung ab einer beginDistance bis hin zu
 * einer endDistance. Bewegt sich das Ziel vom gesteuerten Aktor weg,
 * muss die maximale Distanz, bevor die Verfolgung endet, kleiner oder
 * gleich maxDistance sein.
 */
public class FollowBehaviour extends TargetedBehaviour
{
	private int beginDistance;

	private int endDistance;

	private StepController horizontalMovementController;

	private double horizontalSpeed;

	private Point lastCheckActorLocation;

	private Point lastCheckPoint;

	private int maxDistance;

	private int pathIndex;

	private Point[] pathToTarget;

	private long requiredDistance;

	private StepController verticalMovementController;

	private double verticalSpeed;

	/**
	 * Erstellt ein neues Verfolgungsverhalten fuer den angegebenen Aktor im angegebenen
	 * Level
	 * @param controlledActor zu steuernder Aktor
	 * @param level Umgebendes Level
	 */
	public FollowBehaviour(final GameActor controlledActor, Level level)
	{
		super(controlledActor, level);
		horizontalMovementController = new StepController();
		verticalMovementController = new StepController();
	}

	@Override
	public void executeBehaviour(final double time)
	{
		//Aktualisiere Schrittsystem
		horizontalMovementController.updateTime(time);
		verticalMovementController.updateTime(time);

		if (!horizontalMovementController.requiresUpdate() && !verticalMovementController.requiresUpdate())
			return;

		//Pruefe, ob ein neuer Pfad berechnet werden muss

		boolean recalculate = false;

		int targetDist = Math.abs(getControlledActor().getCenter().x - getTarget().getCenter().x) +
		                 Math.abs(getControlledActor().getCenter().y - getTarget().getCenter().y) * 2;

		if (targetDist > beginDistance && pathToTarget == null)
			return;
		if (targetDist > maxDistance && pathToTarget != null)
		{
			pathToTarget = null;
			return;
		}

		if (lastCheckActorLocation != null)
		{
			if (!lastCheckActorLocation.equals(getControlledActor().getCenter()))
				recalculate = true;
		}

		if (pathToTarget == null)
		{
			if (lastCheckPoint == null || Math.abs(getTarget().getCenter().x - lastCheckPoint.x) +
			                              Math.abs(getTarget().getCenter().y - lastCheckPoint.y) * 2 >=
			                              requiredDistance)
			{
				recalculate |= targetDist <= beginDistance;
				if (!recalculate)
				{
					lastCheckPoint = getTarget().getCenter();
					requiredDistance = targetDist - beginDistance;
				}
			}
		}
		else if (!PathUtil.pathContains(pathToTarget, getTarget().getCenter()))
			recalculate = true;
		else if (!PathUtil.pathContains(pathToTarget, getControlledActor().getCenter()))
			recalculate = true;

		//Berechne neuen Pfad

		if (recalculate)
		{
			pathIndex = 0;
			Point[] newPath = getLevel().getMap().findPath(
					getControlledActor().getCenter(),
					getTarget().getCenter(),
					getControlledActor().getBounds().width,
					getControlledActor().getBounds().height);
			if (newPath != null && ((pathToTarget != null && PathUtil.pathLength(newPath, 1, 2) > maxDistance) ||
			                        (pathToTarget == null && PathUtil.pathLength(newPath, 1, 2) > beginDistance)))
			{
				lastCheckPoint = getTarget().getCenter();
				requiredDistance = newPath.length - beginDistance;
				pathToTarget = null;
			}
			else
			{
				pathToTarget = newPath;
			}
		}

		//Verfolge Pfad

		if (pathToTarget != null)
		{
			int targetIndex = PathUtil.pathIndex(pathToTarget, getTarget().getCenter());

			if (!getLevel().getMap().canSee(getControlledActor().getCenter(), getTarget().getCenter())
			    || PathUtil.pathLength(pathToTarget, pathIndex, targetIndex, 1, 2) >=
			       endDistance)
			{
				int horizontalSteps = horizontalMovementController.getNumberOfSteps();
				int verticalSteps   = verticalMovementController.getNumberOfSteps();
				int steps           = Math.min(horizontalSteps, verticalSteps);
				if (targetIndex - pathIndex > 0)
					pathIndex += steps;
				else if (pathIndex - targetIndex > 0)
					pathIndex -= steps;

				if (pathIndex < 0)
					pathIndex = 0;
				else if (pathIndex >= pathToTarget.length)
					pathIndex = pathToTarget.length - 1;

				if (horizontalSteps > verticalSteps)
					for (int i = 0; i < horizontalSteps - verticalSteps; i++)
					{
						int step = 0;
						if (targetIndex - pathIndex > 0)
						{
							if (pathIndex + 1 < pathToTarget.length)
								step = 1;
							else
								break;
						}
						else if (targetIndex - pathIndex < 0)
						{
							if (pathIndex >= 1)
								step = -1;
							else
								break;
						}
						if (step != 0)
						{
							Point     start = pathToTarget[pathIndex];
							Point     end   = pathToTarget[pathIndex + step];
							Direction dir   = Direction.direction(start, end);
							if (dir == Direction.LEFT || dir == Direction.RIGHT)
								pathIndex += step;
						}
					}
				else if (verticalSteps > horizontalSteps)
					for (int i = 0; i < verticalSteps - horizontalSteps; i++)
					{
						int step = 0;
						if (targetIndex - pathIndex > 0)
						{
							if (pathIndex + 1 < pathToTarget.length)
								step = 1;
							else
								break;
						}
						else if (targetIndex - pathIndex < 0)
						{
							if (pathIndex >= 1)
								step = -1;
							else
								break;
						}
						if (step != 0)
						{
							Direction dir = Direction.direction(
									pathToTarget[pathIndex],
									pathToTarget[pathIndex + step]);
							if (dir == Direction.UP || dir == Direction.DOWN)
								pathIndex += step;
						}
					}
				if (pathIndex >= pathToTarget.length)
					pathIndex = pathToTarget.length - 1;
				else if (pathIndex < 0)
					pathIndex = 0;
				getControlledActor().enterMovementState();
				getControlledActor().setCenter(pathToTarget[pathIndex]);
			}
		}
		lastCheckActorLocation = getControlledActor().getCenter();
	}

	/**
	 * Gibt die Startdistanz an, bei welcher die Verfolgung beginnt
	 * @return Verfolgungsstartdistanz
	 */
	public int getBeginDistance()
	{
		return beginDistance;
	}

	/**
	 * Gibt die Zieldistanz an, bis zu welcher das Ziel verfolgt wird
	 * @return Verfolgungszieldistanz
	 */
	public int getEndDistance()
	{
		return endDistance;
	}

	/**
	 * Gibt die horizontale Geschwindigkeit des Aktors an
	 * @return Horizontale Geschwindigkeit in Schritten pro Sekunde
	 */
	public double getHorizontalSpeed()
	{
		return horizontalSpeed;
	}

	/**
	 * Gibt die maximale Distanz an, bei welcher die Verfolgung aktiv bleibt
	 * @return Maximaldistanz waehrend der Verfolgung
	 */
	public int getMaxDistance()
	{
		return maxDistance;
	}

	/**
	 * Gibt die vertikale Geschwindigkeit des Aktors an
	 * @return Vertikale Geschwindigkeit in Schritten pro Sekunde
	 */
	public double getVerticalSpeed()
	{
		return verticalSpeed;
	}

	/**
	 * Gibt an, ob aktuell ein Ziel verfolgt wird.
	 * @return true, wenn ein Ziel verfolgt wird, false, wenn nicht
	 */
	public boolean isFollowing()
	{
		return pathToTarget != null;
	}

	/**
	 * Setzt die Distanz, die maximal zum Start der Verfolgung zum Ziel
	 * vorhanden sein kann
	 * @param beginDistance Verfolgungsstartdistanz
	 */
	public void setBeginDistance(final int beginDistance)
	{
		this.beginDistance = beginDistance;
	}

	/**
	 * Setzt die Distanz, bis zu welcher das Ziel verfolgt wird
	 * @param endDistance Zieldistanz
	 */
	public void setEndDistance(final int endDistance)
	{
		this.endDistance = endDistance;
	}

	/**
	 * Setzt die horizontale Geschwindigkeit des Aktors.
	 * Veraenderungen werden erst nach einem Neustart des Verhaltens aktiv
	 * @param horizontalSpeed Horizontale Geschwindigkeit in Schritten pro Sekunde
	 */
	public void setHorizontalSpeed(final double horizontalSpeed)
	{
		this.horizontalSpeed = horizontalSpeed;
	}

	/**
	 * Setzt die maximale Distanz, die der Aktor waehrend der Verfolgung
	 * zum Ziel besitzen kann, bevor die Verfolgung beendet wird.
	 * @param maxDistance Maximale Verfolgungsaufrechterhaltungsdistanz
	 */
	public void setMaxDistance(final int maxDistance)
	{
		this.maxDistance = maxDistance;
	}

	/**
	 * Setzt die vertikale Geschwindigkeit des Aktors.
	 * Veraenderungen werden erst nach einem Neustart des Verhaltens aktiv
	 * @param verticalSpeed Vertikale Geschwindigkeit in Schritten pro Sekunde
	 */
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
	protected void skipExecution(final double time)
	{
		horizontalMovementController.updateTime(time);
		verticalMovementController.updateTime(time);
	}
}
