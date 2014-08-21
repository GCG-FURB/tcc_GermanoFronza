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
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import br.furb.inf.tcc.server.GameServer;
import br.furb.inf.tcc.tankcoders.game.Player;
import br.furb.inf.tcc.tankcoders.message.UserLogoff;
import br.furb.inf.tcc.tankcoders.message.UserLogon;
import br.furb.inf.tcc.util.jgn.JGNRegisterUtil;

import com.captiveimagination.jgn.JGN;
import com.captiveimagination.jgn.clientserver.JGNClient;
import com.captiveimagination.jgn.clientserver.JGNConnectionListener;
import com.captiveimagination.jgn.event.MessageListener;
import com.captiveimagination.jgn.message.Message;
import com.captiveimagination.jgn.message.type.PlayerMessage;
import com.captiveimagination.jgn.synchronization.GraphicalController;
import com.jme.scene.Spatial;

/**
 * This is a singleton class to handle all the comunications with the game server.
 * @author Germano Fronza
 */
public class GameClient {
	
	/** Instance of this singleton */
	private static GameClient instance;
	
	private static SocketAddress serverReliableAddress;
	private static SocketAddress serverFastAddress;
	private static JGNClient jgnClient;
	
	private JGNConnectionListener clientConnectionListener;
	private JGNConnectionListener serverConnectionListener;
	private MessageListener messageListener;
	
	/**
	 * Player owner of this client.
	 */
	private Player player;
	
	private GameClient() throws Exception {}
	
	public void setPlayer(Player p) {
		this.player = p;
	}
	
	public void setupCommunication(InetAddress serverAddress) throws Exception {
		serverReliableAddress = new InetSocketAddress(serverAddress, GameServer.RELIABLE_PORT);
		serverFastAddress = new InetSocketAddress(serverAddress, GameServer.FAST_PORT);

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~GAMBI~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
		// !! hardcode to create the JGNClient in an available TCP/UDP port. !! //
		int incPort = 0;
		while (true/*uoww forever*/) {
			try {
				jgnClient = new JGNClient(new InetSocketAddress(InetAddress.getLocalHost(), 1100 + incPort), new InetSocketAddress(InetAddress.getLocalHost(), 2100 + incPort));
				break;
			}
			catch (BindException be) {
				incPort++;
			}
		}
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~GAMBI~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
		
		JGN.createThread(jgnClient).start();
		JGNRegisterUtil.registerTankCodersClassMessages();
		
		jgnClient.connectAndWait(serverReliableAddress, serverFastAddress, 15000);
		
		player.setId(jgnClient.getPlayerId());
	}
	
	public void addServerConnectionListener(JGNConnectionListener listener) {
		serverConnectionListener = listener;
		jgnClient.addServerConnectionListener(listener);
	}

	public void addClientConnectionListener(JGNConnectionListener listener) {
		clientConnectionListener = listener;
		jgnClient.addClientConnectionListener(listener);
	}
	
	public void addMessageListener(MessageListener listener) {
		messageListener = listener;
		jgnClient.addMessageListener(listener);
	}
	
	public void removeAllCurrentListeners() {
		jgnClient.removeClientConnectionListener(clientConnectionListener);
		jgnClient.removeServerConnectionListener(serverConnectionListener);
		jgnClient.removeMessageListener(messageListener);
	}
	
	public void loginPlayer() {		
		UserLogon ul = new UserLogon();
		ul.loadByPlayer(player);
		
		sendToServer(ul);
	}
	
	public void logoffPlayer() {
		sendToServer(new UserLogoff());
	}
	
	public void disconnectFromServer() {
		try {
			jgnClient.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeClientSokects() {
		try {
			jgnClient.getReliableServer().closeAndWait(2000);
			jgnClient.getFastServer().closeAndWait(2000);
		} catch (IOException e) {
			// no problem...
		}
	}
	
	public void sendToServer(Message message) {
		jgnClient.sendToServer(message);
	}
	
	public <T extends Message & PlayerMessage> void sendToPlayer(T message, short playerId) {
		jgnClient.sendToPlayer(message, playerId);
	}
	
	public void broadcast(Message message) {
		jgnClient.broadcast(message);
	}
	
	public boolean isConnected() {
		return (jgnClient != null) && (jgnClient.isAlive());
	}
	
	public static GameClient getInstance() throws Exception {
		// lazy instantiation.
		if (instance == null) {
			instance = new GameClient();
		}
		
		return instance;
	}
	
	public SynchronizationManager createSyncManager(GraphicalController<Spatial> controller) {
		return new SynchronizationManager(jgnClient, controller);
	}
}
