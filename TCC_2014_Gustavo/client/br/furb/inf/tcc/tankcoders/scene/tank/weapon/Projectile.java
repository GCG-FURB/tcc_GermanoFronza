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
package br.furb.inf.tcc.tankcoders.scene.tank.weapon;

import br.furb.inf.tcc.tankcoders.scene.tank.ITank;

import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsNode;

/**
 * Define all the basic operations of projectile objects.
 * @author Germano Fronza
 */
public interface Projectile {

	/** Owner of the projectile */
	public ITank getOwner();
	
	/** Vector of direction (based on current owner heading) */
	public Vector3f getVector();
	
	/** Mult force over getVector() */
	public float getMultForce();
	
	/** Current projectile location */
	public Vector3f getLocation();
	
	/** Power of damage. This value is related with ITank.INITIAL_HEALTH */
	public int getPower();
	
	/** Remove from scene node */
	public void removeFromScene();
	
	/** Gets dynamic node from the projectile */
	public DynamicPhysicsNode getBulletDyn();
	
	/** Gets world bound from the sphere node */
	public BoundingVolume getSphereWorldBound();
	
	/** Called when a projectile hits another projectile */
	public void hitByProjectile(Projectile projectile);
	
	public void incFrameCount();
	
	public boolean isAlive();
	
	public boolean isCanDamage();
}
