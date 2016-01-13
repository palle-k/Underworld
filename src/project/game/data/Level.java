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

import java.awt.Rectangle;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

/**
 * Klasse zur Verwaltung des Datenmodells eines Levels
 * Speicherung von Gegnern, dem Spieler, Schluesseln,
 * der Karte, Ein- und Ausgaengen.
 */
public class Level implements Serializable
{
	private Enemy[] enemies;
	private transient Rectangle entranceBounds;
	private transient Rectangle[] exitBounds;
	private Key[]   keys;
	private transient Map     map;
	private Player  player;
	private URL     source;

	/**
	 * Erstellt ein neues Level mit der Karten-Properties-File an gegebener URL.
	 * Die Karten-Properties-File muss folgende Keys besitzen:
	 * <ul>
	 *     <li><b>Width</b> - Breite des Levels</li>
	 *     <li><b>Height</b> - Hoehe des Levels</li>
	 *     <li><b>x,y</b> - Jeden Punkt der nicht leer ist:
	 *         <ul>
	 *             <li><b>0</b> - Der Punkt ist eine Wand</li>
	 *             <li><b>1</b> - Der Punkt ist ein Eingang</li>
	 *             <li><b>2</b> - Der Punkt ist ein Ausgang</li>
	 *             <li><b>3</b> - An dem Punkt befindet sich ein statischer Gegner</li>
	 *             <li><b>4</b> - An dem Punkt befindet sich ein bewegter Gegner</li>
	 *             <li><b>5</b> - An dem Punkt befindet sich ein Schluessel</li>
	 *         </ul>
	 *     </li>
	 * </ul>
	 * @param url Pfad zur Quelldatei
	 * @throws IOException wenn die Quelldatei nicht vorhanden oder lesbar ist.
	 */
	public Level(URL url) throws IOException
	{
		source = url;
		load();
	}

	/**
	 * Gibt an, wie viele Schluessel schon eingesammelt wurden.
	 * @return Anzahl eingesammelter Schluessel
	 */
	public int getCollectedKeyCount()
	{
		return (int) Arrays.stream(keys).filter(Key::isCollected).count();
	}

	/**
	 * Gibt saemtliche Gegner, welche im Level vorhanden sind, zurueck.
	 * @return Gegner
	 */
	public Enemy[] getEnemies()
	{
		if (enemies == null)
			try
			{
				load();
			}
			catch (IOException e)
			{
				throw new RuntimeException("Map could not be loaded.", e);
			}
		return enemies;
	}

	/**
	 * Gibt die Begrenzungen des Eingangs an. Der Spieler muss an dieser Position starten.
	 * @return Begrenzungen des Eingangs
	 */
	public Rectangle getEntranceBounds()
	{
		if (entranceBounds == null)
			try
			{
				load();
			}
			catch (IOException e)
			{
				throw new RuntimeException("Map could not be loaded.", e);
			}
		return entranceBounds;
	}

	/**
	 * Gibt die Begrenzungen aller moeglichen Ausgaenge an.
	 * Ein Spieler muss, um das Level zu beenden, einen dieser
	 * Ausgaenge erreichen und alle Schluessel eingesammelt haben.
	 * @return Begrenzungen aller Ausgaenge
	 */
	public Rectangle[] getExitBounds()
	{
		if (exitBounds == null)
			try
			{
				load();
			}
			catch (IOException e)
			{
				throw new RuntimeException("Map could not be loaded.", e);
			}
		return exitBounds;
	}

	/**
	 * Gibt die Hoehe der Karte zurueck
	 * @return Hoehe der Karte
	 */
	public int getHeight()
	{
		return getMap().getHeight();
	}

	/**
	 * Gibt alle Schluessel zurueck, die sich auf der Karte befinden.
	 * Diese werden auch zurueckgegeben, falls diese schon eingesammelt wurden.
	 * @return Alle Schluessel
	 */
	public Key[] getKeys()
	{
		if (keys == null)
			try
			{
				load();
			}
			catch (IOException e)
			{
				throw new RuntimeException("Map could not be loaded.", e);
			}
		return keys;
	}

	/**
	 * Gibt die zugrunde liegende Karte zurueck.
	 * Diese ermoeglicht Pathfinding und Berechnung von Sichtbarkeiten.
	 * @return Karte des Levels
	 */
	public Map getMap()
	{
		if (map == null)
			try
			{
				load();
			}
			catch (IOException e)
			{
				throw new RuntimeException("Map could not be loaded.", e);
			}
		return map;
	}

