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

import java.util.ArrayList;
import java.util.List;

/**
 * Klasse zur Verarbeitung von KeyEvents und zur Verwaltung und
 * Steuerung eines Responder Models.
 */
public abstract class TResponder
{
	private final List<TResponder> subresponders;
	private       boolean          allowsFirstResponder;
	private       TEventHandler    eventHandler;
	private       boolean          isFirstResponder;
	private       boolean          isSingleFirstResponder;
	private       TResponder       parentResponder;

	/**
	 * Erstellt einen neuen TResponder
	 */
	protected TResponder()
	{
		subresponders = new ArrayList<>();
	}

	/**
	 * Fuegt dem Responder einen neuen Kind-Responder hinzu.
	 * @param responder Kindresponder
	 */
	public void addResponder(TResponder responder)
	{
		subresponders.add(responder);
		responder.parentResponder = this;
		responderChainUpdated();
	}

	/**
	 * Gibt an, ob der Responder First Responder werden kann
	 * @return true, wenn der Responder First Responder sein kann, sonst false
	 */
	public boolean allowsFirstResponder()
	{
		return allowsFirstResponder;
	}

	/**
	 * Verarbeitet ein KeyEvent. Ist der Responder nicht
	 * First Responder, wird das Event ignoriert.
	 * @param event zu verarbeitendes Event
	 */
	public void dispatchEvent(TEvent event)
	{
		if (isFirstResponder)
		{
			if (event.getState() == TEvent.KEY_DOWN)
				keyDown(event);
			else if (event.getState() == TEvent.KEY_UP)
				keyUp(event);
		}
	}

	/**
	 * Gibt den EventHandler zurueck, der KeyEvents verarbeitet
	 * @return EventHandler
	 */
	public TEventHandler getEventHandler()
	{
		return eventHandler;
	}

	/**
	 * Gibt den First Responder zurueck, falls dieser ein Kindresponder
	 * dieses Responders ist. Andernfalls wird null zurueckgegeben.
	 * @return First Responder oder null
	 */
	public TResponder getFirstResponder()
	{
		if (isFirstResponder)
			return this;
		for (int i = 0; i < subresponders.size(); i++)
		{
			TResponder child          = subresponders.get(i);
			TResponder childResponder = child.getFirstResponder();
			if (childResponder != null)
				return childResponder;
		}
		return null;
	}

	/**
	 * Gibt den naechstmoeglichen First Responder zurueck.
	 * @return naechstmoeglicher First Responder oder null,
	 * falls kein naechster gefunden wurde.
	 */
	public TResponder getNextResponder()
	{
		boolean foundFirstResponder = false;
		for (int i = 0; i < subresponders.size(); i++)
		{
			TResponder subresponder = subresponders.get(i);
			foundFirstResponder |= subresponder.hasFirstResponder();
			if (foundFirstResponder)
			{
				TResponder nextResponder = subresponder.getNextResponder();
				if (nextResponder != null)
				{
					return nextResponder;
				}
			}
		}
		if (foundFirstResponder && allowsFirstResponder())
			return this;
		if (!foundFirstResponder)
			for (int i = 0; i < subresponders.size(); i++)
			{
				TResponder subresponder  = subresponders.get(i);
				TResponder nextResponder = subresponder.getNextResponder();
				if (nextResponder != null)
				{
					return nextResponder;
				}
			}
		if (allowsFirstResponder())
			return this;
		else
			return null;
	}

	/**
	 * Gibt an, ob es sich bei dem Responder um den First Responder handelt.
	 * @return true, wenn der Responder First Responder ist, sonst false
	 */
	public boolean isFirstResponder()
	{
		return isFirstResponder;
	}

	/**
	 * Gibt an, ob der Responder alleiniger First Responder ist und kein anderer
	 * Responder First Responder werden kann
	 * @return true, wenn der Responder alleiniger First Responder ist, sonst false
	 */
	public boolean isSingleFirstResponder()
	{
		return isSingleFirstResponder;
	}

	/**
	 * Entfernt alle Kindresponder.
	 * Wenn ein Kindresponder First Responder ist, wird ein neuer FirstResponder gesucht.
	 */
	public void removeAll()
	{
		while (!subresponders.isEmpty())
			removeResponder(subresponders.get(0));
	}

