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

import br.furb.inf.tcc.tankcoders.scene.tank.ITank;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.InputActionInterface;


/**
 * @author Germano Fronza
 */
public class ActionAccelerator implements InputActionInterface {

	ITank tank;
	String tankName;
	int direction;
	
	public ActionAccelerator(final ITank tank, final int direcao) {
		this.tank = tank;
		this.tankName = tank.getTankName();
		this.direction = direcao;
	}
	
	public void performAction(final InputActionEvent e) {
		if (e.getTriggerPressed()) {
            tank.accelerate(direction);
        }
        else {
            tank.dropAccelerator();
        }
	}

}
