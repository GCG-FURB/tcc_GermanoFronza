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
package br.furb.inf.tcc.sandbox;

import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingCapsule;
import com.jme.image.Texture;
import com.jme.intersection.BoundingCollisionResults;
import com.jme.intersection.CollisionResults;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * <code>TestCollision</code>
 * 
 * @author Mark Powell
 * @version $Id: TestCollision.java,v 1.26 2007/08/02 23:51:30 nca Exp $
 */
public class TestCollision extends SimpleGame {
    private static final Logger logger = Logger.getLogger(TestCollision.class
            .getName());

	private TriMesh t;

	private TriMesh t2;

	private Text text;

	private Node scene;

	private Quaternion rotQuat = new Quaternion();

	private float angle = 0;

	private Vector3f axis = new Vector3f(1, 0, 0);

	private float tInc = -40.0f;

	private float t2Inc = -10.0f;
	
	private CollisionResults results;
	
	private Node n1, n2;

	/**
	 * Entry point for the test,
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TestCollision app = new TestCollision();
		app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
		app.start();
	}

	protected void simpleUpdate() {
		if (timer.getTimePerFrame() < 1) {
			angle = angle + (timer.getTimePerFrame() * 1);
			if (angle > 360) {
				angle = 0;
			}
		}

		rotQuat.fromAngleAxis(angle, axis);

		t.setLocalRotation(rotQuat);

		t.getLocalTranslation().y += tInc * timer.getTimePerFrame();
		t2.getLocalTranslation().x += t2Inc * timer.getTimePerFrame();

		if (t.getLocalTranslation().y > 40) {
			t.getLocalTranslation().y = 40;
			tInc *= -1;
		} else if (t.getLocalTranslation().y < -40) {
			t.getLocalTranslation().y = -40;
			tInc *= -1;
		}

		if (t2.getLocalTranslation().x > 40) {
			t2.getLocalTranslation().x = 40;
			t2Inc *= -1;
		} else if (t2.getLocalTranslation().x < -40) {
			t2.getLocalTranslation().x = -40;
			t2Inc *= -1;
		}

		
		results.clear();
		n1.calculateCollisions(scene, results);
		
		if(n1.hasCollision(scene, false)) {
			logger.info("hasCollision also reports true");
		}
	}

	/**
	 * builds the trimesh.
	 * 
	 * @see com.jme.app.SimpleGame#initGame()
	 */
	protected void simpleInitGame() {
		results = new BoundingCollisionResults();
		
		display.setTitle("Collision Detection");
		cam.setLocation(new Vector3f(0.0f, 0.0f, 75.0f));
		cam.update();

		text = new Text("Text Label", "Collision: No");
		text.setLocalTranslation(new Vector3f(1, 60, 0));
		fpsNode.attachChild(text);

		scene = new Node("3D Scene Root");

		Vector3f max = new Vector3f(5, 5, 5);
		Vector3f min = new Vector3f(-5, -5, -5);

		n1 = new Node("Node 1");
		n2 = new Node("Node 2");
		
		t = new Box("Box 1", min, max);
		t.setModelBound(new BoundingCapsule());
		t.updateModelBound();
		t.setLocalTranslation(new Vector3f(0, 30, 0));
        t.setLocalScale(new Vector3f(1,2,3));

		t2 = new Box("Box 2", min, max);
		t2.setModelBound(new BoundingCapsule());
		t2.updateModelBound();
		t2.setLocalTranslation(new Vector3f(30, 0, 0));
		n1.attachChild(t);
		n2.attachChild(t2);
		scene.attachChild(n1);
		scene.attachChild(n2);

		TextureState ts = display.getRenderer().createTextureState();
		ts.setEnabled(true);
		ts.setTexture(TextureManager.loadTexture(
				TestCollision.class.getClassLoader().getResource(
						"jmetest/data/images/Monkey.jpg"), Texture.MM_LINEAR,
				Texture.FM_LINEAR));

		scene.setRenderState(ts);
		rootNode.attachChild(scene);
	}
}