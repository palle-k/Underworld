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

package project.game.data.state;

import project.game.data.Level;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.prefs.Preferences;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Spielstatus: Level
 * Sicherung des aktuellen Levels (komprimiert)
 * Index des aktuellen Levels
 * Status des Tutorials
 * Sichern oder Laden ueber die java-Preferences-API
 */
public class LevelState implements StateSaving
{
	private boolean askedForTutorial;

	private boolean didShowPostLevelText;

	private boolean didShowPreLevelText;

	private int     levelIndex;

	private boolean levelPlayed;

	private String  savedLevel;

	private int     tutorialIndex;

	private boolean tutorialPlayed;

	/**
	 * Erstelle neuen Levelstatus und lade aus den angegebenen Preferences
	 * @param preferences Preferences, aus denen der Levelstatus geladen wird
	 */
	protected LevelState(Preferences preferences)
	{
		load(preferences);
	}

	/**
	 * Gibt an, ob das Tutorial aktuell gespielt wird.
	 * @return true, wenn das Tutorial gerade gespielt wird oder false, wenn nicht
	 */
	public boolean askedForTutorial()
	{
		return askedForTutorial;
	}

	/**
	 * Gibt an, ob ein Text, welcher nach einem Level angezeigt werden kann,
	 * schon gezeigt wurde.
	 *
	 * @return true, wenn ein Text vor einem Level gezeigt wurde,
	 * sonst false
	 */
	public boolean didShowPostLevelText()
	{
		return didShowPostLevelText;
	}

	/**
	 * Gibt an, ob ein Text, welcher vor einem Level angezeit werden kann,
	 * schon gezeigt wurde.
	 *
	 * @return true, wenn ein Text nach einem Level angezeigt wurde,
	 * sonst false
	 */
	public boolean didShowPreLevelText()
	{
		return didShowPreLevelText;
	}

	/**
	 * Gibt den Levelindex zurueck. Dieser gibt die Nummer des aktuellen
	 * Levels an. Informationen ueber das Level koennen aus Configuration.properties
	 * mit dem Schluesselprefix level_INDEX_ geladen werden
	 * @return Levelindex
	 */
	public int getLevelIndex()
	{
		return levelIndex;
	}

