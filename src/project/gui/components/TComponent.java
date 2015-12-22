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

package project.gui.components;

import project.gui.dynamics.animation.Animation;
import project.gui.event.TEvent;
import project.gui.graphics.TGraphics;
import project.gui.layout.TLayoutManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TComponent /*extends TResponder*/
{
	protected Rectangle frame;
	protected boolean needsDisplay;
	private boolean allowsFirstResponder;
	private List<Animation> animations;
	private Color backgroundColor;
	private Color borderColor;
	private List<TComponent> children;
	private boolean drawsBackground;
	private boolean drawsBorder;
	private boolean firstResponder;
	private TLayoutManager layoutManager;
	private boolean maskToBounds;
	private TComponent parent;
	private boolean visible;

	public TComponent()
	{
		children = new ArrayList<>();
		animations = new ArrayList<>();
		frame = new Rectangle();
		needsDisplay = true;
		visible = true;
		setMaskToBounds(true);
	}

	public void add(TComponent child)
	{
		synchronized (children)
		{
			children.add(child);
		}
		child.setParent(this);
		setNeedsLayout();
		child.setNeedsDisplay();
	}

	public void add(TComponent child, int index)
	{
		synchronized (children)
		{
			children.add(index, child);
		}
		child.setParent(this);
		setNeedsLayout();
		child.setNeedsDisplay();
	}

	public void addAnimation(Animation animation)
	{
		animations.add(animation);
	}

	public boolean drawsBackground()
	{
		return drawsBackground;
	}

	public boolean drawsBorder()
	{
		return drawsBorder;
	}

	public Color getBackgroundColor()
	{
		return backgroundColor;
	}

	public Color getBorderColor()
	{
		return borderColor;
	}

	public TComponent[] getChildren()
	{
		return children.toArray(new TComponent[0]);
	}

	public Rectangle getFrame()
	{
		return frame;
	}

	public int getHeight()
	{
		return getSize().height;
	}

	public TLayoutManager getLayoutManager()
	{
		return layoutManager;
	}

	public Point getLocation()
	{
		return frame.getLocation();
	}

	public int getPosX()
	{
		return getLocation().x;
	}

	public int getPosY()
	{
		return getLocation().y;
	}

	public Dimension getSize()
	{
		return frame.getSize();
	}

	public int getWidth()
	{
		return getSize().width;
	}

	public boolean isVisible()
	{
		return visible;
	}

	public boolean masksToBounds()
	{
		return maskToBounds;
	}

	public boolean needsDisplay()
	{
		return needsDisplay;
	}

	public void remove(TComponent child)
	{
		synchronized (children)
		{
			children.remove(child);
		}
		child.setParent(null);
		setNeedsLayout();
		setNeedsDisplay();
	}

	public void removeAnimation(Animation animation)
	{
		animations.remove(animation);
	}

	public void setBackgroundColor(Color color)
	{
		this.backgroundColor = color;
		setNeedsDisplay();
	}

	public void setBackgroundColor(int r, int g, int b)
	{
		setBackgroundColor(new Color(r, g, b));
	}

	public void setBorderColor(Color borderColor)
	{
		this.borderColor = borderColor;
	}

	public void setDrawsBackground(boolean drawsBackground)
	{
		this.drawsBackground = drawsBackground;
		setNeedsDisplay();
	}

	public void setDrawsBorder(boolean drawsBorder)
	{
		this.drawsBorder = drawsBorder;
		setNeedsDisplay();
	}

	public void setFrame(Rectangle frame)
	{
		Rectangle previousFrame = this.frame;
		this.frame = frame;
		setNeedsLayout();
		if (previousFrame.intersects(frame))
		{
			Rectangle dirtyRect = frame.union(previousFrame);
			dirtyRect.translate(-getLocation().x, -getLocation().y);
			setNeedsDisplay(dirtyRect);
		} else
		{
			Rectangle dirtyRect = new Rectangle(frame);
			setNeedsDisplay(dirtyRect);
			dirtyRect = new Rectangle(previousFrame);
			setNeedsDisplay(previousFrame);
		}
	}

	public void setFrame(Point point, Dimension size)
	{
		setFrame(new Rectangle(point, size));
	}

	public void setHeight(int height)
	{
		setSize(frame.width, height);
	}

	public void setLayoutManager(TLayoutManager layoutManager)
	{
		this.layoutManager = layoutManager;
	}

	public void setLocation(Point location)
	{
		setFrame(location, getSize());
	}

	public void setLocation(int x, int y)
	{
		setLocation(new Point(x, y));
	}

	public void setMaskToBounds(boolean maskToBounds)
	{
		this.maskToBounds = maskToBounds;
		setNeedsDisplay(null);
	}

	public void setNeedsDisplay()
	{
		Rectangle dirtyRect = new Rectangle(getFrame());
		dirtyRect.translate(-getPosX(), -getPosY());
		setNeedsDisplay(dirtyRect);
	}

	public void setNeedsLayout()
	{
		if (layoutManager != null)
			synchronized (children)
			{
				layoutManager.layoutComponent(this);
			}
	}

	public void setPosX(int x)
	{
		setLocation(x, frame.y);
	}

	public void setPosY(int y)
	{
		setLocation(frame.x, y);
	}

	public void setSize(Dimension size)
	{
		setFrame(getLocation(), size);
	}

	public void setSize(int width, int height)
	{
		setSize(new Dimension(width, height));
	}

	public void setVisible(boolean visible)
	{
		this.visible = visible;
		setNeedsDisplay();
	}

	public void setWidth(int width)
	{
		setSize(width, frame.height);
	}

	public void updateAnimations(double time, double timeDelta)
	{
		synchronized (animations)
		{
			for (Animation animation : animations)
			{
				if (!animation.isRunning())
					animation.setStartTime(time);
				animation.update(time, timeDelta);
			}
			for (int i = 0; i < animations.size(); i++)
				if (animations.get(i).isFinished(time))
				{
					animations.remove(i);
					i--;
				}

		}
		synchronized (children)
		{
			for (TComponent child : children)
			{
				child.updateAnimations(time, timeDelta);
			}
		}
	}

	protected TComponent getParent()
	{
		return parent;
	}

	protected void keyDown(TEvent event)
	{
		//implement in subclass
	}

	protected void keyUp(TEvent event)
	{
		//implement in subclass
	}

	protected void paintComponent(TGraphics graphics)
	{
		if (getFrame() == null)
			return;
		if (drawsBackground)
		{
			graphics.setFillBackground(backgroundColor);
			graphics.setFillChar(' ');
			graphics.fillRect(new Rectangle(new Point(), getSize()));
		}
		if (drawsBorder)
		{
			graphics.setStrokeBackground(borderColor);
			graphics.setStrokeChar(' ');
			graphics.strokeRect(new Rectangle(new Point(), getSize()));
		}
	}

	protected void resetNeedsDisplay()
	{
		needsDisplay = false;
	}

	protected void setAllowsFirstResponder(boolean allowsFirstResponder)
	{
		this.allowsFirstResponder = allowsFirstResponder;
	}

	protected void setNeedsDisplay(Rectangle dirtyRect)
	{
		needsDisplay = true;
		if (parent != null)
		{
			if (dirtyRect == null)
			{
				if (masksToBounds())
					parent.setNeedsDisplay(getFrame());
				else
					parent.setNeedsDisplay(null);
			} else
			{
				Rectangle parentDirtyRect = new Rectangle(dirtyRect);
				parentDirtyRect.translate(getLocation().x, getLocation().y);
				parent.setNeedsDisplay(parentDirtyRect);
			}
		}
	}

	void dispatchRepaint(TGraphics graphics, Rectangle dirtyRect)
	{
		paintComponent(graphics);
		resetNeedsDisplay();
		synchronized (children)
		{
			for (TComponent child : children)
				if (!child.isVisible())
					continue;
				else if (!child.maskToBounds || child.getFrame().intersects(dirtyRect))
				{
					Rectangle r = new Rectangle(dirtyRect);
					if (child.maskToBounds)
						r = r.intersection(child.getFrame());
					r.translate(-child.getLocation().x, -child.getLocation().y);
					child.dispatchRepaint(graphics.getChildContext(child.getFrame(), child.maskToBounds), r);
				}
		}
	}

	private void becomeFirstResponder()
	{
		firstResponder = true;
	}

	private boolean canBecomeFirstResponder()
	{
		return allowsFirstResponder;
	}

	private void dispatchEvent(TEvent event)
	{
		if (isFirstResponder())
		{
			if (event.getState() == TEvent.KEY_DOWN)
				keyDown(event);
			else if (event.getState() == TEvent.KEY_UP)
				keyUp(event);
		}
	}

	private TComponent getFirstResponder()
	{
		if (isFirstResponder())
			return this;
		else
		{
			for (TComponent child : children)
				if (child.includesFirstResponder())
					return child.getFirstResponder();
		}
		return null;
	}

	private boolean hasNextResponder()
	{
		boolean foundFirstResponder = false;
		boolean hasNext = false;
		for (int i = 0; i < children.size(); i++)
		{
			if (foundFirstResponder || children.get(i).includesFirstResponder())
				foundFirstResponder = true;
			if (foundFirstResponder)
			{
				hasNext |= children.get(i).hasNextResponder();
			}
		}
		return hasNext;
	}

	private boolean includesFirstResponder()
	{
		if (isFirstResponder())
			return true;
		for (TComponent child : children)
			if (child.includesFirstResponder())
				return true;
		return false;
	}

	private boolean isFirstResponder()
	{
		return firstResponder;
	}

	private void setParent(TComponent parent)
	{
		this.parent = parent;
		setNeedsDisplay();
	}

	private void shiftNextFirstResponder()
	{
		boolean found = false;
		boolean assigned = false;
		for (int i = 0; i < children.size(); i++)
		{
			if (children.get(i).includesFirstResponder())
				found = true;
			if (found && children.get(i).hasNextResponder())
			{
				children.get(i).shiftNextFirstResponder();
				assigned = false;
				break;
			}
		}
		if (found && !assigned && canBecomeFirstResponder())
			becomeFirstResponder();

	}
}