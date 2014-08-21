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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import br.furb.inf.tcc.tankcoders.client.NVEHandler;
import br.furb.inf.tcc.tankcoders.game.PlayerTeam;
import br.furb.inf.tcc.tankcoders.game.TankTeam;
import br.furb.inf.tcc.tankcoders.jason.AgentRepository;
import br.furb.inf.tcc.tankcoders.scene.tank.callback.TankUpdater;
import br.furb.inf.tcc.tankcoders.scene.tank.panther.JadgPantherTank;
import br.furb.inf.tcc.tankcoders.scene.tank.suspension.Suspension;
import br.furb.inf.tcc.tankcoders.scene.tank.weapon.Bullet;
import br.furb.inf.tcc.tankcoders.scene.tank.weapon.Projectile;
import br.furb.inf.tcc.tankcoders.scene.tank.weapon.WeaponManager;
import br.furb.inf.tcc.tankcoders.scene.terrain.Terrain;
import br.furb.inf.tcc.util.game.GamePersistentProperties;
import br.furb.inf.tcc.util.scene.ModelUtils;
import br.furb.inf.tcc.util.scene.Text;

import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.material.Material;

/**
 * This class is an abstract implementation of the interface ITank.
 * Implements all the basic operations of the Tanks.
 * @author Germano Fronza
 */
public abstract class AbstractTank extends Node implements ITank {

	private static final long serialVersionUID = 1L;

	/**
	 * Physics space instance reference.
	 */
	PhysicsSpace physicsSpace;
	
	/**
	 * Tank physics updates reference
	 */
	private TankUpdater tankUpdater;
	
	/**
	 * Represents the Tank Chassi. 
	 * This is the main node of the Tank, all the other stuff are directly related to it.
	 */
	protected DynamicPhysicsNode chassi;
	
	/**
	 * Represents the static Tank Chassi. 
	 * This is the main node of the Tank, all the other stuff are directly related to it.
	 */
	protected StaticPhysicsNode staticChassi;
	
	/**
	 * Represent the ghost suspensions.
	 * This suspensions composes the base of the four (ghost) wheels of the tank.
	 */
	private Suspension rearSuspension, frontSuspension;
	
	/**
	 * Determines if the hydraulic jacking is acting.
	 */
	private boolean hydraulicJacking = false;
	
	/**
	 * Determines if the player (agent or human) is the owner of this tank.
	 * I need to know about this because some texts only can be shown to the owners.
	 */
	private boolean playerIsTheOwner;
	
	/**
	 * Determines if the camera is following this tank.
	 */
	private boolean cameraAtThisTank;
	
	/**
	 * Id of player owner.
	 */
	private short playerIdOwner;
	
	/**
	 * Name of the tank.
	 */
	private String tankName;
	
	/**
	 * Label to display tank name.
	 */
	private Text tankNameLabel;
	
	/**
	 * Tank's team.
	 */
	private TankTeam team;
	
	/**
	 * Current health of the tank.
	 */
	private int health;
	
	/**
	 * Current bullets count.
	 */
	private int mainGunQtyBullets;

	/**
	 * True if an agent controls this tank.
	 */
	private boolean agentControls;
	
	/**
	 * This tank is an remote tank?
	 */
	private boolean remote;
	
	/**
	 * Queue of main gun bullets to fire.
	 */
	private Queue<Short> fireMainGunBullets;
	
	/**
	 * Queue of machine gun bullets to fire.
	 */
	private Queue<Short> fireMachineGunBullets;
	
	/**
	 * Define if this tank needs to be killed on next frame update.
	 */
	private boolean killOnNextFrameUpdate;
	
	/**
	 * Time in nanoseconds of last frame update.
	 */
	private long lastUpdateTime;
	
	/**
	 * Rate to notify the tank's position.
	 */
	private final long notifyTankInfoRate;
	
	/**
	 * Location of the tank before move ahead/back.
	 */
	private Vector3f worldLocBeforeMove;
	
	/**
	 * Move tank ahead N meters.
	 */
	private int currentAheadQty;
	
	/**
	 * Move tank back N meters.
	 */
	private int currentBackQty;
	
