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

package project.gui.controller;

import project.gui.components.TComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for ViewControllers on the same level
 */
public class PageController extends ViewController
{
	private final List<ViewController> viewControllerList;
	private int currentControllerIndex = -1;

	public PageController(final ViewController parent, final TComponent view)
	{
		super(parent, view);
		viewControllerList = new ArrayList<>();
	}

	public PageController(final TComponent view)
	{
		super(view);
		viewControllerList = new ArrayList<>();
	}

	public void addController(ViewController controller)
	{
		viewControllerList.add(controller);
	}

	public void addController(ViewController controller, int index)
	{
		viewControllerList.add(index, controller);
	}

	public void next()
	{
		hideCurrentPage();
		currentControllerIndex = (currentControllerIndex + 1) % viewControllerList.size();
		showCurrentPage();
	}

	public void previous()
	{
		hideCurrentPage();
		if (currentControllerIndex <= 0)
			currentControllerIndex = viewControllerList.size();
		currentControllerIndex--;
		showCurrentPage();
	}

	public void removeController(ViewController controller)
	{
		viewControllerList.remove(controller);
	}

	private void hideCurrentPage()
	{
		if (currentControllerIndex != -1 && currentControllerIndex < viewControllerList.size())
		{
			getView().remove(viewControllerList.get(currentControllerIndex).getView());
			viewControllerList.get(currentControllerIndex).viewDidDisappear();
		}
	}

	private void showCurrentPage()
	{
		if (currentControllerIndex != -1 && currentControllerIndex < viewControllerList.size())
		{
			getView().add(viewControllerList.get(currentControllerIndex).getView());
			viewControllerList.get(currentControllerIndex).viewDidAppear();
		}
	}
}
