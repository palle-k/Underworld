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

/**
 * Controller zur Steuerung einer Komponente
 * Dient als Bruecke zwischen Daten- und View-Modell
 */
public abstract class ViewController
{
	private final TComponent     view;
	private       ViewController parent;
	private       boolean        replacesParentViewController;
	private       boolean        viewInitialized;

	/**
	 * Erstellt einen neuen ViewController mit angegebenem Parent-ViewController und
	 * angegebener, zu verwaltender Komponente
	 *
	 * @param parent Uebergeordneter ViewController
	 * @param view   zu verwaltende Komponente
	 */
	public ViewController(ViewController parent, TComponent view)
	{
		this.parent = parent;
		this.view = view;
		replacesParentViewController = true;
		viewInitialized = false;
	}

	/**
	 * Erstellt einen neuen ViewController mit angegebener, zu verwaltender Komponente
	 * @param view zu verwaltende Komponente
	 */
	public ViewController(TComponent view)
	{
		this.view = view;
		replacesParentViewController = true;
	}

	/**
	 * Erstellt einen neuen ViewController
	 */
	public ViewController()
	{
		this.view = new TComponent();
		replacesParentViewController = true;
	}

	/**
	 * Gibt den naechsten NavigationController an, der sich in der Controller-Hierarchie
	 * ueber diesem Controller befindet. Existiert in der Controller-Hierarchie kein
	 * NavigationController, wird null zurueckgegeben
	 * @return naechster NavigationController oder null
	 */
	public NavigationController getNavigationController()
	{
		if (this instanceof NavigationController)
			return (NavigationController) this;
		else if (parent != null)
			return parent.getNavigationController();
		return null;
	}

	/**
	 * Gibt den naechsten PageController an, der sich in der Controller-Hierarchie
	 * ueber diesem Controller befindet. Existiert in der Controller-Hierarchie kein
	 * PageController, wird null zurueckgegeben
	 * @return naechster PageController oder null
	 */
	public PageController getPageController()
	{
		if (this instanceof PageController)
			return (PageController) this;
		else if (parent != null)
			return parent.getPageController();
		return null;
	}

	/**
	 * Gibt den ViewController an, der sich in der Controller-Hierarchie ueber
	 * diesem ViewController befindet
	 * @return uebergeordneter ViewController
	 */
	public ViewController getParent()
	{
		return parent;
	}

	/**
	 * Gibt die Komponente an, die von diesem ViewController verwaltet wird.
	 * @return verwaltete Komponente
	 */
	public TComponent getView()
	{
		return view;
	}

	/**
	 * Gibt an, ob die von diesem ViewController verwaltete Komponente die
	 * Komponente des uebergeordneten ViewControllers ueberdeckt oder
	 * lediglich nichtleere Bereiche ueberzeichnet
	 * @return true, wenn der uebergeordnete ViewController ueberdeckt wird, sonst false
	 */
	public boolean replacesParentViewController()
	{
		return replacesParentViewController;
	}

	/**
	 * Legt fest,ob die von diesem ViewController verwaltete Komponente die
	 * Komponente des uebergeordneten ViewControllers ueberdeckt oder
	 * lediglich nichtleere Bereiche ueberzeichnet
	 * @param replacesParentViewController true, wenn der uebergeordnete ViewController
	 *                                     ueberdeckt wird, sonst false
	 */
	public void setReplacesParentViewController(final boolean replacesParentViewController)
	{
		this.replacesParentViewController = replacesParentViewController;
	}

	/**
	 * Initialisiert die Komponente, welche von diesem ViewController verwaltet wird.
	 * In Subklassen, die eine erweiterte Benutzeroberflaeche darstellen, ist diese Methode
	 * zu ueberschreiben.
	 */
	protected void initializeView()
	{
		getView().addOnAnimationUpdate(this::updateViews);
	}

	/**
	 * Aktualisiert die Komponente, die von diesem ViewController verwaltet wird
	 * und alle Subkomponenten. In Subklassen, die eine Zeitgesteuerte Aktualisierung benoetigen,
	 * ist diese Methode zu ueberschreiben
	 *
	 * @param time      Zeitpunkt der Aktualisierung in Sekunden
	 * @param timeDelta Zeit seit der letzten Aktualisierung in Sekunden
	 */
	protected void updateViews(double time)
	{

	}

	/**
	 * Die Komponente, welche von diesem ViewController verwaltet wird,
	 * ist sichtbar gemacht worden.
	 */
	protected void viewDidAppear()
	{
		if (!viewInitialized)
		{
			initializeView();
			viewInitialized = true;
		}
	}

	/**
	 * Die Komponente, welche von diesem ViewController verwaltet wird,
	 * ist nicht mehr sichtbar.
	 */
	protected void viewDidDisappear()
	{

	}

	/**
	 * Setzt den uebergeordneten Controller
	 * @param parent uebergeordneter Controller
	 */
	void setParent(final ViewController parent)
	{
		this.parent = parent;
	}
}
