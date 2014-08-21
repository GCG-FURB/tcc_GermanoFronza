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
package br.furb.inf.tcc.util.scene;

import com.jme.image.Texture;
import com.jme.util.TextureManager;

/**
 * This class contain some base methods for texture manipulations.
 * @author Erick B Passos (Adapted by Germano Fronza)
 * @version 1.0.0.0 
 */
public class TextureUtils {

	private static Texture blueTexture;
	private static Texture redTexture;
	
	public static Texture getBlueTexture() {
		if (blueTexture == null) {
			blueTexture = TextureManager.loadTexture(
					TextureUtils.class.getClassLoader().getResource("data/texture/color/blue.png"), 
	                Texture.MM_LINEAR_LINEAR, 
	                Texture.FM_LINEAR);
		}
		
		return blueTexture;
	}
	
	public static Texture getRedTexture() {
		if (redTexture == null) {
			redTexture = TextureManager.loadTexture(
					TextureUtils.class.getClassLoader().getResource("data/texture/color/red.png"), 
	                Texture.MM_LINEAR_LINEAR, 
	                Texture.FM_LINEAR);
		}
		
		return redTexture;
	}
	
	/**
	 * Loads a texture image.
	 * @param filePathSrc Path and file name of the texture image file.
	 * @return Texture Texture created object.
	 */
	public static Texture loadTexture(String filePathSrc){
		Texture textura = TextureManager.loadTexture(TextureUtils.class.getClassLoader().getResource(filePathSrc),Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR);
		textura.setApply(Texture.AM_COMBINE);
		configTexture(textura);
		return textura;
	}

	/**
	 * Configure a texture.
	 * @param Texture texture object to be configured.
	 */
	public static void configTexture(Texture textura) {
		textura.setCombineFuncRGB(Texture.ACF_MODULATE);
		textura.setCombineSrc0RGB(Texture.ACS_TEXTURE);
		textura.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
		textura.setCombineSrc1RGB(Texture.ACS_PREVIOUS);
		textura.setCombineOp1RGB(Texture.ACO_SRC_COLOR);
		textura.setCombineScaleRGB(1.0f);
	}
}
