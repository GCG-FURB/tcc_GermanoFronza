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
package br.furb.inf.tcc.tankcoders.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import br.furb.inf.tcc.tankcoders.TankCoders;
import br.furb.inf.tcc.tankcoders.game.Player;
import br.furb.inf.tcc.tankcoders.game.PlayerTank;
import br.furb.inf.tcc.tankcoders.game.PlayerTeam;
import br.furb.inf.tcc.tankcoders.game.PlayerType;
import br.furb.inf.tcc.tankcoders.game.StartGameArguments;
import br.furb.inf.tcc.tankcoders.jason.AgentRepository;
import br.furb.inf.tcc.tankcoders.message.AllPlayersAreInGameState;
import br.furb.inf.tcc.tankcoders.message.PlayerInBattle;
import br.furb.inf.tcc.tankcoders.message.PlayerLeftGame;
import br.furb.inf.tcc.tankcoders.message.TankActionShotMachineGun;
import br.furb.inf.tcc.tankcoders.message.TankActionShotMainGun;
import br.furb.inf.tcc.tankcoders.message.TankActionStopTurnMainGun;
import br.furb.inf.tcc.tankcoders.message.TankActionTurnMainGun;
import br.furb.inf.tcc.tankcoders.message.TankBulletHit;
import br.furb.inf.tcc.tankcoders.message.TankDead;
import br.furb.inf.tcc.tankcoders.message.TeamLeftGame;
import br.furb.inf.tcc.tankcoders.scene.InGameState;
import br.furb.inf.tcc.tankcoders.scene.hud.Panel;
import br.furb.inf.tcc.tankcoders.scene.tank.ITank;
import br.furb.inf.tcc.tankcoders.scene.tank.weapon.Bullet;
import br.furb.inf.tcc.util.lang.GameLanguage;

import com.captiveimagination.jgn.JGN;
import com.captiveimagination.jgn.clientserver.JGNConnection;
import com.captiveimagination.jgn.clientserver.JGNConnectionListener;
import com.captiveimagination.jgn.event.MessageListener;
import com.captiveimagination.jgn.message.Message;

/**
 * Networked Virtual Environment handler.<br/>
 * This class is a central point to send messages and to process incoming messages.
 * @author Germano Fronza
 */
public class NVEHandler {

	/**
	 * JGN GameClient instance.
	 */
	private static GameClient gameClient;
	
	/**
	 * Active players.
	 */
	private static Map<Short, Player> players;
	
	/**
	 * Instance of local player.
	 */
	private static Player localPlayer;
	
	/**
	 * Tanks of this battle.
	 */
	private static Map<String, ITank> tanks;
	
	/**
	 * Hud panel.
	 */
	private static Panel hud;
	
	/**
	 * Instance of InGameState of the game.
	 */
	private static InGameState inGameState;
	
	/**
	 * Instance of Graphical controller.
	 */
	private static SyncTankObjectManager syncObjectManager;
	
	/**
	 * Synchronization Manager of game objects.
	 */
	private static SynchronizationManager clientSyncManager;
	
	/**
	 * Setup the NVE handler.
	 */
	public static void setup(StartGameArguments gameArgs, InGameState inGameState) {		
		try {
			players = new HashMap<Short, Player>();
			for (Iterator<Player> iterator = gameArgs.getPlayersIterator(); iterator.hasNext();) {
				Player player = iterator.next();
				players.put(player.getId(), player);
			}
			
			NVEHandler.localPlayer = gameArgs.getLocalPlayer();
			NVEHandler.tanks = inGameState.getTanks();
			NVEHandler.hud = inGameState.getHud();
			NVEHandler.inGameState = inGameState;
			NVEHandler.gameClient = GameClient.getInstance();
			NVEHandler.syncObjectManager = new SyncTankObjectManager(tanks);
			
			setupListeners();
			setupSynchronizationManager();
			
			// notify the server that I change to ingame state.
			gameClient.sendToServer(new PlayerInBattle());
		} catch (Exception e) {
			// ok, no problem here.
		}
	}

