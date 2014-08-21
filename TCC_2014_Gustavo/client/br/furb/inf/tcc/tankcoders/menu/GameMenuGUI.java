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
import org.fenggui.FengGUI;
import org.fenggui.GameMenuButton;
import org.fenggui.composites.MessageWindow;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.RowLayout;
import org.fenggui.layout.StaticLayout;
import org.fenggui.util.Point;
import org.fenggui.util.Spacing;

import br.furb.inf.tcc.tankcoders.TankCoders;
import br.furb.inf.tcc.util.ui.FengGuiUtils;
import br.furb.inf.tcc.util.ui.MyTheme;

/**
 * FengGUI for the Main Menu of the game.
 * @author Germano Fronza
 */
public class GameMenuGUI
{
	/**
	 * Main Menu Buttons
	 */
	private GameMenuButton joinGame, instructions, credits, options, quit;
	
	/**
	 * Options Buttons
	 */
	private GameMenuButton sound, graphics, back, network;

	/**
	 * Builds GUI components.
	 * @param display to add the widgets.
	 */
	public void buildGUI(Display display) {
		FengGUI.setTheme(new MyTheme());
		
		// create a new container for the image background.
		final Container c = new Container();
		c.setMinSize(754, 473);
		c.setSizeToMinSize();
		c.getAppearance().add(MenuPixmapBackgrounds.getMainMenuPixmapBackground());
		c.setLayoutManager(new StaticLayout());	
		StaticLayout.center(c, display);
		
		display.addWidget(c);
		
		// create a new container for all the buttons
		final Container cButtons = new Container();
		cButtons.getAppearance().setPadding(new Spacing(0, 10));
		cButtons.setLayoutManager(new RowLayout(false));
		
		c.addWidget(cButtons);
		
		// create and initialize all the buttons
		initButtons(cButtons, c, display);

		// start building the main menu
		buildMainMenu(cButtons);
	}
	
	/**
	 * Creates all the buttons and your respective events.
	 * @param c
	 * @param display
	 */
	private void initButtons(final Container c, final Container parentContainer, final Display display)
	{
		joinGame = new GameMenuButton(FengGuiUtils.getRsrc("data/images/buttons/btn_joingame_0.png"), FengGuiUtils.getRsrc("data/images/buttons/btn_joingame_1.png"));
		instructions = new GameMenuButton(FengGuiUtils.getRsrc("data/images/buttons/btn_instructions_0.png"), FengGuiUtils.getRsrc("data/images/buttons/btn_instructions_1.png"));
		options = new GameMenuButton(FengGuiUtils.getRsrc("data/images/buttons/btn_options_0.png"), FengGuiUtils.getRsrc("data/images/buttons/btn_options_1.png"));
		credits = new GameMenuButton(FengGuiUtils.getRsrc("data/images/buttons/btn_credits_0.png"), FengGuiUtils.getRsrc("data/images/buttons/btn_credits_1.png"));
		quit = new GameMenuButton(FengGuiUtils.getRsrc("data/images/buttons/btn_quit_0.png"), FengGuiUtils.getRsrc("data/images/buttons/btn_quit_1.png"));		
		joinGame.addButtonPressedListener(new IButtonPressedListener()
		{
			public void buttonPressed(ButtonPressedEvent e)
			{
				display.removeAllWidgets();
				new JoinGameMenuGUI().buildGUI(display);
			}
		});

		credits.addButtonPressedListener(new IButtonPressedListener()
		{
			public void buttonPressed(ButtonPressedEvent e)
			{
				MessageWindow mw = new MessageWindow("We dont take credit for FengGUI :)");
				mw.pack();
				display.addWidget(mw);
				StaticLayout.center(mw, display);
			}
		});

		options.addButtonPressedListener(new IButtonPressedListener()
		{
			public void buttonPressed(ButtonPressedEvent e)
			{
				buildOptionsMenu(c, display);
			}
		});

		quit.addButtonPressedListener(new IButtonPressedListener()
		{

			public void buttonPressed(ButtonPressedEvent e)
			{
				TankCoders.getGameInstance().quitGame();
			}
		});
		
		sound = new GameMenuButton(FengGuiUtils.getRsrc("data/images/buttons/sound0.png"), FengGuiUtils.getRsrc("data/images/buttons/sound1.png"));
		graphics = new GameMenuButton(FengGuiUtils.getRsrc("data/images/buttons/graphics0.png"), FengGuiUtils.getRsrc("data/images/buttons/graphics1.png"));
		back = new GameMenuButton(FengGuiUtils.getRsrc("data/images/buttons/back0.png"), FengGuiUtils.getRsrc("data/images/buttons/back1.png"));
		network = new GameMenuButton(FengGuiUtils.getRsrc("data/images/buttons/network0.png"), FengGuiUtils.getRsrc("data/images/buttons/network1.png"));
		
		back.addButtonPressedListener(new IButtonPressedListener()
		{

			public void buttonPressed(ButtonPressedEvent e)
			{
				buildMainMenu(c);
			}
		});
	}
	
	/**
	 * Builds main menu with the buttons.
	 * @param c
	 * @param parentContainer
	 */
	public void buildMainMenu(final Container c)
	{
		c.removeAllWidgets();
		
		c.addWidget(joinGame);
		c.addWidget(instructions);
		c.addWidget(options);
		c.addWidget(credits);
		c.addWidget(quit);
		
		c.pack();
		
		// set position of the buttons container
		c.setPosition(new Point(0,25));
	}
	
	/**
	 * Builds the Options Menu with the buttons.
	 * @param c
	 * @param display
	 */
	private void buildOptionsMenu(final Container c, final Display display)
	{
		c.removeAllWidgets();
		
		c.addWidget(graphics);
		c.addWidget(sound);
		c.addWidget(network);
		c.addWidget(back);
		
		c.pack();
		StaticLayout.center(c, display);
	}
}
