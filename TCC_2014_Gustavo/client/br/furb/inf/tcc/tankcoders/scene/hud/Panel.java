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
package br.furb.inf.tcc.tankcoders.scene.hud;


import br.furb.inf.tcc.tankcoders.scene.InGameState;
import br.furb.inf.tcc.tankcoders.scene.battle.Stopwatch;
import br.furb.inf.tcc.tankcoders.scene.tank.ITank;
import br.furb.inf.tcc.util.scene.RenderStatesUtils;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.Text;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;

/**
 * Panel for show some informations about the current battle and about the tank.
 * @author Germano Fronza
 */
public class Panel extends Node {

	private static final long serialVersionUID = 1L;
	private Text textSpeedometer, textTankName, textMessage, textWaitingOtherPlayers, textTime, textPrimaryGunBullets, textHealth;
	private StringBuffer speed = new StringBuffer(30);
	private StringBuffer tankName = new StringBuffer(30);
	private StringBuffer message = new StringBuffer(80);
	private StringBuffer time = new StringBuffer(30);
	private StringBuffer primaryGunBullets = new StringBuffer(30);
	private StringBuffer health = new StringBuffer(30);
	
	private int framesCountMessage;

	private InGameState inGameState;

	/**
	 * Constructor of the HUD.
	 * @param inGameState
	 */
	public Panel(InGameState inGameState, String battleName) {
		super("hud");
		this.inGameState = inGameState;
		
		makeTransparency();
		makeLabels();
		
		updateRenderState();
	}

	/**
	 * Creates labels for show informations.
	 */
	private void makeLabels() {
		textTime = Text.createDefaultTextLabel("time");
		textTime.setCullMode(SceneElement.CULL_NEVER);
		textTime.setTextureCombineMode(TextureState.REPLACE);
		textTime.setLocalTranslation(new Vector3f(0, 20, 0));
		this.attachChild(textTime);
		
		textTankName = Text.createDefaultTextLabel("tankName");
		textTankName.setCullMode(SceneElement.CULL_NEVER);
		textTankName.setTextureCombineMode(TextureState.REPLACE);
		textTankName.setLightCombineMode(LightState.OFF);
		textTankName.setLocalTranslation(new Vector3f(0, DisplaySystem.getDisplaySystem().getHeight()-20, 0));
		this.attachChild(textTankName);

		textMessage = Text.createDefaultTextLabel("message");
		textMessage.setCullMode(SceneElement.CULL_NEVER);
		textMessage.setTextureCombineMode(TextureState.REPLACE);
		textMessage.setLightCombineMode(LightState.OFF);
		textMessage.setLocalTranslation(new Vector3f(240, DisplaySystem.getDisplaySystem().getHeight()-20, 0));
		this.attachChild(textMessage);
		
		textWaitingOtherPlayers = Text.createDefaultTextLabel("textWaitPlayers");
		textWaitingOtherPlayers.setCullMode(SceneElement.CULL_NEVER);
		textWaitingOtherPlayers.setTextureCombineMode(TextureState.REPLACE);
		textWaitingOtherPlayers.setLightCombineMode(LightState.OFF);
		textWaitingOtherPlayers.setLocalTranslation(new Vector3f(240, DisplaySystem.getDisplaySystem().getHeight()/2, 0));
		this.attachChild(textWaitingOtherPlayers);
		
		textSpeedometer = Text.createDefaultTextLabel("speed");
		textSpeedometer.setCullMode(SceneElement.CULL_NEVER);
		textSpeedometer.setTextureCombineMode(TextureState.REPLACE);
		textSpeedometer.setLocalTranslation(new Vector3f(0, 40, 0));
		this.attachChild(textSpeedometer);

		textPrimaryGunBullets = Text.createDefaultTextLabel("primaryGunBullets");
		textPrimaryGunBullets.setCullMode(SceneElement.CULL_NEVER);
		textPrimaryGunBullets.setTextureCombineMode(TextureState.REPLACE);
		textPrimaryGunBullets.setLocalTranslation(new Vector3f(0, 60, 0));
		this.attachChild(textPrimaryGunBullets);
		
		System.out.println(textPrimaryGunBullets.getLightCombineMode());
		
		textHealth = Text.createDefaultTextLabel("health");
		textHealth.setCullMode(SceneElement.CULL_NEVER);
		textHealth.setTextureCombineMode(TextureState.REPLACE);
		textHealth.setLocalTranslation(new Vector3f(0, 80, 0));
		this.attachChild(textHealth);
	}

	/**
	 * Creates a transparecy for the panel.
	 */
	private void makeTransparency() {
		Quad hudQuad = new Quad("hudQuad", DisplaySystem.getDisplaySystem().getWidth(), 110);
		hudQuad.getLocalTranslation().addLocal(new Vector3f(DisplaySystem.getDisplaySystem().getWidth() / 2, 48, 0));
		hudQuad.setDefaultColor(new ColorRGBA(0, 0, 0, 0.5f));
		hudQuad.setLightCombineMode(LightState.OFF);
		hudQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		hudQuad.setRenderState(RenderStatesUtils.createTransparency());
		hudQuad.updateRenderState();
		this.attachChild(hudQuad);
	}

	/**
	 * Prepare to show the informations.
	 */
	public void update() {	
		ITank tank = inGameState.getCameraTargetTank();
		
		time.setLength(0);
		time.append("Time: " + Stopwatch.getInstance().getFormattedTime());
		textTime.print(time);
		
		speed.setLength(0);
		speed.append("Speed: " + tank.getSpeed() + " km/h");
		textSpeedometer.print(speed);
		
		int qtyBullets = tank.getMainGunQtyBullets();
		primaryGunBullets.setLength(0);
		primaryGunBullets.append("Primary Gun: " + qtyBullets + " bullets");
		textPrimaryGunBullets.print(primaryGunBullets);
		
		health.setLength(0);
		health.append("Health: " + tank.getHealth() + "%");
		textHealth.print(health);
		
		if (message.length() > 0) {
			textMessage.print(message);
			textMessage.setTextColor(ColorRGBA.white);
			
			framesCountMessage++;
			if (framesCountMessage == 60) {
				message.setLength(0);
				textMessage.print("");
			}
		}
		
		if (textWaitingOtherPlayers != null) {
			textWaitingOtherPlayers.print("Waiting the other players");
		}
	}
	
	public void updateTankName() {
		ITank tank = inGameState.getCameraTargetTank();
		
		tankName.setLength(0);
		tankName.append(tank.getTankName());
		textTankName.print(tankName);
		textTankName.setTextColor(tank.getTeam().getColor());
	}
	
	public void showMessage(String msg) {
		message.setLength(0);
		if (msg != null) {
			message.append("Info: " + msg);
			framesCountMessage = 0;
		}
	}
	
	public void removeWaitingOtherPlayersMessage() {
		this.detachChild(textWaitingOtherPlayers);
		textWaitingOtherPlayers = null;
	}
}
