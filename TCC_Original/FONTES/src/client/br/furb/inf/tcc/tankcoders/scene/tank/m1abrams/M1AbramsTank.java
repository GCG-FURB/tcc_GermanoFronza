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

import br.furb.inf.tcc.tankcoders.game.TankTeam;
import br.furb.inf.tcc.tankcoders.message.SynchronizeCreateTankMessage;
import br.furb.inf.tcc.tankcoders.scene.tank.AbstractTank;
import br.furb.inf.tcc.tankcoders.scene.tank.ITank;
import br.furb.inf.tcc.tankcoders.scene.tank.suspension.Suspension;
import br.furb.inf.tcc.tankcoders.scene.tank.weapon.Bullet;
import br.furb.inf.tcc.tankcoders.scene.tank.weapon.machinegun.bullet.M1AbramsMachineGunBullet;
import br.furb.inf.tcc.tankcoders.scene.tank.weapon.maingun.MainGun;
import br.furb.inf.tcc.tankcoders.scene.tank.weapon.maingun.bullet.M1AbramsMainGunBullet;
import br.furb.inf.tcc.util.scene.ModelUtils;

import com.captiveimagination.jgn.synchronization.message.SynchronizeCreateMessage;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;

/**
 * M1-Abrams is an concrete implementation of the Tank
 * @author Germano Fronza
 */
public class M1AbramsTank extends AbstractTank implements ITank {

	private static final long serialVersionUID = 1L;
	
	private static final String CHASSI_MODEL_PATH = "data/model/m1abrams/chassi.obj";
	private static final String LEFT_WHEELS_MODEL_PATH = "data/model/m1abrams/wheels_left.obj";
	private static final String RIGHT_WHEELS_MODEL_PATH = "data/model/m1abrams/wheels_right.obj";
	private static final float CHASSI_MASS = 240;
	private static final float AXIS_DISTANCE = 15;
	private static final float SUSPENSION_HEIGHT = -2.5f;

	private MainGun mainGun;

	public M1AbramsTank(short playerIdOwner, String tankName, TankTeam team, final PhysicsSpace pSpace, final Vector3f position, final boolean remoteTank, final boolean agentControls) {
		super(playerIdOwner, tankName, team, pSpace, position, remoteTank, agentControls);
		
		makeMainGun(pSpace);
	}

	protected void makeDynamicChassi(final PhysicsSpace pSpace, final Vector3f position) {
		super.makeDynamicChassi(pSpace, position);
		
		Node modelWheels = ModelUtils.getNodeByObj(LEFT_WHEELS_MODEL_PATH);
		chassi.attachChild(modelWheels);
		
		modelWheels = ModelUtils.getNodeByObj(RIGHT_WHEELS_MODEL_PATH);
		chassi.attachChild(modelWheels);
	}
	
	protected void makeStaticChassi(PhysicsSpace space, Vector3f position) {
		super.makeStaticChassi(space, position);
		
		Node modelWheels = ModelUtils.getNodeByObj(LEFT_WHEELS_MODEL_PATH);
		staticChassi.attachChild(modelWheels);
		
		modelWheels = ModelUtils.getNodeByObj(RIGHT_WHEELS_MODEL_PATH);
		staticChassi.attachChild(modelWheels);
	}
	
	private void makeMainGun(PhysicsSpace space) {
		if (staticChassi != null) {
			mainGun = new M1AbramsMainGun(staticChassi, new Vector3f(-2.2f, 2.2f, -0.48f));
			this.attachChild(mainGun);
		}
		else {
			mainGun = new M1AbramsMainGun(chassi, new Vector3f(-2.2f, 2.4f, -0.48f));
			this.attachChild(mainGun);
		}
	}
	
	public void stopTurningMainGun() {
		mainGun.stopTurningMainGun();
	}

	public void turnMainGun(int direction) {
		mainGun.turnMainGun(direction);
	}

	public boolean intersectsWith(BoundingVolume bv) {
		if (super.intersectsWith(bv)) {
			return true;
		}
		else {
			return mainGun.intersectsWith(bv);
		}
	}
	
	public boolean hasIndependentMainGun() {
		return true;
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
	
	public MainGun getMainGun() {
		return this.mainGun;
	}

	public float getCurrentMainGunHeading() {
		// delegate to maingun
		return mainGun.getCurrentHeading();
	}
	
	public float getCurrentMainGunZRotAngle() {
		// delegate to maingun
		return mainGun.getCurrentZRotAngle();
	}
	
	protected Suspension getSuspensionImpl(PhysicsSpace space, DynamicPhysicsNode base, Vector3f relativePosition, boolean canTurn) {
		return new M1AbramsSuspension(space, base, relativePosition, canTurn);
	}
	
	protected Bullet getMainGunBulletImpl(ITank owner) {
		return new M1AbramsMainGunBullet(owner, new Vector3f(16f, .2f, 0.4f));
	}
	
	protected Bullet getMachineGunBulletImpl(ITank owner) {
		return new M1AbramsMachineGunBullet(owner, new Vector3f(16f, 1.8f, 2f));
	}
	
	protected int getInitialQtyBullets() {
		return 60;
	}
	
	public SynchronizeCreateMessage getSynchronizeCreateMessageImpl() {
		SynchronizeCreateTankMessage m = new SynchronizeCreateTankMessage();
		m.setTankName(this.getTankName());
		return m;
	}
}
