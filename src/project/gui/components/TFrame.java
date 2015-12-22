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

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;
import project.gui.graphics.TGraphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.reflect.Method;

public class TFrame extends TBufferedView
{
	private class TFrameListener implements ComponentListener
	{

		@Override
		public void componentHidden(ComponentEvent e)
		{

		}

		@Override
		public void componentMoved(ComponentEvent e)
		{

		}

		@Override
		public void componentResized(ComponentEvent e)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					setNeedsDisplay(null);
				}
			});
		}

		@Override
		public void componentShown(ComponentEvent e)
		{

		}
	}

	private static Font createDefaultNormalFont()
	{
		if (System.getProperty("os.name", "").toLowerCase().indexOf("win") >= 0)
		{
			return new Font("Courier New", 0, 14);
		}
		return new Font("Monospaced", 0, 14);
	}

	private SwingTerminal terminal;

	public TFrame()
	{
		super();
		terminal = TerminalFacade.createSwingTerminal(80, 25);
		setMaskToBounds(true);
	}

	public String getTitle()
	{
		JFrame underlyingFrame = getUnderlyingFrame();
		if (underlyingFrame != null)
			return underlyingFrame.getTitle();
		else
			return null;
	}

	@Override
	public void setFrame(Rectangle frame)
	{
		super.setFrame(frame);
		JFrame underlyingFrame = getUnderlyingFrame();
		if (underlyingFrame != null)
		{
			FontMetrics fontMetrics = underlyingFrame.getGraphics().getFontMetrics(createDefaultNormalFont());
			underlyingFrame.setSize(fontMetrics.charWidth(' ') * frame.getSize().width, fontMetrics.getHeight() * frame.getSize().height + 22);
		}
	}

	@Override
	public void setLocation(Point location)
	{
		super.setLocation(location);
		getUnderlyingFrame().setLocation(location);
	}

	@Override
	public void setNeedsDisplay(Rectangle dirtyRect)
	{
		if (terminal == null || getUnderlyingFrame() == null)
			return;
		super.setNeedsDisplay(dirtyRect);
		final Rectangle bridge_DirtyRect = dirtyRect;
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				Rectangle dirtyRect = bridge_DirtyRect;
				boolean needsLayout = false;
				if (frame.width != terminal.getTerminalSize().getColumns())
				{
					frame.width = terminal.getTerminalSize().getColumns();
					needsLayout = true;
				}
				if (frame.height != terminal.getTerminalSize().getRows())
				{
					frame.height = terminal.getTerminalSize().getRows();
					needsLayout = true;
				}
				if (needsLayout)
					setNeedsLayout();
				if (dirtyRect == null)
				{
					//terminal.clearScreen();
					//clearFramebuffer();
					dirtyRect = new Rectangle(0, 0, getWidth(), getHeight());
				}
				if (!needsDisplay())
					return;
				TGraphics g = new TGraphics(terminal, dirtyRect);
				dispatchRepaint(g, dirtyRect);
				terminal.moveCursor(getWidth(), getHeight());
			}
		});
	}

	@Override
	public void setSize(Dimension size)
	{
		super.setSize(size);
		JFrame underlyingFrame = getUnderlyingFrame();
		if (underlyingFrame != null)
		{
			FontMetrics fontMetrics = underlyingFrame.getGraphics().getFontMetrics(createDefaultNormalFont());
			underlyingFrame.setSize(fontMetrics.charWidth(' ') * size.width, fontMetrics.getHeight() * size.height);
		}
	}

	public void setTitle(String title)
	{
		JFrame underlyingFrame = getUnderlyingFrame();
		if (underlyingFrame != null)
			underlyingFrame.setTitle(title);
	}

	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		if (visible)
			terminal.enterPrivateMode();
		else
			terminal.exitPrivateMode();
		terminal.setCursorVisible(false);
		if (visible)
		{
			getUnderlyingFrame().addComponentListener(new TFrameListener());
			try
			{
				@SuppressWarnings("rawtypes")
				Class util = Class.forName("com.apple.eawt.FullScreenUtilities");
				@SuppressWarnings("rawtypes")
				Class params[] = new Class[]{ Window.class, Boolean.TYPE };
				@SuppressWarnings("unchecked")
				Method method = util.getMethod("setWindowCanFullScreen", params);
				method.invoke(util, getUnderlyingFrame(), true);
			} catch (Exception e)
			{

			}
			setSize(getSize());
			setDrawsBackground(true);
			setNeedsDisplay(null);
		}
	}

	private JFrame getUnderlyingFrame()
	{
		return terminal.getJFrame();
	}
}
