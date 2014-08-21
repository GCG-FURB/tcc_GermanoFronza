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
package br.furb.inf.tcc.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Formatter;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import br.furb.inf.tcc.server.GameBattle;
import br.furb.inf.tcc.server.GameServer;
import br.furb.inf.tcc.server.InternalServerListener;
import br.furb.inf.tcc.server.OnlinePlayer;
import br.furb.inf.tcc.server.ServerStatus;
import br.furb.inf.tcc.tankcoders.game.GameRulesConstants;
import br.furb.inf.tcc.util.lang.GameLanguage;

/**
 * User interface for TankCoders Game Server.
 * @author Germano Fronza
 */
public class ServerGUI extends javax.swing.JFrame {

	private static final long serialVersionUID = 1L;

	{
		//Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static final String APP_TITLE = "TankCoders Game Server";
	private static final String DEFAULT_BATTLENAME = "TankCoders Battle";
	private static final String DEFAULT_TEAM1NAME = "Team Blue";
	private static final String DEFAULT_TEAM2NAME = "Team Red";
	
	private JLabel jBattleName;
	private JTextField tfTeam1Name;
	private JLabel lTeam1Name;
	private JSpinner sMaxTanksTeam2;
	private JLabel lLastEvent;
	private JScrollPane jScrollPane1;
	private JLabel lUsersOnline;
	private JButton bStartServer;
	private JTable tOnlinePlayers;
	private JLabel lTotalTanks;
	private JLabel lMaxTanksTeam2;
	private JLabel lMaxTanksTeam1;
	private JSpinner sMaxTanksTeam1;
	private JTextField tfTeam2Name;
	private JLabel lTeam2Name;
	private JTextField tfBattleName;
	private JComboBox cbTerrain;
	private JLabel lTerrain;

	private GameServer server;
	private int currentAvailablePlayers = GameRulesConstants.MAX_TANKS;
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ServerGUI inst = new ServerGUI();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public ServerGUI() {
		super();
		initGUI();
		server = new GameServer();
		registerInternalServerListeners();
	}

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setTitle(APP_TITLE + " - " + GameLanguage.getString("server.stopped"));
			getContentPane().setLayout(null);
			{
				jBattleName = new JLabel();
				getContentPane().add(jBattleName);
				jBattleName.setText(GameLanguage.getString("server.battleName") + ":");
				jBattleName.setBounds(28, 15, 62, 14);
			}
			{
				tfBattleName = new JTextField();
				getContentPane().add(tfBattleName);
				tfBattleName.setBounds(108, 12, 134, 21);
				tfBattleName.setText(DEFAULT_BATTLENAME);
			}
			{
				lTeam1Name = new JLabel();
				getContentPane().add(lTeam1Name);
				lTeam1Name.setText(GameLanguage.getString("server.nameOfTeam1") + ":");
				lTeam1Name.setBounds(8, 48, 82, 14);
			}
			{
				tfTeam1Name = new JTextField();
				getContentPane().add(tfTeam1Name);
				tfTeam1Name.setBounds(108, 45, 134, 21);
				tfTeam1Name.setText(DEFAULT_TEAM1NAME);
			}
			{
				lTeam2Name = new JLabel();
				getContentPane().add(lTeam2Name);
				lTeam2Name.setText(GameLanguage.getString("server.nameOfTeam2") + ":");
				lTeam2Name.setBounds(8, 82, 82, 14);
			}
			{
				tfTeam2Name = new JTextField();
				getContentPane().add(tfTeam2Name);
				tfTeam2Name.setBounds(108, 79, 134, 21);
				tfTeam2Name.setText(DEFAULT_TEAM2NAME);
			}
			
			ChangeListener spinnerListener = new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					int valueTeam1 = (Integer)sMaxTanksTeam1.getValue();
					int valueTeam2 = (Integer)sMaxTanksTeam2.getValue();
					
					JSpinner source = (JSpinner)e.getSource();
					int value = (Integer)source.getValue();
					if (value < 0) {
						source.setValue(0);
					}
					else if ((valueTeam1 + valueTeam2) > GameRulesConstants.MAX_TANKS) {
						source.setValue(value-1);
						return;
					}
					
					currentAvailablePlayers = GameRulesConstants.MAX_TANKS - (valueTeam1 + valueTeam2);
					
					lTotalTanks.setText(GameLanguage.getString("server.tankAvailable") + ": " + currentAvailablePlayers);
				}
			};

			{
				sMaxTanksTeam1 = new JSpinner();
				sMaxTanksTeam1.setValue(0);
				getContentPane().add(sMaxTanksTeam1);
				sMaxTanksTeam1.setBounds(327, 45, 45, 21);
				sMaxTanksTeam1.addChangeListener(spinnerListener);
			}
			{
				lMaxTanksTeam1 = new JLabel();
				getContentPane().add(lMaxTanksTeam1);
				lMaxTanksTeam1.setText(GameLanguage.getString("server.maxTanks") + ":");
				lMaxTanksTeam1.setBounds(260, 48, 70, 14);
			}
			{
				lMaxTanksTeam2 = new JLabel();
				getContentPane().add(lMaxTanksTeam2);
				lMaxTanksTeam2.setText(GameLanguage.getString("server.maxTanks") + ":");
				lMaxTanksTeam2.setBounds(260, 82, 67, 14);
			}
			{
				sMaxTanksTeam2 = new JSpinner();
				sMaxTanksTeam2.setValue(0);
				getContentPane().add(sMaxTanksTeam2);
				sMaxTanksTeam2.setBounds(327, 79, 45, 21);
				sMaxTanksTeam2.addChangeListener(spinnerListener);
			}
			{
				lTotalTanks = new JLabel();
				getContentPane().add(lTotalTanks);
				lTotalTanks.setText(GameLanguage.getString("server.tankAvailable") + ": " + currentAvailablePlayers);
				lTotalTanks.setBounds(260, 15, 118, 14);
			}
			{
				jScrollPane1 = new JScrollPane();
				getContentPane().add(jScrollPane1);
				jScrollPane1.setBounds(8, 152, 363, 137);
				{
					TableModel tOnlinePlayersModel = 
						new DefaultTableModel(
								new String[][] {},
								new String[] {"#", GameLanguage.getString("server.table.playerName"), 
												   GameLanguage.getString("server.table.tanks"), 
												   GameLanguage.getString("server.table.address"), 
												   GameLanguage.getString("server.table.status")});
					tOnlinePlayers = new JTable();
					jScrollPane1.setViewportView(tOnlinePlayers);
					tOnlinePlayers.setModel(tOnlinePlayersModel);
					tOnlinePlayers.setBounds(8, 152, 363, 151);
					tOnlinePlayers.setCellSelectionEnabled(true);
					tOnlinePlayers.setBorder(new LineBorder(new java.awt.Color(0,0,0), 1, false));
					tOnlinePlayers.getColumn("#").setMaxWidth(24);
					tOnlinePlayers.getColumn(GameLanguage.getString("server.table.playerName")).setMinWidth(120);
					tOnlinePlayers.getColumn(GameLanguage.getString("server.table.tanks")).setMaxWidth(38);
					tOnlinePlayers.getColumn(GameLanguage.getString("server.table.address")).setMinWidth(120);
				}
			}
			{
				bStartServer = new JButton();
				getContentPane().add(bStartServer);
				bStartServer.setText(GameLanguage.getString("server.startServer"));
				bStartServer.setBounds(290, 120, 82, 21);
				bStartServer.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							try {
								if (server.isRunning()) {
									server.stopCurrentBattle();
									
									changeEnableFormControls(true);
									clearFormControls();
									
									bStartServer.setText(GameLanguage.getString("server.startServer"));
									setTitle(APP_TITLE + " - " + GameLanguage.getString("server.stopped"));
									setLastEvent(GameLanguage.getString("server.serverStopped"));
								}
								else {
									GameBattle battle = new GameBattle();
									battle.setBattleName(tfBattleName.getText());
									battle.setTeam1Name(tfTeam1Name.getText());
									battle.setTeam2Name(tfTeam2Name.getText());
									battle.setTeam1MaxTanks((Integer)sMaxTanksTeam1.getValue());
									battle.setTeam2MaxTanks((Integer)sMaxTanksTeam2.getValue());
									battle.setTerrainHeightMapImage((String)cbTerrain.getSelectedItem());
									server.startNewBattle(battle);
									
									changeEnableFormControls(false);
									
									bStartServer.setText(GameLanguage.getString("server.stopServer"));
									setTitle(APP_TITLE + " - " + GameLanguage.getString("server.waitingForConnections"));
									setLastEvent(GameLanguage.getString("server.serverStarted"));
								}
							} catch (IOException e1) {
								e1.printStackTrace(); // TODO: handle exception.
							}
						}						
					}
				);
			}
			{
				lUsersOnline = new JLabel();
				getContentPane().add(lUsersOnline);
				lUsersOnline.setText(GameLanguage.getString("server.players"));
				lUsersOnline.setBounds(8, 132, 43, 14);
			}
			{
				lLastEvent = new JLabel();
				getContentPane().add(lLastEvent);
				lLastEvent.setText(GameLanguage.getString("server.lastEvent") + ":");
				lLastEvent.setBounds(8, 295, 363, 14);
			}
			{
				lTerrain = new JLabel();
				getContentPane().add(lTerrain);
				lTerrain.setText("Terrain:");
				lTerrain.setBounds(52, 115, 38, 14);
			}
			{
				ComboBoxModel jComboBox1Model = 
					new DefaultComboBoxModel(
							new String[] {"terrain.png"});
				cbTerrain = new JComboBox();
				getContentPane().add(cbTerrain);
				cbTerrain.setModel(jComboBox1Model);
				cbTerrain.setBounds(108, 112, 134, 21);
			}
			pack();
			this.setResizable(false);
			this.setSize(391, 349);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void changeEnableFormControls(boolean enable) {
		tfBattleName.setEnabled(enable);
		tfTeam1Name.setEnabled(enable);
		tfTeam2Name.setEnabled(enable);
		sMaxTanksTeam1.setEnabled(enable);
		sMaxTanksTeam2.setEnabled(enable);
		cbTerrain.setEnabled(enable);
	}
	
	private void clearFormControls() {
		tfBattleName.setText(DEFAULT_BATTLENAME);
		tfTeam1Name.setText(DEFAULT_TEAM1NAME);
		tfTeam2Name.setText(DEFAULT_TEAM2NAME);
		sMaxTanksTeam1.setValue(0);
		sMaxTanksTeam2.setValue(0);
		
		DefaultTableModel dataModel = (DefaultTableModel)tOnlinePlayers.getModel();
		for (int i = 0; i < dataModel.getRowCount(); i++) {
			dataModel.removeRow(i);
		}
	}
	
	private void setLastEvent(String string) {
		lLastEvent.setText(GameLanguage.getString("server.lastEvent") + ": ".concat(string));
	}	
	
	private void registerInternalServerListeners() {
		// on player login.
		server.setLogonEventListener(
			new InternalServerListener() {
				public void execute(Object... params) {
					// add player in the table.
					OnlinePlayer op = (OnlinePlayer)params[0];
					DefaultTableModel dataModel = (DefaultTableModel)tOnlinePlayers.getModel();
					dataModel.addRow(new String[]{String.valueOf(op.getPlayerId()),
												  op.getPlayerName(), 
												  String.valueOf(op.getTanks().length), 
												  op.getIpAddress(), 
												  "Normal"});
					setLastEvent(new Formatter().format(GameLanguage.getString("server.playerLoggedIn"), op.getPlayerName()).toString());
				}
			}		
		);
		
		server.setLogoffEventListener(
			new InternalServerListener() {
				public void execute(Object... params) {
					// remove player from the table.
					OnlinePlayer op = (OnlinePlayer)params[0];
					DefaultTableModel dataModel = (DefaultTableModel)tOnlinePlayers.getModel();
					for (int i = 0; i < dataModel.getRowCount(); i++) {
						if (dataModel.getValueAt(i, 0).equals(String.valueOf(op.getPlayerId()))) {
							dataModel.removeRow(i);
							break;
						}
					}
					
					setLastEvent(new Formatter().format(GameLanguage.getString("server.playerLogout"), op.getPlayerName()).toString());
				}
			}		
		);
		
		server.setUserReadyEventListener(
			new InternalServerListener() {
				public void execute(Object... params) {
					// remove player from the table.
					OnlinePlayer op = (OnlinePlayer)params[0];
					DefaultTableModel dataModel = (DefaultTableModel)tOnlinePlayers.getModel();
					for (int i = 0; i < dataModel.getRowCount(); i++) {
						if (dataModel.getValueAt(i, 0).equals(String.valueOf(op.getPlayerId()))) {
							dataModel.setValueAt("Ready", i, 4);
							
							break;
						}
					}
					
					setLastEvent(new Formatter().format(GameLanguage.getString("server.playerReady"), op.getPlayerName()).toString());
				}
			}
		);
		
		server.setUserUnReadyEventListener(
			new InternalServerListener() {
				public void execute(Object... params) {
					// remove player from the table.
					OnlinePlayer op = (OnlinePlayer)params[0];
					DefaultTableModel dataModel = (DefaultTableModel)tOnlinePlayers.getModel();
					for (int i = 0; i < dataModel.getRowCount(); i++) {
						if (dataModel.getValueAt(i, 0).equals(String.valueOf(op.getPlayerId()))) {
							dataModel.setValueAt("Normal", i, 4);
							
							break;
						}
					}
					
					setLastEvent(new Formatter().format(GameLanguage.getString("server.playerNotReady"), op.getPlayerName()).toString());
				}
			}
		);
		
		server.setServerStatusChangedListener(
			new InternalServerListener() {
				public void execute(Object... params) {
					ServerStatus ss = (ServerStatus)params[0];
					if (ss == ServerStatus.INGAME) {
						setTitle(APP_TITLE + " - " + GameLanguage.getString("server.inGame"));
						setLastEvent(GameLanguage.getString("server.statusChangedToInGame"));
					}
					else {
						setTitle(APP_TITLE + " - " + GameLanguage.getString("server.waitingForConnections"));
						setLastEvent(GameLanguage.getString("server.statusChangedToWaitingForConnections"));
					}
				}
			}		
		);
		
		server.setUserInBattleEventListener(
				new InternalServerListener() {
					public void execute(Object... params) {
						OnlinePlayer op = (OnlinePlayer)params[0];
						DefaultTableModel dataModel = (DefaultTableModel)tOnlinePlayers.getModel();
						for (int i = 0; i < dataModel.getRowCount(); i++) {
							if (dataModel.getValueAt(i, 0).equals(String.valueOf(op.getPlayerId()))) {
								dataModel.setValueAt(GameLanguage.getString("server.inBattle"), i, 4);
								
								break;
							}
						}
						
						setLastEvent(new Formatter().format(GameLanguage.getString("server.playerReadyToPlayInBattle"), op.getPlayerName()).toString());
					}
				}
			);
	}
}
