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
import java.util.List;

/**
 * Represents an abstract player.
 * @author Germano Fronza
 */
public class Player {

	/**
	 * Player ID. Generated by JGN.
	 */
	private short id;
	
	/**
	 * Player Name. User input.
	 */
	private String name;
	
	/**
	 * Player Type: Avatar or MAS.
	 */
	private PlayerType type;
	
	/**
	 * Tanks of this player.
	 */
	private List<PlayerTank> tanks;
	
	public Player(PlayerType type) {
		this.type = type;
		this.tanks = new ArrayList<PlayerTank>();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public PlayerType getType() {
		return type;
	}
	
	public List<PlayerTank> getTanks() {
		return tanks;
	}
	
	public PlayerTank getTankByName(String tankName) {
		for (PlayerTank tank : tanks) {
			if (tank.getTankName().equals(tankName)) {
				return tank;
			}
		}
		
		return null;
	}

	public void addTank(PlayerTank tank) {
		tanks.add(tank);
	}
	
	public void removeTank(String tankName) {
		for (PlayerTank tank : tanks) {
			if (tank.getTankName().equals(tankName)) {
				tanks.remove(tank);
				break;
			}
		}
	}

	public short getId() {
		return id;
	}

	public void setId(short id) {
		this.id = id;
	}
	
	public String toString() {
		return "Player id: " + id + ", Name: " + name + ", Qty. tanks: " + tanks.size();
	}
}
