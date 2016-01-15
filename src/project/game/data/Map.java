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

package project.game.data;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static java.lang.Math.max;
import static java.lang.Math.sqrt;

/**
 * Klasse zur Verwaltung von Kartendaten,
 * Pathfinding und Sichtbarkeitsberechnung
 */
public class Map
{
	/**
	 * Innere Klasse zur Pfadberechnung
	 * Zur Pfadberechnung wird der A*-Pathfinding-Algorithmus verwendet.
	 */
	private class PathFinder
	{
		/**
		 * Pfadelement fuer Pathfinding
		 * Speichert geschaetzte Distanzkosten zum Ziel,
		 * Kosten fuer zurueckgelegte Distanz,
		 * die vorherige PathFindingNode,
		 * sowie die Position
		 */
		//Benchmarks:
		//650ms (linear search for insertion point, unoptimized)
		//160ms (binary search for insertion point)
		//ca. 10ms (binary search for insertion point, 2d array of closed points instead of list
		//using a HashMap with x*height+y as key is slower
		private class PathFindingNode implements Comparable<PathFindingNode>
		{
			private int             distanceCost;
			private PathFindingNode parent;
			private int             stepCost;
			private int             x;
			private int             y;

			/**
			 * Erstellt eine neue PathFindingNode
			 * @param x x-Position
			 * @param y y-Position
			 * @param stepCost Kosten fuer zurueckgelegte Strecke
			 * @param distanceCost Kostenabschaetzung fuer nachfolgende Strecke
			 * @param parent Vorherige PathFindingNode
			 */
			private PathFindingNode(final int x, final int y, final int stepCost, final int distanceCost, PathFindingNode parent)
			{
				this.x = x;
				this.y = y;
				this.stepCost = stepCost;
				this.distanceCost = distanceCost;
				this.parent = parent;
			}

			/**
			 * Vergleichen zu anderer PathFindingNode zur Sortierung von PathFindingNodes in einer Prioritaetswarteschlange
			 */
			@Override
			public int compareTo(final PathFindingNode o)
			{
				if (stepCost + distanceCost == o.stepCost + o.distanceCost)
					return 0;
				else if (stepCost + distanceCost > o.stepCost + o.distanceCost)
					return 1;
				else
					return -1;
			}

			/**
			 * Gibt den Punkt an, an welchem sich das Pfadelement befindet
			 * @return (x, y)-Punkt
			 */
			private Point getPoint()
			{
				return new Point(x, y);
			}

		}

		private final Point                 end;
		private final Point                 start;
		private       PathFindingNode[][]   closed;
		private       int                   height;
		private       List<PathFindingNode> open;
		private       int                   width;

		/**
		 * Initialisiert einen neuen Pathfinder
		 * @param start Ausgangspunkt
		 * @param end Zielpunkt
		 */
		private PathFinder(final Point start, final Point end)
		{
			this.start = start;
			this.end = end;
			//an array takes more space but is up to 10 times faster than a list
			this.closed = new PathFindingNode[getWidth()][getHeight()];
			this.open = new ArrayList<>();
			width = 1;
			height = 1;
		}

		/**
		 * Fuegt einen Pathfinding-Knoten zum Feld der ueberprueften Punkte hinzu
		 * @param node Pathfinding-Knoten
		 */
		private void addClosed(PathFindingNode node)
		{
			//closed.add(node);
			closed[node.x][node.y] = node;
		}

		/**
		 * Fuegt einen Pathfinding-Knoten zur Prioritaetswarteschlange der offenen
		 * (nicht ueberprueften Punkte) hinzu.
		 * @param node neuer, offener Punkt
		 */
		private void addOpen(PathFindingNode node)
		{
			int n = open.size();
			if (n == 0)
			{
				open.add(node);
				return;
			}
			/*
			Bineare Suche nach Einfuegepunkt
			 (4x Geschwindigkeitssteigerung des Gesamtalgorithmus
			 gegenueber linearer Suche)
			 */
			int left  = 0;
			int right = n - 1;
			while (left < right)
			{
				int mid  = (left + right) / 2;
				int comp = node.compareTo(open.get(mid));
				if (comp > 0)
					left = mid + 1;
				else if (comp < 0)
					right = mid - 1;
				else
				{
					right = mid;
					left = mid;
				}
			}
			if (open.get(left).compareTo(node) < 0)
				left++;
			open.add(left, node);
		}

