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
package br.furb.inf.tcc.util.ui;

import java.io.IOException;

import org.fenggui.background.PixmapBackground;
import org.fenggui.render.Binding;
import org.fenggui.render.ITexture;
import org.fenggui.render.Pixmap;

import br.furb.inf.tcc.util.exception.DevtimeException;

/**
 * Utils for FengGUI.
 * @author Germano Fronza
 */
public class FengGuiUtils {

	/**
	 * Gets a image resource in project.
	 * @param name
	 * @return String resource filepath.
	 */
	public static String getRsrc(String name) {
		try {
			String urlStr = FengGuiUtils.class.getClassLoader().getResource(name).toString();
			return urlStr.substring(6);
		}
		catch (NullPointerException e) {
			throw new DevtimeException("Resource \"" + name + "\" not found");
		}
	}
	
	/**
	 * Loads a texture and put it in a Pixmap background.
	 * @param textureFilePath
	 * @return
	 */
	public static PixmapBackground getPixmapBackground(String textureFilePath) {
		ITexture texture = null;
		try {
			texture = Binding.getInstance().getTexture(FengGuiUtils.class.getClassLoader().getResource(textureFilePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Pixmap pixmap = new Pixmap(texture);
		return new PixmapBackground(pixmap, true);
	}
	
}
