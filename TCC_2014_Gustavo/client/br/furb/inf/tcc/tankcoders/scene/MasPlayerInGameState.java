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

import br.furb.inf.tcc.tankcoders.game.GameRulesConstants;
import br.furb.inf.tcc.tankcoders.game.PlayerTank;
import br.furb.inf.tcc.tankcoders.game.PlayerTeam;
import br.furb.inf.tcc.tankcoders.game.StartGameArguments;
import br.furb.inf.tcc.tankcoders.jason.AgentRepository;
import br.furb.inf.tcc.tankcoders.scene.camera.CameraUpdateController;
import br.furb.inf.tcc.tankcoders.scene.camera.CustomChaseCamera;
import br.furb.inf.tcc.tankcoders.scene.tank.ITank;
import br.furb.inf.tcc.tankcoders.scene.tank.action.ActionChangeCamera;
import br.furb.inf.tcc.tankcoders.scene.tank.action.ActionChangeCameraTarget;
import br.furb.inf.tcc.tankcoders.scene.tank.action.ActionChangeRenderState;
import br.furb.inf.tcc.util.game.GamePersistentProperties;

import com.jme.input.ChaseCamera;
import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;

/**
 * In game state for Mas player type.
 * @author Germano Fronza
 */
public class MasPlayerInGameState extends InGameState {

	/**
	 * Controller of chase camera to follow the target.
	 */
	private CameraUpdateController cameraController;
	
	/**
	 * Time in nanoseconds of last frame update.
	 */
	private long lastUpdateTime;
	
	/**
	 * Rate to notify the tank's informations.
	 */
	private final long notifyTankInfoRate;
	
	/**
	 * Creates a new MasPlayerInGameState.
	 * @param gameArgs
	 */
	public MasPlayerInGameState(StartGameArguments gameArgs) {
		super(gameArgs);
		this.notifyTankInfoRate = GamePersistentProperties.getInstance().getNotifyTankInfoRate();
		
		finishSetupGameState();
	}
	
	/**
	 * After super.makeTanks() here is defined the first tank of the player as camera target.<br>
	 * Note: the player can change the camera target pressing TAB.
	 */
	protected void makeTanks(StartGameArguments gameArgs) {
		super.makeTanks(gameArgs);
		
		// set the first tank of the player as camera target
		String tankName = gameArgs.getLocalPlayer().getTanks().get(0).getTankName();
		
		setCameraTargetTank(tanks.get(tankName), true);
	}
	
	/**
	 * Make keyboard commands to control the camera.
	 */
	private void makeKeyboardController() {
		// camera control			
		input.addAction(new ActionChangeCamera(cameraTargetTank, input), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_V, InputHandler.AXIS_NONE, false);
		input.addAction(new ActionChangeCameraTarget(tanks, gameArgs.getLocalPlayer(), this, input), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_TAB, InputHandler.AXIS_NONE, false);
		
		// wireframed state
		input.addAction(new ActionChangeRenderState(tanks), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_O, InputHandler.AXIS_NONE, false);
		
		cameraController = new CameraUpdateController((CustomChaseCamera)input);
		getPhysicsSpace().addToUpdateCallbacks(cameraController);
	}
	
	/**
	 * After super.update() here is checked the tanks radar.
	 */
	public void update(float f) {
		super.update(f);
		
		if (lastUpdateTime + notifyTankInfoRate < System.nanoTime()) {
        	checkTanksRadar();
        	
        	lastUpdateTime = System.nanoTime();
        }
	}
	
	/**
	 * Checks the radar of all tanks (tanks controlled by agents).
	 */
	private void checkTanksRadar() {
		int tanksListSize = tanksList.size();
        for (int i = 0; i < tanksListSize-1; i++) {
        	ITank tankI = tanksList.get(i);
        	PlayerTeam tankITeam = tankI.getTeam().getTeamEnum();
        	Vector3f posTankI = tankI.getWorldPosition();
        	boolean tankIAgentControls = tankI.isAgentControls();
        	
        	for (int j = i+1; j < tanksListSize; j++) {	
        		ITank tankJ = tanksList.get(j);
        		boolean tankJAgentControls = tankJ.isAgentControls();
        		
        		if (tankIAgentControls || tankJAgentControls) {
        			
        			PlayerTeam tankJTeam = tankJ.getTeam().getTeamEnum();
            		Vector3f posTankJ = tankJ.getWorldPosition();
        			boolean isEnemy = (tankITeam != tankJTeam);
	        		
	        		float distance = posTankI.subtract(posTankJ).length() / 2;
	        		if (distance < GameRulesConstants.RADAR_SCOPE) {
	        			if (tankIAgentControls) AgentRepository.notifyRadarScanTank(tankI.getTankName(), tankJ.getTankName(), tankJ.getMainNode().getLocalTranslation(), isEnemy);
	        			if (tankJAgentControls) AgentRepository.notifyRadarScanTank(tankJ.getTankName(), tankI.getTankName(), tankI.getMainNode().getLocalTranslation(), isEnemy);
	        			
	        			// is really near then check intersects.
	        			if (distance < 90) {
	        				if (tankI.intersectsWith(tankJ.getWorldBound())) {
	        					if (tankIAgentControls) AgentRepository.notifyHitTank(tankI.getTankName(), tankJ.getTankName(), isEnemy);
	        					if (tankJAgentControls) AgentRepository.notifyHitTank(tankJ.getTankName(), tankI.getTankName(), isEnemy);
	        				}
	        			}
	        		}
        		}
    		}			
		}
	}
	
	/**
	 * Remove the keyboard actions.<br>
	 * Call super.killLocalPlayerTank()
	 * If the player has more tanks, then setup the chase camera in the first one, 
	 * else setup a free camera.
	 */
	public void killLocalPlayerTank(String tankName) {
		input.removeAllActions();
		
		super.killLocalPlayerTank(tankName);
		
		// set camera target on another tank of the local player
		for (PlayerTank pTank : gameArgs.getLocalPlayer().getTanks()) {
			if (pTank.getTankName() != tankName) {
				ITank tank = tanks.get(pTank.getTankName());
				
				setCameraTargetTank(tank, true);
				
				((ChaseCamera)input).setTarget(tank.getMainNode());
				
				return;
			}
		}
		
		// if localplayer has no more tanks
		getPhysicsSpace().removeFromUpdateCallbacks(cameraController);
		remoteHUD();
		setupFreeLookCamera();
	}

	/**
	 * Called by game client when all the players are in InGameState.
	 * Here all the agents are nofified that battle has been started.
	 */
	public void allPlayersAreInGameState() {
		makeKeyboardController();
		
		AgentRepository.notifyTanksStartBattle();
	}
}
