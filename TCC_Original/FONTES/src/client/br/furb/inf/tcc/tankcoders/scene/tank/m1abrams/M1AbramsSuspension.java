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
package br.furb.inf.tcc.tankcoders.scene.tank.m1abrams;

import br.furb.inf.tcc.tankcoders.scene.tank.suspension.Suspension;
import br.furb.inf.tcc.tankcoders.scene.tank.suspension.Wheel;

import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;

/**
 * M1 Abrams suspension.
 * @author Germano Fronza
 */
public class M1AbramsSuspension extends Suspension {

	private static final long serialVersionUID = 1L;
	private static final Vector3f TYRE_RELATIVE_POSITION = new Vector3f(0,0,2.3f);
	private static final float DAMPER_SPEED = -15;
	private static final float DAMPER_ACCELERATION = 750;
	private static final float DAMPER_MAX_DISPLACEMENT = 0.0f;
	private static final float BASE_MASS = 7.5f;
	private static final Vector3f BASE_RELATIVE_POSITION = new Vector3f(0,0,5.8f);
	
	public M1AbramsSuspension(PhysicsSpace space, DynamicPhysicsNode chassi, Vector3f position, boolean canTurn) {
		super(space, chassi, position, canTurn);
	}
	
	protected Vector3f getTyreRelativePosition() {
		return TYRE_RELATIVE_POSITION;
	}
	
	protected float getDamperSpeed() {
		return DAMPER_SPEED;
	}
	
	protected float getDamperAcceleration() {
		return DAMPER_ACCELERATION;
	}
	
	protected float getDamperMaxDisplacement() {
		return DAMPER_MAX_DISPLACEMENT;
	}
	
	protected float getBaseMass() {
		return BASE_MASS;
	}
	
	protected Vector3f getBaseRelativePosition() {
		return BASE_RELATIVE_POSITION;
	}

	// factory method
	protected Wheel makeWheel(DynamicPhysicsNode suspensionBase, Vector3f relativePosition, boolean canTurn) {
		return new M1AbramsWheel(suspensionBase, relativePosition, canTurn);
	}		
}
