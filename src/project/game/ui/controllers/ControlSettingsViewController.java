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

import project.game.data.state.SavedGameState;
import project.game.data.state.SettingsState;
import project.gui.components.TButton;
import project.gui.components.TLabel;
import project.gui.controller.ViewController;
import project.gui.event.SelectableGroup;
import project.gui.layout.VerticalFlowLayout;

import static project.game.localization.LocalizedString.LocalizedString;

public class ControlSettingsViewController extends ViewController
{
	public static final String[] keycodeDescriptions =
			{
					"Space",
					"Page Up",
					"Page Down",
					"End",
					"Home",
					"Arrow Left",
					"Arrow Up",
					"Arrow Right",
					"Arrow Down",
					")",
					"*",
					"+",
					",",
					"-",
					".",
					"/",
					"0",
					"1",
					"2",
					"3",
					"4",
					"5",
					"6",
					"7",
					"8",
					"9",
					":",
					";",
					"<",
					"=",
					">",
					"?",
					"@",
					"A",
					"B",
					"C",
					"D",
					"E",
					"F",
					"G",
					"H",
					"I",
					"J",
					"K",
					"L",
					"M",
					"N",
					"O",
					"P",
					"Q",
					"R",
					"S",
					"T",
					"U",
					"V",
					"W",
					"X",
					"Y",
					"Z",
					"[",
					"\\",
					"]",
					"^",
					"_",
					"Numpad 0",
					"Numpad 1",
					"Numpad 2",
					"Numpad 3",
					"Numpad 4",
					"Numpad 5",
					"Numpad 6",
					"Numpad 7",
					"Numpad 8",
					"Numpad 9",
					"Multiply",
					"Add",
					"Separator",
					"Subtract",
					"Decimal",
					"Divide",
					"F1",
					"F2",
					"F3",
					"F4",
					"F5",
					"F6",
					"F7",
					"F8",
					"F9",
					"F10",
					"F11",
					"F12",
					"|",
					"}",
					"~",
					"Delete"
			};