		/**
		 * Gibt den Pathfinding-Knoten zurueck, welcher die guenstigste Position
		 * hat und noch nicht ueberprueft wurde. Die guenstigste Position wird
		 * durch die Summe von Distanzestimation und zurueckgelegter Distanz berechnet.
		 * Der Knoten, bei dem diese Summe am niedrigsten ist, wird zurueckgegeben.
		 * @return Bestmoeglicher Pathfinding-Knoten
		 */
		private PathFindingNode bestOpen()
		{
			return open.get(0);
		}

		/**
		 * Distanzabschaetzung zwischen zwei Punkten.
		 * Als Annaeherung wird hier die euklidische Distanz gewaehlt.
		 * Die tatsaechliche Distanz muss in jedem fall groesser als
		 * die geschaetzte Distanz sein, da sonst die Wahl der optimalen
		 * Pathfinding-Knoten falsch sein kann.
		 * <br><br>
		 * Eine weitere, moegliche Annaeherung ist die sog. Manhattan-Distanz:
		 * abs(x1 - x2) + abs(y1 - y2)<br>
		 * Die Abschaetzung mittels euklidischer Distanz hat sich in den meisten
		 * Faellen als besser erwiesen und fuehrte zu kuerzeren Pfaden.
		 *
		 * @param p1 erster punkt (x1; y1)
		 * @param p2 zweiter punkt (x2; y2)
		 * @return sqrt((x1 - x2)^2 + (y1 - y2)^2)
		 */
		private int distance(Point p1, Point p2)
		{
			//return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
			int dx = p1.x - p2.x;
			int dy = p1.y - p2.y;
			return (int) sqrt(dx * dx + dy * dy);
		}

		/**
		 * Sucht den Pfad von den bei der Initialisierung spezifizierten Punkten.
		 * Implementierung des A*-Pathfinding-Algorithmus
		 * @return Pfad von Startpunkt nach Endpunkt oder null,
		 * wenn kein Pfad gefunden werden kann.
		 */
		private Point[] findPath()
		{
			/*
			Der Startpunkt wird der Liste nicht ueberpruefter Punkte hinzugefuegt
			 */
			addOpen(new PathFindingNode(start.x, start.y, 0, distance(end, start), null));

			/*
			Solange noch offene Punkte vorhanden sind, wird der Pfad weiter gesucht.
			Sind keine offenen Punkte mehr vorhanden und der Endpunkt wurde noch nicht
			erreicht, existiert kein Pfad zwischen Start- und Endposition
			 */
			while (!open.isEmpty())
			{
				/*
				Ueberpruefe die bestmoegliche, offene Position
				 */
				PathFindingNode current = bestOpen();

				/*
				Abbruch, wenn Pfad gefunden
				 */
				if (current.x == end.x && current.y == end.y)
				{
					/*
					Kehre die Reihenfolge des Pfades um
					 */
					Stack<PathFindingNode> inverseNodes = new Stack<>();
					do
					{
						inverseNodes.push(current);
						current = current.parent;
					}
					while (current != null && current.parent != null);

					List<Point> nodes = new ArrayList<>();
					while (!inverseNodes.isEmpty())
						nodes.add(inverseNodes.pop().getPoint());

					if (System.getProperty("com.palleklewitz.underworld.map.showpaths", "false").equalsIgnoreCase("true"))
					{
						if (lastCalculatedPath != null)
							for (Point point : lastCalculatedPath)
								points[point.x][point.y] = 0;
						for (Point node : nodes)
							points[node.x][node.y] = -1;
						lastCalculatedPath = nodes.toArray(new Point[nodes.size()]);
					}

					/*
					Gebe Pfad als Liste von Punkten zurueck
					 */
					return nodes.toArray(new Point[nodes.size()]);
				}

				/*
				Aktueller Punkt wurde nun ueberprueft, entferne aus Liste offener Punkte und
				fuege den geschlossenen Punkten hinzu
				 */
				removeOpen(current);
				addClosed(current);

				/*
				Suche nach erreichbaren Nachbarpositionen
				 */
				Point[] neighbours = getReachableNeighbours(current.getPoint());

				for (Point point : neighbours)
				{
					/*
					Berechne zurueckgelegte Strecke
					 */
					int stepCost = current.stepCost + 1;

					/*
					Falls der Punkt schon ueberprueft wurde und die zurueckgelegte
					Strecke bei der vorhergegangen ueberpruefung geringer war,
					muss der Punkt nicht mehr ueberprueft werden.
					 */
					PathFindingNode neighbourNode = getClosed(point);
					if (neighbourNode != null && stepCost >= neighbourNode.stepCost)
						continue;

					/*
					Pruefe, ob der aktuelle Nachbar schon in der Liste offener Punkte ist und wenn ja,
					pruefe, ob die aktuelle Strecke besser ist. Ist der Punkt nicht in der Liste
					offener Punkte, wird er dieser hinzugefuegt. Ist er schon enthalten und ist ein besserer
					Weg gefunden worden, so wird der Punkt ersetzt
					 */
					neighbourNode = getOpen(point);
					if (neighbourNode == null)
						addOpen(new PathFindingNode(point.x, point.y, stepCost, distance(end, point), current));
					else if (neighbourNode.stepCost > stepCost)
					{
						/*
						Objekt wird entfernt und wieder hinzugefuegt, um die Ordnung aufrecht zu erhalten
						 */
						open.remove(neighbourNode);
						neighbourNode.stepCost = stepCost;
						addOpen(neighbourNode);
					}
				}
			}
			return null;
		}

