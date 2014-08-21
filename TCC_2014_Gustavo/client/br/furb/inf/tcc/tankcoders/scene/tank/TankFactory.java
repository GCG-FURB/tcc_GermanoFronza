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

import jason.infra.centralised.RunCentralisedMAS;
import br.furb.inf.tcc.tankcoders.game.PlayerTank;
import br.furb.inf.tcc.tankcoders.jason.AgentRepository;
import br.furb.inf.tcc.tankcoders.jason.TankAgArch;
import br.furb.inf.tcc.tankcoders.scene.tank.m1abrams.M1AbramsTank;
import br.furb.inf.tcc.tankcoders.scene.tank.panther.JadgPantherTank;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmex.physics.PhysicsSpace;

/**
 * This class is responsable by creation of tank instances.
 * @author Germano Fronza
 */
public class TankFactory {

	public static ITank makeTank(short playerIdOwner, PlayerTank tank, PhysicsSpace pSpace, Vector3f location, final boolean remoteTank, final boolean masPlayer) {
		String tankName = tank.getTankName();
		Node tankNode = null;
		switch (tank.getModel()) {
			case M1_ABRAMS: tankNode = new M1AbramsTank(playerIdOwner, tankName, tank.getTeam(), pSpace, location, remoteTank, masPlayer); break;
			case JADGE_PANTHER: tankNode = new JadgPantherTank(playerIdOwner, tankName, tank.getTeam(), pSpace, location, remoteTank, masPlayer); break;
		}
		
		ITank tankObj = (ITank)tankNode;
		
		// check if player owner is local and is a MAS player.
		if (!remoteTank && masPlayer) {
			// extract agArch instance from the centralised MAS.
			TankAgArch agArch = (TankAgArch)RunCentralisedMAS.getRunner().getAg(tankName).getUserAgArch();
			// inject instance of the agent in the customized class of AgArch
			agArch.setTankInstance(tankObj);
			// put agent architecture in the repository map. 
			AgentRepository.putTank(tankName, agArch);
		}
		
		return tankObj;
	}
}
