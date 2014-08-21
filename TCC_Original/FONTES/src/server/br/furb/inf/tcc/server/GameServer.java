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
package br.furb.inf.tcc.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.furb.inf.tcc.tankcoders.game.PlayerTank;
import br.furb.inf.tcc.tankcoders.game.PlayerTeam;
import br.furb.inf.tcc.tankcoders.game.TankModel;
import br.furb.inf.tcc.tankcoders.message.AbstractGameMessage;
import br.furb.inf.tcc.tankcoders.message.AllPlayersAreInGameState;
import br.furb.inf.tcc.tankcoders.message.AnotherPlayerChangeModelResponse;
import br.furb.inf.tcc.tankcoders.message.AnotherUserLogonRespose;
import br.furb.inf.tcc.tankcoders.message.ChangeModel;
import br.furb.inf.tcc.tankcoders.message.ChangeTeam;
import br.furb.inf.tcc.tankcoders.message.InvalidChangeTeamResponse;
import br.furb.inf.tcc.tankcoders.message.PlayerChangeTeamResponse;
import br.furb.inf.tcc.tankcoders.message.PlayerInBattle;
import br.furb.inf.tcc.tankcoders.message.PlayerLeftGame;
import br.furb.inf.tcc.tankcoders.message.StartBattle;
import br.furb.inf.tcc.tankcoders.message.TankDead;
import br.furb.inf.tcc.tankcoders.message.TeamLeftGame;
import br.furb.inf.tcc.tankcoders.message.UserLogoff;
import br.furb.inf.tcc.tankcoders.message.UserLogon;
import br.furb.inf.tcc.tankcoders.message.UserLogonFailedResponse;
import br.furb.inf.tcc.tankcoders.message.UserLogonRespose;
import br.furb.inf.tcc.tankcoders.message.UserReady;
import br.furb.inf.tcc.tankcoders.message.UserUnready;
import br.furb.inf.tcc.util.jgn.JGNRegisterUtil;
import br.furb.inf.tcc.util.lang.GameLanguage;

import com.captiveimagination.jgn.JGN;
import com.captiveimagination.jgn.clientserver.JGNConnection;
import com.captiveimagination.jgn.clientserver.JGNConnectionListener;
import com.captiveimagination.jgn.clientserver.JGNServer;
import com.captiveimagination.jgn.event.MessageListener;
import com.captiveimagination.jgn.message.Message;
import com.captiveimagination.jgn.synchronization.message.SynchronizeRequestIDMessage;
import com.jme.util.LoggingSystem;

/**
 * Main class of the server game.
 * @author Germano Fronza
 */
public class GameServer {

	// Ports contants
	public static final int RELIABLE_PORT = 1000;
	public static final int FAST_PORT = 2000;
	public static final int FINDSERVER_PORT = 2001;
	
	// sockets
	private SocketAddress serverReliableAddress;
	private SocketAddress serverFastAddress;
	
	// servers
	private JGNServer gameServer;
	private DatagramSocket findServerSocket;
	
	// internal controls
	private boolean running;
	private ServerStatus serverStatus;
	private GameBattle currentBattle;
	
	// listeners
	private InternalServerListener logonEventListener;
	private InternalServerListener logoffEventListener;
	private InternalServerListener userReadyEventListener;
	private InternalServerListener userUnReadyEventListener;
	private InternalServerListener userInBattleEventListener;
	private InternalServerListener serverStatusChangedListener;
	
	// current listeners
	private JGNConnectionListener clientConnectionListener;
	private MessageListener messageListener;
	
	// used to control Ids of syncronization managers of the clients.
	private Queue<Short> used;
	
	private static Logger logger;
	private static boolean debug;
	
	public GameServer() {
		running = false;
		serverStatus = ServerStatus.AVAILABLE;
		debug = true;
		
		prepareLogger();
	}

