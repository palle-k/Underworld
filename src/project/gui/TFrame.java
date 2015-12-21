/******************************************************************************
 * Copyright (c) 2015 Palle Klewitz.                                          *
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

package project.gui;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class TFrame extends TComponent
{
	private SwingTerminal terminal;

	private class TFrameListener implements ComponentListener
	{

		@Override
		public void componentResized(ComponentEvent e)
		{
			SwingUtilities.invokeLater(() -> setNeedsDisplay(null));
		}

		@Override
		public void componentMoved(ComponentEvent e)
		{

		}

		@Override
		public void componentShown(ComponentEvent e)
		{

		}

		@Override
		public void componentHidden(ComponentEvent e)
		{

		}
	}

	public TFrame()
	{
		super();
		terminal = TerminalFacade.createSwingTerminal(80, 25);
		setMaskToBounds(true);
	}

	@Override
	public void setFrame(Rectangle frame)
	{
		super.setFrame(frame);
		JFrame underlyingFrame = getUnderlyingFrame();
		if (underlyingFrame != null)
		{
			FontMetrics fontMetrics = underlyingFrame.getGraphics().getFontMetrics(createDefaultNormalFont());
			underlyingFrame.setSize(fontMetrics.charWidth(' ') * frame.getSize().width,fontMetrics.getHeight() * frame.getSize().height);
		}
	}

	@Override
	public void setLocation(Point location)
	{
		super.setLocation(location);
		getUnderlyingFrame().setLocation(location);
	}

	@Override
	public void setSize(Dimension size)
	{
		super.setSize(size);
		JFrame underlyingFrame = getUnderlyingFrame();
		if (underlyingFrame != null)
		{
			FontMetrics fontMetrics = underlyingFrame.getGraphics().getFontMetrics(createDefaultNormalFont());
			underlyingFrame.setSize(fontMetrics.charWidth(' ') * size.width,fontMetrics.getHeight() * size.height);
		}
	}

	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		if (visible)
			terminal.enterPrivateMode();
		else
			terminal.exitPrivateMode();
		getUnderlyingFrame().addComponentListener(new TFrameListener());
		setSize(getSize());
		setDrawsBackground(true);
		setNeedsDisplay(null);
	}

	private JFrame getUnderlyingFrame()
	{
		return terminal.getJFrame();
	}

	private static Font createDefaultNormalFont()
	{
		if (System.getProperty("os.name", "").toLowerCase().indexOf("win") >= 0)
		{
			return new Font("Courier New", 0, 14);
		}
		return new Font("Monospaced", 0, 14);
	}

	@Override
	public void setNeedsDisplay(Rectangle dirtyRect)
	{
		if (terminal == null)
			return;
		if (dirtyRect == null)
		{
			terminal.clearScreen();
			dirtyRect = new Rectangle(new Point(), getSize());
		}
		frame.width = terminal.getTerminalSize().getColumns();
		frame.height = terminal.getTerminalSize().getRows();
		super.setNeedsDisplay(dirtyRect);
		TGraphics g = new TGraphics(terminal, dirtyRect);
		dispatchRepaint(g, dirtyRect);
		terminal.moveCursor(getWidth(), getHeight());
	}
}