	@Override
	public void initializeView()
	{
		super.initializeView();

		TLabel label = new TLabel();
		label.setSize(54, 7);
		label.setText(LocalizedString("control_settings_title"));
		getView().add(label);

		final SettingsState settingsState = SavedGameState.getSavedGameState().getSettingsState();

		TButton moveUp = new TButton();
		moveUp.setSize(30, 1);
		getView().add(moveUp);

		TButton moveLeft = new TButton();
		moveLeft.setSize(30, 1);
		getView().add(moveLeft);

		TButton moveRight = new TButton();
		moveRight.setSize(30, 1);
		getView().add(moveRight);

		TButton moveDown = new TButton();
		moveDown.setSize(30, 1);
		getView().add(moveDown);

		TButton baseAttack = new TButton();
		baseAttack.setSize(30, 1);
		getView().add(baseAttack);

		TButton skill1 = new TButton();
		skill1.setSize(30, 1);
		getView().add(skill1);

		TButton skill2 = new TButton();
		skill2.setSize(30, 1);
		getView().add(skill2);

		TButton skill3 = new TButton();
		skill3.setSize(30, 1);
		getView().add(skill3);

		TButton skill4 = new TButton();
		skill4.setSize(30, 1);
		getView().add(skill4);

		TButton healthPotion = new TButton();
		healthPotion.setSize(30, 1);
		getView().add(healthPotion);

		TButton attackPotion = new TButton();
		attackPotion.setSize(30, 1);
		getView().add(attackPotion);

		TButton back = new TButton();
		back.setSize(30, 1);
		back.setText(LocalizedString("control_settings_back"));
		back.setActionHandler(() -> getNavigationController().pop());
		getView().add(back);

		SelectableGroup buttonGroup = new SelectableGroup();
		buttonGroup.addResponder(moveUp);
		buttonGroup.addResponder(moveLeft);
		buttonGroup.addResponder(moveRight);
		buttonGroup.addResponder(moveDown);
		buttonGroup.addResponder(baseAttack);
		buttonGroup.addResponder(skill1);
		buttonGroup.addResponder(skill2);
		buttonGroup.addResponder(skill3);
		buttonGroup.addResponder(skill4);
		buttonGroup.addResponder(healthPotion);
		buttonGroup.addResponder(attackPotion);
		buttonGroup.addResponder(back);
		getView().addResponder(buttonGroup);

		Runnable labelUpdate = () ->
		{

				moveUp.setText(LocalizedString("control_settings_up") + " (" +
				               keycodeDescriptions[settingsState.getMoveUpKey() - 32] + ")");
				moveLeft.setText(LocalizedString("control_settings_left") + " (" +
				                 keycodeDescriptions[settingsState.getMoveLeftKey() - 32] + ")");
				moveRight.setText(LocalizedString("control_settings_right") + " (" +
				                  keycodeDescriptions[settingsState.getMoveRightKey() - 32] + ")");
				moveDown.setText(LocalizedString("control_settings_down") + " (" +
				                 keycodeDescriptions[settingsState.getMoveDownKey() - 32] + ")");
				baseAttack.setText(LocalizedString("control_settings_base_attack") + " (" +
				                   keycodeDescriptions[settingsState.getBaseAttackKey() - 32] + ")");
				skill1.setText(LocalizedString("control_settings_skill_1") + " (" +
				               keycodeDescriptions[settingsState.getSkill1Key() - 32] + ")");
				skill2.setText(LocalizedString("control_settings_skill_2") + " (" +
				               keycodeDescriptions[settingsState.getSkill2Key() - 32] + ")");
				skill3.setText(LocalizedString("control_settings_skill_3") + " (" +
				               keycodeDescriptions[settingsState.getSkill3Key() - 32] + ")");
				skill4.setText(LocalizedString("control_settings_skill_4") + " (" +
				               keycodeDescriptions[settingsState.getSkill4Key() - 32] + ")");
				healthPotion.setText(LocalizedString("control_settings_health_potion") + " (" +
				                     keycodeDescriptions[settingsState.getHealthPotionKey() - 32] + ")");
				attackPotion.setText(LocalizedString("control_settings_attack_potion") + " (" +
				                     keycodeDescriptions[settingsState.getAttackPotionKey() - 32] + ")");

		};
		labelUpdate.run();

		moveUp.setActionHandler(new ControlSettingsChange(
				getNavigationController(),
				(int newKey) -> {
					settingsState.setMoveUpKey(newKey);
					labelUpdate.run();
				}));
		moveLeft.setActionHandler(new ControlSettingsChange(
				getNavigationController(),
				(int newKey) -> {
					settingsState.setMoveLeftKey(newKey);
					labelUpdate.run();
				}));
		moveRight.setActionHandler(new ControlSettingsChange(
				getNavigationController(),
				(int newKey) -> {
					settingsState.setMoveRightKey(newKey);
					labelUpdate.run();
				}));
		moveDown.setActionHandler(new ControlSettingsChange(
				getNavigationController(),
				(int newKey) -> {
					settingsState.setMoveDownKey(newKey);
					labelUpdate.run();
				}));

		baseAttack.setActionHandler(new ControlSettingsChange(
				getNavigationController(),
				(int newKey) -> {
					settingsState.setBaseAttackKey(newKey);
					labelUpdate.run();
				}));
		skill1.setActionHandler(new ControlSettingsChange(
				getNavigationController(),
				(int newKey) -> {
					settingsState.setSkill1Key(newKey);
					labelUpdate.run();
				}));
		skill2.setActionHandler(new ControlSettingsChange(
				getNavigationController(),
				(int newKey) -> {
					settingsState.setSkill2Key(newKey);
					labelUpdate.run();
				}));
		skill3.setActionHandler(new ControlSettingsChange(
				getNavigationController(),
				(int newKey) -> {
					settingsState.setSkill3Key(newKey);
					labelUpdate.run();
				}));
		skill4.setActionHandler(new ControlSettingsChange(
				getNavigationController(),
				(int newKey) -> {
					settingsState.setSkill4Key(newKey);
					labelUpdate.run();
				}));

		healthPotion.setActionHandler(new ControlSettingsChange(
				getNavigationController(),
				(int newKey) -> {
					settingsState.setHealthPotionKey(newKey);
					labelUpdate.run();
				}));
		attackPotion.setActionHandler(new ControlSettingsChange(
				getNavigationController(),
				(int newKey) -> {
					settingsState.setAttackPotionKey(newKey);
					labelUpdate.run();
				}));

		VerticalFlowLayout layout = new VerticalFlowLayout();
		layout.setSpacing(1);
		layout.setHorizontalAlignment(VerticalFlowLayout.CENTER);
		layout.setLayoutInsets(3, 5, 0, 0);
		getView().setLayoutManager(layout);
	}
}
