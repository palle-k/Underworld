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

/**
 * Delegate zur Rueckmeldung ueber den Status von GameActor-Objekten
 * Zur Einbindung in eine Model-View-Controller-Struktur
 * @see GameActor
 */
public interface GameActorDelegate extends MapObjectDelegate
{
	/**
	 * Wenn die Lebenspunkte des Aktors sich aendern, wird eine Implementierung
	 * dieser Methode aufgerufen. Der geaenderte Aktor wird als Parameter uebergeben
	 * @param actor geaenderter Aktor
	 */
	void actorDidChangeHealth(GameActor actor);

	/**
	 * Wenn der Status (Warten, Bewegen, Angreifen, Verteidigen) des Aktors geaendert
	 * wurde, wird die Delegate durch Aufruf dieser Methode darueber benachrichtigt.
	 * Der geanderte Aktor wird als Parameter uebergeben.
	 * @param actor geaenderter Aktor
	 */
	void actorDidChangeState(GameActor actor);
}
