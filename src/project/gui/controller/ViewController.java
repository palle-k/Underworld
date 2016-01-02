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

public class ViewController
{
	private final TComponent     view;
	private       ViewController parent;
	private       boolean        replacesParentViewController;
	private       boolean        viewInitialized;

	public ViewController(ViewController parent, TComponent view)
	{
		this.parent = parent;
		this.view = view;
		replacesParentViewController = true;
		viewInitialized = false;
	}

	public ViewController(TComponent view)
	{
		this.view = view;
		replacesParentViewController = true;
	}

	public ViewController()
	{
		this.view = new TComponent();
		replacesParentViewController = true;
	}

	public NavigationController getNavigationController()
	{
		if (this instanceof NavigationController)
			return (NavigationController) this;
		else if (parent != null)
			return parent.getNavigationController();
		return null;
	}

	public PageController getPageController()
	{
		if (this instanceof PageController)
			return (PageController) this;
		else if (parent != null)
			return parent.getPageController();
		return null;
	}

	public ViewController getParent()
	{
		return parent;
	}

	public TComponent getView()
	{
		return view;
	}

	public void initializeView()
	{
		getView().setOnAnimationUpdate((double time, double timeDelta) -> updateViews(time, timeDelta));
	}

	public boolean replacesParentViewController()
	{
		return replacesParentViewController;
	}

	public void setReplacesParentViewController(final boolean replacesParentViewController)
	{
		this.replacesParentViewController = replacesParentViewController;
	}

	public void viewDidAppear()
	{
		if (!viewInitialized)
		{
			initializeView();
			viewInitialized = true;
		}
	}

	public void viewDidDisappear()
	{

	}

	protected void setParent(final ViewController parent)
	{
		this.parent = parent;
	}

	protected void updateViews(double time, double timeDelta)
	{

	}
}
