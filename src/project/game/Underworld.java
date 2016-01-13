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
 * TBufferedView - Gepufferte Komponente, schneller, hoeherer Speicherverbrauch
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
 */

package project.game;

import project.game.ui.controllers.LaunchViewController;
import project.gui.components.TFrame;
import project.gui.controller.NavigationController;
import project.gui.graphics.Appearance;

import java.awt.Color;
import java.awt.Rectangle;

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
	 *     <li>-showpaths: Zeigt berechnete Pfade auf der Karte an</li>
	 *     <li>-performancemetrics: Gibt lange Berechnungszeiten aus</li>
	 * </ul>
	 * @param args Startargumente
	 */
	public static void main(String[] args)
	{
		//Setze Properties zur besseren Kompatibilitaet mit OS X
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Underworld");

		for (String arg : args)
		{
			if (arg.equalsIgnoreCase("-showpaths"))
				System.setProperty("com.palleklewitz.underworld.map.showpaths", "true");
			else if (arg.equalsIgnoreCase("-performancemetrics"))
				//TODO implement performance logging
				System.setProperty("com.palleklewitz.underworld.performancemetrics", "true");
		}

		//Appearance.defaultBackgroundColor = new Color(30, 30, 30);
		Appearance.defaultBackgroundColor = Color.BLACK;
		Appearance.defaultTextColor = Color.WHITE;
		Appearance.defaultBorderColor = Color.WHITE;

		TFrame frame = new TFrame();
		frame.setFrame(new Rectangle(0, 0, 140, 40));
		frame.setVisible(true);
		frame.setTitle("Underworld");

		NavigationController navigationController = new NavigationController(frame);
		navigationController.push(new LaunchViewController());
	}
}
