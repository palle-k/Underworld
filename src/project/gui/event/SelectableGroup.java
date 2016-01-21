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

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasse zur Steuerung und Verwaltung von mehreren Selectable-Elementen
 * Implementiert selbst Selectable, eine SelectableGroup kann also einer SelectableGroup
 * hinzugefuegt werden
 */
public class SelectableGroup extends TResponder implements Selectable
{
	private char             backwardsKey;
	private char             forwardsKey;
	private SelectableGroup  parent;
	private List<Selectable> selectables;

	/**
	 * Erstellt eine neue Gruppe von Selectable-Elementen
	 */
	public SelectableGroup()
	{
		this.selectables = new ArrayList<>();
		setAllowsFirstResponder(true);
		forwardsKey = KeyEvent.VK_DOWN;
		backwardsKey = KeyEvent.VK_UP;
	}

	@Override
	public void addResponder(final TResponder responder)
	{
		if (responder instanceof Selectable)
		{
			Selectable selectable = (Selectable) responder;
			if (selectable instanceof SelectableGroup)
				((SelectableGroup) selectable).parent = this;
			boolean firstEnabledElement = selectable.selectionEnabled();
			for (Selectable sel : selectables)
				firstEnabledElement &= !sel.selectionEnabled();
			selectables.add(selectable);
			if (firstEnabledElement)
				selectable.select();
		}
		super.addResponder(responder);
	}

	@Override
	public boolean allowsFirstResponder()
	{
		return parent == null && super.allowsFirstResponder();
	}

	@Override
	public void deselect()
	{
		deselectAll();
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
	public boolean isSelected()
	{
		for (Selectable selectable : selectables)
			if (selectable.isSelected())
				return true;
		return false;
	}

	@Override
	public void performAction()
	{
		selectables.get(getSelectedIndex()).performAction();
	}

	@Override
	public void removeResponder(final TResponder responder)
	{
		super.removeResponder(responder);
		if (responder instanceof Selectable)
		{
			Selectable selectable = (Selectable) responder;
			if (selectable.isSelected())
				selectNext();
			selectables.remove(selectable);
			if (selectable instanceof SelectableGroup)
				((SelectableGroup) selectable).parent = null;
		}
	}

	@Override
	public void resignFirstResponder()
	{
		super.resignFirstResponder();
		deselectAll();
	}

	@Override
	public void select()
	{
		deselectAll();
		selectNext();
	}

	/**
	 * Waehlt das naechste Selectable-Element aus
	 */
	public void selectNext()
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

	/**
	 * Waehlt das vorherige Selectable-Element aus
	 */
	public void selectPrevious()
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

	@Override
	public boolean selectionEnabled()
	{
		return parent != null;
	}

	/**
	 * Setzt die Taste, mit der das vorherige Element ausgewaehlt wird
	 * @param backwardsKey Taste fuer vorheriges Element
	 */
	public void setBackwardsKey(final char backwardsKey)
	{
		if (forwardsKey == KeyEvent.VK_ENTER)
			throw new RuntimeException("Enter not allowed as focus traversal key");
		this.backwardsKey = backwardsKey;
	}

	/**
	 * Setzt die Taste, mit der das naechste Element ausgewaehlt wird
	 * @param forwardsKey Taste fuer naechstes Element
	 */
	public void setForwardsKey(final char forwardsKey)
	{
		if (forwardsKey == KeyEvent.VK_ENTER)
			throw new RuntimeException("Enter not allowed as focus traversal key");
		this.forwardsKey = forwardsKey;
	}

	@Override
	protected void becomeFirstResponder()
	{
		if (!isFirstResponder())
		{
			super.becomeFirstResponder();
			selectNext();
		}
	}

	@Override
	protected void keyDown(final TEvent event)
	{
		if (event.getKey() == forwardsKey)
			selectNext();
		else if (event.getKey() == backwardsKey)
			selectPrevious();
		else
		{
			super.keyDown(event);
			selectables.stream()
					.filter(Selectable::isSelected)
					.filter(selectable -> selectable instanceof SelectableGroup)
					.forEach(selectable -> ((SelectableGroup) selectable).keyDown(event));
		}
	}

	@Override
	protected void keyUp(final TEvent event)
	{
		if (event.getKey() == KeyEvent.VK_ENTER)
		{
			int selectedIndex = getSelectedIndex();
			if (selectedIndex >= 0)
				selectables.get(selectedIndex).performAction();
		}
		else
			super.keyUp(event);
	}

	/**
	 * Deselektiert alle Selectable-Elemente
	 */
	private void deselectAll()
	{
		selectables.stream().filter(Selectable::isSelected).forEach(Selectable::deselect);
	}

	/**
	 * Gibt den Index des ausgewaehlten Selectable-Elements an
	 * @return Index oder -1, wenn kein Element ausgewaehlt ist
	 */
	private int getSelectedIndex()
	{
		for (int i = 0; i < selectables.size(); i++)
			if (selectables.get(i).isSelected())
				return i;
		return -1;
	}
}
