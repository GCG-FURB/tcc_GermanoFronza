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
package br.furb.inf.tcc.tankcoders.menu;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fenggui.Container;
import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.GameMenuButton;
import org.fenggui.Label;
import org.fenggui.ScrollContainer;
import org.fenggui.TextEditor;
import org.fenggui.composites.MessageWindow;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowExLayoutData;
import org.fenggui.layout.RowLayout;
import org.fenggui.layout.StaticLayout;
import org.fenggui.render.Font;
import org.fenggui.table.ITableModel;
import org.fenggui.table.Table;
import org.fenggui.util.Alphabet;
import org.fenggui.util.Color;
import org.fenggui.util.Point;
import org.fenggui.util.Spacing;
import org.fenggui.util.fonttoolkit.FontFactory;

import br.furb.inf.tcc.server.ServerStatus;
import br.furb.inf.tcc.tankcoders.TankCoders;
import br.furb.inf.tcc.tankcoders.game.Player;
import br.furb.inf.tcc.tankcoders.game.PlayerTank;
import br.furb.inf.tcc.tankcoders.game.PlayerType;
import br.furb.inf.tcc.util.lang.GameLanguage;
import br.furb.inf.tcc.util.ui.FengGuiUtils;

/**
 * Menu Play of the game.
 * @author Germano Fronza
 */
public class JoinGameMenuGUI {
	
	private Label lPlayerName;
	private TextEditor tPlayerName;
	private Table table = new Table();
	private GameMenuButton back, refresh, join;
	
	public static final String SERVERNAME_ENTRY = "serverName";
	public static final String ADDRESS_ENTRY = "ipAddress";
	public static final String STATUS_ENTRY = "status";
	public static final String TEAM1NAME_ENTRY = "team1Name";
	public static final String TEAM2NAME_ENTRY = "team2Name";
	
	private List<HashMap<String, Object>> serverList;
	
	public void buildGUI(Display display) {	
		// create a new container for the image background.
		final Container c = new Container();
		c.setMinSize(754, 473);
		c.setSizeToMinSize();
		c.getAppearance().add(MenuPixmapBackgrounds.getJoinGamePixmapBackground());
		c.setLayoutManager(new StaticLayout());	
		StaticLayout.center(c, display);
		
		display.addWidget(c);
		
		final Container cPlayerName = new Container();
		cPlayerName.getAppearance().setPadding(new Spacing(0, 10));
		cPlayerName.setLayoutManager(new StaticLayout());
		
		c.addWidget(cPlayerName);
		
		initComponents(c, cPlayerName, display);
		buildComponents(c, cPlayerName);
	}
	
	private Font createAntiAliasedFont() {
		return FontFactory.renderStandardFont(new java.awt.Font("Sans", java.awt.Font.BOLD, 14), true, Alphabet.getDefaultAlphabet());
	}
	
