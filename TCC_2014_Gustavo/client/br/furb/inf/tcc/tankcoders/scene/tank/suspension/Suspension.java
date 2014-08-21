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
package br.furb.inf.tcc.tankcoders.scene.tank.suspension;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.TranslationalJointAxis;
import com.jmex.physics.material.Material;

/**
 * Represents a invisible suspension to control all the four wheels
 * @author Germano Fronza
 */
public abstract class Suspension extends Node {

	private static final long serialVersionUID = 1L;
	private static final Vector3f DAMPER_AXIS = new Vector3f(0,1,0);
	private Wheel leftWheel, rightWheel;
	private DynamicPhysicsNode leftBase, rightBase;
	
	public Suspension(final PhysicsSpace pSpace, final DynamicPhysicsNode chassi, final Vector3f position, boolean canTurn) {
		super("suspensao");
		
		// creating two support bases of the suspension.
		leftBase = makeBase(pSpace, chassi, position.add(getBaseRelativePosition()));
		rightBase = makeBase(pSpace, chassi, position.subtract(getBaseRelativePosition()));
		
		// creating two wheels, one per support base.
		leftWheel = makeWheel(leftBase, getTyreRelativePosition(), canTurn);
		this.attachChild(leftWheel);
		
		rightWheel = makeWheel(rightBase, getTyreRelativePosition().negate(), canTurn);
		this.attachChild(rightWheel);
	}
	
	private DynamicPhysicsNode makeBase(final PhysicsSpace pSpace, final DynamicPhysicsNode chassi, final Vector3f relativePosition) {
		DynamicPhysicsNode suspensionBase = pSpace.createDynamicNode();
		suspensionBase.setName("suspensionBase");
		suspensionBase.setLocalTranslation(chassi.getLocalTranslation().add(relativePosition));
		suspensionBase.createBox("baseBox");
		suspensionBase.setLocalScale(0.1f);
		suspensionBase.setMass(getBaseMass());
		suspensionBase.setMaterial(Material.GHOST); // all the bases are invisible
		this.attachChild(suspensionBase);
		
		Joint suspensionJoint = pSpace.createJoint();
		suspensionJoint.attach(suspensionBase, chassi);
		
		// axis for damper of the suspension
		TranslationalJointAxis damper = suspensionJoint.createTranslationalAxis();
		damper.setPositionMaximum(getDamperMaxDisplacement());
		damper.setPositionMinimum(-getDamperMaxDisplacement());
		damper.setAvailableAcceleration(getDamperAcceleration());
		damper.setDesiredVelocity(getDamperSpeed());
		damper.setDirection(DAMPER_AXIS);
		
		return suspensionBase;
	}

	public void accelerate(final int direction) {		
		leftWheel.accelerate(direction);
		rightWheel.accelerate(direction);
	}

	public void dropAccelerator() {
		leftWheel.dropAccelerator();
		rightWheel.dropAccelerator();
	}

	public void turnWheel(final int direction) {
		leftWheel.turnWheel(direction);
		rightWheel.turnWheel(direction);
	}

	public void stopTurningWheel() {
		leftWheel.stopTurningWheel();
		rightWheel.stopTurningWheel();
	}
	
	public void showWheelsSphere() {
		leftWheel.showWheelSphere();
		rightWheel.showWheelSphere();
	}
	
	public void hideWheelsSphere() {
		leftWheel.hideWheelSphere();
		rightWheel.hideWheelSphere();
	}
	
	protected abstract Wheel makeWheel(final DynamicPhysicsNode suspensionBase, final Vector3f relativePosition, boolean canTurn);
	
	protected abstract Vector3f getTyreRelativePosition();
	
	protected abstract float getDamperSpeed();
	
	protected abstract float getDamperAcceleration();
	
	protected abstract float getDamperMaxDisplacement();
	
	protected abstract float getBaseMass();
	
	protected abstract Vector3f getBaseRelativePosition();
}
