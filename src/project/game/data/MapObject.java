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

import project.util.StringUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Properties;

/**
 * Objekt, welches sich in einem Level auf der Karte befinden kann.
 * Hierbei kann es sich zum Beispiel um Schluessel, Gegner oder den Spieler handeln.
 *
 * SUBKLASSENERSTELLUNG:
 * Wird eine Subklasse erstellt, so muss diese saemtliche Eigenschaften, die nicht wiederhergestellt
 * werden muessen, als transient-Felder gespeichert werden, da die Serialisierung eines solchen Objekts
 * minimal klein gehalten werden muss. Auf die Properties kann mithilfe der Instanzvariable properties
 * zur Initialisierung zugegriffen werden.
 *
 * @see Key
 * @see Enemy
 * @see Player
 */
public abstract class MapObject implements Serializable
{

	/**
	 * Die Begrenzungen des Objekts
	 */
	protected           Rectangle         bounds;

	/**
	 * Die Farbe des Objekts
	 */
	protected transient Color             color;

	/**
	 * Delegate fuer Rueckmeldungen ueber Statusaenderungen
	 */
	protected transient MapObjectDelegate delegate;

	/**
	 * Properties zum Laden des Objekts
	 */
	protected transient Properties        properties;

	/**
	 * Grundstatus des Objekts
	 */
	protected transient String            restingState;

	/**
	 * URL, von welcher das Objekt geladen wird
	 */
	private             URL               source;

	/**
	 * Laed ein MapObject aus den gegebenen Properties
	 * DEPRECATED. Use new MapObject(URL source) instead
	 * @param properties Quellproperties
	 */
	@Deprecated
	protected MapObject(Properties properties)
	{
		this.properties = properties;
		bounds = new Rectangle();
		
		restingState = properties.getProperty("resting");
		bounds.setSize(StringUtils.getStringDimensions(restingState));

		int r = Integer.parseInt(properties.getProperty("color_r"));
		int g = Integer.parseInt(properties.getProperty("color_g"));
		int b = Integer.parseInt(properties.getProperty("color_b"));

		color = new Color(r, g, b);

	}

	/**
	 * Erstellt ein neues MapObject und laed die entsprechenden Eigenschaften
	 * aus einer Properties-File, welche an gegebenem Ort liegt
	 * @param source Ort der Properties-File
	 * @throws IOException wenn die Datei nicht existiert oder nicht gelesen werden kann
	 */
	protected MapObject(URL source) throws IOException
	{
		if (source == null)
			throw new NullPointerException("Source must not be null");

		this.source = source;

		properties = new Properties();
		InputStream stream = source.openStream();
		properties.load(stream);

		restore();

		properties = null;
	}

	/**
	 * Gibt die Begrenzungen des Objekts an
	 * @return Begrenzungen
	 */
	public Rectangle getBounds()
	{
		return new Rectangle(bounds);
	}

	/**
	 * Gibt den Mittelpunkt des Aktors an.
	 *
	 * @return Mittelpunkt des Aktors
	 */
	public Point getCenter()
	{
		return new Point((int) getBounds().getCenterX(), (int) getBounds().getCenterY());
	}

	/**
	 * Gibt die Farbe des Objekts an
	 * @return Objektfarbe
	 */
	public Color getColor()
	{
		return color;
	}

	/**
	 * Gibt die Delegate fuer Rueckmeldungen ueber Statusaenderungen an
	 * @return Delegate
	 */
	public MapObjectDelegate getDelegate()
	{
		return delegate;
	}

	/**
	 * Gibt den Grundzustand des Objekts an
	 * @return Grundzustand
	 */
	public String getRestingState()
	{
		return restingState;
	}

	/**
	 * Setzt die Begrenzungen des Objekts
	 * @param bounds Objektbegrenzungen
	 */
	public void setBounds(final Rectangle bounds)
	{
		if (this.bounds.equals(bounds))
			return;
		this.bounds = bounds;
		if (delegate != null)
			delegate.mapObjectDidMove(this);
	}

	/**
	 * Setzt den Mittelpunkt des Aktors.
	 * Hierbei findet eine Translation des gesamten Aktors statt.
	 * @param newCenter Neuer Mittelpunkt des Aktors
	 */
	public void setCenter(Point newCenter)
	{
		Point     currentCenter = getCenter();
		int       dx            = newCenter.x - currentCenter.x;
		int       dy            = newCenter.y - currentCenter.y;
		Rectangle bounds        = getBounds();
		bounds.translate(dx, dy);
		setBounds(bounds);
	}

	/**
	 * Setzt die Delegate fuer Rueckmeldungen ueber Statusaenderungen
	 * @param delegate Delegate
	 */
	public void setDelegate(final MapObjectDelegate delegate)
	{
		this.delegate = delegate;
	}

	/**
	 * Setzt den Punkt, an dem sich das Objekt befindet.
	 * @param location neue Position
	 */
	public void setLocation(Point location)
	{
		Rectangle bounds = new Rectangle(getBounds());
		bounds.setLocation(location);
		setBounds(bounds);
	}

	/**
	 * Setzt die Groesse, welche das Objekt besitzt
	 * @param size neue Objektgroesse
	 */
	public void setSize(Dimension size)
	{
		Rectangle bounds = new Rectangle(getBounds());
		bounds.setSize(size);
		setBounds(bounds);
	}

	/**
	 * Stellt das Objekt aus der Properties-File properties
	 * wieder her. Ist zu ueberschreiben, wenn eine Subklasse erstellt wird,
	 * welche zusaetzliche transient-Felder nach einer Deserialisierung wiederherstellen
	 * muss.
	 */
	protected void restore()
	{
		if (bounds == null)
			bounds = new Rectangle();

		restingState = properties.getProperty("resting");
		bounds.setSize(StringUtils.getStringDimensions(restingState));

		int r = Integer.parseInt(properties.getProperty("color_r"));
		int g = Integer.parseInt(properties.getProperty("color_g"));
		int b = Integer.parseInt(properties.getProperty("color_b"));

		color = new Color(r, g, b);
	}

	/**
	 * Wird bei Deserialisierung aufgerufen und loest Wiederherstellung aus der gespeicherten
	 * Properties-File-URL aus
	 * @param in ObjectInputStream, aus welchem das Objekt gelesen wird
	 * @throws IOException falls ein streambasierter Fehler auftritt
	 * @throws ClassNotFoundException falls ein Deserialisierungsfehler auftritt
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		properties = new Properties();
		properties.load(source.openStream());

		restore();

		properties = null;
	}

}