	private void initComponents(final Container parentContainer, final Container cPlayerName, final Display display) {	
		lPlayerName = FengGUI.createLabel(cPlayerName);
		lPlayerName.setText(GameLanguage.getString("menu.joingame.playerName") + ":");
		lPlayerName.getAppearance().setTextColor(Color.BLACK);
		lPlayerName.getAppearance().setFont(FontFactory.renderStandardFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12)));
		lPlayerName.setSize(80, 20);
		
		tPlayerName = FengGUI.createTextField(cPlayerName);
		tPlayerName.setSize(135, 20);
		
		table = new Table();
		table.getAppearance().setGridColor(Color.BLACK);
		table.getAppearance().setHeaderBackgroundColor(Color.BLACK);
		table.getAppearance().setHeadTextColor(Color.WHITE);
		table.getAppearance().setSelectionColor(Color.DARK_YELLOW);
		table.getAppearance().setFont(createAntiAliasedFont());

		updateTableModel();
		
		back = new GameMenuButton(FengGuiUtils.getRsrc("data/images/buttons/btn_back_0.png"), FengGuiUtils.getRsrc("data/images/buttons/btn_back_1.png"));
		back.addButtonPressedListener(new IButtonPressedListener()
		{

			public void buttonPressed(ButtonPressedEvent e)
			{
				display.removeAllWidgets();
				new GameMenuGUI().buildGUI(display);
			}
		});
		
		refresh = new GameMenuButton(FengGuiUtils.getRsrc("data/images/buttons/btn_refresh_0.png"), FengGuiUtils.getRsrc("data/images/buttons/btn_refresh_1.png"));
		refresh.addButtonPressedListener(new IButtonPressedListener()
		{

			public void buttonPressed(ButtonPressedEvent e)
			{
				((ServersTableModel)table.getModel()).update();
			}
		});
		
		join = new GameMenuButton(FengGuiUtils.getRsrc("data/images/buttons/btn_join_0.png"), FengGuiUtils.getRsrc("data/images/buttons/btn_join_1.png"));
		join.addButtonPressedListener(new IButtonPressedListener()
		{

			public void buttonPressed(ButtonPressedEvent e)
			{
				if (tPlayerName.getText().equals("")) {
					MessageWindow mw = new MessageWindow("           " + GameLanguage.getString("menu.joingame.messages.typePlayerName") + "           ");
					mw.setTitle(GameLanguage.getString("windowMessage.warning"));
					mw.pack();
					display.addWidget(mw);
					StaticLayout.center(mw, display);
					return;
				}
				
				int selectedIndex = table.getSelection();
				if (selectedIndex > -1) {
	 				Map<String, Object> server = ((ServersTableModel)table.getModel()).getServer(selectedIndex);
					if (server != null) {
						ServerStatus serverStatus = (ServerStatus)server.get(STATUS_ENTRY);
						if (serverStatus == ServerStatus.AVAILABLE) {
							Player player = null;
							TankCoders game = TankCoders.getGameInstance();
							
							if (game.isMAS()) {
								// Create a new MAS Player with him tanks
								player = new Player(PlayerType.MAS);
								player.setName(tPlayerName.getText());
								
								for (String agName : game.getAgents()) {
									PlayerTank pt = new PlayerTank();
									pt.setTankName(agName);
									player.addTank(pt);
								}
							}
							else {
								// Create a new AVATAR Player with one tank
								player = new Player(PlayerType.AVATAR);
								player.setName(tPlayerName.getText());
								PlayerTank pt = new PlayerTank();
								pt.setTankName(tPlayerName.getText());
								player.addTank(pt);
							}
							
							PrepareBattleMenuGUI pbMenuGUI =  new PrepareBattleMenuGUI(server, player);
							pbMenuGUI.buildGUI(display);
						}
						else {
							String msg;
							if (serverStatus == ServerStatus.INGAME) {
								msg = GameLanguage.getString("menu.joingame.messages.alreadyRunningBattle");
							}
							else {
								msg = "      " + GameLanguage.getString("menu.joingame.messages.serverFull") + "      ";
							}
							
							MessageWindow mw = new MessageWindow(msg);
							mw.setTitle(GameLanguage.getString("windowMessage.warning"));
							mw.pack();
							display.addWidget(mw);
							StaticLayout.center(mw, display);
						}
					}
					else {
						MessageWindow mw = new MessageWindow("            " + GameLanguage.getString("menu.joingame.messages.selectAServer") + "            ");
						mw.setTitle(GameLanguage.getString("windowMessage.warning"));
						mw.pack();
						display.addWidget(mw);
						StaticLayout.center(mw, display);
					}
				}
				else {
					MessageWindow mw = new MessageWindow("            " + GameLanguage.getString("menu.joingame.messages.selectAServer") + "            ");
					mw.setTitle(GameLanguage.getString("windowMessage.warning"));
					mw.pack();
					display.addWidget(mw);
					StaticLayout.center(mw, display);
				}
			}
		});
	}
	
	public void buildComponents(Container ownerContainer, final Container cPlayerName) {
		tPlayerName.setLayoutData(new RowExLayoutData(true, true));
		tPlayerName.setPosition(new Point(80, 0));
		cPlayerName.pack();
		cPlayerName.setPosition(new Point(9, 243));
		cPlayerName.setSize(250, 20);
		
		// table servers
		final Container cTable = new Container();
		
		final ScrollContainer sc = new ScrollContainer();
		sc.setLayoutData(BorderLayoutData.CENTER);

		cTable.addWidget(sc);
		ownerContainer.addWidget(cTable);
		
		sc.setInnerWidget(table);
		cTable.setPosition(new Point(20, 93));
		cTable.setSize(420, 140);
		
		// container for buttons refresh and join
		final Container cServerHandle = new Container();
		cServerHandle.getAppearance().setPadding(new Spacing(0, 10));
		cServerHandle.setLayoutManager(new RowLayout(true));
		
		cServerHandle.addWidget(refresh);
		cServerHandle.addWidget(join);
	
		ownerContainer.addWidget(cServerHandle);
		cServerHandle.pack();
		cServerHandle.setPosition(new Point(285,70));
		
		// button back
		final Container c = new Container();
		c.getAppearance().setPadding(new Spacing(0, 10));
		c.setLayoutManager(new RowLayout(true));
		
		ownerContainer.addWidget(c);
		
		c.addWidget(back);
		c.pack();
		c.setPosition(new Point(0,25));
	}

	private void updateTableModel() {
		table.setModel(new ServersTableModel());
	}
	
	private void updateServerList() {
		serverList = new ArrayList<HashMap<String,Object>>();
		
		try {
			DatagramSocket socket = new DatagramSocket();
			try {
				String ipAddress = "255.255.255.255"; // broadcast
				if (TankCoders.isServerLocal()) {
					ipAddress = "127.0.0.1";
				}
				InetAddress addr = InetAddress.getByName(ipAddress);
				
				byte[] buffer = "ServerStatus".getBytes();
				DatagramPacket dgp = new DatagramPacket(buffer, buffer.length, addr, 2001);
				socket.send(dgp);
		
				DatagramPacket answer = new DatagramPacket(new byte[110], 110);
				socket.setSoTimeout(2000);
				try {
					socket.receive(answer);
				}
				catch (SocketTimeoutException e) {
					return;
				}
				String answerStr = new String(answer.getData());
				
				// removes first char of the string, because it's a slash.
				String serverAddress = answer.getAddress().toString().substring(1);
				String[] serverInfos = answerStr.split("@@");
				
				HashMap<String, Object> server = new HashMap<String, Object>();
				server.put(SERVERNAME_ENTRY, serverInfos[0]);
				server.put(ADDRESS_ENTRY, serverAddress);
				server.put(STATUS_ENTRY, ServerStatus.valueOf(serverInfos[1]));
				server.put(TEAM1NAME_ENTRY, serverInfos[2]);
				server.put(TEAM2NAME_ENTRY, serverInfos[3]);
				
				serverList.add(server);
			}
			finally {
				socket.close();
			}
		}
		catch(Exception e) {
			// TODO: handle
			e.printStackTrace();
		}
	}
	
	class ServersTableModel implements ITableModel {
		int rowCount;
		String[] header = {"menu.joingame.tableHeader.serverName", "menu.joingame.tableHeader.serverIp", "menu.joingame.tableHeader.serverStatus"};
		String[][] matrix;
		
		public ServersTableModel() {
			createModel();
		}

		public void update() {
			createModel();
		}
		
		private void createModel() {
			updateServerList();

			// sempre deve ter pelo menos 6 elementos
			rowCount = 6;
			if (serverList.size() > 6) {
				rowCount = serverList.size();
			}
			
			matrix = new String[rowCount][3];
			
			for (int i = 0; i < serverList.size(); i++) {
				matrix[i][0] = (String)serverList.get(i).get(SERVERNAME_ENTRY);
				matrix[i][1] = (String)serverList.get(i).get(ADDRESS_ENTRY);
				matrix[i][2] = GameLanguage.getString(((ServerStatus)serverList.get(i).get(STATUS_ENTRY)).getName());
			}
		}

		public String getColumnName(int columnIndex) {
			return GameLanguage.getString(header[columnIndex]);
		}

		public int getColumnCount() {
			return matrix[0].length;
		}

		public Object getValue(int row, int column) {
			return matrix[row][column];
		}

		public int getRowCount() {
			return matrix.length;
		}

		public void clear() {

		}

		public HashMap<String, Object> getServer(int row) {
			if (matrix[row][0] == null) {
				return null;
			}
			else {
				String serverIpAddress =  matrix[row][1];
				for (HashMap<String, Object> server : serverList) {
					if (server.get(ADDRESS_ENTRY).equals(serverIpAddress)) {
						return server; 
					}
				}
				
				return null;
			}
		}
	}

}
