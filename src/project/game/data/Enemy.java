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

import project.game.controllers.EnemyController;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Properties;

import static project.game.localization.LocalizedString.LocalizedString;

/**
 * Klasse fuer einen Gegner
 * Diese Klasse speichert den Zustand sowie das Aussehen eines Gegners.
 * Ein Gegner kann ueber einen EnemyController gesteuert werden.
 * Moegliche Gegner sind dynamische oder statische Gegner. Diese
 * werden aus entsprechenden Property-Files geladen.
 * @see EnemyController
 */
public class Enemy extends GameActor implements Serializable
{
	/**
	 * Erzeugt einen neuen Gegner mit angegebenem Namen der Konfigurationsdatei ohne Suffix.
	 * Hierzu muss die Datei name.properties im Ordner objects, welcher ein Subordner der Package project.game.data
	 * ist, gesichert sein.
	 * @param name Konfigurationsname
	 * @return Gegner mit Eigenschaften aus der Konfigurationsdatei
	 * @throws IOException wenn die Datei nicht vorhanden ist oder nicht gelesen werden kann
	 */
	public static Enemy create(String name) throws IOException
	{
		return new Enemy(Enemy.class.getResource("objects/" + name + ".properties"));
	}

	/**
	 * Erzeugt einen dynamischen Gegner. Hierzu muss die Datei PossessedKnight.properties
	 * im Ordner objects, welcher ein Subordner der Package project.game.data ist,
	 * gesichert sein.
	 * @return dynamischer Gegner
	 * @throws IOException wenn die Datei nicht vorhanden ist oder nicht gelesen werden kann
	 */
	public static Enemy createDynamic() throws IOException
	{
		//Properties properties = new Properties();
		//properties.load(Enemy.class.getResourceAsStream("objects/PossessedKnight.properties"));
		//return new Enemy(properties);
		return new Enemy(Enemy.class.getResource("objects/PossessedKnight.properties"));
	}

	/**
	 * Erzeugt einen statischen Gegner. Hierzu muss die Datei GiantHogweed.properties
	 * im Ordner objects, welcher ein Subordner der Package project.game.data ist,
	 * gesichert sein.
	 * @return statischer Gegner
	 * @throws IOException wenn die Datei nicht vorhanden ist oder nicht gelesen werden kann
	 */
	public static Enemy createStatic() throws IOException
	{
		//Properties properties = new Properties();
		//properties.load(Enemy.class.getResourceAsStream("objects/GiantHogweed.properties"));
		return new Enemy(Enemy.class.getResource("objects/GiantHogweed.properties"));
	}

	private transient int    follow_range; //Maximum distance to continue following the player
	private transient int    level;
	private transient String name;
	private transient int    visionRange;

	/**
	 * Erzeugt einen neuen Gegner mit den Eigenschaften, die im angegebenen Properties-Objekt
	 * angegeben sind.<br>
	 * DEPRECATED. Instanzieren ueber new Enemy(URL source) moeglich.
	 * @param properties Eigenschaften des Gegners
	 */
	@Deprecated
	public Enemy(Properties properties)
	{
		super(properties);
		follow_range = Integer.parseInt(properties.getProperty("follow_range"));
		visionRange = Integer.parseInt(properties.getProperty("vision_range"));
		name = properties.getProperty("name");
	}

	/**
	 * Erzeugt einen Gegner aus einer Properties-File, die sich am Ort source befindet.
	 * @param source Quellurl fuer Eigenschaften des Gegners
	 * @throws IOException Datei existiert nicht oder kann nicht gelesen werden.
	 */
	public Enemy(URL source) throws IOException
	{
		super(source);
	}

	/**
	 * Gibt die maximale Distanz an, die der Gegner vom Spieler entfernt sein kann,
	 * bevor die Verfolgung des Spielers aufhoert.
	 * @return Maximale Distanz bei Verfolgung
	 */
	public int getFollowRange()
	{
		return follow_range;
	}

	/**
	 * Gibt das Level des Gegners an.
	 * @return Level des Gegners
	 */
	@Override
	public int getLevel()
	{
		return level;
	}

	/**
	 * Gibt den Namen des Gegners an
	 * @return Name des Gegners
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gibt die maximale Distanz an, bei der der Gegner die Verfolgung des Spielers
	 * beginnen kann.
	 * @return maximale Sichtdistanz
	 */
	public int getVisionRange()
	{
		return visionRange;
	}

	/**
	 * Methode zur Wiederherstellung von Eigenschaften des Gegners
	 */
	@Override
	protected void restore()
	{
		super.restore();

		follow_range = Integer.parseInt(properties.getProperty("follow_range"));
		visionRange = Integer.parseInt(properties.getProperty("vision_range"));
		name = LocalizedString(properties.getProperty("name"));
		level = Integer.parseInt(properties.getProperty("level", "1"));
	}
}
