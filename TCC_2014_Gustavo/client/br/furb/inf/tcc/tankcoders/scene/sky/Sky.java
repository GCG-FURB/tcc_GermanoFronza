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
package br.furb.inf.tcc.tankcoders.scene.sky;

import br.furb.inf.tcc.util.scene.TextureUtils;

import com.jme.image.Texture;
import com.jme.renderer.Camera;
import com.jme.scene.Node;
import com.jme.scene.Skybox;

/**
 * Skybox to represents the sky of the world.
 * @author Germano Fronza
 */
public class Sky extends Node {
	
	private static final long serialVersionUID = 1L;
	private Skybox skybox;
	private Camera camera;
	
	/**
	 * Creates a new Sky.
	 * @param Camera. Used to set skybox location based on camera location.
	 */
	public Sky(Camera camera) {
		super("ceu");
		this.camera = camera;
		// criando o SkyBox
		skybox = new Skybox("skybox", 200, 200, 200);

		// carregando as texturas
	    Texture norte = TextureUtils.loadTexture("data/texture/skybox/north.png");
	    Texture sul = TextureUtils.loadTexture("data/texture/skybox/south.png");
	    Texture leste = TextureUtils.loadTexture("data/texture/skybox/east.png");
	    Texture oeste = TextureUtils.loadTexture("data/texture/skybox/west.png");
	    Texture topo = TextureUtils.loadTexture("data/texture/skybox/top.png");
	    Texture baixo = TextureUtils.loadTexture("data/texture/skybox/bottom.jpg");

	    // vinculando as texturas ao skybox
	    skybox.setTexture(Skybox.NORTH, norte);
	    skybox.setTexture(Skybox.WEST, oeste);
	    skybox.setTexture(Skybox.SOUTH, sul);
	    skybox.setTexture(Skybox.EAST, leste);
	    skybox.setTexture(Skybox.UP, topo);
	    skybox.setTexture(Skybox.DOWN, baixo);
	    skybox.preloadTextures();
	    this.attachChild(skybox);
	}

	/**
	 * Gets the SkyBox instance.
	 * @return
	 */
	public Skybox getSkybox() {
		return skybox;
	}

	/**
	 * Update the location based on camera location.
	 */
	public void update() {
		skybox.setLocalTranslation(camera.getLocation());
	}
}