	private void setupGameServer() throws IOException {
		serverReliableAddress = new InetSocketAddress(InetAddress.getLocalHost(), RELIABLE_PORT);
		serverFastAddress = new InetSocketAddress(InetAddress.getLocalHost(), FAST_PORT);
		
		gameServer = new JGNServer(serverReliableAddress, serverFastAddress);
		addClientConnectionListener(
			new JGNConnectionListener() {
				public void connected(JGNConnection conn) {
					
				}

				public void disconnected(JGNConnection conn) {
					OnlinePlayer op = currentBattle.getOnlinePlayerById(conn.getPlayerId());
					if (op != null) {
						currentBattle.removeOnlinePlayerById(op.getPlayerId());
						logoffEventListener.execute(op);
						
						if (serverStatus == ServerStatus.INGAME) {
							AbstractGameMessage m = null;
							if (currentBattle.getCurrentTanksCountTeam1() == 0) {
								m = new TeamLeftGame();
								((TeamLeftGame)m).setTeam(PlayerTeam.TEAM_1);
							}
							else if (currentBattle.getCurrentTanksCountTeam2() == 0) {
								m = new TeamLeftGame();
								((TeamLeftGame)m).setTeam(PlayerTeam.TEAM_2);
							}
							else {
								m = new PlayerLeftGame();
								m.setPlayerId(conn.getPlayerId());
							}
							
							gameServer.sendToAll(m);
						}
					}
				}
			}
		);
		
		addMessageListener(new MessageListener() {
			public void messageCertified(Message message) {}
			public void messageFailed(Message message) {}
			public void messageSent(Message message) {}
			public void messageReceived(Message message) {
				if (message instanceof UserLogon) {
					processUserLogonMessage((UserLogon)message);
				}
				else if (message instanceof UserLogoff) {
					processUserLogoffMessage((UserLogoff)message);
				}
				else if (message instanceof UserReady) {
					processUserReady((UserReady)message);
				}
				else if (message instanceof UserUnready) {
					processUserUnready((UserUnready)message);
				}
				else if (message instanceof ChangeTeam) {
					processChangeTeam((ChangeTeam)message);
				}
				else if (message instanceof ChangeModel) {
					processChangeModel((ChangeModel)message);
				}
				
				// just log the received message.
				logger.info("Received message: " + message);
			}
		});
		
		JGN.createThread(gameServer).start();
		JGNRegisterUtil.registerTankCodersClassMessages();
	}
	
	private void setupFindServerSocket() {
		new Thread(
				new Runnable() {
					public void run() {
						try {
							findServerSocket = new DatagramSocket(FINDSERVER_PORT);
							
							while (isRunning()) {
								logger.info("Waiting FindServer requests...");
								
								DatagramPacket question = new DatagramPacket(new byte[12], 12);
								try {
									findServerSocket.receive(question);
								}
								catch (SocketException e) {
									logger.info("Socket closed by user");
									return;
								}
								
								String questionStr = new String(question.getData());
								if (questionStr.equals("ServerStatus")) {
									String statusStr;
									
									int maxPlayerTanks = currentBattle.getTeam1MaxTanks() + currentBattle.getTeam2MaxTanks();
									if (currentBattle.getCurrentTanksCount() >= maxPlayerTanks) {
										statusStr = "FULL";
									}
									else {
										statusStr = (serverStatus == ServerStatus.AVAILABLE) ? "AVAILABLE" : "INGAME";
									}
									
									String t1Name = currentBattle.getTeam1Name();
									String t2Name = currentBattle.getTeam2Name();
									
									byte[] buffer = (currentBattle.getBattleName() + "@@" + statusStr + "@@" + t1Name + "@@" + t2Name + "@@").getBytes();
									DatagramPacket dgp = new DatagramPacket(buffer, buffer.length, question.getAddress(), question.getPort());
									findServerSocket.send(dgp);
								}
							}
							
							logger.info("StartSocket Finished");
							findServerSocket.close();
						} catch (Exception e) {
							e.printStackTrace(); // TODO: handle this exception
						}
					}
				}
		).start();
	}
	
	private void addClientConnectionListener(JGNConnectionListener listener) {
		// remove current listener
		gameServer.removeClientConnectionListener(this.clientConnectionListener);
		
		this.clientConnectionListener = listener;
		gameServer.addClientConnectionListener(listener);
	}
	
	private void addMessageListener(MessageListener listener) {
		// remove current listener
		gameServer.removeMessageListener(this.messageListener);
		
		this.messageListener = listener;
		gameServer.addMessageListener(listener);
	}
	
