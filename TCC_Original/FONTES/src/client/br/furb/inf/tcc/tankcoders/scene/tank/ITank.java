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
package br.furb.inf.tcc.tankcoders.scene.tank;

import br.furb.inf.tcc.tankcoders.game.TankTeam;
import br.furb.inf.tcc.tankcoders.scene.tank.callback.TankUpdater;
import br.furb.inf.tcc.tankcoders.scene.tank.weapon.Projectile;

import com.captiveimagination.jgn.synchronization.message.SynchronizeCreateMessage;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;

/**
 * Represents a base tank.
 * @author Germano Fronza
 */
public interface ITank {

	public static final int FORWARD_ORIENTATION = 1;
	public static final int BACKWARD_ORIENTATION = -1;
	public static final int RIGHT_ORIENTATION = 1;
	public static final int LEFT_ORIENTATION = -1;

	public static final int INITIAL_HEALTH = 100;
	
	public abstract void update();

	public abstract void accelerate(int direction);

	public abstract void dropAccelerator();

	public abstract void turnWheel(final int direction);

	public abstract void stopTurningWheel();
	
	public abstract void turnMainGun(final int direction);

	public abstract void stopTurningMainGun();
	
	public void kill();
	
	public void addFireMainGunBullet(short type);

	public void addFireMachineGunBullet(short type);
	
	public void fireMainGunBullet();
	
	public void fireMachineGunBullet();

	public void powerOnHydraulicJacking();
	
	public void shutdownHydralicJacking();
	
	public DynamicPhysicsNode getChassi();
	
	public StaticPhysicsNode getStaticChassi();
	
	public Node getMainNode();
	
	public boolean hasIndependentMainGun();
	
	public String getTankName();
	
	public int getSpeed();
	
	public int getHealth();
	
	public int getMainGunQtyBullets();
	
	public float getCurrentMainGunHeading();
	
	public float getCurrentMainGunZRotAngle();
	
	public BoundingVolume getWorldBound();
	
	public boolean intersectsWith(BoundingVolume bv);
	
	public void hitByBullet(Projectile p);
	
	public void hitByRemoteBullet(int power);
	
	public void setPlayerIsTheOwner(boolean playerIsTheOwner);

	public void setCameraAtThisTank(boolean cameraAtThisTank);
	
	public TankTeam getTeam();	
	
	public boolean isRemote();
	
	public boolean isAgentControls();
	
	public SynchronizeCreateMessage getSynchronizeCreateMessageImpl();
	
	public void setKillOnNextFrameUpdate();
	
	public void showWheelsSphere();
	
	public void hideWheelsSphere();
	
	public void setTankUpdater(TankUpdater tankUpdater);
	
	public TankUpdater getTankUpdater();
	
	public Vector3f getWorldPosition();
	
	public void setAhead(int currentAheadQty);
	
	public void setBack(int currentBackQty);
	
	public void setTurnRightAngle(float degreesAngle, int direction);
	
	public void setTurnLeftAngle(float degreesAngle, int direction);
	
	public void setTurnMainGunRightAngle(float degreesAngle);
	
	public void setTurnMainGunLeftAngle(float degreesAngle);
}