	/**
	 * Turn tank to the right N agle (in DEGREES).
	 */
	private float turnRightAngleDesired = -1;

	/**
	 * Turn tank to the left N agle (in DEGREES).
	 */
	private float turnLeftAngleDesired = -1;
	
	/**
	 * Turn main gun to the right N agle (in DEGREES).
	 */
	private float turnMainGunRightAngleDesired = -1;

	/**
	 * Turn main gun to the left N agle (in DEGREES).
	 */
	private float turnMainGunLeftAngleDesired = -1;
	
	/**
	 * Synchronization object to kill a tank.
	 */
	private Object killTankSyncObj = new Object();
	
	/**
	 * Build the basic structure.
	 * @param tankName
	 * @param pSpace
	 * @param position
	 */
	public AbstractTank(short playerIdOwner, String tankName, TankTeam team, final PhysicsSpace pSpace, final Vector3f position, final boolean remoteTank, final boolean agentControls) {
		super(tankName);
		this.physicsSpace = pSpace;
		this.playerIdOwner = playerIdOwner;
		this.tankName = tankName;
		this.team = team;
		this.health = INITIAL_HEALTH;
		this.mainGunQtyBullets = getInitialQtyBullets();
		this.remote = remoteTank;
		this.agentControls = agentControls && (!remote);
		this.fireMainGunBullets = new ConcurrentLinkedQueue<Short>();
		this.fireMachineGunBullets = new ConcurrentLinkedQueue<Short>();
		this.notifyTankInfoRate = GamePersistentProperties.getInstance().getNotifyTankInfoRate();
		this.lastUpdateTime = System.nanoTime() - notifyTankInfoRate; // make ready for immediate update
		
		if (!remoteTank) {
			makeDynamicChassi(pSpace, position);
			makeSuspensions(pSpace);
			makeTankIdentificationLabel(chassi);
			makeModelBound(chassi);
		}
		else {
			makeStaticChassi(pSpace, position);
			makeTankIdentificationLabel(staticChassi);
			makeModelBound(staticChassi);
		}
	}
	
	/////////////////////// TANK INTERNAL LOGIC ////////////////////////

	/**
	 * Make Dynamic Chassi and attach it in THIS node.
	 * @param pSpace
	 * @param position
	 */
	protected void makeDynamicChassi(final PhysicsSpace pSpace, final Vector3f position) {
		chassi = pSpace.createDynamicNode();
		chassi.setName("tankChassi");
		chassi.setLocalTranslation(position);
		
		Node model = ModelUtils.getNodeByObj(getChassiModelPath());
		chassi.attachChild(model);
		
		chassi.generatePhysicsGeometry(true);
		chassi.setMaterial(Material.WOOD);
		chassi.setMass(getChassiMass());
		
		this.attachChild(chassi);
	}
	
	/**
	 * Make Dynamic Chassi and attach it in THIS node.
	 * @param pSpace
	 * @param position
	 */
	protected void makeStaticChassi(final PhysicsSpace pSpace, final Vector3f position) {
		staticChassi = pSpace.createStaticNode();
		staticChassi.setName("tankChassi");
		staticChassi.setLocalTranslation(position);
		
		Node model = ModelUtils.getNodeByObj(getChassiModelPath());
		staticChassi.attachChild(model);
		
		staticChassi.generatePhysicsGeometry(true);
		staticChassi.setMaterial(Material.WOOD);
		
		this.attachChild(staticChassi);
	}
	
	/**
	 * Make the rear and front suspensions and attach it in the THIS node
	 * @param pSpace
	 */
	private void makeSuspensions(final PhysicsSpace pSpace) {	
		rearSuspension = getSuspensionImpl(pSpace, chassi, new Vector3f(-getAxisDistances()/2, getSuspensionHeight(), 0), false);
		this.attachChild(rearSuspension);
		
		frontSuspension = getSuspensionImpl(pSpace, chassi, new Vector3f(getAxisDistances()/2, getSuspensionHeight(), 0), true);
		this.attachChild(frontSuspension);
	}
	
