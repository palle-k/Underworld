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

package project.gui.controller;

import project.gui.components.TComponent;
import project.gui.layout.FullSizeSubviewLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller zur Praesentation von ViewControllern,
 * die sich hierarchisch auf einer Ebene befinden
 */
public class PageController extends ViewController
{
	private final List<ViewController> viewControllerList;
	private ViewController currentController;
	private int currentControllerIndex = 0;

	/**
	 * Erstellt einen neuen PageController mit angegebenem uebergeordneten ViewController
	 * und angegeber, zu steuernder Komponente
	 *
	 * @param parent uebergeordneter ViewController
	 * @param view   zu steuernde Komponente
	 */
	public PageController(final ViewController parent, final TComponent view)
	{
		super(parent, view);
		view.setLayoutManager(new FullSizeSubviewLayout());
		viewControllerList = new ArrayList<>();
	}

	/**
	 * Erstellt einen neuen PageController mit angegebener, zu steuernder Komponente
	 * @param view zu steuernde Komponente
	 */
	public PageController(final TComponent view)
	{
		super(view);
		view.setLayoutManager(new FullSizeSubviewLayout());
		viewControllerList = new ArrayList<>();
	}

	/**
	 * Erstellt einen neuen PageController
	 */
	public PageController()
	{
		super();
		getView().setLayoutManager(new FullSizeSubviewLayout());
		viewControllerList = new ArrayList<>();
	}

	/**
	 * Fuegt einen neuen ViewController hinzu.
	 * Dieser wird nach einer endlichen Anzahl von Aufrufen von next() oder previous()
	 * erscheinen.
	 * @param controller neuer ViewController.
	 */
	public void addController(ViewController controller)
	{
		viewControllerList.add(controller);
		controller.setParent(this);
	}

	/**
	 * Fuegt einen neuen ViewController an gegebenem Index hinzu.
	 * Dieser wird nach einer endlichen Anzahl von Aufrufen von next() oder previous()
	 * erscheinen
	 * @param controller neuer ViewController
	 * @param index Index, an dem der neue ViewController eingefuegt werden soll
	 */
	public void addController(ViewController controller, int index)
	{
		viewControllerList.add(index, controller);
		controller.setParent(this);
	}

	/**
	 * Gibt an, ob ein naechster ViewController mit next() gezeigt werden kann.
	 * Die Standardimplementierung zeigt, nachdem der letzte ViewController gezeigt wurde,
	 * den ersten ViewController erneut.
	 * @return true, wenn ein naechster ViewController exisitiert, sonst false
	 */
	public boolean hasNext()
	{
		return true;
	}

	/**
	 * Gibt an, ob ein vorheriger ViewController mit previous() gezeigt werden kann.
	 * Die Standardimplementierung zeigt, wenn der erste ViewController gezeigt wurde,
	 * den letzten ViewController.
	 * @return true, wenn ein vorheriger ViewController existiert, sonst false
	 */
	public boolean hasPrevious()
	{
		return true;
	}

	/**
	 * Zeigt den naechsten ViewController.
	 */
	public void next()
	{
		if (!hasNext())
		{
			System.err.printf("Warning: %s has no next page. Ignoring next()-call.\n", this.toString());
			return;
		}
		hideCurrentPage();
		currentController = getNextPage();
		showCurrentPage();
	}

	/**
	 * Zeigt den vorherigen ViewController
	 */
	public void previous()
	{
		if (!hasPrevious())
		{
			System.err.printf("Warning: %s has no previous page. Ignoring previous()-call.\n", this.toString());
			return;
		}
		hideCurrentPage();
		currentController = getPreviousPage();
		showCurrentPage();
	}

	/**
	 * Entfernt den angegebenen ViewController aus der Liste der zu zeigenden Controller.
	 * @param controller zu entfernender ViewController
	 */
	public void removeController(ViewController controller)
	{
		viewControllerList.remove(controller);
		controller.setParent(null);
	}

	@Override
	public void viewDidAppear()
	{
		super.viewDidAppear();
		if (currentController == null)
			currentController = getNextPage();
		showCurrentPage();
	}

	@Override
	public void viewDidDisappear()
	{
		super.viewDidDisappear();
		hideCurrentPage();
	}

	/**
	 * Gibt die naechste Seite an.
	 * Ueberschreiben dieser Methode in Subklassen empfohlen.
	 * @return naechste Seite
	 */
	protected ViewController getNextPage()
	{
		if (currentController == null)
			currentControllerIndex--;
		currentControllerIndex = (currentControllerIndex + 1) % viewControllerList.size();
		return viewControllerList.get(currentControllerIndex);
	}

	/**
	 * Gibt die vorherige Seite an.
	 * Ueberschreiben dieser Methode in Subklassen empfohlen.
	 * @return vorherige Seite
	 */
	protected ViewController getPreviousPage()
	{
		if (currentControllerIndex <= 0)
			currentControllerIndex = viewControllerList.size();
		currentControllerIndex--;
		return viewControllerList.get(currentControllerIndex);
	}

	/**
	 * Versteckt die aktuelle Seite
	 */
	private void hideCurrentPage()
	{
		if (currentController == null)
			return;
		currentController.setParent(null);
		getView().remove(currentController.getView());
		currentController.viewDidDisappear();
	}

	/**
	 * Zeigt die aktuelle Seite
	 */
	private void showCurrentPage()
	{
		if (currentController == null)
			return;
		currentController.setParent(this);
		getView().add(currentController.getView());
		currentController.viewDidAppear();
	}
}
