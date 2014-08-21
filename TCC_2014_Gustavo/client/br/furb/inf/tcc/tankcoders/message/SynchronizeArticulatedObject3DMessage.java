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
package br.furb.inf.tcc.tankcoders.message;

import com.captiveimagination.jgn.synchronization.message.Synchronize3DMessage;

/**
 * Custom implementation of Synchronize3DMessage.
 * @author Germano Fronza
 */
public class SynchronizeArticulatedObject3DMessage extends Synchronize3DMessage {
	
	private float mainGunRotationX;
	private float mainGunRotationY;
	private float mainGunRotationZ;
	private float mainGunRotationW;
	
	public float getMainGunRotationX() {
		return mainGunRotationX;
	}
	public void setMainGunRotationX(float mainGunRotationX) {
		this.mainGunRotationX = mainGunRotationX;
	}
	public float getMainGunRotationY() {
		return mainGunRotationY;
	}
	public void setMainGunRotationY(float mainGunRotationY) {
		this.mainGunRotationY = mainGunRotationY;
	}
	public float getMainGunRotationZ() {
		return mainGunRotationZ;
	}
	public void setMainGunRotationZ(float mainGunRotationZ) {
		this.mainGunRotationZ = mainGunRotationZ;
	}
	public float getMainGunRotationW() {
		return mainGunRotationW;
	}
	public void setMainGunRotationW(float mainGunRotationW) {
		this.mainGunRotationW = mainGunRotationW;
	}
}
