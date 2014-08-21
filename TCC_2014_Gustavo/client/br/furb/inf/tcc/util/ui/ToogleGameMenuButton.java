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
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.render.Binding;
import org.fenggui.render.Pixmap;
import org.fenggui.switches.SetPixmapSwitch;

/**
 * Toogle game menu button.
 * @author Germano Fronza
 */
public class ToogleGameMenuButton extends Button {
	
	String s = null;
	Pixmap lowlightPix;
	Pixmap highlightPix;
	Pixmap pressedPix;
	boolean pressed;
	
	public ToogleGameMenuButton(String lowlightFile, String highlightFile, String focusedFile) {
		getAppearance().removeAll();
		try	{
			lowlightPix = new Pixmap(Binding.getInstance().getTexture(lowlightFile));
			highlightPix = new Pixmap(Binding.getInstance().getTexture(highlightFile));
			pressedPix = new Pixmap(Binding.getInstance().getTexture(focusedFile));
		
			getAppearance().add(new SetPixmapSwitch(Button.LABEL_DEFAULT, lowlightPix));
			getAppearance().add(new SetPixmapSwitch(Button.LABEL_MOUSEHOVER, highlightPix));
			getAppearance().add(new SetPixmapSwitch(Button.LABEL_FOCUSED, highlightPix));
			
			getAppearance().setEnabled(Button.LABEL_DEFAULT, true);
			
			addButtonPressedListener(
				new IButtonPressedListener() {
					public void buttonPressed(ButtonPressedEvent arg0) {
						if (pressed) {
							getAppearance().add(new SetPixmapSwitch(Button.LABEL_DEFAULT, lowlightPix));
						}
						else {
							getAppearance().add(new SetPixmapSwitch(Button.LABEL_DEFAULT, pressedPix));
						}
						
						pressed = !pressed;
						getAppearance().setEnabled(Button.LABEL_DEFAULT, true);
					}
				}
			);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String toString() {
		return s;
	}

	public boolean isPressed() {
		return pressed;
	}
}