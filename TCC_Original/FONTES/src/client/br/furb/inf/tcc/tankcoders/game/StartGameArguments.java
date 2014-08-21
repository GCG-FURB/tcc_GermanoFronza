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
package br.furb.inf.tcc.tankcoders.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.furb.inf.tcc.util.exception.PlayerNameIsAlreadyUsedException;
import br.furb.inf.tcc.util.exception.PlayerNameNullPointerException;
import br.furb.inf.tcc.util.exception.PlayerNotFoundException;
import br.furb.inf.tcc.util.exception.PlayerTeamNullPointerException;
import br.furb.inf.tcc.util.exception.PlayerWithoutInitialSlotException;
import br.furb.inf.tcc.util.exception.TanksCountExceededException;

/**
 * Arguments for start of the game.
 * @author Germano Fronza
 */
public class StartGameArguments {

	/**
	 * Name of the battle.
	 */
	private String battleName;
	
	/**
	 * Height-map image used to create the terrain level.
	 */
	private String terrainHeightMapImage;
	
	/**
	 * All player of the battle.
	 */
	private Map<String, Player> players;
	
	/**
	 * Instance's copy of the local player.
	 */
	private Player localPlayer;
	
	/**
	 * Current count tanks.
	 */
	private int tanksCount;

	public StartGameArguments() {
		this.battleName = "TankCoders battle";
		this.players = new HashMap<String, Player>();
		
		// TODO: remove this hardcode
		this.terrainHeightMapImage = "data/texture/terrain/terrain.png";
	}

	public String getBattleName() {
		return battleName;
	}

	public void setBattleName(String battleName) {
		this.battleName = battleName;
	}
	
	/**
	 * Add a player in the map.
	 * @param p
	 * @throws <b>unchecked</b> PlayerNameNullPointerException, PlayerTeamNullPointerException, TanksCountExceededException
	 */
	public void addPlayer(Player p) {
		if (p.getName() == null) {
			throw new PlayerNameNullPointerException();
		}
		
		if (players.containsKey(p.getName())) {
			throw new PlayerNameIsAlreadyUsedException(p.getName());
		}
		
		for (PlayerTank tank : p.getTanks()) {
			// can enter one more tank?
			if (tanksCount+1 > GameRulesConstants.MAX_TANKS) {
				throw new TanksCountExceededException(p.getName());
			}
			
			// is in a team
			TankTeam tt = tank.getTeam();
			if (tt == null) {
				throw new PlayerTeamNullPointerException(p.getName());
			}
			
			// has an initial slot location?
			if (tank.getInitialSlotLocation() == -1) {
				throw new PlayerWithoutInitialSlotException(p.getName());
			}
			
			tanksCount++;
		}
		
		this.players.put(p.getName(), p);
	}
	
	/**
	 * Gets a player with the given name.
	 * @param playerName
	 * @return Player
	 * @throws <b><u>unchecked</u></b> PlayerNotFoundException
	 */
	public Player getPlayer(String playerName) {
		Player p = this.players.get(playerName);
		if (p != null) {
			return p;
		}
		else {
			throw new PlayerNotFoundException(playerName);
		}
	}
	
	public Map<String, Player> getPlayersMap() {
		return this.players;
	}
	
	public Iterator<Player> getPlayersIterator() {
		return this.players.values().iterator();
	}
	
	public Iterator<String> getPlayersNamesIterator() {
		return this.players.keySet().iterator();
	}
	
	public Player getLocalPlayer() {
		return localPlayer;
	}

	public void setLocalPlayer(Player localPlayer) {
		this.localPlayer = localPlayer;
	}	
	
	/**
	 * Gets a list of tanks (AvatarPlayer || AgentAvatarPlayer) 
	 * @param team
	 * @return
	 */
	public List<PlayerTank> getTanksByTeam(PlayerTeam team) {
		List<PlayerTank> players = new ArrayList<PlayerTank>();
		
		for (Player p : this.players.values()) {
			for (PlayerTank tank : p.getTanks()) {
				if (tank.getTeam().getTeamEnum() == team) {
					players.add(tank);
				}
			}
		}
		
		return players;
	}

	public String getTerrainHeightMapImage() {
		return terrainHeightMapImage;
	}

	public void setTerrainHeightMapImage(String terrainHeightMapImage) {
		this.terrainHeightMapImage = "data/texture/terrain/" + terrainHeightMapImage;
	}
}
