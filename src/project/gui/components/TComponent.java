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

/**
 * Basiskomponente fuer Darstellung von jeglichen Objekten
 * in einem Lanterna-Terminal.
 * Kann in einer Komponentenhierarchie verwendet werden.
 */
public class TComponent extends TResponder
{
	public enum Composite
	{
		SRC_OVER,
		MULTIPLY,
		ADD,
		BRIGHTNESS
	}

	/**
	 * Beim Zeichnen der Komponente wird die Farbe der Komponente der uebergeordneten Komponente hinzuaddiert
	 */
	public static Composite ADD = Composite.ADD;

	/**
	 * Beim Zeichnen der Komponente wird die Farbe der Komponente mit der
	 * Helligkeit der uebergeordneten Komponente multipliziert
	 */
	public static Composite BRIGHTNESS = Composite.BRIGHTNESS;

	/**
	 * Beim Zeichnen der Komponente wird die Farbe der Komponente mit der
	 * Farbe der uebergeordneten Komponente multipliziert
	 */
	public static Composite MULTIPLY = Composite.MULTIPLY;

	/**
	 * Beim Zeichnen der Komponente ueberdeckt diese die Farbe der uebergeordneten Komponente
	 */
	public static Composite SRC_OVER = Composite.SRC_OVER;

	/**
	 * Abmessungen der Komponente
	 */
	protected Rectangle        frame;

	/**
	 * Angabe, ob die Komponente neu gezeichnet werden muss
	 */
	protected boolean          needsDisplay;

	private List<GameloopAction> animationUpdates;

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

	private   TComponent       parent;
	private   boolean          visible;

