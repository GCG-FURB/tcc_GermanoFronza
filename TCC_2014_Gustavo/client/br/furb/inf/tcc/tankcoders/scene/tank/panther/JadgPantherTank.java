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
package br.furb.inf.tcc.tankcoders.scene.tank.panther;

import br.furb.inf.tcc.tankcoders.game.TankTeam;
import br.furb.inf.tcc.tankcoders.message.SynchronizeCreateTankMessage;
import br.furb.inf.tcc.tankcoders.scene.tank.AbstractTank;
import br.furb.inf.tcc.tankcoders.scene.tank.ITank;
import br.furb.inf.tcc.tankcoders.scene.tank.suspension.Suspension;
import br.furb.inf.tcc.tankcoders.scene.tank.weapon.Bullet;
import br.furb.inf.tcc.tankcoders.scene.tank.weapon.machinegun.bullet.PantherMachineGunBullet;
import br.furb.inf.tcc.tankcoders.scene.tank.weapon.maingun.bullet.PantherMainGunBullet;
import br.furb.inf.tcc.util.exception.InvalidOperationException;
import br.furb.inf.tcc.util.scene.ModelUtils;

import com.captiveimagination.jgn.synchronization.message.SynchronizeCreateMessage;
import com.jme.bounding.BoundingVolume;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;

/**
 * PantherTank is an concrete implementation of the AbstractTank and 
 * an implementation of the interface ITank.
 * @author Germano Fronza
 */
public class JadgPantherTank extends AbstractTank {

	private static final long serialVersionUID = 1L;
	
	private static final String CHASSI_MODEL_PATH = "data/model/panther/chassi.obj";
	private static final String WHEELS_MODEL_PATH = "data/model/panther/wheels.obj";
	private static final float CHASSI_MASS = 240;
	private static final float AXIS_DISTANCE = 15;
	private static final float SUSPENSION_HEIGHT = -2.5f; 

	public JadgPantherTank(short playerIdOwner, String tankName, TankTeam team, final PhysicsSpace pSpace, final Vector3f position, final boolean remoteTank, final boolean agentControls) {
		super(playerIdOwner, tankName, team, pSpace, position, remoteTank, agentControls);
	}

	protected void makeDynamicChassi(final PhysicsSpace pSpace, final Vector3f position) {
		super.makeDynamicChassi(pSpace, position);
		
		Node modelWheels = ModelUtils.getNodeByObj(WHEELS_MODEL_PATH);
		chassi.attachChild(modelWheels);
	}
	
	protected void makeStaticChassi(PhysicsSpace space, Vector3f position) {
		super.makeStaticChassi(space, position);
		
		Node modelWheels = ModelUtils.getNodeByObj(WHEELS_MODEL_PATH);
		staticChassi.attachChild(modelWheels);
	}
	
	public boolean intersectsWith(BoundingVolume bv) {
		return super.intersectsWith(bv);
	}

	public void stopTurningMainGun() {
		throw new InvalidOperationException("JadgPanther not contain an independent maingun to stop.");
	}

	public void turnMainGun(int direction) {
		throw new InvalidOperationException("JadgPanther not contain an independent maingun to turn.");
	}

	public boolean hasIndependentMainGun() {
		return false;
	}

	protected float getAxisDistances() {
		return AXIS_DISTANCE;
	}

	protected float getChassiMass() {
		return CHASSI_MASS;
	}

	protected String getChassiModelPath() {
		return CHASSI_MODEL_PATH;
	}

	protected float getSuspensionHeight() {
		return SUSPENSION_HEIGHT;
	}
	
	/** Gets the current heading (in radians) of the tank chassi*/
	public float getCurrentMainGunHeading() {
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
	
	/** Gets the current angle based on Z rotation (in radians) of the tank chassi */
	public float getCurrentMainGunZRotAngle() {
		Quaternion q = getMainNode().getLocalRotation();
		
		// set rotation at Z axis.
		q.toAngleAxis(new Vector3f(0, 0, 1));
		
		// get the angle based on Z axis (position two of the returned float array) and convert to Degrees.
		float currentAngle = (q.toAngles((float[])null)[2]/FastMath.DEG_TO_RAD);
		
		// I don't work with negative angles...
		if(currentAngle<0){
			currentAngle+=360;
		}
		
		// invert angle and convert to radians.
		return (360-currentAngle) * FastMath.DEG_TO_RAD;
	}
	
	protected Suspension getSuspensionImpl(PhysicsSpace space, DynamicPhysicsNode base, Vector3f relativePosition, boolean canTurn) {
		return new PantherSuspension(space, base, relativePosition, canTurn);
	}
	
	protected Bullet getMainGunBulletImpl(ITank owner) {
		return new PantherMainGunBullet(owner, getPhysicsSpace(), new Vector3f(20, 3.5f, 0.4f));
	}
	
	protected Bullet getMachineGunBulletImpl(ITank owner) {
		return new PantherMachineGunBullet(owner, getPhysicsSpace(), new Vector3f(20, 3.15f, 2.8f));
	}
	
	protected int getInitialQtyBullets() {
		return 50;
	}

	public SynchronizeCreateMessage getSynchronizeCreateMessageImpl() {
		SynchronizeCreateTankMessage m = new SynchronizeCreateTankMessage();
		m.setTankName(this.getTankName());
		return m;
	}
}