	private void processUserLogonMessage(UserLogon logonMsg) {			
		PlayerTank[] playerTanks = logonMsg.getTanks();
		int maxTanksTeam1 = currentBattle.getTeam1MaxTanks();
		int maxTanksTeam2 = currentBattle.getTeam2MaxTanks();
		int maxPlayerTanks = maxTanksTeam1 + maxTanksTeam2;
		int playerTanksCount = playerTanks.length;
		
		String failureCause = null;
		boolean logonFailed = false;
		
		// current + new <= max
		if ((playerTanksCount + currentBattle.getCurrentTanksCount()) <= maxPlayerTanks) {
			int tanksCountInTeam1 = 0;
			
			// check if the given player name is already in use.
			List<String> usedTankNames = new ArrayList<String>();
			Collection<OnlinePlayer> players = currentBattle.getAllOnlinePlayers();
			for (OnlinePlayer onlinePlayer : players) {
				if (onlinePlayer.getPlayerName().equals(logonMsg.getPlayerName())) {
					logonFailed = true;
					failureCause = "Player name already in use";
					break;
				}
				else {
					for (PlayerTank tank : onlinePlayer.getTanks()) {
						usedTankNames.add(tank.getTankName());
					}
				}
			}
			
			// check if some tank name is already in use.
			if (!logonFailed) {
				for (PlayerTank tank : playerTanks) {
					if (usedTankNames.indexOf(tank.getTankName()) > -1) {
						logonFailed = true;
						failureCause = new Formatter().format("Tank name \"%s\" already in use", tank.getTankName()).toString();
						break;
					}
				}
			}
			
			if (!logonFailed) {
				/* setting tank team */ {
					int team1AvailableSlots = maxTanksTeam1 - currentBattle.getCurrentTanksCountTeam1();
					for(int i = 0; i < team1AvailableSlots && i < playerTanks.length; i++) {
						playerTanks[i].setTeam(currentBattle.getTeam1Obj());
						tanksCountInTeam1++;
					}
					
					int team2AvailableSlots = maxTanksTeam2 - currentBattle.getCurrentTanksCountTeam2();
					for(int i = 0; i < team2AvailableSlots && tanksCountInTeam1 < playerTanks.length; i++) {
						playerTanks[tanksCountInTeam1].setTeam(currentBattle.getTeam2Obj());
						tanksCountInTeam1++;
					}
				}
				
				/* setting initial slot location and tank model*/ {
					for (PlayerTank playerTank : playerTanks) {
						playerTank.setInitialSlotLocation(currentBattle.getFreeSlotLocationInTeam(playerTank.getTeam().getTeamEnum()));
						playerTank.setModel(TankModel.JADGE_PANTHER); // always starts with JADGE PANTHER model
					}
				}
				
				OnlinePlayer op = new OnlinePlayer();
				op.setPlayerId(logonMsg.getPlayerId());
				op.setPlayerName(logonMsg.getPlayerName());
				op.setPlayerType(logonMsg.getPlayerType());
				op.setTanks(playerTanks);
				op.setIpAddress(logonMsg.getMessageClient().getAddress().toString().substring(1));
				currentBattle.addOnlinePlayer(op);
				
				UserLogonRespose ulr = new UserLogonRespose();
				ulr.setTanks(op.getTanks());
				ulr.setOnlinePlayers(currentBattle.getAllOnlinePlayersArrayExcept(op.getPlayerId()));
				gameServer.sendToPlayer(ulr, op.getPlayerId());
				
				AnotherUserLogonRespose anulr = new AnotherUserLogonRespose();
				anulr.setPlayerId(op.getPlayerId());
				anulr.setPlayerName(op.getPlayerName());
				anulr.setPlayerType(op.getPlayerType());
				anulr.setTanks(op.getTanks());
				
				gameServer.sendToAllExcept(anulr, op.getPlayerId());
				
				// notify object listener.
				logonEventListener.execute(op);
			} // end !logonFailed
		} // end current + new <= max
		else {
			logonFailed = true;
			failureCause = GameLanguage.getString("logon.fail.maxTanksExceeded");
		}
		
		if (logonFailed) {
			UserLogonFailedResponse ulfr = new UserLogonFailedResponse();
			ulfr.setCause(failureCause);
			gameServer.sendToPlayer(ulfr, logonMsg.getPlayerId());
		}
	}
	
	private void processUserLogoffMessage(UserLogoff logoffMsg) {
		OnlinePlayer op = currentBattle.getOnlinePlayerById(logoffMsg.getPlayerId());
		currentBattle.removeOnlinePlayerById(logoffMsg.getPlayerId());
		
		// notify object listener.
		logoffEventListener.execute(op);
	}
	