	/**
	 * Make a sphere (blue or red) to identify the tank's team.
	 */
	private void makeTankIdentificationLabel(Node target) {		
		tankNameLabel = new Text(target, new Vector3f(0, 7, 0), tankName);
		tankNameLabel.setHorizontalAlignment(Text.HA_CENTER);
		tankNameLabel.setVerticalAlignment(Text.VA_TOP);
		tankNameLabel.setLocalScale(.7f);
		tankNameLabel.setColor(team.getColor());
		tankNameLabel.setEnabled(true);
		
		this.attachChild(tankNameLabel);
	}
	
	private void makeModelBound(Node mainNode) {
		mainNode.setModelBound(new BoundingSphere());
		mainNode.updateModelBound();
	}
	
	/**
	 * This method is invoked each physics frame update.
	 */
	public void update() {
		if (killOnNextFrameUpdate) {
			// synchronized block (atomic)
			synchronized (killTankSyncObj) {
				getPhysicsSpace().removeFromUpdateCallbacks(tankUpdater);
				
				kill();
				
				return;
			}
		}
		
		if (!remote) {			
			checkHydraulicJackingForce();
			isInTeamHeadQuarter();
			
			if (agentControls) {				
				if (lastUpdateTime + notifyTankInfoRate < System.nanoTime()) {
					float chassiHeading = getCurrentChassiHeading();
					float mainGunHeading;
					if (this instanceof JadgPantherTank) {
						mainGunHeading = chassiHeading;
					}
					else {
						mainGunHeading = getCurrentMainGunHeading();
					}
					
					AgentRepository.notifyTankInfo(tankName, chassi.getLocalTranslation(), health, mainGunQtyBullets, chassiHeading, mainGunHeading);
					lastUpdateTime = System.nanoTime();
				}
				
				// move ahead
				if (currentAheadQty > 0) {
					float distance = chassi.getWorldTranslation().subtract(worldLocBeforeMove).length() / 2;
					if (distance >= currentAheadQty) {
						dropAccelerator();
						currentAheadQty = -1;
						AgentRepository.notifyActionFinished(tankName);
					}
					
					return; // operations are exclusive
				}
				
				// move back
				if (currentBackQty > 0) {
					float distance = chassi.getWorldTranslation().subtract(worldLocBeforeMove).length() / 2;
					if (distance >= currentBackQty) {
						dropAccelerator();
						currentBackQty = -1;
						AgentRepository.notifyActionFinished(tankName);
					}
					
					return; // operations are exclusive
				}
				
				// turn right
				if (turnRightAngleDesired >= 0) {
					float currentHeading = getCurrentChassiHeading();
					
					boolean stopTurning = false;
					if (currentHeading >= 180 && currentHeading <= 360) {
						if (turnRightAngleDesired >= 180 && turnRightAngleDesired <= 360) {
							stopTurning = (currentHeading >= turnRightAngleDesired);
						}
					}
					else {
						stopTurning = (currentHeading >= turnRightAngleDesired);
					}
						
					if (stopTurning) {
						stopTurningWheel();
						dropAccelerator();
						turnRightAngleDesired = -1;
						AgentRepository.notifyActionFinished(tankName);
					}
					
					return;
				} // end turn right
				
				// turn left
				if (turnLeftAngleDesired >= 0) {
					float currentHeading = getCurrentChassiHeading();
					
					boolean stopTurning = false;
					if (currentHeading >= 0 && currentHeading <= 180) {
						if (turnLeftAngleDesired >= 0 && turnLeftAngleDesired <= 180) {
							stopTurning = (currentHeading <= turnLeftAngleDesired);
						}
					}
					else {
						stopTurning = (currentHeading <= turnLeftAngleDesired);
					}
						
					if (stopTurning) {
						stopTurningWheel();
						dropAccelerator();
						turnLeftAngleDesired = -1;
						AgentRepository.notifyActionFinished(tankName);
					}
					
					return;
				} // end turn left
				
				// turn main gun right
				if (turnMainGunRightAngleDesired >= 0) {
					float currentHeading = (getCurrentMainGunHeading() * FastMath.RAD_TO_DEG);
					
					boolean stopTurning = false;
					if (currentHeading >= 180 && currentHeading <= 360) {
						if (turnMainGunRightAngleDesired >= 180 && turnMainGunRightAngleDesired <= 360) {
							stopTurning = (currentHeading >= turnMainGunRightAngleDesired);
						}
					}
					else {
						stopTurning = (currentHeading >= turnMainGunRightAngleDesired);
					}
						
					if (stopTurning) {
						stopTurningMainGun();
						turnMainGunRightAngleDesired = -1;
						AgentRepository.notifyActionFinished(tankName);
					}
				} // end turn main gun right
				
				// turn main gun left
				if (turnMainGunLeftAngleDesired >= 0) {
					float currentHeading = (getCurrentMainGunHeading() * FastMath.RAD_TO_DEG);
					
					boolean stopTurning = false;
					if (currentHeading >= 0 && currentHeading <= 180) {
						if (turnMainGunLeftAngleDesired >= 0 && turnMainGunLeftAngleDesired <= 180) {
							stopTurning = (currentHeading <= turnMainGunLeftAngleDesired);
						}
					}
					else {
						stopTurning = (currentHeading <= turnMainGunLeftAngleDesired);
					}
						
					if (stopTurning) {
						stopTurningMainGun();
						turnMainGunLeftAngleDesired = -1;
						AgentRepository.notifyActionFinished(tankName);
					}
					
					return;
				} // end turn main gun left
			} // end if agentControls
		} // end if !remote
		
		// fire main gun bullets task
		for (int i = 0; i < fireMainGunBullets.size(); i++) {
			short type = fireMainGunBullets.poll();
			switch (type) {
				case Bullet.LOCAL: fireMainGunBullet(); break;
				case Bullet.REMOTE: fireMainGunBulletRemote(); break;
			}
		}
		
		// fire machine gun bullets task
		for (int i = 0; i < fireMachineGunBullets.size(); i++) {
			short type = fireMachineGunBullets.poll();
			switch (type) {
				case Bullet.LOCAL: fireMachineGunBullet(); break;
				case Bullet.REMOTE: fireMachineGunBulletRemote(); break;
			}
		}
	}

