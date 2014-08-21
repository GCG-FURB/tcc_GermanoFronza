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
package br.furb.inf.tcc.tankcoders;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fenggui.Display;
import org.fenggui.composites.MessageWindow;
import org.fenggui.layout.StaticLayout;

import br.furb.inf.tcc.tankcoders.client.GameClient;
import br.furb.inf.tcc.tankcoders.client.NVEHandler;
import br.furb.inf.tcc.tankcoders.game.PlayerType;
import br.furb.inf.tcc.tankcoders.game.StartGameArguments;
import br.furb.inf.tcc.tankcoders.menu.GameMenuState;
import br.furb.inf.tcc.tankcoders.menu.JoinGameMenuGUI;
import br.furb.inf.tcc.tankcoders.scene.AvatarPlayerInGameState;
import br.furb.inf.tcc.tankcoders.scene.InGameState;
import br.furb.inf.tcc.tankcoders.scene.MasPlayerInGameState;
import br.furb.inf.tcc.util.game.GamePersistentProperties;
import br.furb.inf.tcc.util.lang.GameLanguage;

import com.jme.util.GameTaskQueueManager;
import com.jme.util.LoggingSystem;
import com.jmex.game.StandardGame;
import com.jmex.game.state.GameStateManager;

/**
 * Main Class of the game.
 * @author Germano Fronza
 * @version 0.0.0.1 (2008-02-04)
 */
public class TankCoders extends StandardGame {

	/**
	 * Singleton game instance.
	 */
	private static TankCoders gameInstance;
	
	/**
	 * Flag for debug stuffs.
	 */
	private static boolean debug;
	
	/**
	 * Server of the game is in localhost (127.0.0.1).<br>
	 * This option must be used when the machine isn't connected in 
	 * any Ethernet network.
	 */
	private static boolean serverLocal;
	
	/**
	 * Log4j instance.
	 */
	private static Logger logger;
	
	/**
	 * List of agents informed in the argument -mas.
	 */
	private String[] agents;
	
	/**
	 * Main Menu State. 
	 * Provides some options for user start the game.
	 */
	private GameMenuState mainMenuState;
	
	/**
	 * In Game State.
	 */
	private InGameState inGameState;
	
	/**
	 * Creates a new TankCoders game.
	 */
	private TankCoders(String[] args) {
		super("Tank Coders - Smart agents that matters");
		
		gameInstance = this;
		
		checkArgs(args);
		prepareLogger();
		
		setWindowSettings();
		initGameComponents();
		start();
	}
	
	/**
	 * Prepare logger to see the important stuff
	 */
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

