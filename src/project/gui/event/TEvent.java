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

package project.gui.event;

/**
 * Event-Objekt fuer ein KeyEvent
 * Verwaltet den Event-Status (Key up oder Key down)
 * und die gedrueckte Taste
 */
public class TEvent
{
	private enum EventState
	{
		KEY_DOWN,
		KEY_UP
	}

	public static final EventState KEY_DOWN = EventState.KEY_DOWN;
	public static final EventState KEY_UP   = EventState.KEY_UP;

	private final int        key;
	private final EventState state;

	/**
	 * Erstellt ein neues Event mit angegebener Taste und Status
	 *
	 * @param key   gedrueckte Taste
	 * @param state Eventstatus
	 * @see java.awt.event.KeyEvent
	 */
	public TEvent(int key, EventState state)
	{
		this.key = key;
		this.state = state;
	}

	/**
	 * Gibt die gedrueckte Taste an
	 * @return Zahlencode der gedrueckten Taste
	 */
	public int getKey()
	{
		return key;
	}

	/**
	 * Gibt den Eventstatus an
	 * @return Eventstatus
	 */
	public EventState getState()
	{
		return state;
	}
}
