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
package br.furb.inf.tcc.tankcoders.scene.tank.action;

import java.util.Map;

import br.furb.inf.tcc.tankcoders.scene.tank.ITank;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.InputActionInterface;
import com.jme.scene.Node;
import com.jme.scene.state.WireframeState;
import com.jme.system.DisplaySystem;

/**
 * Listener for change camera target.
 * @author Germano Fronza
 */
public class ActionChangeRenderState implements InputActionInterface {

	private Map<String, ITank> tanks;	
	private boolean wireframed = false;
	private WireframeState wfs = null;
	
	public ActionChangeRenderState(Map<String, ITank> tanks) {
		this.tanks = tanks;
	}
	
	/**
	 * Change the chase camera target.
	 */
	public void performAction(InputActionEvent event) {
		if (event.getTriggerPressed()) {			
			for (ITank tank : tanks.values()) {
				if (!wireframed) {
					if (wfs == null) {
						wfs = DisplaySystem.getDisplaySystem().getRenderer().createWireframeState();
						wfs.setEnabled(true);
						wireframed = true;
					}
					
					tank.showWheelsSphere();
					
					((Node)tank).setRenderState(wfs);
					((Node)tank).updateRenderState();
				}
				else {
					tank.hideWheelsSphere();
					
					wfs.setEnabled(false);
					wfs = null;
					wireframed = false;
				}
			}
		}
	}

}
