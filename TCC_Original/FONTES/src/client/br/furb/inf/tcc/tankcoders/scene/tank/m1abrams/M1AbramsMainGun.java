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
package br.furb.inf.tcc.tankcoders.scene.tank.m1abrams;

import br.furb.inf.tcc.tankcoders.scene.tank.weapon.maingun.MainGun;

import com.jme.math.Vector3f;
import com.jme.scene.Node;

/**
 * 
 * @author Germano Fronza
 */
public class M1AbramsMainGun extends MainGun {

	private static final long serialVersionUID = 1L;
	private static final float MAINGUN_MASS = 100;
	private static final float TURN_ACCELERATION = 15;
	private static final float TURN_SPEED = .5f;
	private static final float MAX_TURN = 1.2f;
	private static final String MAINGUN_MODEL_PATH = "data/model/m1abrams/main_gun.obj";

	public M1AbramsMainGun(Node mainNode, Vector3f relativePosition) {
		super(mainNode, relativePosition);
	}

	protected float getMainGunMass() {
		return MAINGUN_MASS;
	}

	protected float getMaxTurn() {
		return MAX_TURN;
	}

	protected String getModelFilePath() {
		return MAINGUN_MODEL_PATH;
	}

	protected float getTurnAcceleration() {
		return TURN_ACCELERATION;
	}
	
	protected float getTurnSpeed() {
		return TURN_SPEED;
	}

}