	private void processUserReady(UserReady userReadyMsg) {
		OnlinePlayer op = currentBattle.getOnlinePlayerById(userReadyMsg.getPlayerId());
		op.setReadyToPlay(true);
		
		// notify object listener.
		userReadyEventListener.execute(op);
		
		checkIfAllPlayerAreReadyToPlay();
	}
	
	private void processUserUnready(UserUnready userUnreadyMsg) {
		OnlinePlayer op = currentBattle.getOnlinePlayerById(userUnreadyMsg.getPlayerId());
		op.setReadyToPlay(false);
		
		// notify object listener.
		userUnReadyEventListener.execute(op);
	}
	
	private void processChangeTeam(ChangeTeam changeTeamMsg) {
		if (currentBattle.acceptOneMoreTankInTeam(changeTeamMsg.getTeam())) {
			// change team of the tank.
			int newSlotLocation = currentBattle.changePlayerTankTeam(changeTeamMsg.getPlayerId(), changeTeamMsg.getTankName(), changeTeamMsg.getTeam());
			
			// notify the other players of this change
			PlayerChangeTeamResponse apctr = new PlayerChangeTeamResponse();
			apctr.setPlayerId(changeTeamMsg.getPlayerId());
			apctr.setTankName(changeTeamMsg.getTankName());
			apctr.setTeam(changeTeamMsg.getTeam());
			apctr.setNewInitialSlotLocation(newSlotLocation);
	
			// enqeue message to be sent.
			gameServer.sendToAll(apctr);
		}
		else {
			// team is full, notify the sender of the message.
			InvalidChangeTeamResponse ictr = new InvalidChangeTeamResponse();
			ictr.setPlayerId(changeTeamMsg.getPlayerId());
			ictr.setTankName(changeTeamMsg.getTankName());
			ictr.setTeam(changeTeamMsg.getTeam());
			
			gameServer.sendToPlayer(ictr, changeTeamMsg.getPlayerId());
		}
	}
	
	private void processChangeModel(ChangeModel changeModelMsg) {
		// change team of the tank.
		currentBattle.changePlayerTankModel(changeModelMsg.getPlayerId(), changeModelMsg.getTankName(), changeModelMsg.getModel());
		
		// notify the other players of this change
		AnotherPlayerChangeModelResponse apctr = new AnotherPlayerChangeModelResponse();
		apctr.setPlayerId(changeModelMsg.getPlayerId());
		apctr.setTankName(changeModelMsg.getTankName());
		apctr.setModel(changeModelMsg.getModel());

		// enqeue message to be sent.
		gameServer.sendToAllExcept(apctr, changeModelMsg.getPlayerId());
	}
	
	private void checkIfAllPlayerAreReadyToPlay() {
		Collection<OnlinePlayer> playersOnline = currentBattle.getAllOnlinePlayers();
		int qtyReady = 0;
		
		for (OnlinePlayer onlinePlayer : playersOnline) {
			if (onlinePlayer.isReadyToPlay()) {
				qtyReady++;
			}
		}
		
		if (playersOnline.size() == qtyReady) {
			serverStatus = ServerStatus.INGAME;
			switchToInGameState();
			
			// notify object listener
			serverStatusChangedListener.execute(ServerStatus.INGAME);
			
			// send message to all clients to start battle.
			StartBattle sb = new StartBattle();
			sb.setTerrainHeightmapImage(currentBattle.getTerrainHeightMapImage());
			gameServer.sendToAll(sb);
		}
	}

	public void startNewBattle(GameBattle battle) throws IOException {
		running = true;
		setCurrentBattle(battle);
		setServerStatus(ServerStatus.AVAILABLE);
		
		setupFindServerSocket();
		setupGameServer();
	}
	
	
	public void stopCurrentBattle() throws IOException {
		running = false;
		setCurrentBattle(null);
		setServerStatus(null);
		
		findServerSocket.close();	
		gameServer.close();
	}
	
