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
package br.furb.inf.tcc.tankcoders.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.furb.inf.tcc.tankcoders.client.NVEHandler;
import br.furb.inf.tcc.tankcoders.game.Player;
import br.furb.inf.tcc.tankcoders.game.PlayerTank;
import br.furb.inf.tcc.tankcoders.game.PlayerType;
import br.furb.inf.tcc.tankcoders.game.StartGameArguments;
import br.furb.inf.tcc.tankcoders.scene.battle.Stopwatch;
import br.furb.inf.tcc.tankcoders.scene.camera.CustomChaseCamera;
import br.furb.inf.tcc.tankcoders.scene.effect.EffectManager;
import br.furb.inf.tcc.tankcoders.scene.hud.Panel;
import br.furb.inf.tcc.tankcoders.scene.sky.Sky;
import br.furb.inf.tcc.tankcoders.scene.tank.ITank;
import br.furb.inf.tcc.tankcoders.scene.tank.TankFactory;
import br.furb.inf.tcc.tankcoders.scene.tank.callback.TankUpdater;
import br.furb.inf.tcc.tankcoders.scene.tank.weapon.WeaponManager;
import br.furb.inf.tcc.tankcoders.scene.terrain.ITerrain;
import br.furb.inf.tcc.tankcoders.scene.terrain.TerrainFactory;

import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jmex.game.state.GameStateManager;
import com.jmex.game.state.StatisticsGameState;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.util.states.PhysicsGameState;

/**
 * In game state.
 * @author Germano Fronza
 */
public abstract class InGameState extends StatisticsGameState {
	
	private Renderer renderer;
	
	/**
	 * For physics manipulations.
	 * JME Physics 2
	 */
	protected PhysicsGameState physicsGameState;
	
	/**
	 * Input handler.
	 */
	protected InputHandler input;
	
	/**
	 * Current instance of camera.
	 */
	protected Camera cam;
	
	/**
	 * A panel to show important stuff about the tank 
	 * (based on cameraTargetTank attribute).
	 */
	protected Panel hud;
	
	/**
	 * Skybox of the world.
	 */
	protected Sky sky;
	
	/**
	 * Terrain of the battle.
	 * This is the base scene node.
	 */
	protected ITerrain terrain;
	
	/**
	 * Tanks of this battle.
	 */
	protected Map<String, ITank> tanks;
	
	/**
	 * Equivalet to tanks.values()
	 */
	protected List<ITank> tanksList;
	
	/**
	 * Tank's instance which camera follows.
	 */
	protected ITank cameraTargetTank;
	
	/**
	 * Initial game arguments.
	 */
	protected StartGameArguments gameArgs;
	
	/**
	 * Synchronization object to kill a tank.
	 */
	private Object killTankSyncObj = new Object();
	
	/**
	 * Creates a new InGameState.
	 */
	public InGameState(StartGameArguments gameArgs) {
		super();
		this.gameArgs = gameArgs;
		this.rootNode = new Node("RootNode");
		
		initRenderer();
		setupZBuffer();
		
		initLight();
		setupCamera();
		setupPhysicsGameState();
		setupOptimizations();
		
		makeSky();
		makeTerrain(gameArgs);
		makeHUD(gameArgs);
		setupEffectManager();
		makeTanks(gameArgs);
		setupChaseCameraOnTarget();
		setupWeaponManager();
		setupGravity();
		setupGameClient(gameArgs);
		startStopwatch();
	}

	/**
	 * Called each frame update.<br/>
	 * <b>Tasks:</b>
	 * <ul>
	 * 	<li>Skybox update</li>
	 * 	<li>Hud update</li>
	 * 	<li>Input update</li>
	 * </ul> 
	 */
	public void update(float f) {
        super.update(f);
        sky.update();
        hud.update();
        input.update(f);
        
        rootNode.updateGeometricState(f, true);
    }
	
	/**
	 * Called each frame update.<br>
	 * Render the root node.
	 */
	public void render(float f) {
		super.render(f);
		try {
			renderer.draw(rootNode);
		}
		catch (IndexOutOfBoundsException e) {
			// This exception may ocurr when a remote tank dying.
			// But I will hide this exception because on next frame update all gonna be is ok again.
		}
	}

	/**
	 * Initialize the Renderer attribute from DisplaySystem.
	 */
	private void initRenderer() {
		this.renderer = DisplaySystem.getDisplaySystem().getRenderer();
	}
	