	/**
	 * Check if the hydraulic jacking must act
	 */
	public void checkHydraulicJackingForce() {
		if (hydraulicJacking){
			chassi.addForce(Vector3f.UNIT_Y.mult(5000));
		}
	}

	/**
	 * Check if tank is in your headquarter.
	 * If yes, then reload the maingun.
	 */
	private void isInTeamHeadQuarter() {
		float z = getMainNodeLocalTranslaction().z;
		
		boolean isInTheTeamHeadquarter = false;
		if (team.getTeamEnum() == PlayerTeam.TEAM_1) {
			isInTheTeamHeadquarter = (z >= Terrain.ZLOC_TEAM1_HEADQUARTER);
		}
		else {
			isInTheTeamHeadquarter = (z <= Terrain.ZLOC_TEAM2_HEADQUARTER);
		}
		
		if (isInTheTeamHeadquarter) {
			reloadMainGun();
		}
	}
	
	/**
	 * @see AbstractTank.killOnNextFrameUpdate
	 */
	public void setKillOnNextFrameUpdate() {
		this.killOnNextFrameUpdate = true;
	}
	
	/**
	 * @see AbstractTank.fireMainGunBullets
	 */
	public void addFireMainGunBullet(short type) {
		fireMainGunBullets.add((short)type);
	}
	
	/**
	 * @see AbstractTank.fireMachineGunBullets
	 */
	public void addFireMachineGunBullet(short type) {
		fireMachineGunBullets.add((short)type);
	}
	
	/** 
	 * Gets the current heading (in DEGREES) of the tank chassi 
	 */
	public float getCurrentChassiHeading() {
		Quaternion q = getMainNode().getLocalRotation();
		
		// set rotation at Y axis.
		q.toAngleAxis(new Vector3f(0, 1, 0));
		
		// get the angle based on Y axis (position one of the returned float array) and convert to Degrees.
		float currentAngle = (q.toAngles((float[])null)[1]/FastMath.DEG_TO_RAD);
		
		// I don't work with negative angles...
		if(currentAngle<0){
			currentAngle+=360;
		}
		
		// invert angle.
		return 360-currentAngle;
	}
	
