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
package br.furb.inf.tcc.sandbox;

import br.furb.inf.tcc.util.scene.ModelUtils;

import com.jmex.game.StandardGame;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameStateManager;
import com.jmex.game.state.load.TransitionGameState;

public class TestLoadingGameState {
	public static void main(String[] args) throws Exception {
		StandardGame game = new StandardGame("Test LoadingGameState");
		game.getSettings().clear();
		game.start();
		
		GameStateManager.create();
		
		// Enable DebugGameState
		DebugGameState debug = new DebugGameState();
		GameStateManager.getInstance().attachChild(debug);
		debug.setActive(true);
		
		// Create LoadingGameState and enable
		TransitionGameState loading = new TransitionGameState(debug, TestLoadingGameState.class.getClassLoader().getResource("data/images/pages/joingamepage.png"));
		GameStateManager.getInstance().attachChild(loading);
		loading.setActive(true);
		
		// Start our slow loading test
		String status = "Started Loading";
		for (int i = 0; i <= 100; i++) {
			if (i == 100) {
				status = "I'm Finished!";
			} else if (i > 80) {
				status = "Almost There!";
			} else if (i > 70) {
				status = "Loading Something Extremely Useful";
			} else if (i > 50) {
				status = "More Than Half-Way There!";
			} else if (i > 20) {
				status = "Loading Something That You Probably Won't Care About";
			}
			Thread.sleep(20);
			loading.setProgress(i / 100.0f, status);
		}
		
		loading.setActive(false);
		GameStateManager.getInstance().detachChild(loading);
		
		debug = new DebugGameState();
		GameStateManager.getInstance().attachChild(debug);
		debug.setActive(true);
		
		debug.getRootNode().attachChild(ModelUtils.loadJmeModel("data/model/m1abrams/chassi.jme"));
		
//		StartGameArguments gameArgs = new StartGameArguments();
		
		// sample situation
//		TankTeam teamBope = new TankTeam(PlayerTeam.TEAM_1);
//		teamBope.setName("Bope na Roçinha");
//		AvatarPlayer p = new AvatarPlayer(TankModel.M1_ABRAMS);
//		p.setName("Capitão Nascimento");
//		p.setTeam(teamBope);
//		p.setInitialSlotLocation(1);
//		
//		gameArgs.addPlayer(p);
//		gameArgs.setLocalPlayer(p);
	}
}