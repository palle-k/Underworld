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

package project.util;

import java.awt.Point;

import static java.lang.Math.abs;

/**
 * Hilfsfunktionen fuer das Behandeln von Pfaden
 */
public class PathUtil
{
	/**
	 * Gibt zurueck, ob der Pfad den angegeben Punkt enthaelt
	 * @param path zu untersuchender Pfad
	 * @param point zu pruefender Punkt
	 * @return true, wenn der Pfad den Punkt enthaelt, false, wenn nicht
	 */
	public static boolean pathContains(Point[] path, Point point)
	{
		if (path == null)
			return false;
		for (Point p : path)
			if (p.equals(point))
				return true;
		return false;
	}

	/**
	 * Gibt den Index zurueck, an dem sich der Punkt auf gegebenem Pfad befindet
	 * @param path zu untersuchender Pfad
	 * @param point zu findender Punkt
	 * @return index des zu findenden Punkts oder -1, wenn Punkt nicht gefunden
	 */
	public static int pathIndex(Point[] path, Point point)
	{
		if (path == null)
			return -1;
		/*
		Optimierte Suche:
		Da die Distanz zwischen zwei Punkten auf dem Pfad immer 1 ist, kann
		der Index um die Manhattan-Distanz zwischen dem Punkt und dem aktuellen
		Pfadelement inkrementiert werden, da das zu suchende Element fruehestens
		an dieser Position zu finden ist.
		 */
		for (int i = 0; i < path.length; i += abs(path[i].x - point.x) + abs(path[i].y - point.y))
			if (point.equals(path[i]))
				return i;
		return -1;
	}

	/**
	 * Gibt die Laenge des Pfades zwischen den angegebenen Indices mit den angegebenen Skalierungsfaktoren an.
	 * @param path Pfad, der geprueft werden soll
	 * @param start Index des Startpunktes auf dem Pfad
	 * @param end Index des Endpunktes auf dem Pfad
	 * @param horizontalFactor Skalierungsfaktor horizontal
	 * @param verticalFactor Skalierungsfaktor vertikal
	 * @return dx * horizontalFactor + dy * verticalFactor
	 */
	public static int pathLength(Point[] path, int start, int end, int horizontalFactor, int verticalFactor)
	{
		if (path == null)
			return 0;
		if (start < 0 || end < 0)
			return 0;
		if (start > end)
			return pathLength(path, end, start, horizontalFactor, verticalFactor);
		int dist = 0;
		for (int i = start; i < end; i++)
		{
			int dx = Math.abs(path[i].x - path[i + 1].x);
			int dy = Math.abs(path[i].y - path[i + 1].y);
			dist += dx * horizontalFactor + dy * verticalFactor;
		}
		return dist;

	}

	/**
	 * Gibt die Laenge des Pfades an.
	 * @param path Pfad, welcher geprueft werden soll
	 * @param horizontalFactor Skalierungsfaktor horizontal
	 * @param verticalFactor Skalierungsfaktor vertikal
	 * @return dx * horizontalFactor + dy * verticalFactor
	 */
	public static int pathLength(Point[] path, int horizontalFactor, int verticalFactor)
	{
		return pathLength(path, 0, path.length - 1, horizontalFactor, verticalFactor);
	}
}
