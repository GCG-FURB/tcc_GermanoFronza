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
package br.furb.inf.tcc.util.game;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Singleton class to handle persistent properties of the game.
 * @author Germano Fronza
 */
public class GamePersistentProperties {

	private String  renderer;
	private int displayFrequency;
	private int displayWidth;
	private int displayHeight;
	private int depth;
	private boolean fullscreen;
	private String localeLanguage;
	private String localeCountry;
	private int notifyTankInfoRate;
	
	// singleton instance.
	private static GamePersistentProperties instance;
	
	private GamePersistentProperties() {
		URL rsrc = GamePersistentProperties.class.getClassLoader().getResource("data/game-properties.cfg");
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(new File(rsrc.toURI())));
			renderer = p.getProperty("RENDERER");
			displayFrequency = Integer.parseInt(p.getProperty("FREQ"));
			displayWidth = Integer.parseInt(p.getProperty("WIDTH"));
			displayHeight = Integer.parseInt(p.getProperty("HEIGHT"));
			depth = Integer.parseInt(p.getProperty("DEPTH"));
			fullscreen = Boolean.parseBoolean(p.getProperty("FULLSCREEN"));
			localeLanguage = p.getProperty("LOCALE_LANGUAGE");
			localeCountry = p.getProperty("LOCALE_COUNTRY");
			notifyTankInfoRate = Integer.parseInt(p.getProperty("NOTIFYTANKINFO_RATE"));
		} catch (Exception e) {
			// debug stuff
			e.printStackTrace();
		}
	}
	
	public static GamePersistentProperties getInstance() {
		// lazy instantiation.
		if (instance == null) {
			instance = new GamePersistentProperties();
		}
		
		return instance;
	}

	public String getRenderer() {
		return renderer;
	}

	public int getDisplayFrequency() {
		return displayFrequency;
	}

	public int getDisplayWidth() {
		return displayWidth;
	}

	public int getDisplayHeight() {
		return displayHeight;
	}

	public int getDepth() {
		return depth;
	}

	public boolean isFullscreen() {
		return fullscreen;
	}

	public String getLocaleLanguage() {
		return localeLanguage;
	}
	
	public String getLocaleCountry() {
		return localeCountry;
	}

	/**
	 * In the config file this information is given in miliseconds, 
	 * but here I convert to nanoseconds for better timing.
	 * @return long time in nanoseconds
	 */
	public long getNotifyTankInfoRate() {
		return notifyTankInfoRate * 1000000;
	}
}
