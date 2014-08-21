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

import com.jmex.terrain.util.ImageBasedHeightMap;

/**
 * Represents an Adapter to select (filter the access) some method from the class ImageBasedHeightMap.
 * @author Germano Fronza
 */
public class ImageBasedHeightMapAdapter {

	/**
	 * The original height map instance.
	 */
	private ImageBasedHeightMap ibhp;
	
	/**
	 * Creates a new adapter for height map.
	 * @param ibhp
	 */
	public ImageBasedHeightMapAdapter(ImageBasedHeightMap ibhp) {
		this.ibhp = ibhp;
	}
	
	/**
	 * Gets the entire grid of height data..
	 * @return
	 */
	public int[] getHeightMap() {
		return ibhp.getHeightMap();
	}
	
	/**
	 * Gets the scaled value at the point provided.
	 * @param x
	 * @param z
	 * @return
	 */
	public float getScaledHeightAtPoint(int x, int z) {
		return ibhp.getScaledHeightAtPoint(x, z);
	}
	
	/**
	 * Gets the height of a point that does not fall directly on the height posts.
	 * @param x
	 * @param z
	 * @return
	 */
	public float getInterpolatedHeight(float x, float z) {
		return ibhp.getInterpolatedHeight(x, z);
	}
	
	/**
	 * Gets the non-scaled value at the point provided.
	 * @param x
	 * @param z
	 * @return
	 */
	public float getTrueHeightAtPoint(int x, int z) {
		return ibhp.getTrueHeightAtPoint(x, z);
	}
	
	/**
	 * Gets the size of one side the height map.
	 * @return
	 */
	public int getSize() {
		return ibhp.getSize();
	}
}
