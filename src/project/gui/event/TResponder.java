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

public abstract class TResponder
{
	private final List<TResponder> subresponders;
	private       boolean          allowsFirstResponder;
	private       TEventHandler    eventHandler;
	private       boolean          isFirstResponder;
	private       boolean          isSingleFirstResponder;
	private       TResponder       parentResponder;

	protected TResponder()
	{
		subresponders = new ArrayList<>();
	}

	public void addResponder(TResponder responder)
	{
		subresponders.add(responder);
		responder.parentResponder = this;
		responderChainUpdated();
	}

	public boolean allowsFirstResponder()
	{
		return allowsFirstResponder;
	}

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

	public TEventHandler getEventHandler()
	{
		return eventHandler;
	}

	public TResponder getFirstResponder()
	{
		if (isFirstResponder)
			return this;
		for (TResponder child : subresponders)
		{
			TResponder childResponder = child.getFirstResponder();
			if (childResponder != null)
				return childResponder;
		}
		return null;
	}

	public TResponder getNextResponder()
	{
		boolean foundFirstResponder = false;
		for (int i = 0; i < subresponders.size(); i++)
		{
			foundFirstResponder |= subresponders.get(i).hasFirstResponder();
			if (foundFirstResponder)
			{
				TResponder nextResponder = subresponders.get(i).getNextResponder();
				if (nextResponder != null)
				{
					return nextResponder;
				}
			}
		}
		if (foundFirstResponder && allowsFirstResponder())
			return this;
		if (!foundFirstResponder)
		{
			for (int i = 0; i < subresponders.size(); i++)
			{
				TResponder nextResponder = subresponders.get(i).getNextResponder();
				if (nextResponder != null)
				{
					return nextResponder;
				}
			}
		}
		if (allowsFirstResponder())
			return this;
		else
			return null;
	}

	public boolean isFirstResponder()
	{
		return isFirstResponder;
	}

	public boolean isSingleFirstResponder()
	{
		return isSingleFirstResponder;
	}

	public void removeAll()
	{
		while (!subresponders.isEmpty())
			removeResponder(subresponders.get(0));
	}

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

	public void requestFirstResponder()
	{
		childRequestsFirstResponder(this);
	}

	public void resignFirstResponder()
	{
		isFirstResponder = false;
	}

	public void setAllowsFirstResponder(final boolean allowsFirstResponder)
	{
		this.allowsFirstResponder = allowsFirstResponder;
		responderChainUpdated();
	}

	public void setEventHandler(final TEventHandler eventHandler)
	{
		this.eventHandler = eventHandler;
	}

	public void setSingleFirstResponder(final boolean singleFirstResponder)
	{
		isSingleFirstResponder = singleFirstResponder;
		if (isFirstResponder)
			requestFirstResponder();
	}

	protected void becomeFirstResponder()
	{
		isFirstResponder = true;
	}

	protected void keyDown(TEvent event)
	{
		if (eventHandler != null)
			eventHandler.keyDown(event);
	}

	protected void keyUp(TEvent event)
	{
		if (eventHandler != null)
			eventHandler.keyUp(event);
	}

	protected boolean updateFirstResponder(TResponder newFirstResponder)
	{
		if (this == newFirstResponder)
		{
			becomeFirstResponder();
			return true;
		}
		boolean updatedFirstResponder = false;
		for (TResponder child : subresponders)
			updatedFirstResponder |= child.updateFirstResponder(newFirstResponder);
		return updatedFirstResponder;
	}

	private void childRequestsFirstResponder(TResponder child)
	{
		if (parentResponder != null)
		{
			parentResponder.childRequestsFirstResponder(child);
		}
		else
		{
			TResponder firstResponder = getFirstResponder();
			if (firstResponder != null)
				firstResponder.resignFirstResponder();
			updateFirstResponder(child);
		}
	}

	private boolean hasFirstResponder()
	{
		return getFirstResponder() != null;
	}

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
					nextResponder.requestFirstResponder();
			}
		}
	}
}
