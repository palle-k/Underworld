/******************************************************************************
 * Copyright (c) 2015 Palle Klewitz.                                          *
 * *
 * Permission is hereby granted, free of charge, to any person obtaining      *
 * a copy of this software and associated documentation files                 *
 * (the "Software"), to deal in the Software without restriction,             *
 * including without limitation the rights to use, copy, modify,             *
 * merge, publish, distribute, sublicense, and/or sell copies of             *
 * the Software, and to permit persons to whom the Software                  *
 * is furnished to do so, subject to the following conditions:               *
 * *
 * The above copyright notice and this permission notice shall                *
 * be included in all copies or substantial portions of the Software.         *
 * *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY                         *
 * OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT                        *
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS                     *
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.                             *
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS                        *
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,                      *
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,                      *
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE                            *
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.                    *
 ******************************************************************************/

package project.game.data;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Map
{
	private class PathFinder
	{
		//Benchmarks:
		//650ms (linear search for insertion point, unoptimized)
		//160ms (binary search for insertion point)
		//ca. 10ms (binary search for insertion point, 2d array of closed points instead of list)
		//TODO (optional) implement closed points as HashMap<Integer,Node> with hashFunction x*maxY+y and lookup by hash value
		private class PathFindingNode implements Comparable<PathFindingNode>
		{
			private int distanceCost;
			private PathFindingNode parent;
			private int stepCost;
			private int x;
			private int y;

			private PathFindingNode(final int x, final int y, final int stepCost, final int distanceCost, PathFindingNode parent)
			{
				this.x = x;
				this.y = y;
				this.stepCost = stepCost;
				this.distanceCost = distanceCost;
				this.parent = parent;
			}

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

			private Point getPoint()
			{
				return new Point(x, y);
			}

		}

		private final Point end;
		private final Point start;
		private PathFindingNode[][] closed;
		private int closedCount;
		private List<PathFindingNode> open;

		private PathFinder(final Point start, final Point end)
		{
			this.start = start;
			this.end = end;
			//an array takes more space but is up to 10 times faster than a list
			this.closed = new PathFindingNode[getWidth()][getHeight()];
			this.open = new ArrayList<>();
		}

		private void addClosed(PathFindingNode node)
		{
			//closed.add(node);
			closed[node.x][node.y] = node;
			closedCount++;
		}

		private void addOpen(PathFindingNode node)
		{
			int n = open.size();
			if (n == 0)
			{
				open.add(node);
				return;
			}
			int left = 0;
			int right = n - 1;
			while (left < right)
			{
				int mid = (left + right) / 2;
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

		private PathFindingNode bestOpen()
		{
			return open.get(0);
		}

		/**
		 * Distance estimation (result:=deltaX+deltaY as the navigation takes place on a grid)
		 * @param p1
		 * @param p2
		 * @return abs(x1 - x2) + abs(y1 - y2)
		 */
		private int distance(Point p1, Point p2)
		{
			return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
			//int dx = p1.x - p2.x;
			//int dy = p1.y - p2.y;
			//return (int)Math.sqrt(dx*dx + dy*dy);
		}

		private Point[] findPath()
		{
			addOpen(new PathFindingNode(start.x, start.y, 0, distance(end, start), null));

			while (!open.isEmpty())
			{
				PathFindingNode current = bestOpen();

				if (current.x == end.x && current.y == end.y)
				{
					Stack<PathFindingNode> inverseNodes = new Stack<>();
					do
					{
						inverseNodes.push(current);
						current = current.parent;
					}
					while (current.parent != null);

					List<Point> nodes = new ArrayList<>();
					while (!inverseNodes.isEmpty())
						nodes.add(inverseNodes.pop().getPoint());
					return nodes.toArray(new Point[0]);
				}

				removeOpen(current);
				addClosed(current);

				Point[] neighbours = getReachableNeighbours(current.getPoint());

				for (Point point : neighbours)
				{
					int stepCost = current.stepCost + 1;

					PathFindingNode neighbourNode = getClosed(point);
					if (neighbourNode != null && stepCost >= neighbourNode.stepCost)
						continue;

					neighbourNode = getOpen(point);
					if (neighbourNode == null)
						addOpen(new PathFindingNode(point.x, point.y, stepCost, distance(end, point), current));
					else if (neighbourNode.stepCost > stepCost)
					{
						open.remove(neighbourNode);
						neighbourNode.stepCost = stepCost;
						addOpen(neighbourNode);
					}
				}
			}
			return null;
		}

		private PathFindingNode getClosed(Point point)
		{
			return closed[point.x][point.y];
		}

		private PathFindingNode getOpen(Point point)
		{
			for (PathFindingNode n : open)
				if (point.x == n.x && point.y == n.y)
					return n;
			return null;
		}

		private void removeOpen(PathFindingNode node)
		{
			open.remove(node);
		}
	}

	private int[][] points;

	public Map(int width, int height)
	{
		points = new int[width][height];
	}

	public Map(final int[][] points)
	{
		this.points = points;
	}

	public boolean canMoveTo(Point point)
	{
		if (isOutOfBounds(point))
			return false;
		return points[point.x][point.y] <= 0;
	}

	public boolean canSee(Point fromPoint, Point toPoint)
	{
		double dx   = toPoint.x - fromPoint.x;
		double dy   = toPoint.y - fromPoint.y;
		double dist = Math.sqrt(dx * dx + dy * dy);

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

	public Point[] findPath(Point fromPoint, Point toPoint)
	{
		PathFinder pathFinder = new PathFinder(fromPoint, toPoint);
		return pathFinder.findPath();
	}

	public Point[] getFinish()
	{
		List<Point> ends = new LinkedList<>();
		for (int x = 0; x < getWidth(); x++)
		{
			for (int y = 0; y < getHeight(); y++)
			{
				if (points[x][y] == 3)
					ends.add(new Point(x, y));
			}
		}
		return ends.toArray(new Point[0]);
	}

	public int getHeight()
	{
		if (getWidth() == 0)
			return 0;
		else
			return points[0].length;
	}

	public int getPoint(int x, int y)
	{
		if (isOutOfBounds(new Point(x, y)))
			return -1;
		else
			return points[x][y];
	}

	public Point getStart()
	{
		for (int x = 0; x < getWidth(); x++)
		{
			for (int y = 0; y < getHeight(); y++)
			{
				if (points[x][y] == 2)
					return new Point(x, y);
			}
		}
		return null;
	}

	public int getWidth()
	{
		return points.length;
	}

	public boolean isOutOfBounds(Point p)
	{
		return p.x < 0 || p.y < 0 || p.x >= getWidth() || p.y >= getHeight();
	}

	public void removeFinish()
	{
		for (int x = 0; x < getWidth(); x++)
		{
			for (int y = 0; y < getHeight(); y++)
			{
				if (points[x][y] == 3)
					points[x][y] = 0;
			}
		}
	}

	public void removeStart()
	{
		for (int x = 0; x < getWidth(); x++)
		{
			for (int y = 0; y < getHeight(); y++)
			{
				if (points[x][y] == 2)
					points[x][y] = 0;
			}
		}
	}

	public void setPoint(int x, int y, int value)
	{
		if (!isOutOfBounds(new Point(x, y)))
			points[x][y] = value;
	}

	private Point[] getReachableNeighbours(Point point)
	{
		if (isOutOfBounds(point))
			return new Point[0];
		List<Point> neighbours = new ArrayList<Point>(4);
		int x = point.x;
		int y = point.y;

		if (x > 0 && points[x - 1][y] <= 0)
			neighbours.add(new Point(x - 1, y));
		if (x + 1 < getWidth() && points[x + 1][y] <= 0)
			neighbours.add(new Point(x + 1, y));
		if (y > 0 && points[x][y - 1] <= 0)
			neighbours.add(new Point(x, y - 1));
		if (y + 1 < getHeight() && points[x + 1][y] <= 0)
			neighbours.add(new Point(x, y + 1));
		return neighbours.toArray(new Point[0]);
	}
}