		/**
		 * Gibt den Pathfinding-Knoten fuer den angegebenen Punkt zurueck oder null,
		 * wenn kein Knoten am angegebenen Punkt vorhanden ist.
		 * @param point zu pruefender Punkt
		 * @return Knoten an gegebenen Punkt oder null
		 */
		private PathFindingNode getClosed(Point point)
		{
			return closed[point.x][point.y];
		}

		/**
		 * Gibt den Pathfinding-Knoten am gegebenen Punkt zurueck oder null,
		 * wenn kein Knoten am gegebenen Punkt vorhanden ist.
		 * @param point zu pruefender Punkt
		 * @return Knoten an gegebenen Punkt oder null
		 */
		private PathFindingNode getOpen(Point point)
		{
			for (PathFindingNode n : open)
				if (point.x == n.x && point.y == n.y)
					return n;
			return null;
		}

		/**
		 * Gibt die Nachbarn zurueck, die von gegebenen Punkt erreicht werden koennen.
		 * Hierbei wird ueberprueft, ob Nachbarpunkte die Karte verlassen oder ob
		 * Hindernisse vorhanden sind.
		 * @param point zu pruefender Punkt
		 * @return erreichbare Nachbarn des zu pruefenden Punktes
		 */
		private Point[] getReachableNeighbours(Point point)
		{
			if (isOutOfBounds(point))
				return new Point[0];
			List<Point> neighbours = new ArrayList<>(4);
			int         x          = point.x;
			int         y          = point.y;

			/*
			if (x > 0 && points[x - 1][y] <= 0)
				neighbours.add(new Point(x - 1, y));
			if (x + 1 < getWidth() && points[x + 1][y] <= 0)
				neighbours.add(new Point(x + 1, y));
			if (y > 0 && points[x][y - 1] <= 0)
				neighbours.add(new Point(x, y - 1));
			if (y + 1 < getHeight() && points[x + 1][y] <= 0)
				neighbours.add(new Point(x, y + 1));
				*/
/*
			boolean blockedLeft   = false;
			boolean blockedRight  = false;
			boolean blockedTop    = false;
			boolean blockedBottom = false;
			/*
			for (int i = 1; i <= width; i++)
			{
				if (!blockedLeft && (x - i < 0 || points[x - i][y] > 0))
					blockedLeft = true;
				if (!blockedRight && (x + i > getWidth() || points[x + i][y] > 0))
					blockedRight = true;
			}
			for (int i = 1; i <= height; i++)
			{
				if (!blockedTop && (y - i < 0 || points[x][y - i] > 0))
					blockedTop = true;
				if (!blockedBottom && (y + i > getHeight() || points[x][y + i] > 0))
					blockedBottom = true;
			}
			if (!blockedLeft)
				neighbours.add(new Point(x - 1, y));
			if (!blockedRight)
				neighbours.add(new Point(x + 1, y));
			if (!blockedTop)
				neighbours.add(new Point(x, y - 1));
			if (!blockedBottom)
				neighbours.add(new Point(x, y + 1));
*/
/*
			for (int x2 = 0; x2 < width; x2++)
				for (int y2 = 0; y2 < height; y2++)
				{
					int dx = x - width / 2 + x2;
					int dy = y - height / 2 + y2;
					blockedLeft = blockedLeft || dx - 1 < 0 || dx - 1 >= getWidth() || points[dx - 1][y] > 0;
					blockedRight = blockedRight || dx + 1 < 0 || dx + 1 >= getWidth() || points[dx + 1][y] > 0;
					blockedTop = blockedTop || dy - 1 < 0 || dy - 1 >= getHeight() || points[x][dy - 1] > 0;
					blockedBottom = blockedBottom || dy + 1 < 0 || dy + 1 >= getHeight() || points[x][dy + 1] > 0;
				}

			if (!blockedLeft)
				neighbours.add(new Point(x - 1, y));
			if (!blockedRight)
				neighbours.add(new Point(x + 1, y));
			if (!blockedTop)
				neighbours.add(new Point(x, y - 1));
			if (!blockedBottom)
				neighbours.add(new Point(x, y + 1));
*/
			int baseX = point.x - width / 2;
			int baseY = point.y - height / 2;

			if (canMoveTo(new Rectangle(baseX - 1, baseY, width, height)))
				neighbours.add(new Point(x - 1, y));
			if (canMoveTo(new Rectangle(baseX, baseY - 1, width, height)))
				neighbours.add(new Point(x, y - 1));
			if (canMoveTo(new Rectangle(baseX + 1, baseY, width, height)))
				neighbours.add(new Point(x + 1, y));
			if (canMoveTo(new Rectangle(baseX, baseY + 1, width, height)))
				neighbours.add(new Point(x, y + 1));

			return neighbours.toArray(new Point[neighbours.size()]);
		}