	/**
	 * Check application arguments:<br>
	 * <ul>
	 *  <li>-debug</li>
	 *  <li>-serverLocal</li>
	 *  <li>-mas <i>comma-separeted-agent-names-list</i></li>
	 * </ul>
	 * @param String[] args
	 */
	private void checkArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-debug")) {
				debug = true;
			}
			else if (args[i].equals("-serverLocal")) {
				serverLocal = true;
			}
			else if (args[i].equals("-mas")) {
				agents = args[i+1].split(",");
				i++;
			}
		}
	}

	/**
	 * Creates the initial scene objects for the game.
	 */
	private void initGameComponents() {
		GameStateManager.create();
		
		createMainMenu();
	}
	
	/**
	 * Create the Main Menu with the items:
	 * <ul>
	 *   <li>Join Game</li>
	 *   <li>Instructions</li>
	 *   <li>Options</li>
	 *   <li>Credits</li>
	 *   <li>Quit</li>
	 *  </ul>
	 */
	private void createMainMenu() {
		try {
			logger.info(GameLanguage.getString("gameState.startingMainMenuGS"));
            GameTaskQueueManager.getManager().update(new Callable<Object>() {
                public Object call() throws Exception {
                	mainMenuState = new GameMenuState();
                	mainMenuState.setActive(true);
                    
                	if (inGameState != null) {
                		inGameState.setActive(false);
                		GameStateManager.getInstance().detachChild(inGameState);
                	}
                	
                	GameStateManager.getInstance().attachChild(mainMenuState);
                	logger.info(GameLanguage.getString("gameState.mainMenuGSCreated"));
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * Switch current game state to InGameState.
	 */
	public void changeToInGameState(StartGameArguments gameArgs) {		
		mainMenuState.setActive(false);
		GameStateManager.getInstance().detachChild(mainMenuState);
		
		if (gameArgs.getLocalPlayer().getType() == PlayerType.AVATAR) {
			inGameState = new AvatarPlayerInGameState(gameArgs);
		}
		else {
			inGameState = new MasPlayerInGameState(gameArgs);
		}
		
		inGameState.setActive(true);
		GameStateManager.getInstance().attachChild(inGameState);
	}
	
	/**
	 * Switch current game state to MenuGameState.
	 */
	public void changeToMenuState(final String disconnectionCause) {
		try {
			logger.info(GameLanguage.getString("gameState.startingMainMenuGS"));
            GameTaskQueueManager.getManager().update(new Callable<Object>() {
                public Object call() throws Exception {
                	inGameState.setActive(false);
            		GameStateManager.getInstance().detachChild(inGameState);
            		
            		mainMenuState.setActive(true);
            		GameStateManager.getInstance().attachChild(mainMenuState);
            		
            		Display menuDisplay = mainMenuState.getDisplay();
            		new JoinGameMenuGUI().buildGUI(menuDisplay);
            		
            		if (disconnectionCause != null) {
            			MessageWindow mw = new MessageWindow(disconnectionCause);
            			mw.setTitle(GameLanguage.getString("windowMessage.warning"));
            			mw.pack();
            			menuDisplay.addWidget(mw);
            			StaticLayout.center(mw, menuDisplay);
            		}
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	/**
	 * Defines the window settings.
	 */
	private void setWindowSettings() {
		// everytime clear the last settings.
		try {
			this.getSettings().clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// retrieve the game persistent properties.
		GamePersistentProperties prop = GamePersistentProperties.getInstance();
		
		// windows settings
		this.getSettings().setRenderer(prop.getRenderer());
		this.getSettings().setFrequency(prop.getDisplayFrequency());
		this.getSettings().setWidth(prop.getDisplayWidth());
		this.getSettings().setHeight(prop.getDisplayHeight());
		this.getSettings().setFullscreen(prop.isFullscreen());
		this.getSettings().setDepth(prop.getDepth());
	}
	
	/**
	 * Called internally when user quit game.
	 */
	protected void quit() {
		super.quit();
		quitGame();
	}
	
	/**
	 * If connected then disconnect from server.
	 */
	public void quitGame() {
		GameClient gameClient;
		try {
			gameClient = GameClient.getInstance();
			if (gameClient.isConnected()) {
				gameClient.disconnectFromServer();
				gameClient.closeClientSokects();
				NVEHandler.unregisterTanksInNecessary();
				
				Thread.sleep(4000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// disconnect client from server game.
		System.exit(9);
	}
	
	/**
	 * Gets the singleton instance of this class.
	 * @return TankCoders
	 */
	public static TankCoders getGameInstance() {
		return gameInstance;
	}

	/**
	 * Checks if is in debug mode.
	 * @return boolean
	 */
	public static boolean isDebug() {
		return debug;
	}

	/**
	 * Gets log4j instance.
	 * @return Logger
	 */
	public static Logger getLogger() {
		return logger;
	}

	/**
	 * Checks if must connect in local server (127.0.0.1)
	 * @see TankCoders.serverLocal
	 * @return boolean
	 */
	public static boolean isServerLocal() {
		return serverLocal;
	}

	/**
	 * Checks if this game instance was started by a MAS project of Jason BDI engine.
	 * @return
	 */
	public boolean isMAS() {
		return agents != null;
	}
	
	/**
	 * Gets the list of agents which composes the MAS.
	 * @return String[]
	 */
	public String[] getAgents() {
		return agents;
	}
	
	/**
	 * Start point of the game.
	 * @param String args.<br>
	 * <b>Options:</b>
	 * <ul>
	 *  <li>-debug</li>
	 *  <li>-serverLocal</li>
	 *  <li>-mas <i>comma-separeted-agent-names-list</i></li>
	 * </ul>
	 */
	public static void main(String[] args) {
		new TankCoders(args);
	}
}
