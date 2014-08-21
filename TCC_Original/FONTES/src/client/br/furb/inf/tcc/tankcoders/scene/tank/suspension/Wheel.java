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

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Sphere;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.RotationalJointAxis;
import com.jmex.physics.material.Material;

/**
 * Represents an invisible wheel to move the tank along the terrain.
 * @author Germano Fronza
 */
public abstract class Wheel extends Node {

	private static final long serialVersionUID = 1L;
	private static final Vector3f TRACTION_AXIS = new Vector3f(0, 0, 1);
	private static final Vector3f DIRECTION_AXIS = new Vector3f(0, 1, 0);

	private DynamicPhysicsNode tyre;
	private RotationalJointAxis tractionAxis, directionAxis;

	public Wheel(final DynamicPhysicsNode suspensionBase, final Vector3f relativePosition, boolean canTurn) {
		super("wheelNode");
		tyre = suspensionBase.getSpace().createDynamicNode();
		tyre.setName("wheel");
		tyre.setLocalTranslation(suspensionBase.getLocalTranslation().add(relativePosition));
		tyre.createSphere("tyre");
		tyre.setLocalScale(getWheelScale());
		tyre.generatePhysicsGeometry();

		tyre.setMaterial(Material.RUBBER);
		tyre.setMass(getWheelMass());

		Joint wheelBox = suspensionBase.getSpace().createJoint();
		wheelBox.attach(suspensionBase, tyre);
		wheelBox.setAnchor(tyre.getLocalTranslation().subtract(suspensionBase.getLocalTranslation()));

		if (canTurn) {
			directionAxis = wheelBox.createRotationalAxis();
			directionAxis.setDirection(DIRECTION_AXIS);
			directionAxis.setAvailableAcceleration(getTurnAcceleration());
		}
		
		tractionAxis = wheelBox.createRotationalAxis();
		tractionAxis.setDirection(TRACTION_AXIS);
		
		if (canTurn) {
			tractionAxis.setRelativeToSecondObject(true);
			stopTurningWheel();
		}
		
		this.attachChild(tyre);
	}

	public void accelerate(final int direction) {
		tractionAxis.setAvailableAcceleration(getTractionAcceleration());
		tractionAxis.setDesiredVelocity(direction*getTractionSpeed());
	}

	public void dropAccelerator() {
		tractionAxis.setAvailableAcceleration(400);
		tractionAxis.setDesiredVelocity(0);
	}
	
	public void turnWheel(int direction) {
		directionAxis.setDesiredVelocity(direction*getTurnSpeed());
		directionAxis.setPositionMaximum(getMaxTurn());
		directionAxis.setPositionMinimum(-getMaxTurn());
	}
	
	public void stopTurningWheel() {
		directionAxis.setDesiredVelocity(0);
		directionAxis.setPositionMaximum(0);
		directionAxis.setPositionMinimum(0);
	}
	
	public void showWheelSphere() {
		Sphere tyreSphere = new Sphere("tyreSphere", 10, 10, 1);
		tyreSphere.setModelBound(new BoundingBox());
		tyreSphere.updateModelBound();
		tyreSphere.setSolidColor(ColorRGBA.brown);
	
		tyre.attachChild(tyreSphere);
	}
	
	public void hideWheelSphere() {
		tyre.detachChildNamed("tyreSphere");
	}
	
	protected abstract float getWheelMass();
	
	protected abstract float getWheelScale();
	
	protected abstract float getTractionSpeed();
	
	protected abstract float getTurnSpeed();
	
	protected abstract float getTractionAcceleration();
	
	protected abstract float getTurnAcceleration();
	
	protected abstract float getMaxTurn();
}
