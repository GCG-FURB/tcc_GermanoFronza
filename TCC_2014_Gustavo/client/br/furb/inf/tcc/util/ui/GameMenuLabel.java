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

import org.fenggui.Button;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.render.Binding;
import org.fenggui.render.Pixmap;
import org.fenggui.switches.SetPixmapSwitch;

import br.furb.inf.tcc.util.exception.DevtimeException;

/**
 * Custom button for design a FengGUI Label.
 * @author Germano Fronza
 */
public class GameMenuLabel extends Button {

	public GameMenuLabel(String lowlightFile)
	{
		getAppearance().removeAll();
		Pixmap lowlight = null;
		try {
			lowlight = new Pixmap(Binding.getInstance().getTexture(lowlightFile));
			getAppearance().add(new SetPixmapSwitch(Button.LABEL_DEFAULT, lowlight));
			getAppearance().setEnabled(Button.LABEL_DEFAULT, true);
		} catch (IOException e) {
			throw new DevtimeException(e);
		}
	}
	
	public void addButtonPressedListener(IButtonPressedListener arg0) {
		throw new DevtimeException("Labels not support event listeners. Please check your code");
	}
}
