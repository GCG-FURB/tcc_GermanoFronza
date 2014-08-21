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
package br.furb.inf.tcc.tankcoders.message;

import java.util.List;

import br.furb.inf.tcc.tankcoders.game.Player;
import br.furb.inf.tcc.tankcoders.game.PlayerTank;
import br.furb.inf.tcc.tankcoders.game.PlayerType;

/**
 * Message sent to the game server when user logon.
 * @author Germano Fronza
 */
public class UserLogon extends AbstractGameMessage {

	/**
	 * Name of the player (not tank name)
	 */
	private String playerName;
	
	/**
	 * Type of the player.
	 */
	private PlayerType playerType;
	
	/**
	 * Names of all tanks of this player.
	 */
	private PlayerTank[] tanks;

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public PlayerTank[] getTanks() {
		return tanks;
	}

	public void setTanks(PlayerTank[] tanks) {
		this.tanks = tanks;
	}
	
	public void loadByPlayer(Player p) {
		this.playerName = p.getName();
		this.playerType = p.getType();
		
		List<PlayerTank> tanks = p.getTanks();
		this.tanks = new PlayerTank[tanks.size()];
		for (int i = 0; i < tanks.size(); i++) {
			this.tanks[i] = tanks.get(i);
		}
	}

	public PlayerType getPlayerType() {
		return playerType;
	}

	public void setPlayerType(PlayerType playerType) {
		this.playerType = playerType;
	}
}
