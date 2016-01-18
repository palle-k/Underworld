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

/**
 * README - Underworld
 *
 * Unterteilung der Programmierung:
 * Engine:
 * - Darstellung und Zeichnen
 * - Angelehnt an Java-Swing und an CoreGraphics bzw. UIKit (iOS SDK)
 * Funktionen:
 * Darstellung von Text und Formen
 * - Grafikkontexte zum Zerlegen komplexer Objekte in einfache Zeichenbefehle
 * - Maskierung beim Zeichnen
 * - Abfrage von Zeichen
 * FirstResponder-Modell:
 * - KeyEvents werden an FirstResponder geleitet
 * - FirstResponder kann durch Tab-Taste
 * Effizientes Zeichnen in einem Lanterna-SwingTerminal
 * ViewController-Modell:
 * - ViewController steuern Praesentation
 * - NavigationController steuern die Praesentation von ViewControllern in einer Navigationshierarchie
 * - PageController steuern die Praesentation von ViewControllern auf gleichem Level
 * Layout-Engine: Automatisches Layout von Subkomponenten einer Komponente
 * Mitgelieferte Komponenten:
 * TComponent - Standard-View
 * TBufferedView - Gepufferte Komponente, schneller, hoeherer Speicherverbrauch, ermoeglicht Compositing
 * TFrame - Fenster
 * TImageView - Darstellung eines Bildes
 * TLabel - Darstellung von Text
 * TProgressBar - Darstellung eines Fortschrittbalkens
 * TScrollView - Wie eine Standardkomponente nur mit Scrollfunktionalitaet
 * TTextField - Textfeld fuer die Eingabe von Text durch den Nutzer
 * Dialoge:
 * ConfirmDialog - Ja/Nein-Abfrage
 * InputDialog - Texteingabe
 * MessageDialog - Nachricht an den Nutzer
 * SingleKeyInputDialog - Eingabe einer einzelnen Taste (Fuer Tastenbelegungen zur Steuerung)
 *
 * Game:
 * Data Model:
 * Basisklasse: MapObject - Ein statisches Objekt
 * GameActor - ein dynamisches Objekt, welches angreifen und sich bewegen kann
 * Player - ein GameActor, welcher verschiedene Skills besitzt
 * Enemy - ein Gegner (dynamisch oder statisch)
 * Key - ein Schluessel, der vom Spieler eingesammelt werden muss.
 * Map - die Karte, auf der sich Objekte befinden. Bietet Moeglichkeiten zum Pathfinding
 * und zur Sichtbarkeitsberechnung
 *
 * Verhalten:
 * Steuerung von Aktoren ueber Verhalten
 * AutoAttackBehaviour - Ausfuehren eines Skills zum Angreifen eines Gegners
 * FollowBehaviour - Verfolgung eines Gegners
 * RandomMovementBehaviour - Zufaellige Bewegung eines Aktors
 *
 * Controller:
 * Steuerung eines Aktors ueber das Zusammenspiel
 * verschiedener Verhalten
 * EnemyController - Steuerung eines Gegners: Verfolgung und Angreifen des Spielers
 * PlayerController - Steuerung des Spielers ueber Nutzereingaben, Angreifen von Gegnern
 *
 * Skillssystem:
 * SkillConfiguration - Speicherung eines Skills, Auswirkungen und eines Executors
 * zum Ausfuehren eines Skills
 * SkillExecutor - Ausfuehrung eines Skills: Darstellung auf der Benutzeroberflaeche und Anwendung
 * von Effekten auf das Datenmodell
 * SkillCoordinator - Darstellung eines Skills
 *
 * Speichersystem:
 * Automatische Speicherung des Spiels
 * Speicherung ueber java-Preferences-API in stark komprimierter und optimierter Form:
 * Windows: Speicherung in der Registry
 * Mac: Speicherung in ~/Library/Preferences/com.apple.java.util.prefs.plist
 * Manuelles Sichern bietet keinen erheblichen Vorteil, bei Tod geht lediglich der Fortschritt des
 * aktuellen Levels verloren.
 * Export von Einstellungen zum Weitergeben an Andere und zum Verwenden
 * mehrerer Spielstaende
 *
 * Lokalisierung:
 * LocalizedString fuer Mehrsprachensupport
 * Aktuell Unterstuetzung fuer Englisch und Deutsch
 */

package project.game;

import project.game.ui.controllers.LaunchViewController;
import project.gui.components.TFrame;
import project.gui.controller.NavigationController;
import project.gui.graphics.Appearance;

import java.awt.Color;
import java.awt.Rectangle;

import static project.game.localization.LocalizedString.LocalizedString;

/**
 * Main-Klasse<br>
 * Start des Programms mit<br>
 * java -cp .:/Path/To/Lanterna project.game.Underworld<br>
 * Erfordert Java 8 und JavaFX
 */
public class Underworld
{
	/**
	 * Start des Spiels
	 * Setze Standardfarben
	 * Erstelle Frame
	 * Setze Frame als Basiskomponente fuer NavigationController
	 * Pushe den LaunchViewController auf den Navigation stack<br>
	 * Startargumente:
	 * <ul>
	 *     <li><b>-showpaths</b>: Zeigt berechnete Pfade auf der Karte an</li>
	 *     <li><b>-performancemetrics</b>: Gibt lange Berechnungszeiten aus</li>
	 * </ul>
	 * @param args Startargumente
	 */
	public static void main(String[] args)
	{
		for (String arg : args)
		{
			if (arg.equalsIgnoreCase("-showpaths"))
				System.setProperty("com.palleklewitz.underworld.map.showpaths", "true");
			else if (arg.equalsIgnoreCase("-performancemetrics"))
				System.setProperty("com.palleklewitz.underworld.performancemetrics", "true");
		}

		Appearance.defaultBackgroundColor = Color.BLACK;
		Appearance.defaultTextColor = Color.WHITE;
		Appearance.defaultBorderColor = Color.WHITE;

		TFrame frame = new TFrame();
		frame.setFrame(new Rectangle(0, 0, 140, 40));
		frame.setVisible(true);
		frame.setTitle(LocalizedString("window_title"));

		NavigationController navigationController = new NavigationController(frame);
		navigationController.push(new LaunchViewController());
	}
}