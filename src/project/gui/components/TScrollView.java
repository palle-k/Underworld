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

import project.gui.layout.TLayoutManager;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;

/**
 * ScrollView - Komponente, die saemtliche Kindkomponenten
 * verschieben kann und somit mehr Raum zur Darstellung von
 * Inhalten bietet.
 */
public class TScrollView extends TComponent
{
	private TComponent contentView;
	private Point      offset;
	private Insets     scrollInsets;

	/**
	 * Erstellt eine neue ScrollView
	 */
	public TScrollView()
	{
		offset = new Point();
		contentView = new TComponent();
		scrollInsets = new Insets(0, 0, 0, 0);
		super.add(contentView);
	}

	/**
	 * Erstellt eine neue ScrollView, wobei die angegebene contentView
	 * die Inhalte der ScrollView enthalten wird.
	 * @param contentView Inhaltskomponente
	 */
	public TScrollView(TComponent contentView)
	{
		this.contentView = contentView;
		scrollInsets = new Insets(0, 0, 0, 0);
		super.add(contentView);
	}

	/**
	 * Fuegt der ScrollView eine Kindkomponente hinzu.
	 * Die Scrollposition wird automatisch auf die
	 * Kindkomponente angewendet, ohne dass sich die interne
	 * Position der Kindkomponente aendert.
	 * @param child neu hinzuzufuegende Kindkomponente
	 */
	@Override
	public void add(final TComponent child)
	{
		contentView.add(child);
		resizeContentView();
	}

	/**
	 * Fuegt der ScrollView eine Kindkomponente am gegebenen Index hinzu.
	 * Die Scrollposition wird automatisch auf die
	 * Kindkomponente angewendet, ohne dass sich die interne
	 * Position der Kindkomponente aendert.
	 * @param child hinzuzufuegende Kindkomponente
	 * @param index Index der neu hinzuzufuegenden Kindkomponente <= getChildren().length
	 */
	@Override
	public void add(final TComponent child, final int index)
	{
		contentView.add(child, index);
		resizeContentView();
	}

	/**
	 * Gibt die Groesse der Inhaltskomponente an.
	 * Diese wird automatisch auf vergroessert und verkleinert, je nachdem wie es
	 * die Kindkomponenten der ScrollView erfordern
	 * @return Groesse des Inhalts der ScrollView
	 */
	public Dimension getContentViewSize()
	{
		return contentView.getSize();
	}

	@Override
	public TLayoutManager getLayoutManager()
	{
		return contentView.getLayoutManager();
	}

	/**
	 * Gibt die Scrollverschiebung an.
	 * @return Scrollverschiebung
	 */
	public Point getOffset()
	{
		return offset;
	}

	/**
	 * Gibt die Scrolleinrueckungen an.
	 * @return Scrolleinrueckungen
	 */
	public Insets getScrollInsets()
	{
		return scrollInsets;
	}

	@Override
	public void remove(final TComponent child)
	{
		contentView.remove(child);
		resizeContentView();
	}

	@Override
	public void removeAll()
	{
		contentView.removeAll();
		resizeContentView();
	}

	@Override
	public void setLayoutManager(final TLayoutManager layoutManager)
	{
		contentView.setLayoutManager(layoutManager);
	}

	/**
	 * Setzt die Scrollverschiebung.
	 * @param offset neue Scrollverschiebung
	 */
	public void setOffset(final Point offset)
	{
		if (this.offset.equals(offset))
			return;
		this.offset = offset;
		contentView.setLocation(-offset.x + scrollInsets.left, -offset.y + scrollInsets.top);
		setNeedsDisplay();
	}

	/**
	 * Setzt die Scrolleinrueckungen
	 * @param scrollInsets neue Scrolleinrueckungen
	 */
	public void setScrollInsets(final Insets scrollInsets)
	{
		this.scrollInsets = scrollInsets;
	}

	/**
	 * Setzt die Scrolleinrueckungen
	 * @param top Einrueckung oben
	 * @param left Einrueckung links
	 * @param bottom Einrueckung unten
	 * @param right Einrueckung rechts
	 */
	public void setScrollInsets(int top, int left, int bottom, int right)
	{
		scrollInsets = new Insets(top, left, bottom, right);
		contentView.setLocation(-offset.x + scrollInsets.left, -offset.y + scrollInsets.top);
		setNeedsDisplay();
	}

	@Override
	public void updateAnimations(final double time)
	{
		super.updateAnimations(time);
		if (contentView.childrenNeedDisplay())
			resizeContentView();
	}

	/**
	 * Aktualisiert die Groesse der Content View
	 * Die Content View wird auf die minimal moegliche Groesse skaliert.
	 */
	private void resizeContentView()
	{
		int maxX = 0;
		int maxY = 0;
		for (TComponent component : contentView.getChildren())
		{
			maxX = Math.max(component.getPosX() + component.getWidth(), maxX);
			maxY = Math.max(component.getPosY() + component.getHeight(), maxY);
		}
		contentView.setSize(maxX, maxY);
	}
}