		/**
		 * Entfernt einen Knoten aus der Liste der offenen Punkte
		 * @param node zu entfernender Knoten
		 */
		private void removeOpen(PathFindingNode node)
		{
			open.remove(node);
		}

		/**
		 * Setzt die Hoehe des Objektes, fuer welches der Pfad erstellt werden soll
		 * @param height maximale Hoehe eines Ganges, welcher im Pfad enthalten sein kann
		 */
		private void setActorHeight(final int height)
		{
			this.height = max(height - 1, 1);
		}

		/**
		 * Setzt die Breite des Objektes, fuer welches der Pfad erstellt werden soll
		 * @param width maximale Breite eines Ganges, welcher im Pfad enthalten sein kann
		 */
		private void setActorWidth(final int width)
		{
			this.width = max(width - 1, 1);
		}
	}

	/**
	 * Hilfsklasse zur Berechnung von sichtbaren Punkten von einem gegebenen Sichtpunkt aus.
	 * Es wurde eine Hilfsklasse erstellt, damit Sichtbarkeitsdaten gecacht werden koennen und
	 * somit mehrfache Berechnungen von gleichen Punkten eliminiert werden koennen.
	 */
	private class VisibilityCalculator
	{
		private Rectangle calculationBounds;
		private Point     pointOfVision;
		//0: not calculated, 1: invisible, 2: visible
		private int[][]   visibilityData;

		/**
		 * Initialisiert eine neue Instanz zur Berechnung von Sichtbarkeiten
		 * @param calculationBounds Begrenzungen des Viewports zur Verringerung des Rechenbedarfs
		 * @param pointOfVision Punkt, von dem aus betrachtet wird
		 */
		private VisibilityCalculator(final Rectangle calculationBounds, final Point pointOfVision)
		{
			this.calculationBounds = calculationBounds;
			if (calculationBounds.getMaxX() >= getWidth())
				calculationBounds.width = getWidth() - calculationBounds.x - 1;
			if (calculationBounds.getMaxY() >= getHeight())
				calculationBounds.height = getHeight() - calculationBounds.y - 1;
			this.pointOfVision = pointOfVision;
			if (calculationBounds.width < 0)
				calculationBounds.width = 0;
			if (calculationBounds.height < 0)
				calculationBounds.height = 0;
			visibilityData = new int[calculationBounds.width][calculationBounds.height];
		}