	/**
	 * Gets the linear speed of the tank.
	 */
	public int getSpeed() {
		return (int) chassi.getLinearVelocity(Vector3f.ZERO).length();
	}
	
	/**
	 * Gets the tank name.
	 * @return
	 */
	public String getTankName() {
		return tankName;
	}

	/**
	 * Sets the tank name.
	 * @param tankName
	 */
	public void setTankName(String tankName) {
		this.tankName = tankName;
	}
	
	/**
	 * Gets current health of the tank.
	 * @return float.
	 */
	public int getHealth() {
		return health;
	}
	
	/**
	 * Checks if tank is alive.
	 * @return boolean
	 */
	public boolean isAlive() {
		return health > 0;
	}
	
	/**
	 * Kill the tank.
	 */
	public void kill() {
		// Vector3f location = getMainNode().getLocalTranslation().add(new Vector3f(0,0,0));
		this.removeFromParent();
		
		if (agentControls) {
			AgentRepository.notifyTankDeath(tankName);
			AgentRepository.notifyOthersAboutThisDeath(tankName);
		}
		
		// EffectManager.addTankExplosionEffect(location);
	}
	
	public int getMainGunQtyBullets() {
		return mainGunQtyBullets;
	}
	
	protected void decMainGunQtyBullets() {
		mainGunQtyBullets--;
	}
	
	private void reloadMainGun() {
		mainGunQtyBullets = getInitialQtyBullets();
	}
	
	/**
	 * Gets the Chassi dynamic node.
	 */
	public DynamicPhysicsNode getChassi() {
		return chassi;
	}
	
	/**
	 * Gets the Chassi static node.
	 */
	public StaticPhysicsNode getStaticChassi() {
		return staticChassi;
	}
	
	public TankTeam getTeam() {
		return this.team;
	}
	
	/**
	 * Has an independent gun?
	 */
	public abstract boolean hasIndependentMainGun();
	
	/**
	 * Checks if a given BoundingVolume intersects this tank bounding volume.
	 */
	public boolean intersectsWith(BoundingVolume bv) {
		return (getMainNode().getWorldBound().intersects(bv));
	}

	public void setPlayerIsTheOwner(boolean playerIsTheOwner) {
		this.playerIsTheOwner = playerIsTheOwner;
	}

	/**
	 * If camera is following this tank, then the tankLabelName will be disabled.
	 */
	public void setCameraAtThisTank(boolean cameraAtThisTank) {
		this.cameraAtThisTank = cameraAtThisTank;
		
		if (this.cameraAtThisTank) {
			tankNameLabel.setEnabled(false);
		}
		else {
			tankNameLabel.setEnabled(true);
		}
	}
	
	/////////////////////// END TANK INTERNAL LOGIC ////////////////////////
	
	/////////////////////// ACTIONS ////////////////////////
	
	/**
	 * Power on the hydraulic jacking
	 */
	public void powerOnHydraulicJacking() {
		hydraulicJacking = true;
	}

	/**
	 * Shutdown the hydraulic jacking
	 */
	public void shutdownHydralicJacking() {
		hydraulicJacking = false;	
	}
	
	/**
	 * Move tank forward or backaward.
	 */
	public void accelerate(int direction) {
		rearSuspension.accelerate(direction);
		frontSuspension.accelerate(direction);
	}
	
	/**
	 * Release acceleration.
	 */
	public void dropAccelerator() {
		rearSuspension.dropAccelerator();
		frontSuspension.dropAccelerator();
	}
	
	/**
	 * Turn the wheel based on given direction.
	 */
	public void turnWheel(final int direction) {
		frontSuspension.turnWheel(direction);
	}

	/**
	 * Release the wheel.
	 */
	public void stopTurningWheel() {
		frontSuspension.stopTurningWheel();
	}

	/**
	 * Stop turning the main gun.
	 */
	public abstract void stopTurningMainGun();

	/**
	 * Turn the main gun based on given direction.
	 */
	public abstract void turnMainGun(int direction);

	/**
	 * Create a main gun bullet and fire it based on main gun heading.
	 */
	public void fireMainGunBullet() {
		if (mainGunQtyBullets > 0) {
			Bullet bullet = getMainGunBulletImpl(this);
			WeaponManager.fireBullet(bullet);
			
			NVEHandler.sendTankActionShotMainGun(tankName);
			
			decMainGunQtyBullets();
		}
	}
	
