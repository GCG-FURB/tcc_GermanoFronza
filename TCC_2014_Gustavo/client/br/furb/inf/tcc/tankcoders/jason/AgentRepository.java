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

import jason.asSyntax.Literal;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.StringTermImpl;

import java.util.HashMap;
import java.util.Map;

import com.jme.math.Vector3f;

/**
 * This class is a central point to add perceptions to the agents.
 * Ps: All methods of this class are static.
 * @author Germano Fronza
 */
public class AgentRepository {

	/**
	 * Map that represents the relationship between tank names and architecture classes of the agents.
	 */
	private static Map<String, TankAgArch> tankAgArchs;
	
	/**
	 * Invoked when the Class Loader load this class.
	 */
	static {
		tankAgArchs = new HashMap<String, TankAgArch>();
	}
	
	/**
	 * Put a tank in the repository map.
	 * @param tankName
	 * @param agArch
	 */
	public static void putTank(String tankName, TankAgArch agArch) {
		tankAgArchs.put(tankName, agArch);
	}
	
	/**
	 * Notify the tank about his position x, y, z.
	 * @param tankName
	 * @param position
	 */
	public static void notifyTankInfo(String tankName, Vector3f p, int health, int mainGunQtyBullets, float chassiHeading, float mainGunHeading) {
		synchronized (tankAgArchs) {
			Literal info = new Literal("info");
			info.addTerms(new NumberTermImpl(p.x), new NumberTermImpl(p.y), new NumberTermImpl(p.z), 
						  new NumberTermImpl(health), new NumberTermImpl(mainGunQtyBullets),
						  new NumberTermImpl(chassiHeading), new NumberTermImpl(mainGunHeading));
			
			// add the new position as belief
			tankAgArchs.get(tankName).addInfoBel(info);
		}
	}
	
	/**
	 * Notify all agents that the battle begins.
	 */
	public static void notifyTanksStartBattle() {
		Literal startBattle = new Literal("onStartBattle");
		for (TankAgArch agArch : tankAgArchs.values()) {
			agArch.setRunningBattle(true);
			agArch.addPercept(startBattle);
		}
	}
	
	/**
	 * Notify agent that some action invoked has been finished (as belief).
	 */
	public static void notifyActionFinished(String tankName) {
		Literal actionFinished = new Literal("actionFinished");
		
		tankAgArchs.get(tankName).addActionFinishedBel(actionFinished);
	}
	
	/**
	 * Notify tank scan another tank in radar.
	 * @param tankName
	 * @param tankNameScanned
	 * @param isEnemy
	 */
	public static void notifyRadarScanTank(String tankName, String tankNameScanned, Vector3f p, boolean isEnemy) {
		if (tankAgArchs.containsKey(tankName)) {
			Literal scanTank = new Literal("onScanTank");
			scanTank.addTerms(new StringTermImpl(tankNameScanned), new NumberTermImpl((isEnemy) ? 1 : 0),
							  new NumberTermImpl(p.x), new NumberTermImpl(p.y), new NumberTermImpl(p.z));
			tankAgArchs.get(tankName).addPercept(scanTank);
		}
	}
	
	/**
	 * Notify that the tank hit another tank.
	 * @param tankName
	 * @param tankNameScanned
	 * @param isEnemy
	 */
	public static void notifyHitTank(String tankName, String tankNameScanned, boolean isEnemy) {
		if (tankAgArchs.containsKey(tankName)) {
			Literal hitTank = new Literal("onHitTank");
			hitTank.addTerms(new StringTermImpl(tankNameScanned), new NumberTermImpl((isEnemy) ? 1 : 0));
			tankAgArchs.get(tankName).addPercept(hitTank);
		}
	}
	
	/**
	 * Notify that tank's bullet hit another tank (accept friendly fire)
	 * @param tankName
	 * @param tankNameAffected
	 * @param isEnemy
	 */
	public static void notifyBulletHitTank(String tankName, String tankNameAffected, boolean isEnemy) {
		if (tankAgArchs.containsKey(tankName)) {
			Literal bulletHitTank = new Literal("onBulletHit");
			bulletHitTank.addTerms(new StringTermImpl(tankNameAffected), new NumberTermImpl((isEnemy) ? 1 : 0));
			tankAgArchs.get(tankName).addPercept(bulletHitTank);
		}
	}
	
	/**
	 * Notify that took a shot.
	 * @param tankName
	 * @param bulletPower
	 */
	public static void notifyTankHitByBullet(String tankName, int bulletPower) {
		if (tankAgArchs.containsKey(tankName)) {
			Literal hitByBullet = new Literal("onHitByBullet");
			hitByBullet.addTerms(new NumberTermImpl(bulletPower));
			tankAgArchs.get(tankName).addPercept(hitByBullet);
		}
	}
	
	/**
	 * Notify that tank's bullet hit another bullet.
	 * @param tankName
	 */
	public static void notifyBulletHitBullet(String tankName) {
		if (tankAgArchs.containsKey(tankName)) {
			Literal bulletHitBullet = new Literal("onBulletHitBullet");
			tankAgArchs.get(tankName).addPercept(bulletHitBullet);
		}
	}
	
	/**
	 * Notify that tank's bullet missed.
	 * @param tankName
	 */
	public static void notifyBulletMissed(String tankName) {
		if (tankAgArchs.containsKey(tankName)) {
			Literal bulletMissed = new Literal("onBulletMissed");
			tankAgArchs.get(tankName).addPercept(bulletMissed);
		}
	}
	
	/**
	 * Notify that tank has been killed.
	 * @param tankName
	 */
	public static void notifyTankDeath(String tankName) {
		if (tankAgArchs.containsKey(tankName)) {
			TankAgArch agArch = tankAgArchs.get(tankName);
			
			Literal bulletMissed = new Literal("onDeath");
			agArch.addPercept(bulletMissed);
			
			// this tank leaves this list.
			tankAgArchs.remove(tankName);
			
			// stop this dead agent.
			agArch.stopAg();
		}
	}
	
	/**
	 * Notify that tank has been killed.
	 * @param tankName
	 */
	public static void notifyOthersAboutThisDeath(String tankNameDead) {
		Literal tankDeath = new Literal("onTankDeath");
		tankDeath.addTerm(new StringTermImpl(tankNameDead));
		
		for (TankAgArch agArch : tankAgArchs.values()) {
			agArch.addPercept(tankDeath);
		}
	}
	
	/**
	 * Notify that tank's team wins the battle.
	 * @param tankName
	 */
	public static void notifyTeamWin(String tankName) {
		if (tankAgArchs.containsKey(tankName)) {
			Literal teamWin = new Literal("onTeamWin");
			tankAgArchs.get(tankName).addPercept(teamWin);
		}
	}
}
