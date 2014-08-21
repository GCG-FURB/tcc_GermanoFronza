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

import java.io.Serializable;

import br.furb.inf.tcc.util.exception.InvalidInitialSlotLocationException;

public class PlayerTank implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Name of the tank.
	 */
	private String tankName;
	
	/**
	 * Model of the tank.
	 */
	private TankModel model;
	
	/**
	 * Team of the tank.
	 */
	private TankTeam team;
	
	/**
	 * To know if tank is alive or dead.
	 */
	private boolean alive = true;
	
	/**
	 * Initial slot location of the tank in terrain map.
	 */
	private int initialSlotLocation = -1; // invalid value

	public String getTankName() {
		return tankName;
	}

	public void setTankName(String tankName) {
		this.tankName = tankName;
	}

	public TankModel getModel() {
		return model;
	}

	public void setModel(TankModel model) {
		this.model = model;
	}

	public TankTeam getTeam() {
		return team;
	}

	public void setTeam(TankTeam team) {
		this.team = team;
	}
	
	public int getInitialSlotLocation() {
		return initialSlotLocation;
	}

	/**
	 * Initial slot location in the terrain.
	 * PS: The value must be between 0 and (GameRulesConstants.MAX_TANKS-1).
	 * @param initialSlotPosition
	 */
	public void setInitialSlotLocation(int initialSlotPosition) {
		if (initialSlotPosition < 0 || initialSlotPosition > (GameRulesConstants.MAX_TANKS-1)) {
			throw new InvalidInitialSlotLocationException(initialSlotPosition);
		}
		this.initialSlotLocation = initialSlotPosition;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}
