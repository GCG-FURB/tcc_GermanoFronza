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
package br.furb.inf.tcc.tankcoders.scene.camera;

import java.util.HashMap;

import com.jme.input.ChaseCamera;
import com.jme.input.thirdperson.ThirdPersonMouseLook;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Spatial;
/**
 * Customized version of the ChaseCamera.
 * @author Germano Fronza
 */
public class CustomChaseCamera extends ChaseCamera {

	/**
	 * Factory method for CustomChaseCamera.
	 * Sets some initial properties of restrictions before instance the ChaseCamera
	 * @param camera to be updated.
	 * @param cameraTarget Target that camera should folow up.
	 * @return ChaseChamera
	 */
	@SuppressWarnings("unchecked")
	public static ChaseCamera getInstance(Camera camera, Spatial cameraTarget){
		Vector3f targetOffset = new Vector3f();
        targetOffset.y = 3;
        
        HashMap properties = new HashMap();
        properties.put(ThirdPersonMouseLook.PROP_MAXROLLOUT, "80");
        properties.put(ThirdPersonMouseLook.PROP_MINROLLOUT, "25");
        properties.put(ChaseCamera.PROP_TARGETOFFSET, targetOffset);
        properties.put(ThirdPersonMouseLook.PROP_MAXASCENT, ""+45 * FastMath.DEG_TO_RAD);
        properties.put(ThirdPersonMouseLook.PROP_MINASCENT, ""+10 * FastMath.DEG_TO_RAD);
        properties.put(ChaseCamera.PROP_INITIALSPHERECOORDS, new Vector3f(20, 0, 30 * FastMath.DEG_TO_RAD));
        properties.put(ChaseCamera.PROP_TARGETOFFSET, targetOffset);
        CustomChaseCamera input = new CustomChaseCamera(camera, cameraTarget, properties);
        return input;
	}
	/**
	 * @param camera
	 * @param cameraTarget
	 * @param properties
	 */
	@SuppressWarnings("unchecked")
	private CustomChaseCamera(Camera camera, Spatial cameraTarget, HashMap properties) {
		super(camera, cameraTarget, properties);
	}
	
	/**
	 * Not used.
	 */
	@Override
	public void update(float tpf) {
	}
	
	/**
	 * Custum update method.
	 * @param tpf
	 */
	public void updateLinkedToPhysics(float tpf){
		super.update(tpf);
	}

}