	/**
	 * Entfernt einen Kindresponder. Wenn das Kind First Responder ist, wird ein
	 * neuer First Responder gesucht.
	 * @param responder zu entferndender Kind-Responder
	 */
	public void removeResponder(TResponder responder)
	{
		if (subresponders.remove(responder))
		{
			TResponder firstResponder = responder.getFirstResponder();
			if (firstResponder != null)
			{
				TResponder nextResponder = getNextResponder();
				firstResponder.resignFirstResponder();
				if (nextResponder != null)
					nextResponder.requestFirstResponder();
			}
			responder.parentResponder = null;
			responderChainUpdated();
		}
	}

	/**
	 * Erlange First Responder-Status wenn moeglich.
	 */
	public void requestFirstResponder()
	{
		childRequestsFirstResponder(this);
	}

	/**
	 * Gebe First Responder-Status auf.
	 */
	public void resignFirstResponder()
	{
		isFirstResponder = false;
		responderChainUpdated();
	}

	/**
	 * Lege fest, ob dieser Responder First Responder werden kann.
	 * @param allowsFirstResponder true, wenn der Responder First Responder werden kann, sonst false
	 */
	public void setAllowsFirstResponder(final boolean allowsFirstResponder)
	{
		this.allowsFirstResponder = allowsFirstResponder;
		responderChainUpdated();
	}

	/**
	 * Setzt den EventHandler, der KeyEvents verarbeitet
	 * @param eventHandler zu verwendender EventHandler
	 */
	public void setEventHandler(final TEventHandler eventHandler)
	{
		this.eventHandler = eventHandler;
	}

	/**
	 * Legel fest, ob der Responder alleiniger First Responder sein soll
	 * und den Status als First Responder nicht verlieren kann.
	 * @param singleFirstResponder true, wenn der Responder alleiniger FirstResponder
	 *                             sein kann, sonst false
	 */
	public void setSingleFirstResponder(final boolean singleFirstResponder)
	{
		isSingleFirstResponder = singleFirstResponder;
		if (isFirstResponder)
			requestFirstResponder();
	}

	/**
	 * Der Responder wird First Responder und nimmt KeyEvents entgegen
	 */
	protected void becomeFirstResponder()
	{
		isFirstResponder = true;
	}

	/**
	 * Der Responder ist First Responder und eine Taste wurde gedrueckt
	 * @param event KeyEvent
	 */
	protected void keyDown(TEvent event)
	{
		if (eventHandler != null)
			eventHandler.keyDown(event);
	}

	/**
	 * Der Responder ist First Responder und eine Taste wurde losgelassen
	 * @param event KeyEvent
	 */
	protected void keyUp(TEvent event)
	{
		if (eventHandler != null)
			eventHandler.keyUp(event);
	}

	/**
	 * Verarbeite einen First Responder-Request.
	 * Suche den First Responder und deaktiviere ihn, wenn moeglich.
	 * child wird neuer First Responder
	 * @param child neuer First Responder
	 */
	private void childRequestsFirstResponder(TResponder child)
	{
		if (parentResponder != null)
			parentResponder.childRequestsFirstResponder(child);
		else
		{
			TResponder firstResponder = getFirstResponder();
			if (firstResponder != null)
			{
				if (firstResponder.isSingleFirstResponder())
					return;
				firstResponder.resignFirstResponder();
			}
			child.becomeFirstResponder();
		}
	}

	/**
	 * Gibt an, ob der First Responder ein Kindresponder
	 * dieses Responders ist.
	 * @return true, wenn der First Responder ein Kindelement ist, sonst false
	 */
	private boolean hasFirstResponder()
	{
		return getFirstResponder() != null;
	}

	/**
	 * Die Responderkette wurde aktualisiert. Wenn kein First Responder mehr vorhanden
	 * ist, wird ein neuer First Responder gesucht.
	 */
	private void responderChainUpdated()
	{
		if (parentResponder != null)
			parentResponder.responderChainUpdated();
		else
		{
			if (!hasFirstResponder())
			{
				TResponder nextResponder = getNextResponder();
				if (nextResponder != null)
					nextResponder.becomeFirstResponder();
			}
		}
	}
}
