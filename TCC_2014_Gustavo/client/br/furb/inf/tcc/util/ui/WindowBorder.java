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

import org.fenggui.border.PixmapBorder;
import org.fenggui.render.Binding;
import org.fenggui.render.ITexture;
import org.fenggui.render.Pixmap;

/**
 * Window border to use in FengGUI components.
 * @author Germano Fronza
 */
public class WindowBorder {

	public static PixmapBorder getBorder() {
		ITexture thinBorderTex = null;
		try {
			thinBorderTex = Binding.getInstance().getTexture(WindowBorder.class.getClassLoader().getResource("data/texture/window/border.png"));
		} catch (IOException e) {
			// this exception never will be thrown
			e.printStackTrace();
		}
    	
        Pixmap upperLeftCorner = new Pixmap(thinBorderTex, 0, 0, 17, 17);
        Pixmap upperRightCorner = new Pixmap(thinBorderTex, 25, 0, 17, 17);
        Pixmap lowerLeftCorner = new Pixmap(thinBorderTex, 0, 18, 17, 17);
        Pixmap lowerRightCorner = new Pixmap(thinBorderTex, 25, 18, 17, 17);
        	
        Pixmap leftEdge = new Pixmap(thinBorderTex, 0, 17, 17, 1);
        Pixmap rightEdge = new Pixmap(thinBorderTex, 25, 17, 17, 1);
        Pixmap topEdge = new Pixmap(thinBorderTex, 18, 0, 2, 17);
        Pixmap bottomEdge = new Pixmap(thinBorderTex, 17, 18, 1, 17);
        	
        return new PixmapBorder(
            leftEdge,
            rightEdge,
            topEdge,
            bottomEdge,
            upperLeftCorner,
            upperRightCorner,
            lowerLeftCorner,
            lowerRightCorner);
	}
	
}