		/**
		 * Berechnet die Sichtbarkeit zu gegebenem Punkt
		 * gesehen von gegebenm Sichtbarkeitspunkt
		 * @param toPoint zu pruefender Punkt
		 */
		private void calculateVisibilty(Point toPoint)
		{
			//double dx   = pointOfVision.x - toPoint.x;
			//double dy   = pointOfVision.y - toPoint.y;
			double dx   = toPoint.x - pointOfVision.x;
			double dy   = toPoint.y - pointOfVision.y;
			double dist = sqrt(dx * dx + dy * dy);

			double unitDx = dx / dist;
			double unitDy = dy / dist;

			if (dist == 0)
				visibilityData[pointOfVision.x - calculationBounds.x][pointOfVision.y - calculationBounds.y] = 2;

			boolean interrupted = false;

			for (int i = 0; i < dist; i++)
			{
				//int x      = (int) (unitDx * i + toPoint.x);
				//int y      = (int) (unitDy * i + toPoint.y);
				int x      = (int) (unitDx * i + pointOfVision.x);
				int y      = (int) (unitDy * i + pointOfVision.y);
				int arrayX = x - calculationBounds.x;
				int arrayY = y - calculationBounds.y;
				if (interrupted || getPoint(x, y) > 0)
				{
					visibilityData[arrayX][arrayY] = 1;
					interrupted = true;
				}
				else
					visibilityData[arrayX][arrayY] = 2;
				/*if ((getPoint(x, y) > 0 || visibilityData[arrayX][arrayY] == 1))
				{
					interrupted = true;
					break;
				}*/
			}
			visibilityData[toPoint.x - calculationBounds.x][toPoint.y - calculationBounds.y] = interrupted ? 1 : 2;
		}

		/**
		 * Ueberprueft die Sichtbarkeit aller Punkt von gegebenem Sichtbarkeitspunkt
		 * @return bitmaske: [x][y] = true: Punkt ist sichtbar, false, wenn nicht
		 */
		private boolean[][] getVisiblePoints()
		{
			boolean[][] output = new boolean[calculationBounds.width][calculationBounds.height];
			for (int x = calculationBounds.x; x < calculationBounds.getMaxX(); x++)
				for (int y = calculationBounds.y; y < calculationBounds.getMaxY(); y++)
				{
					int arrayX = x - calculationBounds.x;
					int arrayY = y - calculationBounds.y;
					if (visibilityData[arrayX][arrayY] == 0)
					{
						calculateVisibilty(new Point(x, y));
					}
					output[arrayX][arrayY] = visibilityData[arrayX][arrayY] == 2;
				}
			return output;
		}
	}

	private int horizontalScale;
	private Point[] lastCalculatedPath;
	private int[][] points;
	private int verticalScale;

	/**
	 * Erstellt eine neue, leere Karte mit angegebener Breite und Hoehe
	 * @param width Breite der Karte
	 * @param height Hoehe der Karte
	 */
	public Map(int width, int height)
	{
		points = new int[width][height];
	}

	/**
	 * Erstellt eine neue Karte mit angegeben Punkten mit einem Skalierungsfaktor von 1
	 * @param points Werte der Punkte auf der Karte
	 */
	public Map(final int[][] points)
	{
		this(points, 1, 1);
	}

	/**
	 * Erstellt eine neue Karte mit angegebenen Punkte mit dem Skalierungsfaktor (horizontalScale, verticalScale)
	 * Die Skalierung der Werte muss schon vor Initialisierung der Karte stattgefunden haben und dient
	 * lediglich zur Optimierung von Objektsuchen
	 * @param points Werte der Punkte auf der Karte
	 * @param horizontalScale horizontale Skalierung
	 * @param verticalScale vertikale Skalierung
	 */
	public Map(final int[][] points, int horizontalScale, int verticalScale)
	{
		this.points = points;
		this.horizontalScale = horizontalScale;
		this.verticalScale = verticalScale;
	}

