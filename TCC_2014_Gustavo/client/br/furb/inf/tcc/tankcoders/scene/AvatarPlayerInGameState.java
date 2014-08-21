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

import br.furb.inf.tcc.tankcoders.game.StartGameArguments;
import br.furb.inf.tcc.tankcoders.scene.camera.CameraUpdateController;
import br.furb.inf.tcc.tankcoders.scene.camera.CustomChaseCamera;
import br.furb.inf.tcc.tankcoders.scene.tank.ITank;
import br.furb.inf.tcc.tankcoders.scene.tank.action.ActionAccelerator;
import br.furb.inf.tcc.tankcoders.scene.tank.action.ActionChangeCamera;
import br.furb.inf.tcc.tankcoders.scene.tank.action.ActionDirection;
import br.furb.inf.tcc.tankcoders.scene.tank.action.ActionHydraulicJacking;
import br.furb.inf.tcc.tankcoders.scene.tank.action.ActionMainGunDirection;
import br.furb.inf.tcc.tankcoders.scene.tank.action.ActionChangeRenderState;
import br.furb.inf.tcc.tankcoders.scene.tank.action.ActionShotMachineGun;
import br.furb.inf.tcc.tankcoders.scene.tank.action.ActionShotMainGun;

import com.jme.input.InputHandler;
import com.jme.input.KeyInput;

/**
 * In game state for Avatar player type.
 * @author Germano Fronza
 */
public class AvatarPlayerInGameState extends InGameState {

	/**
	 * Controller of chase camera to follow the target.
	 */
	private CameraUpdateController cameraController;
	
	/**
	 * Creates a new AvatarPlayerInGameState
	 * @param loadingState
	 * @param gameArgs
	 */
	public AvatarPlayerInGameState(StartGameArguments gameArgs) {
		super(gameArgs);
        
		finishSetupGameState();
	}
	
	/**
	 * After super.makeTanks() here is defined the tank as camera target.
	 */
	protected void makeTanks(StartGameArguments gameArgs) {
		super.makeTanks(gameArgs);
		
		// get tank of this avatar and set as camera target (player is the owner).
		// NOTE: local player name == your tank name (because local players just have one tank)
		String localPlayerName = gameArgs.getLocalPlayer().getName();
		setCameraTargetTank(tanks.get(localPlayerName), true);
	}
	
	/**
	 * Make keyboard commands to control the tank.
	 */
	private void makeKeyboardController() {
		// tank control
		input.addAction(new ActionAccelerator(cameraTargetTank, ITank.FORWARD_ORIENTATION),	InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_UP, InputHandler.AXIS_NONE, false);
		input.addAction(new ActionAccelerator(cameraTargetTank, ITank.BACKWARD_ORIENTATION), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_DOWN, InputHandler.AXIS_NONE, false);
		input.addAction(new ActionDirection(cameraTargetTank, ITank.LEFT_ORIENTATION), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_LEFT, InputHandler.AXIS_NONE, false);
		input.addAction(new ActionDirection(cameraTargetTank, ITank.RIGHT_ORIENTATION), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_RIGHT, InputHandler.AXIS_NONE, false);

		// hydraulic jacking
		input.addAction(new ActionHydraulicJacking(cameraTargetTank), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_J, InputHandler.AXIS_NONE, false);
				
		// have an independent main gun?
		if (cameraTargetTank.hasIndependentMainGun()) {
			// main gun control
			input.addAction(new ActionMainGunDirection(cameraTargetTank, ITank.LEFT_ORIENTATION), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_A, InputHandler.AXIS_NONE, false);
			input.addAction(new ActionMainGunDirection(cameraTargetTank, ITank.RIGHT_ORIENTATION), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_D, InputHandler.AXIS_NONE, false);
		}
		
		// shot maingun
		input.addAction(new ActionShotMainGun(cameraTargetTank), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_SPACE, InputHandler.AXIS_NONE, false);
		
		// shot machine gun
		input.addAction(new ActionShotMachineGun(cameraTargetTank), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_LCONTROL, InputHandler.AXIS_NONE, false);

		// camera control			
		input.addAction(new ActionChangeCamera(cameraTargetTank, input), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_V, InputHandler.AXIS_NONE, false);
		
		// wireframed state
		input.addAction(new ActionChangeRenderState(tanks), InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_O, InputHandler.AXIS_NONE, false);
		
		cameraController = new CameraUpdateController((CustomChaseCamera)input);
		getPhysicsSpace().addToUpdateCallbacks(cameraController);
	}
	
	/**
	 * Removes the camera controller of the dead tank;<br>
	 * Remove the keyboard actions.<br>
	 * Call super.killLocalPlayerTank()
	 * Setup free look camera because the avatar player has no more tanks to control.
	 */
	public void killLocalPlayerTank(String tankName) {
		getPhysicsSpace().removeFromUpdateCallbacks(cameraController);
		
		// now the user can't control the camera target tank
		input.removeAllActions();
		
		super.killLocalPlayerTank(tankName);
		
		remoteHUD();
		setupFreeLookCamera();
	}

	/**
	 * Called by game client when all the players are in InGameState.
	 * Here is defined the keyboard controller.
	 */
	public void allPlayersAreInGameState() {
		makeKeyboardController();
	}
}
