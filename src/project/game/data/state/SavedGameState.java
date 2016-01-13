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

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static project.game.localization.LocalizedString.LocalizedString;

/**
 * Gesamtspielstand
 * Dieser besteht aus dem Spielerstatus, dem Levelstatus und dem Einstellungsstatus
 * Ermoeglicht das einfache Speichern und Laden des Spielstatus
 * Gesicherter Spielstand ist fuer unerfahrenen Nutzer nicht sichtbar
 * Dieser wird an folgenden Orten gespeichert:
 * <ul>
 * <li>Windows: Registry: HKEY_CURRENT_USER\Software\JavaSoft\Prefs: com.palleklewitz.Underworld</li>
 * <li>Mac OS X: ~/Library/Preferences/com.apple.java.util.prefs.plist: com.palleklewitz.Underworld</li>
 * </ul>
 */
public class SavedGameState
{
	private static final SavedGameState state;

	/*
	Lade den Spielstand
	 */
	static
	{
		Preferences preferences = Preferences.userRoot().node("com.palleklewitz.Underworld");
		state = new SavedGameState(preferences);
		//Automatisches Sichern beim Schliessen
		Runtime.getRuntime().addShutdownHook(new Thread(SavedGameState::save));

	}

	/**
	 * Exportiert den Spielstatus als Datei, damit dieser ueber mehrere Systeme geteilt werden kann.
	 *
	 * @param parent Frame, ueber dem der FileDialog gezeigt werden soll (null moeglich)
	 * @throws IOException
	 * @throws BackingStoreException
	 */
	public static void exportState(Frame parent) throws IOException, BackingStoreException
	{
		save();
		FileDialog fileDialog = new FileDialog(
				parent,
				LocalizedString("export_state_chooser_title"),
				FileDialog.SAVE);
		fileDialog.setVisible(true);
		String filename = fileDialog.getFile();

		String directory = fileDialog.getDirectory();

		if (filename == null)
			return;

		File file = new File(directory + filename);
		BufferedOutputStream preferencesOutputStream
				= new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(
				file)));

		Preferences preferences = Preferences.userRoot().node("com.palleklewitz.Underworld");
		preferences.exportNode(preferencesOutputStream);
		preferencesOutputStream.close();
	}

	/**
	 * Gibt den Levelstatus zurueck. Dieser beinhaltet Informationen zur Wiederherstellung des aktuellen Levels
	 * und der Position des Spielers im Gesamtspielverlauf
	 *
	 * @return Levelstatus
	 */
	public static LevelState getLevelState()
	{
		return state.levelState;
	}

	/**
	 * Gibt den Spielerstatus zurueck. Dieser beinhaltet Informationen ueber die Klasse des Spielers
	 * (Ritter, Jaeger, Magier)
	 *
	 * @return Spielerstatus
	 */
	public static PlayerState getPlayerState()
	{
		return state.playerState;
	}

	/**
	 * Gibt den Status des Spiels zurueck.
	 * DEPRECATED. Do not use. Use the static methods getLevelState(), getPlayerState() and getSettingsState()
	 * instead.
	 *
	 * @return Spielstatus
	 */
	@Deprecated
	public static SavedGameState getSavedGameState()
	{
		if (state == null)
			loadState();
		return state;
	}

	/**
	 * Gibt den Einstellungsstatus des Spiels zurueck.
	 * Dieser beinhaltet Tastenbelegungen zur Steuerung des Spielers
	 * @return Einstellungsstatus
	 */
	public static SettingsState getSettingsState()
	{
		return state.settingsState;
	}

	/**
	 * Importiert einen gespeicherten Spielerstatus
	 *
	 * @param parent Frame, ueber dem der FileDialog gezeigt werden soll (null erlaubt)
	 * @throws IOException
	 * @throws InvalidPreferencesFormatException
	 */
	public static void importState(Frame parent) throws IOException, InvalidPreferencesFormatException
	{
		FileDialog fileDialog = new FileDialog(
				parent,
				LocalizedString("import_state_chooser_title"),
				FileDialog.LOAD);
		fileDialog.setVisible(true);
		String filename  = fileDialog.getFile();
		String directory = fileDialog.getDirectory();

		if (filename == null)
			return;

		File file = new File(directory + filename);
		BufferedInputStream preferencesInputStream = new BufferedInputStream(new GZIPInputStream(new FileInputStream(
				file)));

		Preferences.importPreferences(preferencesInputStream);
		preferencesInputStream.close();

		loadState();
	}

	/**
	 * Setzt saemtliche Spielstaende zurueck.
	 * Die Einstellungen (Tastenbelegungen) bleiben hiervon unberuehrt.
	 */
	public static void reset()
	{
		try
		{
			Preferences.userRoot().node("com.palleklewitz.Underworld").removeNode();
		}
		catch (BackingStoreException e)
		{
			e.printStackTrace();
		}
		Preferences preferences = Preferences.userRoot().node("com.palleklewitz.Underworld");
		state.levelState = new LevelState(preferences);
		state.playerState = new PlayerState(preferences);
		//not resetting settings state
	}

	/**
	 * Sichert Spielstaende und Einstellungen
	 */
	public static void save()
	{
		Preferences preferences = Preferences.userRoot().node("com.palleklewitz.Underworld");
		state.levelState.save(preferences);
		state.playerState.save(preferences);
		state.settingsState.save(preferences);
	}

	/**
	 * Laed Spielstaende und Einstellungen
	 */
	private static void loadState()
	{
		Preferences preferences = Preferences.userRoot().node("com.palleklewitz.Underworld");
		state.levelState.load(preferences);
		state.playerState.load(preferences);
		state.settingsState.load(preferences);
	}

	private LevelState    levelState;
	private PlayerState   playerState;
	private SettingsState settingsState;

	/**
	 * Erstellt einen neuen Spielstatus und laed Einstellungen aus angegebenen Preferences
	 * Private Constructor: Nicht von Aussen instanzierbar.
	 *
	 * @param preferences Quelle
	 */
	private SavedGameState(Preferences preferences)
	{
		levelState = new LevelState(preferences);
		playerState = new PlayerState(preferences);
		settingsState = new SettingsState(preferences);
	}
}