	private void switchToInGameState() {
		used = new ConcurrentLinkedQueue<Short>();
		
		addMessageListener(
			new MessageListener() {
				public void messageCertified(Message arg0) {}
				public void messageFailed(Message arg0) {}
				public void messageSent(Message arg0) {}
				public void messageReceived(Message m) {
					if (m instanceof PlayerInBattle) {
						OnlinePlayer op = currentBattle.getOnlinePlayerById(m.getPlayerId());
						op.setPlaying(true);
						
						// notify object listener.
						userInBattleEventListener.execute(op);
						
						checkIfAllPlayersAreInBattleState();
					}
					else if (m instanceof SynchronizeRequestIDMessage) {
						SynchronizeRequestIDMessage request = (SynchronizeRequestIDMessage)m;
						if (request.getRequestType() == SynchronizeRequestIDMessage.REQUEST_ID) {
							short id = serverNextId();
							request.setRequestType(SynchronizeRequestIDMessage.RESPONSE_ID);
							request.setSyncObjectId(id);
							request.getMessageClient().sendMessage(request);
						} 
					}
					else if (m instanceof TankDead) {
						TankDead td = (TankDead)m;
						OnlinePlayer op = currentBattle.getOnlinePlayerById(m.getPlayerId());
						PlayerTank tank = op.getTankByName(td.getTankName());
						tank.setAlive(false);
						PlayerTeam teamEnum = tank.getTeam().getTeamEnum();
						
						int qtyTanksAliveInTeam = 0;
						Collection<OnlinePlayer> players = currentBattle.getAllOnlinePlayers();
						for (OnlinePlayer onlinePlayer : players) {
							for (PlayerTank pTank : onlinePlayer.getTanks()) {
								if (pTank.getTeam().getTeamEnum() == teamEnum && pTank.isAlive()) {
									qtyTanksAliveInTeam++;
								}
							}
						}
						
						if (qtyTanksAliveInTeam == 0) {
							TeamLeftGame tlg = new TeamLeftGame();
							tlg.setTeam(teamEnum);
							gameServer.sendToAll(tlg);
						}
					}
				}				
			}
		);
	}
	
	private void checkIfAllPlayersAreInBattleState() {
		Collection<OnlinePlayer> playersOnline = currentBattle.getAllOnlinePlayers();
		int qtyInBattleState = 0;
		
		for (OnlinePlayer onlinePlayer : playersOnline) {
			if (onlinePlayer.isPlaying()) {
				qtyInBattleState++;
			}
		}
		
		if (playersOnline.size() == qtyInBattleState) {
			gameServer.sendToAll(new AllPlayersAreInGameState());
		}
	}
	
	/**
	 * Generate an unique id (starting by 1).
	 * @return short
	 */
	private final synchronized short serverNextId() {
		short id = (short)1;
		while (used.contains(id)) {
			id++;
		}
		used.add(id);
		return id;
	}
	
	// TODO: implement a mechanism to release Ids when a player disconnected.
	@SuppressWarnings("unused")
	private final void serverReleaseId(short id) {
		used.remove(id);
	}

	
	private void prepareLogger() {
		logger = LoggingSystem.getLogger();
		if (isDebug()) {
			logger.setLevel(Level.ALL);
		}
		else {
			logger.setLevel(Level.OFF);
		}
		
		if (debug) {
			logger.info(GameLanguage.getString("running.debugMode"));
		}
	}
	
	public static boolean isDebug() {
		return debug;
	}
	
	protected void finalize() throws Throwable {
		super.finalize();
		running = false;
	}
	
	public boolean isRunning() {
		return running;
	}

	public void setServerStatus(ServerStatus serverStatus) {
		this.serverStatus = serverStatus;
	}

	public ServerStatus getServerStatus() {
		return serverStatus;
	}

	public GameBattle getCurrentBattle() {
		return currentBattle;
	}

	public void setCurrentBattle(GameBattle currentBattle) {
		this.currentBattle = currentBattle;
	}

	public void setLogonEventListener(final InternalServerListener logonEventListener) {
		this.logonEventListener = logonEventListener;
	}

	public void setLogoffEventListener(InternalServerListener logoffEventListener) {
		this.logoffEventListener = logoffEventListener;
	}

	public void setUserReadyEventListener(
			InternalServerListener userReadyEventListener) {
		this.userReadyEventListener = userReadyEventListener;
	}

	public void setUserUnReadyEventListener(InternalServerListener userUnReadyEventListener) {
		this.userUnReadyEventListener = userUnReadyEventListener;
	}
	
	public void setUserInBattleEventListener(InternalServerListener userInBattleEventListener) {
		this.userInBattleEventListener = userInBattleEventListener;
	}

	public void setServerStatusChangedListener(InternalServerListener serverStatusChangedListener) {
		this.serverStatusChangedListener = serverStatusChangedListener;
	}
}