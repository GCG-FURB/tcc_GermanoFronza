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
package br.furb.inf.tcc.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.furb.inf.tcc.tankcoders.game.GameRulesConstants;
import br.furb.inf.tcc.tankcoders.game.PlayerTank;
import br.furb.inf.tcc.tankcoders.game.PlayerTeam;
import br.furb.inf.tcc.tankcoders.game.TankModel;
import br.furb.inf.tcc.tankcoders.game.TankTeam;

/**
 * Represents a new battle on the server game.
 * @author Germano Fronza
 */
public class GameBattle {

	private String battleName;
	private String team1Name;
	private String team2Name;
	private TankTeam team1Obj;
	private TankTeam team2Obj;
	private int team1MaxTanks;
	private int team2MaxTanks;
	private String terrainHeightMapImage;
	
	private Map<Short, OnlinePlayer> onlinePlayers = new HashMap<Short, OnlinePlayer>();
	private int currentTanksCount;
	private int currentTanksCountTeam1;
	private int currentTanksCountTeam2;
	
	private List<Integer> usedSlotLocations = new ArrayList<Integer>();
	
	public GameBattle() {
		team1Obj = new TankTeam(PlayerTeam.TEAM_1);
		team2Obj = new TankTeam(PlayerTeam.TEAM_2);
	}
	
	public String getBattleName() {
		return battleName;
	}
	public void setBattleName(String battleName) {
		this.battleName = battleName;
	}
	public String getTeam1Name() {
		return team1Name;
	}
	public void setTeam1Name(String team1Name) {
		this.team1Name = team1Name;
		this.team1Obj.setName(this.team1Name);
	}
	public String getTeam2Name() {
		return team2Name;
	}
	public void setTeam2Name(String team2Name) {
		this.team2Name = team2Name;
		this.team2Obj.setName(this.team2Name);
	}
	public int getTeam1MaxTanks() {
		return team1MaxTanks;
	}
	public void setTeam1MaxTanks(int team1MaxPlayers) {
		this.team1MaxTanks = team1MaxPlayers;
	}
	public int getTeam2MaxTanks() {
		return team2MaxTanks;
	}
	public void setTeam2MaxTanks(int team2MaxPlayers) {
		this.team2MaxTanks = team2MaxPlayers;
	}
	
	public OnlinePlayer getOnlinePlayerById(short id) {
		return onlinePlayers.get(id);
	}
	
	public Collection<OnlinePlayer> getAllOnlinePlayers() {
		return onlinePlayers.values();
	}
	
	public OnlinePlayer[] getAllOnlinePlayersArrayExcept(short playerId) {
		Object[] originalArray = onlinePlayers.values().toArray();
		
		OnlinePlayer[] array = new OnlinePlayer[originalArray.length-1];
		for (int i = 0, j=0; i < originalArray.length; i++) {
			if (((OnlinePlayer)originalArray[i]).getPlayerId() != playerId) {
				array[j] = (OnlinePlayer)originalArray[i];
				j++;
			}
		}
		return array;
	}
	
	public Map<Short, OnlinePlayer> getOnlinePlayersMap() {
		return onlinePlayers;
	}
	
	public void addOnlinePlayer(OnlinePlayer op) {
		onlinePlayers.put(op.getPlayerId(), op);
		
		currentTanksCount += op.getTanks().length;
		
		for (PlayerTank tank : op.getTanks()) {
			if (tank.getTeam().getTeamEnum() == PlayerTeam.TEAM_1) {
				currentTanksCountTeam1++;
			}
			else {
				currentTanksCountTeam2++;
			}
		}
	}
	
	public void removeOnlinePlayerById(short id) {
		OnlinePlayer op = onlinePlayers.get(id);
		currentTanksCount -= op.getTanks().length;
		
		for (PlayerTank tank : op.getTanks()) {
			if (tank.getTeam().getTeamEnum() == PlayerTeam.TEAM_1) {
				currentTanksCountTeam1--;
			}
			else {
				currentTanksCountTeam2--;
			}
		}
		
		onlinePlayers.remove(id);
	}
	
	public int getCurrentTanksCount() {
		return currentTanksCount;
	}

	public TankTeam getTeam1Obj() {
		return team1Obj;
	}

	public TankTeam getTeam2Obj() {
		return team2Obj;
	}

	public int getCurrentTanksCountTeam1() {
		return currentTanksCountTeam1;
	}

	public int getCurrentTanksCountTeam2() {
		return currentTanksCountTeam2;
	}
	
	public TankTeam getTeamObjByEnum(PlayerTeam pt) {
		if (pt == PlayerTeam.TEAM_1) {
			return team1Obj;
		}
		else {
			return team2Obj;
		}
	}
	
	public boolean acceptOneMoreTankInTeam(PlayerTeam pt) {
		int currentTanksCountInTeam;
		int maxTanksCountInTeam;
		if (pt == PlayerTeam.TEAM_1) {
			currentTanksCountInTeam = currentTanksCountTeam1;
			maxTanksCountInTeam = team1MaxTanks;
		}
		else {
			currentTanksCountInTeam = currentTanksCountTeam2;
			maxTanksCountInTeam = team2MaxTanks;
		}
		
		return (currentTanksCountInTeam < maxTanksCountInTeam);
	}
	
	public int changePlayerTankTeam(short playerId, String tankName, PlayerTeam newTeam) {
		OnlinePlayer op = getOnlinePlayerById(playerId);
		PlayerTank pt = op.getTankByName(tankName);
		pt.setTeam(getTeamObjByEnum(newTeam));
		pt.setInitialSlotLocation(getFreeSlotLocationInTeam(newTeam));
		
		if (newTeam == PlayerTeam.TEAM_1) {
			currentTanksCountTeam1++;
			currentTanksCountTeam2--;
		}
		else{ 
			currentTanksCountTeam2++;
			currentTanksCountTeam1--;
		}
		
		return pt.getInitialSlotLocation();
	}
	
	public void changePlayerTankModel(short playerId, String tankName, TankModel newModel) {
		OnlinePlayer op = getOnlinePlayerById(playerId);
		PlayerTank pt = op.getTankByName(tankName);
		pt.setModel(newModel);
	}
	
	public int getFreeSlotLocationInTeam(PlayerTeam team) {
		int start, end;
		if (team == PlayerTeam.TEAM_1) {
			start = 0;
			end = GameRulesConstants.MAX_TANKS/2;
		}
		else {
			start = GameRulesConstants.MAX_TANKS/2;
			end = GameRulesConstants.MAX_TANKS;
		}
		
		for (int i = start; i < end; i++) {
			if (usedSlotLocations.indexOf(i) == -1) {
				usedSlotLocations.add(i);
				return i;
			}
		}
		
		// in this case return 0 or GameRulesConstants.MAX_TANKS/2
		usedSlotLocations.add(start);
		return start;
	}

	public String getTerrainHeightMapImage() {
		return terrainHeightMapImage;
	}

	public void setTerrainHeightMapImage(String terrainHeightMapImage) {
		this.terrainHeightMapImage = terrainHeightMapImage;
	}
}
