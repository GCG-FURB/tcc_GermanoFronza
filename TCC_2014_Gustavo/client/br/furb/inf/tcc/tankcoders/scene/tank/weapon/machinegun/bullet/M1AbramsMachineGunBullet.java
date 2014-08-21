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
import br.furb.inf.tcc.tankcoders.scene.tank.m1abrams.M1AbramsTank;
import br.furb.inf.tcc.tankcoders.scene.tank.weapon.Bullet;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

/**
 * Represents a bullet of the M1Abrams main gun.
 * @author Germano Fronza
 */
public class M1AbramsMachineGunBullet extends Bullet {

	private static final long serialVersionUID = 1L;
	
	public M1AbramsMachineGunBullet(ITank owner, Vector3f relativePosition) {
		super(owner, ((M1AbramsTank)owner).getMainGun().getMainNode(), ((M1AbramsTank)owner).getPhysicsSpace(), relativePosition);
	}

	protected float getBulletMass() {
		return .3f;
	}

	protected float getBulletScale() {
		return 0.1f;
	}
	
	public float getMultForce() {
		return 12000;
	}
	
	public ColorRGBA getColor() {
		return ColorRGBA.yellow;
	}
	
	public int getPower() {
		// this value is related with ITank.INITIAL_HEALTH
		return 5;
	}
}