	/**
	 * Create a machine gun bullet and fire it based on main gun heading.
	 */
	public void fireMachineGunBullet() {
		Bullet bullet = getMachineGunBulletImpl(this);
		WeaponManager.fireBullet(bullet);
	}
	
	/**
	 * Create a main gun bullet and fire it based on main gun heading.
	 */
	private void fireMainGunBulletRemote() {
		Bullet bullet = getMainGunBulletImpl(this);
		bullet.setCanDamage(false);
		WeaponManager.fireBullet(bullet);
	}
	
	/**
	 * Create a machine gun bullet and fire it based on main gun heading.
	 */
	private void fireMachineGunBulletRemote() {
		Bullet bullet = getMachineGunBulletImpl(this);
		bullet.setCanDamage(false);
		WeaponManager.fireBullet(bullet);
	}
	
	/////////////////////// END ACTIONS ////////////////////////
	
	/////////////////////// EVENTS ////////////////////////
	
	/**
	 * Called by ProjectileCollisionController when a bullet hit this tank.
	 * @param p
	 */
	public void hitByBullet(Projectile p) {		
		// check if the local player is the tank's owner, if yes then check if this is still alive,
		// if not just report this shot to the player owner of this tank
		if (playerIsTheOwner) {
			this.health -= p.getPower();
		
			if (agentControls) {
				AgentRepository.notifyTankHitByBullet(tankName, p.getPower());
			}
			
			if (!isAlive()) {
				// before send message, I kill the tank locally (via inGameState)
				NVEHandler.sendTankDead(tankName);
			}
		}
		else {
			NVEHandler.sendTankBulletHit(playerIdOwner, tankName, p.getPower());
		}
	}
	
	/**
	 * Called by NVEHandler when this a bullet hit this tank in another game instance (remote bullet).
	 * @param p
	 */
	public void hitByRemoteBullet(int power) {
		this.health -= power;
		
		if (agentControls) {
			AgentRepository.notifyTankHitByBullet(tankName, power);
		}
		
		if (!isAlive()) {
			// before send message, I kill the tank locally (via inGameState)
			NVEHandler.sendTankDead(tankName);
		}
	}
	
	/////////////////////// END EVENTS ////////////////////////
	
	public void showWheelsSphere() {
		frontSuspension.showWheelsSphere();
		rearSuspension.showWheelsSphere();
	}
	
	public void hideWheelsSphere() {
		frontSuspension.hideWheelsSphere();
		rearSuspension.hideWheelsSphere();
	}
	
	/**
	 * Gets the main node of the tank.
	 */
	public Node getMainNode() {
		if (chassi != null) {
			return chassi;
		}
		else {
			return staticChassi;
		}
	}
	
	public Vector3f getMainNodeLocalTranslaction() {
		return getMainNode().getLocalTranslation();
	}

	public Vector3f getWorldPosition() {
		return getMainNode().getWorldTranslation();
	}
	
	public PhysicsSpace getPhysicsSpace() {
		return physicsSpace;
	}
	
	public boolean isRemote() {
		return remote;
	}
	
	public boolean isAgentControls() {
		return agentControls;
	}
	
	public TankUpdater getTankUpdater() {
		return tankUpdater;
	}

	public void setTankUpdater(TankUpdater tankUpdater) {
		this.tankUpdater = tankUpdater;
	}
	
	/**
	 * Method used by agent to move this tank ahead.
	 * @param currentAheadQty Value in meters.
	 */
	public void setAhead(int currentAheadQty) {
		if (currentAheadQty > 0) {
			this.currentAheadQty = currentAheadQty;
			this.worldLocBeforeMove = new Vector3f(chassi.getWorldTranslation());
			accelerate(1);
		}
	}
	
	/**
	 * Method used by agent to move this tank back.
	 * @param currentBackQty Value in meters.
	 */
	public void setBack(int currentBackQty) {
		if (currentBackQty > 0) {
			this.currentBackQty = currentBackQty;
			this.worldLocBeforeMove = new Vector3f(chassi.getWorldTranslation());
			accelerate(-1);
		}
	}
	
