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

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Properties;

/**
 * Klasse zur Verwaltung eines Schluessels im Labyrinth.
 * Das Aufsammeln aller Schluessel ist erforderlich, um
 * ein Level zu beenden.
 */
public class Key extends MapObject implements Serializable
{
	/**
	 * Erzeugung eines Schluessels. Die Konfigurationsdatei muss im Ordner objects/Key.properties
	 * in der Package project.game.data vorhanden sein.
	 * @return Schluessel
	 * @throws IOException wenn die Konfigurationsdatei nicht vorhanden ist oder nicht gelesen werden konnte.
	 */
	public static Key makeKey() throws IOException
	{
		//Properties properties = new Properties();
		//properties.load(Key.class.getResourceAsStream("objects/Key.properties"));
		return new Key(Key.class.getResource("objects/Key.properties"));
	}

	private boolean isCollected;

	/**
	 * Erstellung eines neuen Schluessels aus den sich in den Properties befindenden Werten
	 * DEPRECATED. Use new Key(URL source) instead.
	 * @param properties Konfiguration des Schluessels
	 */
	@Deprecated
	protected Key(Properties properties)
	{
		super(properties);
	}

	/**
	 * Erstellen eines neuen Schluessels aufgrund der Properties-File, welche sich an angegebener Quelle befindet
	 * @param source Pfad zur Konfigurations-Properties-File
	 * @throws IOException wenn die Konfigurationsdatei nicht vorhanden ist oder nicht gelesen werden konnte.
	 */
	protected Key(final URL source) throws IOException
	{
		super(source);
	}

	/**
	 * Sammelt den Schluessel ein.
	 */
	public void collect()
	{
		isCollected = true;
	}

	/**
	 * Gibt an, ob der Schluessel eingesammelt wurde
	 * @return true, wenn der Schluessel eingesammelt wurde, sonst false
	 */
	public boolean isCollected()
	{
		return isCollected;
	}

}
