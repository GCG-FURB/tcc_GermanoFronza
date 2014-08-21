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
package br.furb.inf.tcc.util.lang;

import java.util.Locale;
import java.util.ResourceBundle;

import br.furb.inf.tcc.util.game.GamePersistentProperties;

/**
 * Singleton class to provide i18n to TankCoders game.
 * @author Germano Fronza
 */
public class GameLanguage {

	/**
	 * Instance of the singleton.
	 */
	private static GameLanguage instance;
	
	/**
	 * Java ResourceBundle instance.
	 */
	private ResourceBundle bundle;
	
	/**
	 * Loads the default locale from language configuration file.
	 */
	private GameLanguage() {
		Locale l = new Locale(GamePersistentProperties.getInstance().getLocaleLanguage(),
							  GamePersistentProperties.getInstance().getLocaleCountry());
		bundle = ResourceBundle.getBundle("data/lang/messages", l);
	}
	
	static {
		if (instance == null) {
			instance = new GameLanguage();
		}
	}
	
	/**
	 * Gets a string of the message file properties.
	 * @param String key
	 * @return String
	 */
	public static String getString(String key) {
		String str = instance.bundle.getString(key);
		if (str == null) {
			throw new RuntimeException("message not found in resource bundle");
		}
		return str;
	}
}