	/**
	 * Method used by agent to turn right this tank. 
	 * @param degreesAngle Angle in degrees between 0º and 90º
	 * @param direction. Positive value to move ahead, Negative value otherwise.
	 */
	public void setTurnRightAngle(float degreesAngle, int direction) {
		if (degreesAngle >= 0 && degreesAngle <= 90) {
			// moving ahead?
			if (direction == 1) {
				this.turnRightAngleDesired = getCurrentChassiHeading() + degreesAngle;
				if (this.turnRightAngleDesired > 360) {
					this.turnRightAngleDesired = this.turnRightAngleDesired - 360;
				}
			}
			else {
				this.turnLeftAngleDesired = getCurrentChassiHeading() - degreesAngle;
				if (this.turnLeftAngleDesired < 0) {
					this.turnLeftAngleDesired = 360 + this.turnLeftAngleDesired; // (is negative)
				}
			}
			
			turnWheel(1);
			accelerate(direction);
		}
		else {
			// TODO: log a message or throw an exception
		}
	}
	
	/**
	 * Method used by agent to turn left this tank. 
	 * @param degreesAngle Angle in degrees between 0º and 90º
	 * @param direction. Positive value to move ahead, Negative value otherwise.
	 */
	public void setTurnLeftAngle(float degreesAngle, int direction) {
		if (degreesAngle >= 0 && degreesAngle <= 90) {
			// moving ahead?
			if (direction == 1) {
				this.turnLeftAngleDesired = getCurrentChassiHeading() - degreesAngle;
				if (this.turnLeftAngleDesired < 0) {
					this.turnLeftAngleDesired = 360 + this.turnLeftAngleDesired; // (is negative)
				}
			}
			else {
				this.turnRightAngleDesired = getCurrentChassiHeading() + degreesAngle;
				if (this.turnRightAngleDesired > 360) {
					this.turnRightAngleDesired = this.turnRightAngleDesired - 360;
				}
			}
			
			turnWheel(-1);
			accelerate(direction);
		}
		else {
			// TODO: log a message or throw an exception
		}
	}
	
	/**
	 * Method used by agent to turn right the main gun of this tank.
	 * @param degreesAngle Angle in degrees between 0º and 90º
	 */
	public void setTurnMainGunRightAngle(float degreesAngle) {
		if (degreesAngle >= 0 && degreesAngle <= 90) {
			this.turnMainGunRightAngleDesired = (getCurrentMainGunHeading() * FastMath.RAD_TO_DEG) + degreesAngle;
			if (this.turnMainGunRightAngleDesired > 360) {
				this.turnMainGunRightAngleDesired = this.turnMainGunRightAngleDesired - 360;
			}
			
			
			turnMainGun(1);
		}
		else {
			// TODO: log a message or throw an exception
		}
	}
	
	/**
	 * Method used by agent to turn left the main gun of this tank.
	 * @param degreesAngle Angle in degrees between 0º and 90º
	 */
	public void setTurnMainGunLeftAngle(float degreesAngle) {
		if (degreesAngle >= 0 && degreesAngle <= 90) {
			this.turnMainGunLeftAngleDesired = (getCurrentMainGunHeading() * FastMath.RAD_TO_DEG) - degreesAngle;
			if (this.turnMainGunLeftAngleDesired < 0) {
				this.turnMainGunLeftAngleDesired = 360 + this.turnMainGunLeftAngleDesired; // (is negative)
			}
		
			
			turnMainGun(-1);
		}
		else {
			// TODO: log a message or throw an exception
		}
	}
	
	protected abstract String getChassiModelPath();
	protected abstract float getChassiMass();
	protected abstract float getAxisDistances();
	protected abstract float getSuspensionHeight();
	protected abstract Suspension getSuspensionImpl(PhysicsSpace pSpace, DynamicPhysicsNode base, Vector3f relativePosition, boolean canTurn);
	protected abstract Bullet getMainGunBulletImpl(ITank owner);
	protected abstract Bullet getMachineGunBulletImpl(ITank owner);
	protected abstract int getInitialQtyBullets();
}