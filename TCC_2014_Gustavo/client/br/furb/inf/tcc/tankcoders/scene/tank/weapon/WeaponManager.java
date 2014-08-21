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
package br.furb.inf.tcc.tankcoders.scene.tank.weapon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.furb.inf.tcc.tankcoders.scene.tank.ITank;
import br.furb.inf.tcc.tankcoders.scene.terrain.ITerrain;

import com.jme.scene.Node;

/**
 * Manage all the weapons and your bullets.
 * @author Germano Fronza
 */
public class WeaponManager {

	private static Node scene;
	private static ITerrain terrain;
	private static List<Projectile> activeProjectiles;
	private static Map<String, ITank> tanks;
	
	private WeaponManager() {}
	
	/**
	 * Setup WeaponManager based on arguments.
	 * @param s
	 * @param t
	 * @param ts
	 */
	public static void setup(Node s, ITerrain t, Map<String, ITank> ts) {
		scene = s;
		terrain = t;
		tanks = ts;
		activeProjectiles = new ArrayList<Projectile>();
	}
	
	/**
	 * Fire a bullet and attach it in the scene node.
	 * @param bullet
	 */
	public static void fireBullet(Bullet bullet) {
		// add the controller that handle collisions.
		bullet.addController(new ProjectileCollisionController(bullet, terrain, tanks, activeProjectiles));
		
		// attach the bullet in the scene node (terrain)
		scene.attachChild(bullet);
		bullet.updateRenderState();
		
		// add a force based on tank (or maingun) heading
		bullet.getBulletDyn().addForce(bullet.getVector().mult(bullet.getMultForce()));
		
		// add the bullet in array for collision detection.
		activeProjectiles.add(bullet);
	}
}
