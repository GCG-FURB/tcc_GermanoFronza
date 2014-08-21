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
package br.furb.inf.tcc.tankcoders.scene.tank.action;

import java.util.List;
import java.util.Map;

import br.furb.inf.tcc.tankcoders.game.Player;
import br.furb.inf.tcc.tankcoders.game.PlayerTank;
import br.furb.inf.tcc.tankcoders.scene.MasPlayerInGameState;
import br.furb.inf.tcc.tankcoders.scene.tank.ITank;

import com.jme.input.ChaseCamera;
import com.jme.input.InputHandler;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.InputActionInterface;

/**
 * Listener for change camera target.
 * @author Germano Fronza
 */
public class ActionChangeCameraTarget implements InputActionInterface {

	private InputHandler input;
	private MasPlayerInGameState masInGameState;	
	private Map<String, ITank> tanks;
	private Player localPlayer;
	private int currentTargetIndex;
	
	public ActionChangeCameraTarget(Map<String, ITank> tanks, Player localPlayer, MasPlayerInGameState masInGameState, InputHandler input) {
		this.masInGameState = masInGameState;
		this.input = input;
		this.tanks = tanks;
		this.localPlayer = localPlayer;
		this.currentTargetIndex = 0;
	}
	
	/**
	 * Change the chase camera target.
	 */
	public void performAction(InputActionEvent event) {
		if (event.getTriggerPressed()) {
			List<PlayerTank> pTanks = localPlayer.getTanks();
			
			// find the new index.
			int nextTargetIndex = this.currentTargetIndex + 1;
			if (nextTargetIndex == pTanks.size()) {
				nextTargetIndex = 0;
			}
			
			// set tank as new camera target tank instance.
			ITank nextTankTarget = tanks.get(pTanks.get(nextTargetIndex).getTankName());
			masInGameState.setCameraTargetTank(nextTankTarget, true);
			
			// set main node of the tank as new chase camera target.
			((ChaseCamera)input).setTarget(nextTankTarget.getMainNode());
			
			// define the new current target index.
			this.currentTargetIndex = nextTargetIndex;
		}
	}

}
