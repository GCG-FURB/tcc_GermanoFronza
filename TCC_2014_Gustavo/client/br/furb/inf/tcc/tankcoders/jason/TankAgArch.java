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
package br.furb.inf.tcc.tankcoders.jason;

import jason.RevisionFailedException;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Agent;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Term;

import java.util.ArrayList;
import java.util.List;

import br.furb.inf.tcc.tankcoders.jason.narch.Action;
import br.furb.inf.tcc.tankcoders.jason.narch.AnnotatedAgArch;
import br.furb.inf.tcc.tankcoders.scene.tank.ITank;
import br.furb.inf.tcc.tankcoders.scene.tank.weapon.Bullet;

/**
 * Custom implementation of Jason AgArch.
 * Each agent controls a single tank.
 * @author Germano Fronza
 */
public class TankAgArch extends AnnotatedAgArch {

	/**
	 * Tank's instance which this agent controls.
	 */
	private ITank tankInstance;
	
	/**
	 * List of current perceptions.
	 */
	private List<Literal> percepts = new ArrayList<Literal>();
	
	/**
	 * Battle already begins?
	 */
	private boolean runningBattle;
	
	/**
	 * Literal of last tank's position in the terrain.
	 */
	private Literal lastTankInfo;
	
	/**
	 * Reference to last action finished.
	 */
	private Literal lastActionFinished;
	
	@Action
	public void ahead(Term[] terms) {
		tankInstance.setAhead(getParamAsInt(0, terms));
	}
	
	@Action
	public void back(Term[] terms) {
		tankInstance.setBack(getParamAsInt(0, terms));
	}
	
	@Action
	public void dropAccelerator(Term[] terms) {
		tankInstance.dropAccelerator();
	}
	
	@Action
	public void turnRight(Term[] terms) {
		tankInstance.setTurnRightAngle(getParamAsInt(0, terms), getParamAsInt(1, terms));
	}
	
	@Action
	public void turnLeft(Term[] terms) {
		tankInstance.setTurnLeftAngle(getParamAsInt(0, terms), getParamAsInt(1, terms));
	}
	
	@Action
	public void stopTurning(Term[] terms) {
		tankInstance.stopTurningWheel();
	}
	
	@Action
	public void turnMainGunRight(Term[] terms) {
		tankInstance.setTurnMainGunRightAngle(getParamAsInt(0, terms)); // this method is only allowed for M1Abrams model.
	}
	
	@Action
	public void turnMainGunLeft(Term[] terms) {
		tankInstance.setTurnMainGunLeftAngle(getParamAsInt(0, terms)); // this method is only allowed for M1Abrams model.
	}
	
	@Action
	public void stopMainGunTurning(Term[] terms) {
		tankInstance.stopTurningMainGun(); // this method is only allowed for M1Abrams model.
	}
	
	@Action
	public void fireMainGun(Term[] terms) {
		tankInstance.addFireMainGunBullet(Bullet.LOCAL);
	}
	
	@Action
	public void fireMachineGun(Term[] terms) {
		tankInstance.addFireMachineGunBullet(Bullet.LOCAL);
	}

	/**
	 * This method is called when agent wants to execute an action.
	 */
	public void act(ActionExec action, List<ActionExec> feedback) {
		// first of all delete the last action finished from Belief Base.
		deleteLastActionFinishedBelief();
		
		super.act(action, feedback);
	}
	
	/**
	 * This method is called by agent class each cycle.
	 */
	public List<Literal> perceive() {
		List<Literal> per = super.perceive();
		if (runningBattle) {
			if (per == null) {
				per = new ArrayList<Literal>();
			}
			
			per.addAll(percepts);
			
			percepts.clear();
		}
		return per;
	}
	
	/**
	 * Adds a perception.
	 * @param Literal p
	 */
	public void addPercept(Literal p) {
		percepts.add(p); // TODO: check if I need to synchronize the access of the percepts attribute
	}
	
	/**
	 * Adds a tank informations belief.
	 * @param Literal p
	 */
	public void addInfoBel(Literal b) {
		try {
			Agent ag = getTS().getAg();
			if (lastTankInfo != null) ag.delBel(lastTankInfo);
			ag.addBel(b);
			lastTankInfo = b;
		} catch (RevisionFailedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a last action finished belief.
	 * @param Literal p
	 */
	public void addActionFinishedBel(Literal b) {
		try {
			getTS().getAg().addBel(b);
			
			lastActionFinished = b;
		} catch (RevisionFailedException e) {
			e.printStackTrace();
		}
	}
	
	private void deleteLastActionFinishedBelief() {
		if (lastActionFinished != null) {
			try {
				getTS().getAg().delBel(lastActionFinished);
			} catch (RevisionFailedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Sets the tank which this agent will to controll.
	 * @param ITank tank
	 */
	public void setTankInstance(ITank tank) {
		this.tankInstance = tank;
	}
	
	/**
	 * Gets param of an action.
	 * @param paramIndex
	 * @param action
	 * @return int
	 */
	private int getParamAsInt(int paramIndex, Term[] terms) {
		return (int)((NumberTerm)terms[paramIndex]).solve();
	}

	public void setRunningBattle(boolean runningBattle) {
		this.runningBattle = runningBattle;
	}
}
