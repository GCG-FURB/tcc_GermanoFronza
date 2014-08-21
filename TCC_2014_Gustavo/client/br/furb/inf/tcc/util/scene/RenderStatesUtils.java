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
package br.furb.inf.tcc.util.scene;

import com.jme.scene.state.AlphaState;
import com.jme.system.DisplaySystem;

/**
 * This class contains some base methods for the Rendering operation.
 * @author Erick B Passos (Adapted by Germano Fronza)
 * @version 1.0.0.0 
 */
public class RenderStatesUtils {
	
	/**
	 * Creates an alpha state.
	 * @return AlphaState generated.
	 */
	public static AlphaState createTransparency() {
		AlphaState as = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
		as.setBlendEnabled(true);
        as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        as.setTestEnabled(false);
        as.setEnabled(true);
        return as;
	}
	
}
