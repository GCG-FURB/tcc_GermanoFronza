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
package br.furb.inf.tcc.tankcoders.client;

import java.util.Map;

import br.furb.inf.tcc.tankcoders.message.SynchronizeCreateTankMessage;
import br.furb.inf.tcc.tankcoders.scene.tank.ITank;

import com.captiveimagination.jgn.synchronization.SyncObjectManager;
import com.captiveimagination.jgn.synchronization.message.SynchronizeCreateMessage;
import com.captiveimagination.jgn.synchronization.message.SynchronizeRemoveMessage;

/**
 * Custom implementation of SyncObjectManager.
 * @author Germano Fronza
 */
public class SyncTankObjectManager implements SyncObjectManager {

	/**
	 * Tanks of this battle.
	 */
	private Map<String, ITank> tanks;
	
	/**
	 * Creates a new synchronization tank object manager.
	 * @param tanks
	 */
	public SyncTankObjectManager(Map<String, ITank> tanks) {
		this.tanks = tanks;
	}
	
	/**
	 * Method called by SyncronizationManager to create a remote object (a Tank).
	 * All the vehicles are already built, here I just need to return the object instance.
	 */
	public Object create(SynchronizeCreateMessage message) {
		SynchronizeCreateTankMessage createTankMsg = (SynchronizeCreateTankMessage)message;
		return tanks.get(createTankMsg.getTankName());
	}

	/**
	 * Method called by SyncronizationManager to remove a remote object (a Tank).
	 */
	public boolean remove(SynchronizeRemoveMessage message, Object object) {
		// this method never can remove an object from the scene, I implement this task by my own.
		return true;
	}
	
}