	/**
	 * Gibt das gespeicherte Level zurueck. Wurde kein Level gespeichert,
	 * wird null zurueckgegeben.
	 * @return Gespeichertes Level oder null
	 */
	/*
	Speichere das Aktuelle Level in den Preferences.
	Da Preference-Strings limitiert sind, wird das Level in der Serialisierung
	stark optimiert. Es werden lediglich Quell-Property-Dateien gespeichert.
	(Siehe MapObject.java). Um die Groesse des zu speichernden Strings weiter
	zu reduzieren, wird das serialisierte Level zusaetzlich GZIP-komprimiert.
	Tritt ein Fehler auf, laesst sich das aktuelle Level nicht wiederherstellen
	und es wird null zurueckgegeben.
	 */
	public Level getSavedLevel()
	{
		if (savedLevel.length() == 0)
			return null;
		try
		{
			ByteArrayInputStream bais  = new ByteArrayInputStream(Base64.getDecoder().decode(savedLevel));
			GZIPInputStream      gzis  = new GZIPInputStream(bais);
			ObjectInputStream    ois   = new ObjectInputStream(gzis);
			Level                level = (Level) ois.readObject();
			ois.close();
			gzis.close();
			bais.close();
			return level;
		}
		catch (IOException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gibt den Index des aktuellen Tutoriallevels zurueck.
	 * Auf Eigenschaften des Tutoriallevels kann ueber die
	 * Property tutorial_INDEX_ zugegriffen werden.
	 * @return Index des aktuellen Tutorials
	 */
	public int getTutorialIndex()
	{
		return tutorialIndex;
	}

	/**
	 * Gibt an, ob das Level am gespeicherten Index schon beendet wurde.
	 * @return true, wenn das aktuelle Level schon beendet wurde, sonst false
	 */
	public boolean levelPlayed()
	{
		return levelPlayed;
	}

	/**
	 * Lade Einstellungen aus dem angegebenen Preferences-Objekt
	 * Sind Daten bisher nicht gespeichert worden, wird ein Default-Value
	 * geladen. Dieses ist:
	 * <ul>
	 *     <li>tutorialPlayed: false</li>
	 *     <li>savedLevel: null</li>
	 *     <li>tutorialIndex: 1</li>
	 *     <li>levelIndex: 1</li>
	 *     <li>askedForTutorial: false</li>
	 * </ul>
	 * @param preferences Preferences, die geladen werden sollen
	 */
	public void load(Preferences preferences)
	{
		tutorialPlayed = preferences.getBoolean("level_tutorial_played", false);
		savedLevel = preferences.get("level_saved", "");
		tutorialIndex = preferences.getInt("level_tutorial_index", 1);
		levelIndex = preferences.getInt("level_index", 1);
		askedForTutorial = preferences.getBoolean("level_asked_for_tutorial", false);
		didShowPreLevelText = preferences.getBoolean("level_did_show_pre_text", false);
		didShowPostLevelText = preferences.getBoolean("level_did_show_post_text", false);
		levelPlayed = preferences.getBoolean("level_did_finish", false);
	}

	/**
	 * Sichert die Einstellungen mithilfe des angegebenen Preferences-Objekts
	 * @param preferences Ziel
	 */
	public void save(Preferences preferences)
	{
		preferences.putBoolean("level_tutorial_played", tutorialPlayed);
		preferences.put("level_saved", savedLevel);
		preferences.putInt("level_tutorial_index", tutorialIndex);
		preferences.putInt("level_index", levelIndex);
		preferences.putBoolean("level_asked_for_tutorial", askedForTutorial);
		preferences.putBoolean("level_did_show_pre_text", didShowPreLevelText);
		preferences.putBoolean("level_did_show_post_text", didShowPostLevelText);
		preferences.putBoolean("level_did_finish", levelPlayed);
	}

	/**
	 * Setzt, ob das Tutorial gespielt wird
	 *
	 * @param askedForTutorial neuer Wert
	 */
	public void setAskedForTutorial(final boolean askedForTutorial)
	{
		this.askedForTutorial = askedForTutorial;
	}

	/**
	 * Legt fest, ob ein Text, welcher nach einem Level angezeigt werden kann,
	 * schon gezeigt wurde.
	 *
	 * @param didShowPostLevelText true, wenn ein Text vor einem Level gezeigt wurde,
	 *                             sonst false
	 */
	public void setDidShowPostLevelText(final boolean didShowPostLevelText)
	{
		this.didShowPostLevelText = didShowPostLevelText;
	}

	/**
	 * Legt fest, ob ein Text, welcher vor einem Level angezeit werden kann,
	 * schon gezeigt wurde.
	 *
	 * @param didShowPreLevelText true, wenn ein Text nach einem Level angezeigt wurde,
	 *                            sonst false
	 */
	public void setDidShowPreLevelText(final boolean didShowPreLevelText)
	{
		this.didShowPreLevelText = didShowPreLevelText;
	}

	/**
	 * Setzt den LevelIndex
	 * @param levelIndex neuer Wert
	 */
	public void setLevelIndex(final int levelIndex)
	{
		this.levelIndex = levelIndex;
	}

	/**
	 * Legt fest, ob das Level am gespeicherten Index schon beendet wurde.
	 * @param levelPlayed  true, wenn das aktuelle Level schon beendet wurde, sonst false
	 */
	public void setLevelPlayed(final boolean levelPlayed)
	{
		this.levelPlayed = levelPlayed;
	}

	/**
	 * Setzt das zu speichernde Level.
	 * @param level zu speicherndes Level
	 */
	/*
	Das zu speichernde Level wird intern als Base64-encodeter, GZIP-Komprimierter String gespeichert.
	 */
	public void setSavedLevel(final Level level)
	{
		if (level == null)
		{
			savedLevel = "";
			return;
		}
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			GZIPOutputStream      gzos = new GZIPOutputStream(baos);
			ObjectOutputStream    oos  = new ObjectOutputStream(gzos);
			oos.writeObject(level);
			oos.close();
			gzos.close();
			baos.close();
			savedLevel = new String(Base64.getEncoder().encode(baos.toByteArray()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Setzt den Index des aktuellen Tutoriallevels
	 * @param tutorialIndex neuer Index
	 */
	public void setTutorialIndex(final int tutorialIndex)
	{
		this.tutorialIndex = tutorialIndex;
	}

	/**
	 * Setzt, ob das Tutorial gespielt wurde
	 * @param tutorialPlayed true, wenn ja, false, wenn nicht
	 */
	public void setTutorialPlayed(final boolean tutorialPlayed)
	{
		this.tutorialPlayed = tutorialPlayed;
	}

	/**
	 * Gibt an, ob das Tutorial gespielt wurde.
	 * @return true, wenn das Tutorial gespielt wurde, sonst false
	 */
	public boolean tutorialWasPlayed()
	{
		return tutorialPlayed;
	}
}