	/**
	 * Erstellt eine neue Displaykomponente.
	 * Diese hat die in Appearance spezifizierte Hintergrundfarbe,
	 * die Position (0; 0), die Groesse (0; 0), maskiert Inhalte innerhalb der Abmessungen,
	 * hat die Standard-Kantenfarbe, welche in Appearance festgelegt ist, ist sichtbar und
	 * wird mit SRC_OVER gezeichnet.
	 */
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
		animationUpdates = new ArrayList<>();
	}

	/**
	 * Fuegt eine Kindkomponente hinzu.<br>
	 * Die hinzugefuegte Komponente befindet sich ueber allen anderen Komponenten.
	 * Ueberschneiden sich diese Komponente und eine andere Kindkomponente, ist
	 * diese Komponente sichtbar.<br>
	 * Die Kindkomponente wird ueber dem Inhalt dieser Komponente gezeichnet.
	 * @param child neu hinzuzufuegende Kindkomponente
	 */
	public void add(TComponent child)
	{
		children.add(child);
		addResponder(child);
		child.setParent(this);
		setNeedsLayout();
		setNeedsDisplay();
		child.setNeedsLayout();
		child.setNeedsDisplay();
	}

	/**
	 * Fuegt eine Kindkomponente am angegebenen Index hinzu.<br>
	 * Je groesser der Index ist, desto weniger Ebenen koennen diese ueberdecken.<br>
	 * Die Kindkomponente wird ueber dem Inhalt dieser Komponente gezeichnet.
	 * @param child hinzuzufuegende Kindkomponente
	 * @param index Index der neu hinzuzufuegenden Kindkomponente <= getChildren().length
	 */
	public void add(TComponent child, int index)
	{
		children.add(index, child);
		addResponder(child);
		child.setParent(this);
		setNeedsLayout();
		setNeedsDisplay(new Rectangle(new Point(), getSize()));
		child.setNeedsLayout();
		child.setNeedsDisplay();
	}

	/**
	 * Fuegt eine neue Animation hinzu.
	 * Dies ist noetig, damit die Animation aktualisiert werden kann.
	 * Die Animation wird umgehend gestartet.
	 * @param animation hinzuzufuegende Animation
	 */
	public void addAnimation(Animation animation)
	{
		synchronized (animations)
		{
			animations.add(animation);
		}
	}

	/**
	 * Fuegt eine Aktion hinzu, die beim Aktualisieren der Animationen ausgefuehrt werden soll
	 *
	 * @param onAnimationUpdate Aktion fuer Animationsupdate
	 */
	public void addOnAnimationUpdate(final GameloopAction onAnimationUpdate)
	{
		animationUpdates.add(onAnimationUpdate);
	}

	/**
	 * Gibt an, ob Kindkomponenten dieser Komponente neu gezeichnet werden muessen.
	 * @return true, wenn ein Neuzeichnen erforderlich ist, sonst false.
	 */
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

	/**
	 * Gibt an, ob die Komponente ihren Hintergrund zeichnet.
	 * @return true, wenn der Hintergrund gezeichnet wird, sonst false
	 */
	public boolean drawsBackground()
	{
		return drawsBackground;
	}

	/**
	 * Gibt an, ob die Komponente ihre Kanten zeichnet.
	 * @return true, wenn die Kanten gezeichnet werden, sonst false
	 */
	public boolean drawsBorder()
	{
		return drawsBorder;
	}

	/**
	 * Gibt die Hintergrundfarbe der Komponente an
	 * @return Hintergrundfarbe der Komponente
	 */
	public Color getBackgroundColor()
	{
		return backgroundColor;
	}

	/**
	 * Gibt die Kantenfarbe der Komponente an
	 * @return Kantenfarbe der Komponente
	 */
	public Color getBorderColor()
	{
		return borderColor;
	}

	/**
	 * Gibt die Kindkomponenten der Komponente an.
	 * @return Kindkomponenten
	 */
	public TComponent[] getChildren()
	{
		return children.toArray(new TComponent[0]);
	}

	/**
	 * Gibt die Mischmethode an, mit der die Komponente gezeichnet wird.
	 * @return Mischmethode der Komponente
	 */
	public Composite getComposite()
	{
		return composite;
	}

	/**
	 * Gibt die Begrenzungen der Komponente an
	 * @return Komponentenbegrenzungen
	 */
	public Rectangle getFrame()
	{
		return frame.getBounds();
	}

	/**
	 * Gibt die Hoehe der Komponente an
	 * @return Komponentenhoehe
	 */
	public int getHeight()
	{
		return getSize().height;
	}

	/**
	 * Gibt den LayoutManager an, mit welchem die Kindkomponenten in dieser
	 * Komponente ausgerichtet werden.
	 * @return LayoutManager
	 */
	public TLayoutManager getLayoutManager()
	{
		return layoutManager;
	}

	/**
	 * Gibt die Position der Komponente in der uebergeordneten Komponente an
	 * @return Position der Komponente
	 */
	public Point getLocation()
	{
		return frame.getLocation();
	}

	/**
	 * Gibt die uebergeordnete Komponente an.
	 * Ist die Komponente die hoechstgeordnete Komponente
	 * oder in keiner Komponentenhierarchie vorhanden, ist die
	 * uebergeordnete Komponente null.
	 *
	 * @return Uebergeordnete Komponente
	 */
	public TComponent getParent()
	{
		return parent;
	}

	/**
	 * Gibt die x-Komponente der Position der Komponente an
	 * @return x-Komponente der Position
	 */
	public int getPosX()
	{
		return getLocation().x;
	}

	/**
	 * Gibt die y-Komponente der Position der Komponente an
	 * @return y-Komponente der Position
	 */
	public int getPosY()
	{
		return getLocation().y;
	}

	/**
	 * Gibt die Groesse der Komponente an
	 * @return Groesse der Komponente
	 */
	public Dimension getSize()
	{
		return frame.getSize();
	}

	/**
	 * Gibt die Breite der Komponente an
	 * @return Breite der Komponente
	 */
	public int getWidth()
	{
		return getSize().width;
	}

	/**
	 * Gibt an, ob die Komponente gezeichnet werden soll.
	 * @return true, wenn die Komponente sichtbar ist, sonst false
	 */
	public boolean isVisible()
	{
		return visible;
	}

	/**
	 * Gibt an, ob der gegebene Punkt im Koordinatensystem dieser Komponente auf dem Bildschirm sichtbar ist.
	 * @param p zu pruefender Punkt
	 * @return true, wenn der Punkt sichtbar ist, sonst false
	 */
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

	/**
	 * Gibt an, ob die Inhalte der Komponente sowie saemtlicher Kindkomponenten
	 * nicht ueber die Begrenzungen dieser Komponente hinausgehen koennen
	 * @return true, wenn die Begrenzungen die Inhalte der Komponente maskieren, sonst false
	 */
	public boolean masksToBounds()
	{
		return maskToBounds;
	}

	/**
	 * Gibt an, ob die Komponente neu gezeichnet werden muss
	 * @return true, wenn Neuzeichnen erforderlich ist, sonst false
	 */
	public boolean needsDisplay()
	{
		return needsDisplay;
	}

	/**
	 * Entfernt die angegebene Kindkomponente.
	 * Saemtliche Animationen dieser Kindkomponente und ihrer
	 * Kindkomponenten werden nach Entfernung nicht mehr aktualisiert.
	 * @param child zu entfernende Kindkomponente
	 */
	public void remove(TComponent child)
	{
		children.remove(child);
		removeResponder(child);
		child.setParent(null);
		setNeedsLayout();
		setNeedsDisplay();
	}

	/**
	 * Entfernt alle Kindkomponenten aus diese Komponente.
	 * Saemtliche Animationen von Kindkomponenten und deren Kindkomponenten
	 * werden nach Entfernung nicht mehr aktualisiert.
	 */
	public void removeAll()
	{
		super.removeAll();
		while (!children.isEmpty())
			children.get(0).removeFromSuperview();
	}

	/**
	 * Entfernt die angegebene Animation aus dieser Komponente.
	 * Die entfernte Animation wird nicht laenger aktualisiert.
	 * @param animation zu entfernende Animation.
	 */
	public void removeAnimation(Animation animation)
	{
		synchronized (animations)
		{
			animations.remove(animation);
		}
	}

	/**
	 * Entfernt die Komponente aus ihrer uebergeordneten Komponente.
	 * Saemtliche Animationen dieser Komponente und ihrer Kindkomponenten
	 * werden nach Entfernung aus der uebergeordneten Komponente nicht
	 * mehr aktualisiert
	 */
	public void removeFromSuperview()
	{
		if (getParent() != null)
			getParent().remove(this);
	}

	/**
	 * Entfernt eine Aktion, die beim Aktualisieren der Animationen ausgefuehrt wird
	 *
	 * @param onAnimationUpdate zu entfernende Aktion
	 */
	public void removeOnAnimationUpdate(final GameloopAction onAnimationUpdate)
	{
		animationUpdates.remove(onAnimationUpdate);
	}

	/**
	 * Setzt die Hintergrundfarbe der Komponente.
	 * Der Hintergrund wird nur gezeichnet, wenn das Zeichnen
	 * des Hintergrunds mit setDrawsBorder(true) aktiviert wird.
	 * @param color Hintergrundfarbe
	 */
	public void setBackgroundColor(Color color)
	{
		this.backgroundColor = color;
		setNeedsDisplay();
	}

	/**
	 * Setzt die Komponenten der Hintergrundfarbe der Komponente.
	 * Der Hintergrund wird nur gezeichnet, wenn das Zeichnen
	 * des Hintergrunds mit setDrawsBorder(true) aktiviert wird.
	 * @param r Rotkomponente der Hintergrundfarbe
	 * @param g Gruenkomponente der Hintergrundfarbe
	 * @param b Blaukomponente der Hintergrundfarbe
	 */
	public void setBackgroundColor(int r, int g, int b)
	{
		setBackgroundColor(new Color(r, g, b));
	}

	/**
	 * Setzt die Kantenfarbe der Komponente.
	 * Die Kanten der Komponente werden nur gezeichnet,
	 * wenn das Zeichnen der Kanten mit setDrawsBorder(true)
	 * aktiviert wird
	 * @param borderColor Kantenfarbe
	 */
	public void setBorderColor(Color borderColor)
	{
		this.borderColor = borderColor;
	}

	/**
	 * Setzt die Mischmethode, mit der diese Komponente ueber die uebergeordnete
	 * Komponente gezeichnet wird.
	 * @param composite Mischmethode
	 */
	public void setComposite(final Composite composite)
	{
		this.composite = composite;
		setNeedsDisplay();
	}

	/**
	 * Legt fest, ob der Hintergrund der Komponente gezeichnet werden soll.
	 * @param drawsBackground true, wenn der Hintergrund gezeichnet werden soll, sonst false
	 */
	public void setDrawsBackground(boolean drawsBackground)
	{
		this.drawsBackground = drawsBackground;
		setNeedsDisplay();
	}

	/**
	 * Legt fest, ob die Kanten der Komponente gezeichnet werden sollen.
	 * @param drawsBorder true, wenn die Kanten gezeichnet werden sollen, sonst false
	 */
	public void setDrawsBorder(boolean drawsBorder)
	{
		this.drawsBorder = drawsBorder;
		setNeedsDisplay();
	}

	/**
	 * Setzt die Begrenzungen der Komponente
	 * @param frame Komponentenbegrenzungen
	 */
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

	/**
	 * Setzt die Komponentenbegrenzungen
	 * @param point Position der Komponente
	 * @param size Groesse der Komponente
	 */
	public void setFrame(Point point, Dimension size)
	{
		setFrame(new Rectangle(point, size));
	}

	/**
	 * Setzt die Hoehe der Komponente
	 * @param height Komponentenhoehe
	 */
	public void setHeight(int height)
	{
		setSize(frame.width, height);
	}

	/**
	 * Setzt den LayoutManager, mit dem Kindkomponenten in dieser
	 * Komponente ausgerichtet werden sollen.
	 * @param layoutManager LayoutMangager zur Ausrichtung von Kindkomponenten
	 */
	public void setLayoutManager(TLayoutManager layoutManager)
	{
		this.layoutManager = layoutManager;
		setNeedsLayout();
	}

	/**
	 * Setzt die Position der Komponente
	 * @param location neue Komponentenposition
	 */
	public void setLocation(Point location)
	{
		setFrame(location, getSize());
	}

	/**
	 * Setzt die Position der Komponente
	 * @param x x-Koordinate der neuen Komponentenposition
	 * @param y y-Koordinate der neuen Komponentenposition
	 */
	public void setLocation(int x, int y)
	{
		setLocation(new Point(x, y));
	}

	/**
	 * Legt fest, ob die Inhalte der Komponente und von Kindkomponenten
	 * nicht ausserhalb der Begrenzungen gezeichnet werden koennen.
	 * @param maskToBounds true, wenn nicht ueber die Begrenzungen hinaus gezeichnet
	 *                     werden darf, sonst false
	 */
	public void setMaskToBounds(boolean maskToBounds)
	{
		this.maskToBounds = maskToBounds;
		setNeedsDisplay(null);
	}

	/**
	 * Fuehrt ein Neuzeichnen der Komponente herbei.
	 * Dabei wird der gesamte Komponenteninhalt neugezeichnet.
	 */
	public void setNeedsDisplay()
	{
		Rectangle dirtyRect = getFrame();
		dirtyRect.translate(-getPosX(), -getPosY());
		setNeedsDisplay(dirtyRect);
	}

	/**
	 * Gibt an, dass die Komponente im angegebenen Bereich neu gezeichnet werden muss.
	 * Ein Neuzeichnen der Komponente wird herbeigefuehrt.
	 *
	 * @param dirtyRect neuzuzeichnender Bereich
	 */
	public void setNeedsDisplay(Rectangle dirtyRect)
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

	/**
	 * Fuehrt ein Neuberechnen des Layouts der Komponente herbei.
	 * Dabei wird mithilfe des gesetzten LayoutMangers die Position
	 * von Kindkomponenten modifiziert. Ist der LayoutManager null,
	 * hat der Aufruf dieser Methode keine Auswirkungen
	 */
	public void setNeedsLayout()
	{
		if (layoutManager != null)
		{
			modifyingLayout = true;
			layoutManager.layoutComponent(this);
			modifyingLayout = false;
		}
	}

	/**
	 * Setzt die x-Komponente der Position der Komponente
	 * @param x x-Komponente der Position
	 */
	public void setPosX(int x)
	{
		setLocation(x, frame.y);
	}

	/**
	 * Setzt die y-Komponente der Position der Komponente
	 * @param y y-Komponente der Position
	 */
	public void setPosY(int y)
	{
		setLocation(frame.x, y);
	}

	/**
	 * Setzt die Groesse der Komponente
	 * @param size Komponentengroesse
	 */
	public void setSize(Dimension size)
	{
		setFrame(getLocation(), size);
	}

	/**
	 * Setzt die Groesse der Komponente
	 * @param width Breite der Komponente
	 * @param height Hoehe der Komponente
	 */
	public void setSize(int width, int height)
	{
		setSize(new Dimension(width, height));
	}

	/**
	 * Legt fest, ob die Komponente sichtbar sein soll
	 * @param visible true, wenn die Komponente sichtbar sein soll, sonst false
	 */
	public void setVisible(boolean visible)
	{
		this.visible = visible;
		setNeedsDisplay();
	}

	/**
	 * Setzt die Breite der Komponente
	 * @param width Komponentenbreite
	 */
	public void setWidth(int width)
	{
		setSize(width, frame.height);
	}

	/**
	 * Aktualisiert die Animationen der Komponente.
	 * Wenn eine Animationsupdateaktion festgelegt wurde, wird diese Aufgerufen.
	 * @param time Zeit der Aktualisierung
	 * @param timeDelta Zeit seit der letzten Aktualisierung
	 */
	public void updateAnimations(double time)
	{
		for (int i = 0; i < animations.size(); i++)
		{
			Animation animation = animations.get(i);
			if (!animation.isRunning())
				animation.setStartTime(time);
			animation.update(time);
		}
		for (int i = 0; i < animations.size(); i++)
			if (animations.get(i).isFinished(time))
			{
				animations.remove(i);
				i--;
			}
		for (int i = 0; i < animationUpdates.size(); i++)
			animationUpdates.get(i).update(time);
		for (int i = 0; i < children.size(); i++)
			children.get(i).updateAnimations(time);
	}

	/**
	 * Zeichnet die Kindkomponenten der Komponente neu
	 * @param graphics Grafikkontext zum Zeichnen
	 * @param dirtyRect Neuzuzeichnender Bereich
	 */
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

	/**
	 * Zeichnet die Inhalte der Komponente neu.
	 * @param graphics Grafikkontext, in den gezeichnet wird
	 * @param dirtyRect Neuzuzeichnender Bereich
	 */
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

	/**
	 * Setzt die Notwendigkeit des Neuzeichnens der Komponente zurueck.
	 */
	protected void resetNeedsDisplay()
	{
		needsDisplay = false;
	}

	/**
	 * Fuehrt ein Neuzeichnen der Komponente und von Kindkomponenten im angegebenen Bereich durch
	 * @param graphics Grafikkontext, in den gezeichnet wird
	 * @param dirtyRect neuzuzeichnender Bereicht
	 */
	void dispatchRepaint(TGraphics graphics, Rectangle dirtyRect)
	{
		paintComponent(graphics, dirtyRect);
		paintChildren(graphics, dirtyRect);
		resetNeedsDisplay();
	}

	/**
	 * Setzt die uebergeordnete Komponente dieser Komponente
	 * @param parent neue uebergeordnete Komponente
	 */
	private void setParent(TComponent parent)
	{
		if (this.parent != null && parent != null)
			throw new IllegalStateException(
					"Component already has a parent componend. cannot be child of more than one component.");
		this.parent = parent;
		setNeedsDisplay();
	}
}