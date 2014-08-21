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
package br.furb.inf.tcc.tankcoders.scene.flag;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.math.spring.SpringPointForce;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.effects.cloth.ClothUtils;
import com.jmex.effects.cloth.CollidingClothPatch;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.material.Material;

/**
 * Represents a dynamic team headquarter flag.
 * @author Germano Fronza
 */
public class HeadquarterFlag extends Node {

	private static final long serialVersionUID = 1L;
	private ColorRGBA color;
	private Vector3f location;
	private CollidingClothPatch cloth;
	private float windStrength = 10f;
	private Vector3f windDirection = new Vector3f(.3f, 0f, .8f);
	private SpringPointForce wind, gravity, drag;
	private StaticPhysicsNode staticNode;
	
	public HeadquarterFlag(PhysicsSpace pSpace, ColorRGBA color, Vector3f location, boolean rotate) {
		this.color = color;
		this.location = location;
	
		makeStaticNode(pSpace);
		makePillars();
		makeFlag();
		
		if (rotate) {
			staticNode.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * 180, new Vector3f(0,1,0)));
		}
	}

	private void makeStaticNode(PhysicsSpace pSpace) {
		staticNode = pSpace.createStaticNode();
		staticNode.setLocalTranslation(location);
		staticNode.generatePhysicsGeometry(true);
		staticNode.setMaterial( Material.WOOD );
		staticNode.setModelBound(new BoundingBox());
		staticNode.updateModelBound();
		staticNode.setLocalScale(6f);
		this.attachChild(staticNode);
	}

	private void makePillars() {
		Box pillarOne = new Box("pillarOne", new Vector3f(), .15f, 6, .15f);
		pillarOne.setSolidColor(ColorRGBA.brown);
		pillarOne.setLightCombineMode(LightState.OFF);
		staticNode.attachChild(pillarOne);
		
		Box pillarTwo = new Box("pillarTwo", new Vector3f(6,0,0), .15f, 6, .15f);
		pillarTwo.setSolidColor(ColorRGBA.brown);
		pillarTwo.setLightCombineMode(LightState.OFF);
		staticNode.attachChild(pillarTwo);
		
		Box top = new Box("top", new Vector3f(3f,5,0), 6, .15f, .15f);
		top.setSolidColor(ColorRGBA.brown);
		top.setLightCombineMode(LightState.OFF);
		staticNode.attachChild(top);
	}
	
	private void makeFlag() {
		cloth = new CollidingClothPatch("cloth", 50, 50, 1f, 10);
		wind = ClothUtils.createBasicWind(windStrength, windDirection, true);
		cloth.addForce(wind);
		gravity = ClothUtils.createBasicGravity();
		cloth.addForce(gravity);
		drag = ClothUtils.createBasicDrag(20f);
		cloth.addForce(drag);
		cloth.setLocalScale(new Vector3f(.12f, .14f, .12f));
		cloth.setLocalTranslation(new Vector3f(3f, 1.3f, 0));
		
		String flagRsrcStr;
		if (color == ColorRGBA.blue) {
			flagRsrcStr = "data/texture/flag/blue_flag.png";
		}
		else {
			flagRsrcStr = "data/texture/flag/red_flag.png";
		}
		
		TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		ts.setTexture(
			TextureManager.loadTexture(
			HeadquarterFlag.class.getClassLoader().getResource(
			flagRsrcStr),
			Texture.MM_LINEAR_LINEAR,
			Texture.FM_LINEAR));
		cloth.setRenderState(ts);
		
		staticNode.attachChild(cloth);
		for (int i = 0; i < 50; i++) {
			cloth.getSystem().getNode(i).position.x *= .8f;
			cloth.getSystem().getNode(i).setMass(Float.POSITIVE_INFINITY);
		}
	}

	public ColorRGBA getColor() {
		return color;
	}

	public void setColor(ColorRGBA color) {
		this.color = color;
	}

	public Vector3f getLocation() {
		return location;
	}

	public void setLocation(Vector3f location) {
		this.location = location;
	}
}
