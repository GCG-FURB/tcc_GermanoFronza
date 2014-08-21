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

import java.util.List;
import java.util.Map;

import br.furb.inf.tcc.tankcoders.jason.AgentRepository;
import br.furb.inf.tcc.tankcoders.scene.tank.ITank;
import br.furb.inf.tcc.tankcoders.scene.terrain.ITerrain;

import com.jme.bounding.BoundingVolume;
import com.jme.scene.Controller;

/**
 * Controls the all the projecile collisions (with tanks, another bullets and objects).
 * @author Germano Fronza
 */
public class ProjectileCollisionController extends Controller {
	
	private static final long serialVersionUID = 1L;
	private Projectile projectile;
	private List<Projectile> activeProjectiles;
	private Map<String, ITank> tanks;
	
	/**
	 * Creates a new ProjectileCollisionController.
	 * @param bullet
	 * @param terrain
	 * @param ts
	 * @param currentActiveBullets
	 */
	public ProjectileCollisionController(Projectile bullet, ITerrain terrain, Map<String, ITank> ts, List<Projectile> currentActiveBullets) {
		this.projectile = bullet;
		this.tanks = ts;
		this.activeProjectiles = currentActiveBullets;
	}
	
	/**
	 * Called each frame update.
	 * Here is checked all the possible bullet collisions.
	 */
	public void update(float time) {
		// tank which fire the bullet
		ITank tankOwner = projectile.getOwner();
		
		// if bullet is not alive then remove it from scene node.
		if (!projectile.isAlive()) {			
			projectile.removeFromScene();
			
			if (tankOwner.isAgentControls()) {
				AgentRepository.notifyBulletMissed(tankOwner.getTankName());
			}
			
			return;
		}
		
		// increase the frame count of this projectile
		projectile.incFrameCount();
		
		// world bound of the current projectile
		BoundingVolume bv = projectile.getSphereWorldBound();
		
		// check collisions with tanks (except the bullet's owner)
		for (ITank tank : tanks.values()) {
			if (tank != tankOwner) {
				if (tank.intersectsWith(bv)) {
					// notify the affected tank if this projectile can damage another tank.
					if (projectile.isCanDamage()) {
						tank.hitByBullet(projectile);
						
						if (tankOwner.isAgentControls()) {
							boolean isEnemy = tankOwner.getTeam().getTeamEnum() != tank.getTeam().getTeamEnum();
							AgentRepository.notifyBulletHitTank(tankOwner.getTankName(), tank.getTankName(), isEnemy);
						}
					}
					
					projectile.removeFromScene();
					return;
				}
			}
		}
		
		// check collisions with another projectiles
		for (Projectile projectile : activeProjectiles) {
			if (projectile != this.projectile) {
				if (bv.intersects(projectile.getSphereWorldBound())) {
					// notify the bullet
					projectile.hitByProjectile(this.projectile);
					this.projectile.removeFromScene();
					
					if (tankOwner.isAgentControls()) {
						AgentRepository.notifyBulletHitBullet(tankOwner.getTankName());
					}
					
					return;
				}
			}
		}
	}
}
