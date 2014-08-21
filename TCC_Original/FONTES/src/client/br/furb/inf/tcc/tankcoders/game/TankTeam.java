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

import com.jme.renderer.ColorRGBA;

/**
 * JavaBean.
 * Represents a tank team.
 * @author Germano Fronza
 */
public class TankTeam implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Teams enum.
	 */
	private PlayerTeam team;
	
	/**
	 * Team name.
	 */
	private String name;
	
	/**
	 * Team's color.
	 */
	private ColorRGBA color;
	
	public ColorRGBA getColor() {
		return color;
	}

	public TankTeam(PlayerTeam pTeam) {
		this.team = pTeam;
		
		if (team == PlayerTeam.TEAM_1) {
			color = ColorRGBA.blue;
		}
		else {
			color = ColorRGBA.red;
		}
	}

	public PlayerTeam getTeamEnum() {
		return team;
	}

	public void setTeam(PlayerTeam team) {
		this.team = team;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
