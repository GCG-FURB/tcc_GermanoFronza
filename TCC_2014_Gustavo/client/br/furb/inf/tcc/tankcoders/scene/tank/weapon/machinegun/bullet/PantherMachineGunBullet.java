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
package br.furb.inf.tcc.tankcoders.scene.tank.weapon.machinegun.bullet;

import br.furb.inf.tcc.tankcoders.scene.tank.ITank;
import br.furb.inf.tcc.tankcoders.scene.tank.weapon.Bullet;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jmex.physics.PhysicsSpace;

/**
 * Represents a bullet of the Panther main gun.
 * @author Germano Fronza
 */
public class PantherMachineGunBullet extends Bullet {

	private static final long serialVersionUID = 1L;
	
	public PantherMachineGunBullet(ITank owner, PhysicsSpace pSpace, Vector3f relativePosition) {
		super(owner, owner.getMainNode(), pSpace, relativePosition);
	}

	protected float getBulletMass() {
		return .3f;
	}

	protected float getBulletScale() {
		return 0.08f;
	}

	public float getMultForce() {
		return 12000;
	}

	public ColorRGBA getColor() {
		return ColorRGBA.yellow;
	}
	
	/** This value is related with ITank.INITIAL_HEALTH */
	public int getPower() {
		return 4;
	}
}