	/**
	 * Gibt an, ob ein Objekt an den gegebenen Punkt bewegt werden kann.
	 * Dies ist der Fall, wenn an gegebenem Punkt keine Wand vorhanden ist.
	 * @param point zu pruefender Punkt
	 * @return true, wenn der Punkt erreicht werden kann, sonst false
	 */
	public boolean canMoveTo(Point point)
	{
		return !isOutOfBounds(point) && points[point.x][point.y] <= 0;
	}

	/**
	 * Ueberprueft, ob der gegebene Bereich erreicht werden kann, daher ob fuer jeden Punkt,
	 * welcher Element des Bereichs ist, gilt, dass dieser erreicht werden kann.
	 * @param rectangle zu pruefender Bereich
	 * @return true, wenn der Bereich erreicht werden kann, sonst false
	 */
	public boolean canMoveTo(Rectangle rectangle)
	{
		if (!canMoveTo(new Point(rectangle.x, rectangle.y)))
			return false;
		int right = rectangle.x + rectangle.width - 1;
		if (!canMoveTo(new Point(right, rectangle.y)))
			return false;
		int bottom = rectangle.y + rectangle.height - 1;
		return canMoveTo(new Point(rectangle.x, bottom)) && canMoveTo(new Point(right, bottom));
	}

	/**
	 * Ueberprueft die Sichtbarkeit eines einzelnen Punkts von einem anderen Punkt
	 * @param fromPoint Startpunkt der Sichtstrecke
	 * @param toPoint Endpunkt der Sichtstrecke
	 * @return true, wenn die Sichtstrecke nicht durch Waende unterbrochen wird, sonst false
	 */
	public boolean canSee(Point fromPoint, Point toPoint)
	{
		double dx   = toPoint.x - fromPoint.x;
		double dy   = toPoint.y - fromPoint.y;
		double dist = sqrt(dx * dx + dy * dy);

		double unitDx = dx / dist;
		double unitDy = dy / dist;

		if (dist == 0)
			return true;

		for (int i = 0; i < dist; i++)
		{
			double x = unitDx * i + fromPoint.x;
			double y = unitDy * i + fromPoint.y;
			if (getPoint((int) x, (int) y) > 0)
				return false;
		}
		return true;
	}

	/**
	 * Sucht einen moeglichst kurzen Pfad zwischen den gegebenen Punkten
	 * @param fromPoint Ausgangspunkt
	 * @param toPoint Zielpunkt
	 * @return Pfad oder null, wenn kein Pfad exisitert
	 */
	public Point[] findPath(Point fromPoint, Point toPoint)
	{
		PathFinder pathFinder = new PathFinder(fromPoint, toPoint);
		return pathFinder.findPath();
	}

	/**
	 * Sucht einen moeglichst kurzen Pfad zwischen den gegebenen Punkten.
	 * Beruecksichtigt die Groesse des Aktors
	 * @param fromPoint Ausgangspunkt
	 * @param toPoint Zielpunkt
	 * @return Pfad oder null, wenn kein Pfad exisitert
	 * @param fromPoint Ausgangspunkt
	 * @param toPoint Zielpunkt
	 * @param width Breite des Aktors
	 * @param height Hoehe des Aktors
	 * @return Pfad oder null, wenn kein Pfad exisitert
	 */
	public Point[] findPath(Point fromPoint, Point toPoint, int width, int height)
	{
		PathFinder pathFinder = new PathFinder(fromPoint, toPoint);
		pathFinder.setActorWidth(width);
		pathFinder.setActorHeight(height);
		return pathFinder.findPath();
	}

	/**
	 * Gibt die Begrenzungen von dynamischen Gegnern zurueck
	 * @return Begrenzungen dynamischer Gegner
	 */
	public Rectangle[] getDynamicEnemies()
	{
		return findFeatures(5);
	}

	/**
	 * Gibt die Begrenzungen von Zielen zurueck
	 * @return Zielbegrenzungen
	 */
	public Rectangle[] getFinish()
	{
		return findFeatures(3);
	}

	/**
	 * Gibt die Hoehe der Karte an
	 * @return Hoehe der Karte
	 */
	public int getHeight()
	{
		if (getWidth() == 0)
			return 0;
		else
			return points[0].length;
	}

