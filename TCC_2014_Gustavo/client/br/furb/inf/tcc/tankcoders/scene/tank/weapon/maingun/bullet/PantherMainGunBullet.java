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
package br.furb.inf.tcc.tankcoders.scene.tank.weapon.maingun.bullet;

import br.furb.inf.tcc.tankcoders.scene.tank.ITank;
import br.furb.inf.tcc.tankcoders.scene.tank.weapon.Bullet;

import com.jme.math.Vector3f;
import com.jmex.physics.PhysicsSpace;

/**
 * Represents a bullet of the Panther main gun.
 * @author Germano Fronza
 */
public class PantherMainGunBullet extends Bullet {

	private static final long serialVersionUID = 1L;
	
	public PantherMainGunBullet(ITank owner, PhysicsSpace pSpace, Vector3f relativePosition) {
		super(owner, owner.getMainNode(), pSpace, relativePosition);
	}

	protected float getBulletMass() {
		return 1;
	}

	protected float getBulletScale() {
		return 0.3f;
	}

	public float getMultForce() {
		return 40000;
	}

	/** This value is related with ITank.INITIAL_HEALTH */
	public int getPower() {
		return 20;
	}
}
