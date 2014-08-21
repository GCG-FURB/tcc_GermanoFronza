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

import br.furb.inf.tcc.tankcoders.scene.tank.suspension.Wheel;

import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsNode;

/**
 * Wheels of the M1-Abrams tank
 * @author Germano Fronza
 */
public class M1AbramsWheel extends Wheel {

	private static final long serialVersionUID = 1L;
	private static final float WHEEL_MASS = 3f;
	private static final float WHEEL_SCALE = 0.8f;
	private static final float TRACTION_SPEED = 20;
	private static final float TURN_SPEED = 75;
	private static final float TRACTION_ACCELERATION = 230;
	private static final float TURN_ACCELERATION = 500;
	private static final float MAX_TURN = 0.2f;
	
	public M1AbramsWheel(DynamicPhysicsNode suspensionBase, Vector3f relativePosition, boolean canTurn) {
		super(suspensionBase, relativePosition, canTurn);
	}
	
	protected float getWheelMass() {
		return WHEEL_MASS;
	}
	
	protected float getWheelScale() {
		return WHEEL_SCALE;
	}
	
	protected float getTractionSpeed() {
		return TRACTION_SPEED;
	}
	
	protected float getTurnSpeed() {
		return TURN_SPEED;
	}
	
	protected float getTractionAcceleration() {
		return TRACTION_ACCELERATION;
	}
	
	public float getTurnAcceleration() {
		return TURN_ACCELERATION;
	}
	
	public float getMaxTurn() {
		return MAX_TURN;
	}
}
