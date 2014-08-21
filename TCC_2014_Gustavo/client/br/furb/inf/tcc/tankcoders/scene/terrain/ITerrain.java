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
package br.furb.inf.tcc.tankcoders.scene.terrain;

import com.jme.math.Vector3f;
import com.jmex.terrain.TerrainPage;

/**
 * Interface used to define new terrain implementations.
 * @author Germano Fronza
 */
public interface ITerrain {

	public abstract TerrainPage getTerrainBlock();

	public abstract float getTerrainBaseHeight();

	public abstract Vector3f[] getTanksInitialLocation();
}