	/**
	 * Creates the ZBufferState and set it in rootNode.
	 */
	private void setupZBuffer() {
		ZBufferState zbs = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
		zbs.setEnabled(true);
		zbs.setFunction(ZBufferState.CF_LEQUAL);
		rootNode.setRenderState(zbs);
	}
	
	/**
	 * Initialize the PointLight of the scene.
	 */
	private void initLight() {
		PointLight light = new PointLight();
        light.setDiffuse(new ColorRGBA(1, 1, 1, 1 ));
        light.setAmbient(new ColorRGBA(1f, 1f, 1f, 1.0f ));
        light.setLocation(new Vector3f(0, 10000, 0));
        light.setAttenuate(true);
        light.setEnabled(true);

        LightState lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        lightState.setEnabled(true);
        lightState.setTwoSidedLighting(true);
        
        lightState.attach(light);
        getRootNode().setRenderState(lightState);
    }

	/**
	 * Setup frustum and frame of the camera.
	 */
	private void setupCamera() {
		float aspect = (float) DisplaySystem.getDisplaySystem().getWidth() /
        	           (float) DisplaySystem.getDisplaySystem().getHeight();
		
		cam = DisplaySystem.getDisplaySystem().getRenderer().getCamera();
		cam.setFrustumPerspective(45.0f, aspect, 1, 10000); 
        cam.setParallelProjection( false );
        cam.update();
        
        Vector3f loc = new Vector3f(0.0f, 0.0f, 25.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
        
        cam.setFrame(loc, left, up, dir);
        cam.update();
	}
	
	/**
	 * Creates the PhysicsGameState and register it on GameStateManager.
	 */
	private void setupPhysicsGameState() {		
		physicsGameState = new PhysicsGameState("physicsGameState");
		GameStateManager.getInstance().attachChild(physicsGameState);
		physicsGameState.setActive(true);
	}
	
	/**
	 * Removes triangules turned back throught of the backface culling, to improve performance.
	 */
	private void setupOptimizations() {
		CullState cs = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
		cs.setCullMode(CullState.CS_BACK);
		cs.setEnabled(true);
		getRootNode().setRenderState(cs);
	}
	
	/**
	 * Make the Skybox to "envolve" the scene.
	 */
	private void makeSky() {
		sky = new Sky(cam);
		getRootNode().attachChild(sky);
	}
	
	/**
	 * Makes Panel to show informations about the current camera target tank.
	 * @param gameArgs
	 */
	private void makeHUD(StartGameArguments gameArgs) {		
		hud = new Panel(this, gameArgs.getBattleName());		
		getRootNode().attachChild(hud);
	}
	
	/**
	 * Removes Panel from the scene. 
	 */
	protected void remoteHUD() {				
		getRootNode().detachChild(hud);
	}
	
	/**
	 * Make the terrain based on heightmap image.
	 * @param gameArgs
	 */
	private void makeTerrain(StartGameArguments gameArgs) {
		terrain = TerrainFactory.makeTerrain(getPhysicsSpace(), gameArgs.getTerrainHeightMapImage());
		getRootNode().attachChild((Node)terrain);
	}
	
	/**
	 * Pre-cache resources used by EffectManager.
	 */
	private void setupEffectManager() {
		EffectManager.setup(terrain);
	}
	
	/**
	 * Setup the Weapon manager of the tanks.
	 */
	private void setupWeaponManager() {
		WeaponManager.setup(getRootNode(), terrain, tanks);
	}
	
	/**
	 * Creates all tanks of the battle (from local and remote players).
	 * @param gameArgs
	 */
	protected void makeTanks(StartGameArguments gameArgs) {
		tanks = new HashMap<String, ITank>();

		for (Iterator<String> i = gameArgs.getPlayersNamesIterator(); i.hasNext(); ) {
			String playerName = i.next();
			
			Player p = gameArgs.getPlayer(playerName);
			boolean isRemote = (p.getId() != gameArgs.getLocalPlayer().getId());
			boolean isMASPlayer = (p.getType() == PlayerType.MAS);
			
			for (PlayerTank tank : p.getTanks()) {
				makeTank(p.getId(), tank, isRemote, isMASPlayer);
			}
		}
		
		tanksList = new ArrayList<ITank>();
		tanksList.addAll(tanks.values());
	}
	
	/**
	 * Make a specific tank.
	 * @param playerIdOwner
	 * @param pTank
	 * @param remoteTank
	 * @param masPlayer
	 */
	private void makeTank(short playerIdOwner, PlayerTank pTank, boolean remoteTank, boolean masPlayer) {
		Vector3f location = terrain.getTanksInitialLocation()[pTank.getInitialSlotLocation()];
		
		ITank tank = TankFactory.makeTank(playerIdOwner, pTank, getPhysicsSpace(), location, remoteTank, masPlayer);
		tank.setPlayerIsTheOwner(!remoteTank);
		
		((Node)terrain).attachChild((Node)tank);
		
		getPhysicsSpace().addToUpdateCallbacks(new TankUpdater(tank));
		
		tanks.put(pTank.getTankName(), tank);
	}
	
	/**
	 * Creates a new chase camera and set it target.
	 */
	protected void setupChaseCameraOnTarget() {
		input = CustomChaseCamera.getInstance(cam, cameraTargetTank.getMainNode());
	}
	
	/**
	 * Setup a free look camera.
	 * This camera will be defined when the local player has no more tanks.
	 */
	protected void setupFreeLookCamera() {
		input = new FirstPersonHandler(cam, 50, 1);
	}
	
	/**
	 * Finish setup of this game state.<br>
	 * Generates the geometricState of the rootNode.
	 */
	protected void finishSetupGameState() {
		DisplaySystem.getDisplaySystem().getRenderer().enableStatistics(true);
		
		rootNode.updateRenderState();
        rootNode.updateWorldBound();
        rootNode.updateGeometricState(0.0f, true);
	}
	
	/**
	 * Setup gravity of the world.
	 */
	private void setupGravity() {
		getPhysicsSpace().setDirectionalGravity(new Vector3f(0, -9.81f, 0));
	}
	
	/**
	 * Setup the game client (NVEHandler).
	 * @param gameArgs
	 */
	private void setupGameClient(StartGameArguments gameArgs) {
		NVEHandler.setup(gameArgs, this);
	}
	
	/**
	 * Start the stopwatch to show in the hud.
	 */
	private void startStopwatch() {
		Stopwatch.getInstance().startCounter();
	}
	
	/**
	 * Gets PhysicsSpace instance.
	 * @return
	 */
	public PhysicsSpace getPhysicsSpace() {
		return physicsGameState.getPhysicsSpace();
	}

	/**
	 * Gets the current tank defined as chase camera target.
	 * @deprecated. Use getCameraTargetTank()
	 * @return ITank.
	 */
	public ITank getTank() {
		return cameraTargetTank;
	}
	
	/**
	 * Set a new tank as current camera target.
	 * @param tank
	 * @param playerIsTheOwner Inform true if the localplayer is the owner of this tank.
	 */
	public void setCameraTargetTank(ITank tank, boolean playerIsTheOwner) {
		cameraTargetTank = tank;
		cameraTargetTank.setCameraAtThisTank(true);
		cameraTargetTank.setPlayerIsTheOwner(playerIsTheOwner);
		
		hud.updateTankName();
	}
	
	/**
	 * Called by game client when all the players are in InGameState.
	 */
	public abstract void allPlayersAreInGameState();
	
	/**
	 * Kill a local tank.
	 * @param tankName
	 */
	public void killLocalPlayerTank(String tankName) {
		synchronized (killTankSyncObj) {
			ITank localTank = tanks.get(tankName);
			
			// kill tank
			localTank.setKillOnNextFrameUpdate();
			
			tanks.remove(tankName);
			gameArgs.getLocalPlayer().removeTank(tankName);
		}
	}
	
	/**
	 * Kill a remote tank.
	 * @param tankName
	 */
	public void killRemotePlayerTank(String tankName) {
		ITank remoteTank = tanks.get(tankName);
		
		// kill tank
		remoteTank.setKillOnNextFrameUpdate();
		
		tanks.remove(tankName);
	}

	/**
	 * Get the panel hud instance.
	 * @return
	 */
	public Panel getHud() {
		return hud;
	}

	/**
	 * Gets the map of tanks.
	 * @return
	 */
	public Map<String, ITank> getTanks() {
		return tanks;
	}

	/**
	 * Gets the current tank defined as chase camera target.
	 * @return ITank
	 */
	public ITank getCameraTargetTank() {
		return cameraTargetTank;
	}
}