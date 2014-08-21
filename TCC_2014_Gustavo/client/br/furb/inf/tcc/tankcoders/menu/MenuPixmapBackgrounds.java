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

import org.fenggui.background.PixmapBackground;

import br.furb.inf.tcc.util.ui.FengGuiUtils;

/**
 * Utility class to load all the pixmap backgrounds used in the game menu.
 * @author Germano Fronza
 */
public class MenuPixmapBackgrounds {

	private static PixmapBackground mainMenuPixmapBackground;
	private static PixmapBackground joinGamePixmapBackground;
	private static PixmapBackground prepareBattlePixmapBackground;
	
	public static void loadImages() {
		mainMenuPixmapBackground = FengGuiUtils.getPixmapBackground("data/images/pages/mainpage.png");
		joinGamePixmapBackground = FengGuiUtils.getPixmapBackground("data/images/pages/joingamepage.png");
		prepareBattlePixmapBackground = FengGuiUtils.getPixmapBackground("data/images/pages/preparebattlepage2.png");
	}

	public static PixmapBackground getPrepareBattlePixmapBackground() {
		return prepareBattlePixmapBackground;
	}

	public static PixmapBackground getMainMenuPixmapBackground() {
		return mainMenuPixmapBackground;
	}

	public static PixmapBackground getJoinGamePixmapBackground() {
		return joinGamePixmapBackground;
	}
}
