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

package project.gui.event;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class SelectableGroup extends TResponder
{
	private char backwardsKey;
	private int currentIndex;
	private char forwardsKey;
	private List<Selectable> selectables;

	public SelectableGroup()
	{
		this.selectables = new ArrayList<>();
		setAllowsFirstResponder(true);
		forwardsKey = KeyEvent.VK_DOWN;
		backwardsKey = KeyEvent.VK_UP;
	}

	public void addSelectable(Selectable selectable)
	{
		boolean firstEnabledElement = selectable.selectionEnabled();
		for (Selectable sel : selectables)
			firstEnabledElement &= !sel.selectionEnabled();
		selectables.add(selectable);
		if (firstEnabledElement)
			selectable.select();
	}

	public char getBackwardsKey()
	{
		return backwardsKey;
	}

	@Override
	public TResponder getFirstResponder()
	{
		return isFirstResponder() ? this : null;
	}

	public char getForwardsKey()
	{
		return forwardsKey;
	}

	@Override
	public void resignFirstResponder()
	{
		super.resignFirstResponder();
		deselectAll();
	}

	public void setBackwardsKey(final char backwardsKey)
	{
		if (forwardsKey == KeyEvent.VK_ENTER)
			throw new RuntimeException("Enter not allowed as focus traversal key");
		this.backwardsKey = backwardsKey;
	}

	public void setForwardsKey(final char forwardsKey)
	{
		if (forwardsKey == KeyEvent.VK_ENTER)
			throw new RuntimeException("Enter not allowed as focus traversal key");
		this.forwardsKey = forwardsKey;
	}

	@Override
	protected void becomeFirstResponder()
	{
		super.becomeFirstResponder();
		selectNext();
	}

	@Override
	protected void keyDown(final TEvent event)
	{
		if (event.getKey() == forwardsKey)
			selectNext();
		else if (event.getKey() == backwardsKey)
			selectPrevious();
		else if (event.getKey() == KeyEvent.VK_ENTER)
			selectables.get(getSelectedIndex()).performAction();
		else
			super.keyDown(event);
	}

	@Override
	protected void keyUp(final TEvent event)
	{
		super.keyUp(event);
	}

	private void deselectAll()
	{
		for (Selectable sel : selectables)
			if (sel.isSelected())
				sel.deselect();
	}

	private int getSelectedIndex()
	{
		for (int i = 0; i < selectables.size(); i++)
			if (selectables.get(i).isSelected())
				return i;
		return -1;
	}

	private void selectNext()
	{
		int selectedIndex = -1;
		for (int i = 0; i < selectables.size(); i++)
			if (selectables.get(i).isSelected())
			{
				selectedIndex = i;
				selectables.get(i).deselect();
				break;
			}
		for (int i = 1; i <= selectables.size(); i++)
		{
			if (selectables.get((i + selectedIndex) % selectables.size()).selectionEnabled())
			{
				selectables.get((i + selectedIndex) % selectables.size()).select();
				return;
			}
		}
	}

	private void selectPrevious()
	{
		int selectedIndex = -1;
		for (int i = 0; i < selectables.size(); i++)
			if (selectables.get(i).isSelected())
			{
				selectedIndex = i;
				selectables.get(i).deselect();
				break;
			}
		selectedIndex += selectables.size();
		for (int i = -1; i >= -selectables.size(); i--)
		{
			if (selectables.get((i + selectedIndex) % selectables.size()).selectionEnabled())
			{
				selectables.get((i + selectedIndex) % selectables.size()).select();
				return;
			}
		}
	}
}
