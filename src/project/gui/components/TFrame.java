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

package project.gui.components;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;
import com.googlecode.lanterna.terminal.swing.TerminalAppearance;
import project.gui.dynamics.GameLoop;
import project.gui.event.TEvent;
import project.gui.event.TResponder;
import project.gui.graphics.Appearance;
import project.gui.graphics.TGraphics;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Method;
import java.util.Stack;

public class TFrame extends TBufferedView
{
	private class TFrameListener implements ComponentListener, KeyListener
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
			SwingUtilities.invokeLater(() -> setNeedsDisplay(null));
		}

		@Override
		public void componentShown(ComponentEvent e)
		{

		}

		@Override
		public void keyPressed(final KeyEvent e)
		{
			if (e.getExtendedKeyCode() == KeyEvent.VK_TAB)
			{
				TResponder firstResponder = getFirstResponder();
				if (!firstResponder.isSingleFirstResponder())
				{
					TResponder nextResponder = getNextResponder();
					if (nextResponder != null)
						nextResponder.requestFirstResponder();
				}
			}
			else
			{
				TResponder firstResponder = getFirstResponder();
				if (firstResponder == null)
					return;
				TEvent event = new TEvent((char) e.getExtendedKeyCode(), TEvent.KEY_DOWN);
				firstResponder.dispatchEvent(event);
			}
		}

		@Override
		public void keyReleased(final KeyEvent e)
		{
			TResponder firstResponder = getFirstResponder();
			if (firstResponder == null)
				return;
			TEvent event = new TEvent((char) e.getExtendedKeyCode(), TEvent.KEY_UP);
			firstResponder.dispatchEvent(event);
		}

		@Override
		public void keyTyped(final KeyEvent e)
		{

		}
	}

	private static Font createDefaultNormalFont()
	{
		if (System.getProperty("os.name", "").toLowerCase().contains("win"))
		{
			return new Font("Courier New", 0, 14);
		}
		return new Font("Monospaced", 0, 14);
	}

	private boolean          addedListener;
	private GameLoop         gameLoop;
	private long             renderCount;
	private long             renderingTime;
	private long             repaintCount;
	private Stack<Rectangle> repaintStack;
	private long             repaintTime;
	private SwingTerminal    terminal;
	private long             totalRenderingTime;
	private long             totalRepaintTime;

	public TFrame()
	{
		super();
		addedListener = false;
		TerminalAppearance appearance = TerminalAppearance.DEFAULT_APPEARANCE;
		terminal = TerminalFacade.createSwingTerminal(appearance, 80, 25);
		setMaskToBounds(true);
		repaintStack = new Stack<>();
	}

	public GameLoop getGameLoop()
	{
		return gameLoop;
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
			underlyingFrame.setSize(
					fontMetrics.charWidth(' ') * frame.getSize().width,
					fontMetrics.getHeight() * frame.getSize().height + 22);
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
		if (dirtyRect != null && dirtyRect.isEmpty())
			return;
		super.setNeedsDisplay(dirtyRect);
		if (dirtyRect == null)
		{
			//terminal.clearScreen();
			//clearFramebuffer();
			dirtyRect = new Rectangle(0, 0, getWidth(), getHeight());
		}
		/*Iterator<Rectangle> dirtyRectIterator = repaintStack.iterator();
		while (dirtyRectIterator.hasNext())
		{
			Rectangle next = dirtyRectIterator.next();
			if (dirtyRect.contains(next))
				dirtyRectIterator.remove();
			else if (next.contains(dirtyRect))
				return;
			else if (next.equals(dirtyRect))
				return;
		}*/

		for (int i = 0; i < repaintStack.size(); i++)
		{
			synchronized (repaintStack)
			{
				if (repaintStack.size() <= i)
					break;
				Rectangle next = repaintStack.get(i);
				if (dirtyRect.contains(next))
				{

					repaintStack.remove(i);
					i--;
				}
				else if (next.contains(dirtyRect))
					return;
				else if (next.equals(dirtyRect))
					return;
			}
		}

		repaintStack.add(dirtyRect);
		SwingUtilities.invokeLater(() -> {
			if (repaintStack.isEmpty())
				return;
			renderCount++;
			long    repaintStart = System.currentTimeMillis();
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

			if (!needsDisplay())
				return;
			while (!repaintStack.isEmpty())
			{
				Rectangle dirtyRect1;
				synchronized (repaintStack)
				{
					if (!repaintStack.isEmpty())
						dirtyRect1 = repaintStack.pop();
					else
						break;
				}
				TGraphics g          = new TGraphics(terminal, dirtyRect1);
				dispatchRepaint(g, dirtyRect1);
				terminal.moveCursor(getWidth(), getHeight());
			}
			long repaintEnd = System.currentTimeMillis();
			renderingTime = repaintEnd - repaintStart;
			totalRenderingTime += renderingTime;
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

	public void setUndecorated(boolean undecorated)
	{
		JFrame underlyingFrame = getUnderlyingFrame();
		if (underlyingFrame != null)
		{
			underlyingFrame.setVisible(false);
			underlyingFrame.dispose();
			underlyingFrame.setUndecorated(undecorated);
			underlyingFrame.setVisible(true);
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
		terminal.setCursorVisible(false);
		if (visible)
		{
			if (!addedListener)
			{
				TFrameListener listener = new TFrameListener();
				getUnderlyingFrame().addComponentListener(listener);
				getUnderlyingFrame().addKeyListener(listener);
				gameLoop = new GameLoop();
				gameLoop.addAction(this::updateAnimations);
				if (System.getProperty("com.palleklewitz.underworld.performancemetrics", "false")
						.equalsIgnoreCase("true"))
					gameLoop.addAction((time, timeDelta) -> setTitle(String.format(                                                       "Rendering (total %d frames):" +
					                                                               "last: %d avg: %dms, Animation: last: %d, avg: %dms, " +
					                                                               "Swing Repaint: last: %d, avg: %dms",
					                                                               renderCount,
					                                                               renderingTime,
					                                                               totalRenderingTime / Math.max(
							                                                               renderCount, 1),
					                                                                                                                      gameLoop.getUpdateTimeDelta(),
					                                                               gameLoop.getTotalUpdateTime()
					                                                               / Math.max(
							                                                               gameLoop.getNumberOfUpdates(),
							                                                               0),
					                                                               repaintTime,
					                                                               totalRepaintTime
					                                                               / Math.max(repaintCount, 1))));
				gameLoop.start();
				addedListener = true;

				Component terminalRenderer = getUnderlyingFrame().getContentPane().getComponent(0);
				getUnderlyingFrame().getContentPane().remove(terminalRenderer);

				JComponent antialiasedPanel = new JPanel()
				{
					@Override
					protected void paintChildren(final Graphics g)
					{
						long time1 = System.currentTimeMillis();
						((Graphics2D) g).setRenderingHint(
								RenderingHints.KEY_TEXT_ANTIALIASING,
								RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
						super.paintChildren(g);
						repaintTime = System.currentTimeMillis() - time1;
						repaintCount++;
						totalRepaintTime += repaintTime;
					}
				};
				antialiasedPanel.setBackground(Appearance.defaultBackgroundColor);
				antialiasedPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
				antialiasedPanel.add(terminalRenderer);
				getUnderlyingFrame().getContentPane().add(antialiasedPanel);

			}
			try
			{
				@SuppressWarnings("rawtypes")
				Class util = Class.forName("com.apple.eawt.FullScreenUtilities");
				@SuppressWarnings("rawtypes")
				Class params[] = new Class[]{ Window.class, Boolean.TYPE };
				@SuppressWarnings("unchecked")
				Method method = util.getMethod("setWindowCanFullScreen", params);
				method.invoke(util, getUnderlyingFrame(), true);
			}
			catch (Exception e)
			{
				//Don't care about exception (fullscreen not supported)
			}
			setSize(getSize());
			setDrawsBackground(true);
			setNeedsDisplay(null);
			TResponder nextResponder = getNextResponder();
			if (nextResponder != null)
				nextResponder.requestFirstResponder();
		}
		else
		{
			gameLoop.stop();
		}
	}

	private JFrame getUnderlyingFrame()
	{
		return terminal.getJFrame();
	}

}
