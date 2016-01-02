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

import java.util.Stack;

/**
 * Controller for hierarchical structures
 */
public class NavigationController extends ViewController
{
	private Stack<ViewController> navigationStack;

	public NavigationController(ViewController parent, TComponent view)
	{
		super(parent, view);
		view.setLayoutManager(new FullSizeSubviewLayout());
		navigationStack = new Stack<>();
	}

	public NavigationController(TComponent view)
	{
		super(view);
		view.setLayoutManager(new FullSizeSubviewLayout());
		navigationStack = new Stack<>();
	}

	public void pop()
	{
		if (!navigationStack.isEmpty())
		{
			getView().remove(navigationStack.peek().getView());
			navigationStack.peek().setParent(null);
			boolean parentWasRemoved = navigationStack.peek().replacesParentViewController();
			navigationStack.pop().viewDidDisappear();
			if (parentWasRemoved)
			{
				getView().add(navigationStack.peek().getView());
				navigationStack.peek().viewDidAppear();
			}
		}
		else
		{
			getView().setVisible(false);
			viewDidDisappear();
		}
	}

	public void push(ViewController controller)
	{
		if (!navigationStack.isEmpty() && controller.replacesParentViewController())
		{
			ViewController previousController = navigationStack.peek();
			getView().remove(previousController.getView());
			previousController.viewDidDisappear();
		}
		controller.setParent(this);
		navigationStack.push(controller);
		getView().add(controller.getView());
		controller.viewDidAppear();
	}

	@Override
	public void viewDidAppear()
	{

	}

	@Override
	public void viewDidDisappear()
	{

	}
}
