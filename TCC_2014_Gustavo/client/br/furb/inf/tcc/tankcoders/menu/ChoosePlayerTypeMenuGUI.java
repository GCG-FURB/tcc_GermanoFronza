/**
 * Copyright (C) 2008 Germano Fronza
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * To contact the author:
 *  - germano.inf@gmail.com
 */
package br.furb.inf.tcc.tankcoders.menu;

import org.fenggui.Container;
import org.fenggui.Display;
import org.fenggui.GameMenuButton;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.RowLayout;
import org.fenggui.util.Point;
import org.fenggui.util.Spacing;

import br.furb.inf.tcc.util.ui.FengGuiUtils;
import br.furb.inf.tcc.util.ui.GameMenuLabel;

/**
 * Menu Play of the game.
 * @author Germano Fronza
 */
public class ChoosePlayerTypeMenuGUI {
	
	private GameMenuLabel label;
	private GameMenuButton avatarPlayer;
	private GameMenuButton iaPlayers;
	private GameMenuButton back;
	
	public void buildGUI(JoinGameMenuGUI parentMenu, Container c, Display display) {	
		
		// create a new container for Label
		final Container cLabel = new Container();
		cLabel.getAppearance().setPadding(new Spacing(0, 10));
		cLabel.setLayoutManager(new RowLayout(false));
		
		c.addWidget(cLabel);
		
		// create a new container for Buttons of player types
		final Container cButtonsPlayerOptions = new Container();
		cButtonsPlayerOptions.getAppearance().setPadding(new Spacing(0, 10));
		cButtonsPlayerOptions.setLayoutManager(new RowLayout(false));
		
		c.addWidget(cButtonsPlayerOptions);
		
		// create a new container for Back button
		final Container cButtonBack = new Container();
		cButtonBack.getAppearance().setPadding(new Spacing(0, 10));
		cButtonBack.setLayoutManager(new RowLayout(false));
		
		c.addWidget(cButtonBack);
		
		initComponents(c);
		buildComponents(cLabel, cButtonsPlayerOptions, cButtonBack, c);
	}
	
	private void initComponents(final Container parentContainer) {
		label = new GameMenuLabel(FengGuiUtils.getRsrc("data/images/labels/lab_chooseplayertype.png"));
		
		avatarPlayer = new GameMenuButton(FengGuiUtils.getRsrc("data/images/buttons/btn_avatarplayer_0.png"), FengGuiUtils.getRsrc("data/images/buttons/btn_avatarplayer_1.png"));
		avatarPlayer.addButtonPressedListener(new IButtonPressedListener()
		{
			public void buttonPressed(ButtonPressedEvent e)
			{
//				StartGameArguments gameArgs = new StartGameArguments();
				
				// sample situation
//				TankTeam teamBope = new TankTeam(PlayerTeam.TEAM_2);
//				teamBope.setName("Bope na Roçinha");
//				AvatarPlayer p = new AvatarPlayer(TankModel.JADGE_PANTHER);
//				p.setName("Capitao Nascimento");
//				p.setTeam(teamBope);
//				p.setInitialSlotLocation(6);
//				
//				AvatarPlayer p2 = new AvatarPlayer(TankModel.JADGE_PANTHER);
//				p2.setName("Zero meia");
//				p2.setTeam(teamBope);
//				p2.setInitialSlotLocation(1);
//				
//				gameArgs.addPlayer(p);
////				gameArgs.addPlayer(p2);
//				gameArgs.setLocalPlayer(p);
//				
//				TankCoders.getGameInstance().changeToInGameState(gameArgs);
			}
		});
		
		iaPlayers = new GameMenuButton(FengGuiUtils.getRsrc("data/images/buttons/btn_iaplayer_0.png"), FengGuiUtils.getRsrc("data/images/buttons/btn_iaplayer_1.png"));
		iaPlayers.addButtonPressedListener(new IButtonPressedListener()
		{
			public void buttonPressed(ButtonPressedEvent e)
			{
				
			} 
		});
		
		back = new GameMenuButton(FengGuiUtils.getRsrc("data/images/buttons/btn_back_0.png"), FengGuiUtils.getRsrc("data/images/buttons/btn_back_1.png"));
		back.addButtonPressedListener(new IButtonPressedListener()
		{
			public void buttonPressed(ButtonPressedEvent e)
			{
//				new JoinGameMenuGUI().buildGUI(display);
			}
		});
	}
	
	private void buildComponents(Container cLabel, Container cButtonsPlayerTypes, Container cButtonBack, final Container parentContainer) {
		cLabel.removeAllWidgets();
		cLabel.addWidget(label);
		cLabel.pack();
		cLabel.setPosition(new Point(0,210));
		
		cButtonsPlayerTypes.removeAllWidgets();
		cButtonsPlayerTypes.addWidget(avatarPlayer);
		cButtonsPlayerTypes.addWidget(iaPlayers);
		cButtonsPlayerTypes.pack();
		cButtonsPlayerTypes.setPosition(new Point(35,120));
		
		cButtonBack.removeAllWidgets();
		cButtonBack.addWidget(back);
		cButtonBack.pack();
		cButtonBack.setPosition(new Point(0,25));
	}
}
