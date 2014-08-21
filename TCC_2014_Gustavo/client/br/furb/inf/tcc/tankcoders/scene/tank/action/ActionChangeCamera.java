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

import java.util.logging.Logger;

import br.furb.inf.tcc.tankcoders.TankCoders;
import br.furb.inf.tcc.tankcoders.scene.camera.CustomChaseCamera;
import br.furb.inf.tcc.tankcoders.scene.tank.ITank;
import br.furb.inf.tcc.tankcoders.scene.tank.m1abrams.M1AbramsTank;
import br.furb.inf.tcc.util.lang.GameLanguage;

import com.jme.input.InputHandler;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.InputActionInterface;

/**
 * Listener for change camera action.
 * @author Germano Fronza
 */
public class ActionChangeCamera implements InputActionInterface {

	private InputHandler input;
	private ITank tank;
	
	public ActionChangeCamera(ITank tank, InputHandler input) {
		this.tank = tank;
		this.input = input;
	}
	
	public void performAction(InputActionEvent event) {
		Logger logger = TankCoders.getLogger(); 
		if (tank instanceof M1AbramsTank) {
			if (event.getTriggerPressed()) {				
				if (input instanceof CustomChaseCamera) {
					CustomChaseCamera chase = ((CustomChaseCamera)input);
					
					if (chase.getTarget() == tank.getChassi()) {
						chase.setTarget(((M1AbramsTank)tank).getMainGun().getMainNode());
						
						if (TankCoders.isDebug()) logger.info(GameLanguage.getString("camera.atMainGun"));
					}
					else {
						chase.setTarget(tank.getChassi());
						
						if (TankCoders.isDebug()) logger.info(GameLanguage.getString("camera.atChassi"));
					}
				}
			}
		}
		
		if (TankCoders.isDebug()) {
			logger.info(GameLanguage.getString("tank.localTranslaction") + " " + tank.getChassi().getLocalTranslation());
		}
	}

}
