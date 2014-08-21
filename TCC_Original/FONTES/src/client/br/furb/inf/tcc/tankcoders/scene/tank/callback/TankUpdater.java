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
package br.furb.inf.tcc.tankcoders.scene.tank.callback;

import br.furb.inf.tcc.tankcoders.scene.tank.ITank;

import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsUpdateCallback;

/**
 * Tank physics updater.
 * @author Germano Fronza
 */
public class TankUpdater implements PhysicsUpdateCallback {
	
	/**
	 * Camera target to update.
	 */
	private ITank tank;
	
	/**
	 * @param tank
	 */
	public TankUpdater(ITank tank) {
		this.tank = tank;
		this.tank.setTankUpdater(this);
	}
	
	/**
	 * Nothing to do after physics simulation.
	 */	
	public void afterStep(PhysicsSpace pSpace, float tpf) {
	}


	/**
	 * Update camera target. 
	 */
	public void beforeStep(PhysicsSpace pSpace, float tpf) {
		tank.update();
	}

}