	/**
	 * Gibt die horizontale Skalierung der Karte an
	 * @return horizontale Skalierung
	 */
	public int getHorizontalScale()
	{
		return horizontalScale;
	}

	/**
	 * Gibt die Begrenzungen von Schluesseln zurueck
	 * @return Schluesselbegrenzungen
	 */
	public Rectangle[] getKeys()
	{
		return findFeatures(6);
	}

	/**
	 * Gibt den Wert eines Punktes an:<br>
	 * 0, wenn leer,<br>
	 * 1, wenn Wand
	 * @param x x-Koordinate
	 * @param y y-Koordinate
	 * @return 0, wenn nichts an dem gegebenen Punkt liegt, sonst 1
	 */
	public int getPoint(int x, int y)
	{
		if (isOutOfBounds(new Point(x, y)))
			return -1;
		else
			return points[x][y];
	}

	/**
	 * Gibt die Begrenzungen des Eingangs an
	 * @return Eingangsbegrenzungen
	 */
	public Rectangle getStart()
	{
		for (int x = 0; x < getWidth(); x+=horizontalScale)
		{
			for (int y = 0; y < getHeight(); y+=verticalScale)
			{
				if (points[x][y] == 2)
					return new Rectangle(x, y, horizontalScale, verticalScale);
			}
		}
		return null;
	}

	/**
	 * Gibt die Begrenzungen von statischen Gegenern zurueck.
	 * @return Begrenzungen statischer Gegner
	 */
	public Rectangle[] getStaticEnemies()
	{
		return findFeatures(4);
	}

	/**
	 * Gibt die vertikale Skalierung an
	 * @return vertikale Skalierung
	 */
	public int getVerticalScale()
	{
		return verticalScale;
	}

	/**
	 * Gibt eine Bitmaske der sichtbaren Punkte zurueck.
	 * @param inRect Begrenzungen der Berechnung
	 * @param fromVisionPoint Sichtpunkt
	 * @return Bitmaske: [x][y] = true, wenn Punkt sichtbar.
	 */
	public boolean[][] getVisiblePoints(Rectangle inRect, Point fromVisionPoint)
	{
		VisibilityCalculator calc = new VisibilityCalculator(inRect, fromVisionPoint);
		return calc.getVisiblePoints();
	}

	/**
	 * Gibt die Breite der Karte an
	 * @return Breite der Karte
	 */
	public int getWidth()
	{
		return points.length;
	}

	/**
	 * Ueberprueft, ob ein Punkt innerhalb der Karte liegt
	 * @param p zu pruefender Punkt
	 * @return true, wenn der Punkt innerhalb der Karte liegt, sonst false
	 */
	public boolean isOutOfBounds(Point p)
	{
		return p.x < 0 || p.y < 0 || p.x >= getWidth() || p.y >= getHeight();
	}

	/**
	 * Entfernt saemtliche Objekte von der Karte.
	 * Diese sind daraufhin nicht mehr abgreifbar.
	 * Hierbei handelt es sich um Eingaenge,
	 * Ausgaenge, Gegner und Schluessel.
	 */
	public void removeFeatures()
	{
		for (int x = 0; x < getWidth(); x++)
			for (int y = 0; y < getHeight(); y++)
				if (points[x][y] > 1)
					points[x][y] = 0;
	}

	/**
	 * Setzt den Punkt auf der Karte an der angegebenen Position (x, y) auf den Wert value
	 * @param x x-Koordinate
	 * @param y y-Koordinate
	 * @param value neuer Wert
	 */
	protected void setPoint(int x, int y, int value)
	{
		if (!isOutOfBounds(new Point(x, y)))
			points[x][y] = value;
	}

	/**
	 * Sucht Begrenzungen von Bereichen mit gegebenem Wert.
	 * @param identifier gegebener Wert
	 * @return Feld von gefundenen Bereichen
	 */
	private Rectangle[] findFeatures(int identifier)
	{
		List<Rectangle> features = new ArrayList<>();
		for (int x = 0; x < getWidth(); x+=horizontalScale)
			for (int y = 0; y < getHeight(); y += verticalScale)
				if (points[x][y] == identifier)
					features.add(new Rectangle(x, y, horizontalScale, verticalScale));
		return features.toArray(new Rectangle[features.size()]);
	}
}
