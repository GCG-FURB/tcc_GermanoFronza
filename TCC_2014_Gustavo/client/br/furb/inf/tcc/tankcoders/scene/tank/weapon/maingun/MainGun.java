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
package br.furb.inf.tcc.tankcoders.scene.tank.weapon.maingun;

import br.furb.inf.tcc.util.scene.ModelUtils;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingVolume;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.RotationalJointAxis;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.material.Material;

/**
 * Represents a Main Gun.
 * @author Germano Fronza
 */
public abstract class MainGun extends Node {

	private static final long serialVersionUID = 1L;
	private static final Vector3f ROTATIONAL_AXIS = new Vector3f(0, 1, 0);

	private DynamicPhysicsNode mainGunDyn;
	private StaticPhysicsNode mainGunStatic;
	private RotationalJointAxis rotationalAxis;
	
	public MainGun(final Node mainNode, final Vector3f relativePosition) {
		super("mainGunNode");
		if (mainNode instanceof DynamicPhysicsNode) {
			createDynamicMainGun((DynamicPhysicsNode)mainNode, relativePosition);
		}
		else {
			createStaticMainGun((StaticPhysicsNode)mainNode, relativePosition);
		}
	}

	private void createDynamicMainGun(final DynamicPhysicsNode chassi, final Vector3f relativePosition) {
		mainGunDyn = chassi.getSpace().createDynamicNode();
		mainGunDyn.setName("mainGunDyn");
		mainGunDyn.setLocalTranslation(chassi.getLocalTranslation().add(relativePosition));
		mainGunDyn.attachChild(ModelUtils.getNodeByObj(getModelFilePath()));
		mainGunDyn.createSphere("mainGunSphere");
		mainGunDyn.generatePhysicsGeometry();

		mainGunDyn.setMaterial(Material.WOOD);
		mainGunDyn.setMass(getMainGunMass());

		mainGunDyn.setModelBound(new BoundingBox());
		mainGunDyn.updateModelBound();
		
		Joint mainGunBox = chassi.getSpace().createJoint();
		mainGunBox.attach(chassi, mainGunDyn);
		mainGunBox.setAnchor(mainGunDyn.getLocalTranslation().subtract(chassi.getLocalTranslation()));

		rotationalAxis = mainGunBox.createRotationalAxis();
		rotationalAxis.setDirection(ROTATIONAL_AXIS);
		rotationalAxis.setAvailableAcceleration(getTurnAcceleration());
		
		resetMainGunPosition();
		
		this.attachChild(mainGunDyn);
	}
	
	private void createStaticMainGun(final StaticPhysicsNode staticChassi, final Vector3f relativePosition) {
		mainGunStatic = staticChassi.getSpace().createStaticNode();
		mainGunStatic.setName("mainGunStatic");
		mainGunStatic.setLocalTranslation(staticChassi.getLocalTranslation().add(relativePosition));
		mainGunStatic.attachChild(ModelUtils.getNodeByObj(getModelFilePath()));
		mainGunStatic.createSphere("mainGunSphere");
		mainGunStatic.generatePhysicsGeometry();

		mainGunStatic.setMaterial(Material.WOOD);
		mainGunStatic.setModelBound(new BoundingBox());
		mainGunStatic.updateModelBound();
		
		this.attachChild(mainGunStatic);
	}
	
	public void turnMainGun(int direction) {
		rotationalAxis.setDesiredVelocity(direction*getTurnSpeed());
		rotationalAxis.setPositionMaximum(getMaxTurn());
		rotationalAxis.setPositionMinimum(-getMaxTurn());
	}
	
	public void stopTurningMainGun() {
		rotationalAxis.setDesiredVelocity(0);
	}

	public void resetMainGunPosition() {
		rotationalAxis.setDesiredVelocity(0);
		rotationalAxis.setPositionMaximum(0);
		rotationalAxis.setPositionMinimum(0);
	}
	
	public float getCurrentHeading() {
		Quaternion q = getMainNode().getLocalRotation();
		
		// set rotation at Y axis.
		q.toAngleAxis(new Vector3f(0, 1, 0));
		
		// get the angle based on Y axis (position one of the returned float array) and convert to Degrees.
		float currentAngle = (q.toAngles((float[])null)[1]/FastMath.DEG_TO_RAD);
		
		// I don't work with negative angles...
		if(currentAngle<0){
			currentAngle+=360;
		}
		
		// invert angle and convert to radians.
		return (360-currentAngle) * FastMath.DEG_TO_RAD;
	}
	
	public float getCurrentZRotAngle() {
		Quaternion q = getMainNode().getLocalRotation();
		
		// set rotation at Y axis.
		q.toAngleAxis(new Vector3f(0, 0, 1));
		
		// get the angle based on Y axis (position two of the returned float array) and convert to Degrees.
		float currentAngle = (q.toAngles((float[])null)[2]/FastMath.DEG_TO_RAD);
		
		// I don't work with negative angles...
		if(currentAngle<0){
			currentAngle+=360;
		}
		
		// invert angle and convert to radians.
		return (360-currentAngle) * FastMath.DEG_TO_RAD;
	}
	
	public boolean intersectsWith(BoundingVolume bv) {
		return (getMainNode().getWorldBound().intersects(bv));
	}
	
	public Node getMainNode() {
		if (mainGunDyn != null) {
			return mainGunDyn;
		}
		else {
			return mainGunStatic;
		}
	}
	
	protected abstract float getTurnAcceleration();

	protected abstract float getTurnSpeed();
	
	protected abstract float getMaxTurn();
	
	protected abstract float getMainGunMass();
	
	protected abstract String getModelFilePath();
}
