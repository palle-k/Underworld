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

package project.game.ui.controllers;

import project.game.data.GameActor;
import project.game.data.GameActorDelegate;
import project.game.data.Level;
import project.game.data.MapObject;
import project.game.data.MapObjectDelegate;
import project.game.data.Player;
import project.game.data.PlayerDelegate;
import project.game.data.state.SavedGameState;
import project.game.ui.views.MapView;
import project.gui.components.TComponent;
import project.gui.components.TLabel;
import project.gui.components.TProgressBar;
import project.gui.controller.ViewController;
import project.gui.event.TEvent;
import project.gui.event.TEventHandler;
import project.gui.layout.TLayoutManager;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class LevelViewController extends ViewController implements PlayerDelegate, GameActorDelegate, MapObjectDelegate
{
	private EnemyController[] enemyControllers;
	private boolean           initialized;
	private Level             level;
	private MapView           mapView;
	private boolean           moveDownKeyPressed;
	private boolean           moveLeftKeyPressed;
	private boolean           moveRightKeyPressed;
	private boolean           moveUpKeyPressed;
	private PlayerController  playerController;
	private TProgressBar      playerHealth;

	@Override
	public void actorDidChangeState(final GameActor actor)
	{

	}

	@Override
	public void initializeView()
	{
		super.initializeView();
		try
		{
			level = new Level(Level.class.getResource("levels/level.properties"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}

		playerController = new PlayerController(level.getPlayer(), level.getMap(), null);

		TComponent topBar = new TComponent();
		getView().add(topBar);

		TLabel playerHealthLabel = new TLabel();
		playerHealthLabel.setSize(10, 1);
		playerHealthLabel.setText("Health:");
		topBar.add(playerHealthLabel);

		playerHealth = new TProgressBar();
		playerHealth.setLocation(10, 0);
		playerHealth.setSize(30, 1);
		playerHealth.setValue(6.0);
		playerHealth.setMaxValue(10.0);
		topBar.add(playerHealth);

		mapView = new MapView();
		mapView.setLevel(level);
		mapView.setMaskToBounds(true);
		getView().add(mapView);

		TComponent bottomBar = new TComponent();
		getView().add(bottomBar);

		getView().setLayoutManager(new TLayoutManager()
		{
			@Override
			public void layoutComponent(final TComponent component)
			{
				TComponent[] children = component.getChildren();
				if (children.length != 3)
					throw new RuntimeException("Layout must have 3 children.");
				int height    = component.getHeight();
				int width     = component.getWidth();
				int barHeight = height / 10;
				children[0].setLocation(0, 0);
				children[0].setSize(width, barHeight);
				children[1].setLocation(0, barHeight);
				children[1].setSize(width, height - 2 * barHeight - 1);
				children[2].setLocation(0, height - barHeight - 1);
				children[2].setSize(width, barHeight);
			}
		});

		getView().setAllowsFirstResponder(true);
		getView().setEventHandler(new TEventHandler()
		{
			@Override
			public void keyDown(final TEvent event)
			{
				if (event.getKey() == KeyEvent.VK_ESCAPE)
					getNavigationController().pop();
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveUpKey())
				{
					moveUpKeyPressed = true;
				}
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveLeftKey())
				{
					moveLeftKeyPressed = true;
				}
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveRightKey())
				{
					moveRightKeyPressed = true;
				}
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveDownKey())
				{
					moveDownKeyPressed = true;
				}

			}

			@Override
			public void keyUp(final TEvent event)
			{
				if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveUpKey())
					moveUpKeyPressed = false;
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveLeftKey())
					moveLeftKeyPressed = false;
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveRightKey())
					moveRightKeyPressed = false;
				else if (event.getKey() == SavedGameState.getSavedGameState().getSettingsState().getMoveDownKey())
					moveDownKeyPressed = false;
			}
		});
		getView().requestFirstResponder();
		//getView().setOnAnimationUpdate((double time, double timeDelta) -> updateViews(time, timeDelta));
		initialized = true;
	}

	@Override
	public void mapObjectDidMove(final MapObject mapObject)
	{

	}

	@Override
	public void playerShouldShowAttackPotionOverlay(final Player player)
	{

	}

	@Override
	public void playerShouldShowHealthPotionOverlay(final Player player)
	{

	}

	@Override
	public void playerShouldShowSkill1Overlay(final Player player, final GameActor target)
	{

	}

	@Override
	public void playerShouldShowSkill1State(final Player player)
	{

	}

	@Override
	public void playerShouldShowSkill2Overlay(final Player player, final GameActor target)
	{

	}

	@Override
	public void playerShouldShowSkill2State(final Player player)
	{

	}

	@Override
	public void playerShouldShowSkill3Overlay(final Player player, final GameActor target)
	{

	}

	@Override
	public void playerShouldShowSkill3State(final Player player)
	{

	}

	@Override
	public void playerShouldShowSkill4Overlay(final Player player, final GameActor target)
	{

	}

	@Override
	public void playerShouldShowSkill4State(final Player player)
	{

	}

	@Override
	protected void updateViews(final double time, final double timeDelta)
	{
		super.updateViews(time, timeDelta);
		if (!initialized)
			return;
		playerController.update(time);

		if (level == null)
			return;

		if (moveUpKeyPressed)
			playerController.moveUp();
		if (moveLeftKeyPressed)
			playerController.moveLeft();
		if (moveRightKeyPressed)
			playerController.moveRight();
		if (moveDownKeyPressed)
			playerController.moveDown();

		Point scrollCenter = level.getPlayer().getBounds().getLocation();
		scrollCenter.translate(-mapView.getWidth() / 2, -mapView.getHeight() / 2);
		mapView.setOffset(scrollCenter);
		mapView.setPointOfVision(level.getPlayer().getCenter());
	}
}
