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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Fenster, welches als Komponente verwendet werden kann.
 */
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
			SwingUtilities.invokeLater(() -> {
				setNeedsLayout();
				setNeedsDisplay();
			});
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

	private final Stack<Rectangle> repaintStack;

	private final SwingTerminal terminal;

	private boolean addedListener;

	private GameLoop gameLoop;

	private long renderCount;

	private long renderingTime;

	private long repaintCount;

	private long repaintTime;

	private long totalRenderingTime;

	private long totalRepaintTime;

	/**
	 * Erstellt ein neues Fenster, welches durch ein Lanterna-Swing-Terminal
	 * gestuetzt wird.
	 */
	public TFrame()
	{
		super();
		addedListener = false;
		TerminalAppearance appearance = TerminalAppearance.DEFAULT_APPEARANCE;
		appearance.getNormalTextFont();
		terminal = TerminalFacade.createSwingTerminal(appearance, 80, 25);
		setMaskToBounds(true);
		repaintStack = new Stack<>();
	}

	/**
	 * Gibt die GameLoop zurueck, welche Animationsaktualisierungen in diesem
	 * Fenster steuert.<br>
	 * Die GameLoop kann erst erhalten werden, nachdem das Fenster sichtbar gemacht
	 * wurde
	 * @return GameLoop
	 */
	public GameLoop getGameLoop()
	{
		return gameLoop;
	}

	/**
	 * Gibt den Titel des Fensters zurueck<br>
	 * Der Titel kann erst nach Sichtbarmachen
	 * des Fensters erhalten werden.
	 * @return Fenstertitel
	 */
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
			FontMetrics fontMetrics = underlyingFrame.getGraphics()
					.getFontMetrics(TerminalAppearance.DEFAULT_NORMAL_FONT);
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
		/* Ueberpruefe, ob ein Neuzeichnen ueberhaupt notwendig ist */

		if (terminal == null || getUnderlyingFrame() == null)
			return;
		if (dirtyRect != null && dirtyRect.isEmpty())
			return;
		if (dirtyRect != null && (dirtyRect.getMaxX() <= 0 || dirtyRect.getMinX() >= getWidth()))
			return;
		if (dirtyRect != null && (dirtyRect.getMaxY() <= 0 || dirtyRect.getMinY() >= getHeight()))
			return;
		super.setNeedsDisplay(dirtyRect);
		if (dirtyRect == null)
			dirtyRect = new Rectangle(0, 0, getWidth(), getHeight());

		/* Ueberpruefe, ob der dreckige Bereich schon als dreckig markiert ist */

		for (int i = 0; i < repaintStack.size(); i++)
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

		/*
		Fuege den dreckigen Bereich dem Repaintstack hinzu,
		in dem saemtliche dreckige Bereiche gespeichert werden
		*/

		repaintStack.add(dirtyRect);

		/*
		Fuehre ein Neuzeichnen im Swing-Thread durch
		Verhindert Flackern, da Lanterna erst aktualisieren kann, wenn
		die Engine den Zeichenvorgang beendet hat
		Erlaubt ausserdem das Zusammenfassen mehrerer Renderbereiche und
		Besseres Multithreading der Engine
		*/

		SwingUtilities.invokeLater(() -> {
			/* Ueberpruefe, ob ein anderer repaint-Befehl schon saemtliche Arbeit erledigt hat */
			if (repaintStack.isEmpty())
				return;

			/* Performance metrics */
			renderCount++;
			long    repaintStart = System.currentTimeMillis();

			/* Ueberpruefe, ob Fenstergroesse geaendert */

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

			/* Zeichne alle dreckigen Bereiche neu */

			while (!repaintStack.isEmpty())
			{
				Rectangle dirtyRect1;

				/*
				Verhindere, dass der RepaintStack von einem anderen Ort
				modifiziert wird ohne dass die ganze Schleife andere Threads blockiert
				*/
				synchronized (repaintStack)
				{
					if (!repaintStack.isEmpty())
						dirtyRect1 = repaintStack.pop();
					else
						break;
				}
				TGraphics g = new TGraphics(terminal, dirtyRect1);
				dispatchRepaint(g, dirtyRect1);
			}

			//Performance metrics
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
			FontMetrics fontMetrics = underlyingFrame.getGraphics()
					.getFontMetrics(TerminalAppearance.DEFAULT_NORMAL_FONT);
			underlyingFrame.setSize(fontMetrics.charWidth(' ') * size.width, fontMetrics.getHeight() * size.height);
		}
	}

	/**
	 * Setzt den Titel des Fensters.<br>
	 * Der Titel des Fenster kann erst veraendert werden,
	 * wenn das Fenster sichtbar ist.
	 * @param title neuer Titel
	 */
	public void setTitle(String title)
	{
		JFrame underlyingFrame = getUnderlyingFrame();
		if (underlyingFrame != null)
			underlyingFrame.setTitle(title);
	}

	/**
	 * Legt fest, ob die Titelleiste des Fensters versteckt werden soll.
	 * Dies ist erst moeglich, wenn das Fenster sichtbar ist.
	 * @param undecorated true, wenn die Titelleiste versteckt werden soll,
	 *                    sonst false
	 */
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
			getUnderlyingFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			if (!addedListener)
			{
				TFrameListener listener = new TFrameListener();
				getUnderlyingFrame().addComponentListener(listener);
				getUnderlyingFrame().addKeyListener(listener);
				gameLoop = new GameLoop();
				gameLoop.addAction(this::updateAnimations);

				//Performance metrics
				if (System.getProperty("com.palleklewitz.underworld.performancemetrics", "false")
						.equalsIgnoreCase("true"))
					gameLoop.addAction((time) -> setTitle(
							String.format(
									"Rendering (total %d frames):" +
									"last: %d avg: %dms, Animation: last: %d, avg: %dms, " +
									"Swing Repaint: last: %d, avg: %dms",
									renderCount,
									renderingTime,
									totalRenderingTime / Math.max(
											renderCount, 1),
									gameLoop.getUpdateTimeDelta(),
									gameLoop.getTotalUpdateTime()
									/ Math.max(gameLoop.getNumberOfUpdates(), 0),
									repaintTime,
									totalRepaintTime
									/ Math.max(repaintCount, 1))));
				gameLoop.start();
				addedListener = true;

				Component terminalRenderer = getUnderlyingFrame().getContentPane().getComponent(0);
				getUnderlyingFrame().getContentPane().remove(terminalRenderer);

				//Antialiasing ueber Einschieben von antialiasedPanel

				JComponent antialiasedPanel = new JPanel()
				{
					@Override
					protected void paintChildren(final Graphics g)
					{
						//Performance metrics
						long time1 = System.currentTimeMillis();

						//Antialiasing aktivieren
						((Graphics2D) g).setRenderingHint(
								RenderingHints.KEY_TEXT_ANTIALIASING,
								RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

						//SwingTerminal zeichnen
						super.paintChildren(g);

						//Performance metrics
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
				//Mac OS X FullScreen support
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
				//Exception kann nicht verhindert werden
				//Ignorieren
			}
			setSize(getSize());
			setDrawsBackground(true);
			new Timer().schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					SwingUtilities.invokeLater(TFrame.this::setNeedsLayout);
				}
			}, 100);
			setNeedsDisplay();
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
