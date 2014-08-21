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

import java.util.logging.Level;

import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.Text;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.util.LoggingSystem;
import com.jmex.game.StandardGame;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameStateManager;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.util.states.PhysicsGameState;

public class TestObjects extends DebugGameState {
	
	public DynamicPhysicsNode dynamicNode;
	public StaticPhysicsNode staticNode;
	
	public boolean alreadyShot = false;
	
	public void update(float arg0) {
		super.update(arg0);
		
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("shot", true) && !alreadyShot) {
//			MainGunBullet bullet = new PantherMainGunBullet(dynamicNode, new Vector3f(0, 2, 2));
//			staticNode.attachChild(bullet);
//			bullet.getBulletDyn().addForce(Vector3f.UNIT_Z.mult(80000));
//			alreadyShot = true;
		}
	}
	
	public static void main(String[] args) throws Exception {
		LoggingSystem.getLogger().setLevel(Level.WARNING); // to see the important stuff

		// Create and start the OpenGL thread.
		final StandardGame game = new StandardGame("Physics tutorial");
		game.start();

		// Create the debug game state. This gives us some basic jME functions useful for
		// demonstrations, such as a root node and some keyboard controls.
		// It also draws the scene, which is somewhat necessary for seeing what's going on.
		final TestObjects debugGameState = new TestObjects();
		
		// Create the PhysicsGameState object. This will track all state related to the physics
		// interactions.
		final PhysicsGameState physicsGameState = new PhysicsGameState("Physics tutorial");

		// Now let jME know about the game states we've created.
		GameStateManager.getInstance().attachChild(debugGameState);
		debugGameState.setActive(true);

		GameStateManager.getInstance().attachChild(physicsGameState);
		physicsGameState.setActive(true);

		// first we will create the floor
		// as the floor can't move we create a _static_ physics node
		StaticPhysicsNode staticNode = physicsGameState.getPhysicsSpace().createStaticNode();
		debugGameState.staticNode = staticNode;
		
		// attach the node to the root node to have it updated each frameit
		debugGameState.getRootNode().attachChild(staticNode);

		// now we do not create a collision geometry but a visual box
		final Box visualFloorBox = new Box("floor", new Vector3f(), 5, 0.25f, 5);
		visualFloorBox.setSolidColor(ColorRGBA.brown);
		// note: we have used the constructor (name, center, xExtent, yExtent, zExtent)
		// thus our box is centered at (0,0,0) and has size (10, 0.5f, 10)

		// we have to attach it to our node
		staticNode.attachChild(visualFloorBox);

		// now we let jME Physics 2 generate the collision geometry for our box
		staticNode.generatePhysicsGeometry();

		// second we create a box that should fall down on the floor
		// as the new box should move we create a _dynamic_ physics node
		DynamicPhysicsNode dynamicNode = physicsGameState.getPhysicsSpace().createDynamicNode();
		debugGameState.dynamicNode = dynamicNode;
		
		debugGameState.getRootNode().attachChild(dynamicNode);

		// again we create a visual box
		final Box visualFallingBox = new Box("falling box", new Vector3f(), 0.5f, 0.5f, 0.5f);
		visualFallingBox.setRandomColors();
		
		// note: again we have used the constructor (name, center, xExtent, yExtent, zExtent)
		// thus our box is centered at (0,0,0) and has size (1, 1, 1)
		// the center is really important here because we want the center of the box to lie in the
		// center
		// of the dynamic physics node - which is the center of gravity!

		// attach it to the dynamic node
		dynamicNode.attachChild(visualFallingBox);

		// and generate collision geometries again
		dynamicNode.generatePhysicsGeometry();

		// we have to move our dynamic node upwards such that is does not start in but above the
		// floor
		dynamicNode.getLocalTranslation().set(0, 15, 0);
		dynamicNode.getLocalRotation().fromAngleAxis(FastMath.DEG_TO_RAD*45, new Vector3f(0,1,0));
		
		Text text = Text.createDefaultTextLabel("text");
		text.setCullMode(SceneElement.CULL_ALWAYS);
		text.setTextureCombineMode(TextureState.REPLACE);
		text.setLocalTranslation(new Vector3f(0, 60, 0));
		text.print("Tanquee");
		
		Node n = new Node();
		n.attachChild(text);
		n.updateRenderState();
		
//		text.setSolidColor(ColorRGBA.red);
		debugGameState.getRootNode().attachChild(n);
		
		// note: we do not move the collision geometry but the physics node!

		// now we have visuals for the physics and don't necessarily need to activate the physics
		// debugger
		// though you can do it (V key) to see physics in the app

		// Once all this is set up, we need to update the render state on the root node.
		debugGameState.getRootNode().updateRenderState();
		
		KeyBindingManager.getKeyBindingManager().add("shot", KeyInput.KEY_LCONTROL);
	}
}