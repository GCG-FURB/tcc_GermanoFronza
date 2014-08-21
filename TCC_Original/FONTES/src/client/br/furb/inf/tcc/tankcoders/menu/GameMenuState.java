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

import org.fenggui.Display;
import org.fenggui.render.lwjgl.LWJGLBinding;

import br.furb.inf.tcc.util.ui.FengJMEInputHandler;

import com.jme.input.MouseInput;
import com.jmex.game.state.GameState;

/**
 * State for MainMenu screen
 * @author Germano Fronza
 */
public class GameMenuState extends GameState {

	/**
     * FengGUI display.
     */
    private Display display = null;
 
    /**
     * FengGUI InputHandler.
     */
    private FengJMEInputHandler input = null;
    
    /**
     * The Constructor.
     */
    public GameMenuState() {
    	customInit();
    }
    
    public void removeAll() {
    	display.removeAllWidgets();
    }
    
    /**
     * Initializes the FengGUI display and 
     * creates the GUI elements.
     */
	public void customInit() {
        display = new Display(new LWJGLBinding());
        input = new FengJMEInputHandler(display);
        
        MenuPixmapBackgrounds.loadImages();
        new GameMenuGUI().buildGUI(display);
        
        display.layout();
        MouseInput.get().setCursorVisible(true);
        
    }
 
    /**
     * Reset RenderStates before rendering FengGUI,
     * especially the TextureState.
     */
    @Override
    public void render(float tpf) {
        // render the GUI
        display.display();
    }
 
    /**
     * update the Inputhandler.
     */
    @Override
    public void update(float tpf) {
        input.update(tpf);
    }
    
    @Override
    public void cleanup() {
        
    }

	public Display getDisplay() {
		return display;
	}
}