	/**
	 * Creates the graphical controller to handle all remote tanks;
	 */
	private static void setupSynchronizationManager() {
		TankGraphicalController controller = new TankGraphicalController();
		
		clientSyncManager = gameClient.createSyncManager(controller);
		clientSyncManager.addSyncObjectManager(NVEHandler.syncObjectManager);
		JGN.createThread(clientSyncManager).start();
	}

	/**
	 * Setup the client listeners:
	 *  - Server connection listener
	 *  - Message listener
	 */
	private static void setupListeners() {
		gameClient.removeAllCurrentListeners();
		
		gameClient.addServerConnectionListener(
			new JGNConnectionListener() {
				public void connected(JGNConnection con) {
					// nothing to do here
				}

				public void disconnected(JGNConnection con) {
					disconnectAndReturnToJoinMenu(false, "        " + GameLanguage.getString("server.disconnected") + "        ");
				}				
			}
		);
		
		gameClient.addMessageListener(
			new MessageListener() {
				public void messageCertified(Message m) {}
				public void messageFailed(Message m) {}
				public void messageSent(Message m) {}
				public void messageReceived(Message m) {
					if (m instanceof PlayerLeftGame) {
						processPlayerLeftGameMessage(m);
					}
					else if (m instanceof TeamLeftGame) {
						processTeamLeftGameMessage(m);
					}
					else if (m instanceof AllPlayersAreInGameState) {
						processAllPlayersAreInGameStateMessage();
					}
					else if (m instanceof TankActionTurnMainGun) {
						processTankActionTurnMainGun(m);
					}
					else if (m instanceof TankActionStopTurnMainGun) {
						processTankActionStopTurnMainGun(m);
					}
					else if (m instanceof TankActionShotMainGun) {
						processTankActionShotMainGun(m);
					}
					else if (m instanceof TankActionShotMachineGun) {
						processTankActionShotMachineGun(m);
					}
					else if (m instanceof TankBulletHit) {
						processTankBulletHit(m);
					}
					else if (m instanceof TankDead) {
						processTankDead(m);
					}
				}
				
				private void processAllPlayersAreInGameStateMessage() {	
					try {
						// register all the tank of local player
						for (PlayerTank pTank : localPlayer.getTanks()) {
							ITank tank = tanks.get(pTank.getTankName());
							clientSyncManager.register(tank, tank.getSynchronizeCreateMessageImpl(), 50); // TODO: check if I can change this in runtime
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					// remove message from the hud
					hud.removeWaitingOtherPlayersMessage();
					
					// notify the inGameState that all players are in game state.
					inGameState.allPlayersAreInGameState();
				}
				private void processTankDead(Message m) {
					TankDead td = (TankDead)m;
					inGameState.killRemotePlayerTank(td.getTankName());
				}
				
				private void processTankBulletHit(Message m) {
					TankBulletHit tbh = (TankBulletHit)m;
					tanks.get(tbh.getTankName()).hitByRemoteBullet(tbh.getPower());
				}
				
				private void processTankActionStopTurnMainGun(Message m) {
					TankActionStopTurnMainGun tac = (TankActionStopTurnMainGun)m;
					tanks.get(tac.getTankName()).stopTurningMainGun();
				}
				
				private void processTankActionTurnMainGun(Message m) {
					TankActionTurnMainGun tac = (TankActionTurnMainGun)m;
					tanks.get(tac.getTankName()).turnMainGun(tac.getDirection());
				}
				
				private void processTankActionShotMachineGun(Message m) {
					TankActionShotMachineGun tasmg = (TankActionShotMachineGun)m;
					tanks.get(tasmg.getTankName()).addFireMachineGunBullet(Bullet.REMOTE);
				}
				
				private void processTankActionShotMainGun(Message m) {
					TankActionShotMainGun tasmg = (TankActionShotMainGun)m;
					tanks.get(tasmg.getTankName()).addFireMainGunBullet(Bullet.REMOTE);
				}
				
				private void processPlayerLeftGameMessage(Message m) {
					Player p = players.get(m.getPlayerId());
					for (PlayerTank pTank : p.getTanks()) {
						ITank tank = tanks.get(pTank.getTankName());
						tank.kill();
					}
					players.remove(p.getId());
					hud.showMessage("Player " + p.getName() + " left game");
				}

				private void processTeamLeftGameMessage(Message m) {
					TeamLeftGame tlg = (TeamLeftGame)m;
					
					if (localPlayer.getType() == PlayerType.MAS) {
						for (PlayerTank tank : localPlayer.getTanks()) {
							if (tank.getTeam().getTeamEnum() == tlg.getTeam()) {
								AgentRepository.notifyTeamWin(tank.getTankName());
							}
						}
					}
					
					if (tlg.getTeam() == PlayerTeam.TEAM_1) {
						disconnectAndReturnToJoinMenu(true, "        Team 2 win! Team 1 left game        ");
					}
					else {
						disconnectAndReturnToJoinMenu(true, "        Team 1 win! Team 2 left game        ");
					}
				}
			}
		);
	}
	
	/**
	 * Disconnect form server and return to Join Menu page.
	 * @param sendDisconnect
	 * @param disconnectionCause
	 */
	private static void disconnectAndReturnToJoinMenu(boolean sendDisconnect, final String disconnectionCause) {
		if (gameClient.isConnected()) {
			if (sendDisconnect) {
				gameClient.disconnectFromServer();
			}
			gameClient.closeClientSokects();
		}
		
		new Thread(
			new Runnable() {
				public void run() {
					TankCoders.getGameInstance().changeToMenuState(disconnectionCause);
				}
			}
		).start();
	}
	
	//////////////////////////////////// SEND MESSAGES /////////////////////////////////////
	
	public static void sendTankActionTurnMainGun(String tankName, int direction) {
		TankActionTurnMainGun tatmg = new TankActionTurnMainGun();
		tatmg.setTankName(tankName);
		tatmg.setDirection(direction);
		
		gameClient.broadcast(tatmg);
	}
	
	public static void sendTankActionStopTurnMainGun(String tankName) {
		TankActionStopTurnMainGun ttastmg = new TankActionStopTurnMainGun();
		ttastmg.setTankName(tankName);
		
		gameClient.broadcast(ttastmg);
	}
	
	public static void sendTankActionShotMainGun(String tankName) {
		TankActionShotMainGun tasmg = new TankActionShotMainGun();
		tasmg.setTankName(tankName);
		
		gameClient.broadcast(tasmg);
	}
	
	public static void sendTankActionShotMachineGun(String tankName) {
		TankActionShotMachineGun tasmg = new TankActionShotMachineGun();
		tasmg.setTankName(tankName);
		
		gameClient.broadcast(tasmg);
	}
	
	public static void sendTankBulletHit(short playerId, String tankName, int power) {
		TankBulletHit tbh = new TankBulletHit();
		tbh.setTankName(tankName);
		tbh.setPower(power);
		
		// send message only to tank's owner.
		gameClient.sendToPlayer(tbh, playerId);
	}
	
	public static void sendTankDead(String tankName) {		
		ITank tank = tanks.get(tankName);
		
		inGameState.killLocalPlayerTank(tankName);
		
		TankDead td = new TankDead();
		td.setTankName(tankName);
		gameClient.broadcast(td);
		
		clientSyncManager.unregister(tank);
	}
	
	public static void unregisterTanksInNecessary() {
		if (clientSyncManager != null) {
			try {
				// unregister all the tank of local player
				for (PlayerTank pTank : localPlayer.getTanks()) {
					ITank tank = tanks.get(pTank.getTankName());
					clientSyncManager.unregister(tank);
				}
			} catch (NullPointerException e) {
				// ignore
			}
		}
	}
}
