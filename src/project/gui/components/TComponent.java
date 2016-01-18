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

package project.gui.components;

import project.gui.dynamics.GameloopAction;
import project.gui.dynamics.animation.Animation;
import project.gui.event.TResponder;
import project.gui.graphics.Appearance;
import project.gui.graphics.TGraphics;
import project.gui.layout.TLayoutManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class TComponent extends TResponder
{
	public enum Composite
	{
		SRC_OVER,
		MULTIPLY,
		ADD,
		BRIGHTNESS
	}
	public static Composite ADD = Composite.ADD;
	public static Composite BRIGHTNESS = Composite.BRIGHTNESS;
	public static Composite MULTIPLY = Composite.MULTIPLY;
	public static Composite SRC_OVER = Composite.SRC_OVER;
	protected Rectangle        frame;
	protected boolean          needsDisplay;
	private   List<Animation>  animations;
	private   Color            backgroundColor;
	private   Color            borderColor;
	private   List<TComponent> children;
	private Composite composite;
	private   boolean          drawsBackground;
	private   boolean          drawsBorder;
	private   TLayoutManager   layoutManager;
	private   boolean          maskToBounds;
	private boolean modifyingLayout;
	private   GameloopAction   onAnimationUpdate;
	private   TComponent       parent;
	private   boolean          visible;

	public TComponent()
	{
		children = new ArrayList<>();
		animations = new ArrayList<>();
		frame = new Rectangle();
		needsDisplay = true;
		visible = true;
		setMaskToBounds(true);
		borderColor = Appearance.defaultBorderColor;
		backgroundColor = Appearance.defaultBackgroundColor;
		composite = Composite.SRC_OVER;
	}

	public void add(TComponent child)
	{
		children.add(child);
		addResponder(child);
		child.setParent(this);
		setNeedsLayout();
		setNeedsDisplay(new Rectangle(new Point(), getSize()));
		child.setNeedsDisplay();
	}

	public void add(TComponent child, int index)
	{
		children.add(index, child);
		addResponder(child);
		child.setParent(this);
		setNeedsLayout();
		setNeedsDisplay(new Rectangle(new Point(), getSize()));
		child.setNeedsDisplay();
	}

	public void addAnimation(Animation animation)
	{
		synchronized (animations)
		{
			animations.add(animation);
		}
	}

	public boolean childrenNeedDisplay()
	{
		for (int i = 0; i < children.size(); i++)
		{
			TComponent child = children.get(i);
			if (child.needsDisplay() || child.childrenNeedDisplay())
				return true;
		}
		return false;
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

	public Composite getComposite()
	{
		return composite;
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

	public GameloopAction getOnAnimationUpdate()
	{
		return onAnimationUpdate;
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

	public boolean isVisible(Point p)
	{
		if (new Rectangle(new Point(), getSize()).contains(p))
		{
			if (parent == null)
				return true;
			Point offsetPoint = new Point(p);
			offsetPoint.translate(getPosX(), getPosY());
			return parent.isVisible(offsetPoint);
		}
		else
			return false;
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
		children.remove(child);
		removeResponder(child);
		child.setParent(null);
		setNeedsLayout();
		setNeedsDisplay(new Rectangle(new Point(), getSize()));
	}

	public void removeAll()
	{
		super.removeAll();
		for (int i = 0; i < children.size(); i++)
			children.get(i).removeFromSuperview();
	}

	public void removeAnimation(Animation animation)
	{
		synchronized (animations)
		{
			animations.remove(animation);
		}
	}

	public void removeFromSuperview()
	{
		if (getParent() != null)
			getParent().remove(this);
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

	public void setComposite(final Composite composite)
	{
		this.composite = composite;
		setNeedsDisplay(new Rectangle(new Point(), getSize()));
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
		if (this.frame == frame || this.frame.equals(frame))
			return;
		Rectangle previousFrame = this.frame;
		this.frame = frame;
		setNeedsLayout();
		if (parent != null && !parent.modifyingLayout)
			parent.setNeedsLayout();
		if (previousFrame.intersects(frame))
		{
			Rectangle dirtyRect = frame.union(previousFrame);
			dirtyRect.translate(-getLocation().x, -getLocation().y);
			setNeedsDisplay(dirtyRect);
		}
		else
		{
			Rectangle dirtyRect = new Rectangle(frame);
			setNeedsDisplay(dirtyRect);
			dirtyRect.translate(-getLocation().x, -getLocation().y);
			dirtyRect = new Rectangle(previousFrame);
			dirtyRect.translate(-getLocation().x, -getLocation().y);
			setNeedsDisplay(dirtyRect);
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
		setNeedsLayout();
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
		{
			modifyingLayout = true;
			layoutManager.layoutComponent(this);
			modifyingLayout = false;
		}
	}

	public void setOnAnimationUpdate(final GameloopAction onAnimationUpdate)
	{
		this.onAnimationUpdate = onAnimationUpdate;
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
			for (int i = 0; i < animations.size(); i++)
			{
				Animation animation = animations.get(i);
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
		for (int i = 0; i < children.size(); i++)
		{
			TComponent child = children.get(i);
			child.updateAnimations(time, timeDelta);
		}
		if (onAnimationUpdate != null)
			onAnimationUpdate.update(time, timeDelta);
	}

	protected TComponent getParent()
	{
		return parent;
	}

	protected void paintChildren(TGraphics graphics, Rectangle dirtyRect)
	{
		for (int i = 0; i < children.size(); i++)
		{
			TComponent child = children.get(i);
			if (!child.isVisible())
				continue;
			else if (!child.maskToBounds || child.getFrame().intersects(dirtyRect))
			{
				Rectangle r = new Rectangle(dirtyRect);
				if (child.maskToBounds)
					r = r.intersection(child.getFrame());
				r.translate(-child.getLocation().x, -child.getLocation().y);
				TGraphics childContext = graphics.getChildContext(child.getFrame(), child.maskToBounds);
				childContext.setComposite(child.getComposite());
				child.dispatchRepaint(childContext, r);
			}
		}
	}

	protected void paintComponent(TGraphics graphics, Rectangle dirtyRect)
	{
		if (getFrame() == null)
			return;
		if (drawsBackground)
		{
			graphics.setFillBackground(backgroundColor);
			graphics.setFillChar(' ');
			graphics.fillRect(new Rectangle(new Point(), getSize()).intersection(dirtyRect));
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
			}
			else
			{
				Rectangle parentDirtyRect = new Rectangle(dirtyRect);
				parentDirtyRect.translate(getLocation().x, getLocation().y);
				parent.setNeedsDisplay(parentDirtyRect);
			}
		}
	}

	void dispatchRepaint(TGraphics graphics, Rectangle dirtyRect)
	{
		paintComponent(graphics, dirtyRect);
		resetNeedsDisplay();
		paintChildren(graphics, dirtyRect);
	}

	private void setParent(TComponent parent)
	{
		this.parent = parent;
		setNeedsDisplay();
	}
}