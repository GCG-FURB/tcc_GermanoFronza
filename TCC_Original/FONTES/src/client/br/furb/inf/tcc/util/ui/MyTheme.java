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

import org.fenggui.background.PlainBackground;
import org.fenggui.border.PlainBorder;
import org.fenggui.composites.Window;
import org.fenggui.theme.DefaultTheme;
import org.fenggui.util.Color;

/**
 * Custom implementation of FengGUI theme.
 * @author Germano Fronza
 */
public class MyTheme extends DefaultTheme {

	@Override
	public void setUp(Window w) {
		super.setUp(w);
		w.getAppearance().add(new PlainBackground(Color.DARK_GRAY));
		w.getAppearance().add(new PlainBorder(Color.BLACK, 0));
		w.getTitleBar().getAppearance().add(new PlainBackground(new Color(110, 110, 110)));
	}	
}