	/**
	 * Gibt den Wert eines Punktes des Levels an.
	 * Moegliche Werte:
	 * <ol start="0">
	 *     <li>An dem Punkt (x, y) befindet sich nichts</li>
	 *     <li>An dem Punkt (x, y) befindet sich eine Wand</li>
	 * </ol>
	 * Objekte, welche im Level verteilt sind, werden nicht angegeben.
	 * @param x x-Koordinate des zu testenden Punkts
	 * @param y y-Koordinate des zu testenden Punkts
	 * @return 0, wenn an dem Punkt (x, y) nichts vorhanden ist, sonst 1
	 */
	public int getPixel(int x, int y)
	{
		return getMap().getPoint(x, y);
	}

	/**
	 * Gibt das Spielerobjekt des Levels zurueck.
	 * Dieser verwaltet Ebenen fuer Angriffe, sowie den Status des Spielers.
	 * @return Spieler
	 */
	public Player getPlayer()
	{
		if (player == null)
			try
			{
				load();
			}
			catch (IOException e)
			{
				throw new RuntimeException("Map could not be loaded.", e);
			}
		return player;
	}

	/**
	 * Gibt die Quelle des Levels zurueck.
	 * @return Quelle des Levels
	 */
	public URL getSource()
	{
		return source;
	}

	/**
	 * Gibt die Breite der Karte zurueck.
	 * @return Breite der Karte
	 */
	public int getWidth()
	{
		return getMap().getWidth();
	}

	/**
	 * Laed das Level aus der zur Initialisierung mitgeteilten Quellurl,
	 * skaliert dieses hoch und initialisiert saemtliche Objekte, welche sich auf der
	 * Karte befinden.
	 * @throws IOException Wenn die Quelldatei nicht geladen werden konnte.
	 */
	private void load() throws IOException
	{
		if (source == null)
			throw new NullPointerException("No Source Specified.");
		Properties properties = new Properties();
		properties.load(source.openStream());

		/*
		Skalierung des Levels: x-Skalierung: 8x,
		y-Skalierung: 4x, da Zeichen im Terminal ca. doppelt so hoch, wie breit sind.
		 */
		int width  = Integer.parseInt(properties.getProperty("Width")) * 8;
		int height = Integer.parseInt(properties.getProperty("Height")) * 4;

		int[][] points = new int[width][height];

		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
			{
				String value = properties.getProperty(x / 8 + "," + y / 4);
				if (value == null)
					points[x][y] = 0;
				else
					points[x][y] = Integer.parseInt(value) + 1;
			}

		/*
		Erstelle eine neue Karte mit den eingelesenen Daten
		 */
		this.map = new Map(points, 8, 4);
		Rectangle start = map.getStart();

		/*
		Initialisiere Spieler
		 */
		if (player == null)
		{
			player = Player.makePlayer();
			if (player != null)
			{
				player.setLocation(start.getLocation());
			}
		}

		/*
		Initialisiere Gegner
		 */
		if (enemies == null)
		{
			Rectangle[] staticEnemyBounds = map.getStaticEnemies();
			Rectangle[] dynamicEnemyBounds = map.getDynamicEnemies();

			enemies = new Enemy[staticEnemyBounds.length + dynamicEnemyBounds.length];

			for (int i = 0, staticEnemyBoundsLength = staticEnemyBounds.length; i < staticEnemyBoundsLength; i++)
			{
				Rectangle enemyBounds = staticEnemyBounds[i];
				Enemy enemy = Enemy.createStatic();
				enemy.setLocation(enemyBounds.getLocation());
				enemies[i] = enemy;
			}

			for (int i = 0, dynamicEnemyBoundsLength = dynamicEnemyBounds.length; i < dynamicEnemyBoundsLength; i++)
			{
				Rectangle enemyBounds = dynamicEnemyBounds[i];
				Enemy enemy = Enemy.createDynamic();
				enemy.setLocation(enemyBounds.getLocation());
				enemies[i + staticEnemyBounds.length] = enemy;
			}
		}

		/*
		Initialisiere Schluessel.
		 */
		if (keys == null)
		{
			Rectangle[] keyBounds = map.getKeys();
			keys = new Key[keyBounds.length];
			for (int i = 0, keyBoundsLength = keyBounds.length; i < keyBoundsLength; i++)
			{
				Rectangle bounds = keyBounds[i];
				Key       key    = Key.makeKey();
				key.setLocation(bounds.getLocation());
				keys[i] = key;
			}
		}
		/*
		Initialisiere wichtige Punkte auf der Karte
		 */
		entranceBounds = map.getStart();
		exitBounds = map.getFinish();

		/*
		Entferne Objekte von der Karte, da diese nicht laenger benoetigt werden und beim Pathfinding und der
		Sichtbarkeitsberechnung stoeren.
		 */
		map.removeFeatures();
	}
}
