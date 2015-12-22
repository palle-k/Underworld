/******************************************************************************
 * Copyright (c) 2015 Palle Klewitz.                                          *
 * *
 * Permission is hereby granted, free of charge, to any person obtaining      *
 * a copy of this software and associated documentation files                 *
 * (the "Software"), to deal in the Software without restriction,             *
 * including without limitation the rights to use, copy, modify,             *
 * merge, publish, distribute, sublicense, and/or sell copies of             *
 * the Software, and to permit persons to whom the Software                  *
 * is furnished to do so, subject to the following conditions:               *
 * *
 * The above copyright notice and this permission notice shall                *
 * be included in all copies or substantial portions of the Software.         *
 * *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY                         *
 * OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT                        *
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS                     *
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.                             *
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS                        *
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,                      *
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,                      *
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE                            *
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.                    *
 ******************************************************************************/

package project.gui.components;

import project.gui.event.TEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class TResponder
{
	private final List<TResponder> subresponders;
	private boolean allowsFirstResponder;
	private boolean isFirstResponder;
	private TResponder parentResponder;

	protected TResponder()
	{
		subresponders = new ArrayList<>();
	}

	public boolean allowsFirstResponder()
	{
		return allowsFirstResponder;
	}

	public void requestFirstResponder()
	{

	}

	public void setAllowsFirstResponder(final boolean allowsFirstResponder)
	{
		this.allowsFirstResponder = allowsFirstResponder;
	}

	protected void addResponder(TResponder responder)
	{
		subresponders.add(responder);
		responder.parentResponder = this;
	}

	protected void becomeFirstResponder()
	{
		isFirstResponder = true;
	}

	protected void keyDown(TEvent event)
	{
		//implement in subclass
	}

	protected void keyUp(TEvent event)
	{
		//implement in subclass
	}

	protected void removeResponder(TResponder responder)
	{
		if (subresponders.remove(responder))
			responder.parentResponder = null;
	}

	protected void resignFirstResponder()
	{
		isFirstResponder = false;
	}

	private boolean hasFirstResponder()
	{
		if (isFirstResponder)
			return true;
		boolean includesFirstResponder = false;
		for (TResponder child : subresponders)
			includesFirstResponder |= child.hasFirstResponder();
		return includesFirstResponder;
	}

	private void updateFirstResponder(TResponder newFirstResponder)
	{

	}
}
