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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.captiveimagination.jgn.ConnectionException;
import com.captiveimagination.jgn.Updatable;
import com.captiveimagination.jgn.clientserver.JGNClient;
import com.captiveimagination.jgn.clientserver.JGNConnection;
import com.captiveimagination.jgn.clientserver.JGNConnectionListener;
import com.captiveimagination.jgn.clientserver.JGNServer;
import com.captiveimagination.jgn.event.MessageListener;
import com.captiveimagination.jgn.message.Message;
import com.captiveimagination.jgn.synchronization.GraphicalController;
import com.captiveimagination.jgn.synchronization.SyncObjectManager;
import com.captiveimagination.jgn.synchronization.message.SynchronizeCreateMessage;
import com.captiveimagination.jgn.synchronization.message.SynchronizeMessage;
import com.captiveimagination.jgn.synchronization.message.SynchronizeRemoveMessage;
import com.captiveimagination.jgn.synchronization.message.SynchronizeRequestIDMessage;

/**
 * Custom implementation of JGN SynchronizationManager of the author Matthew D. Hicks.
 * This implementation improve the objects synchronization. 
 * @author Germano Fronza
 */
public class SynchronizationManager implements Updatable, MessageListener, JGNConnectionListener {
	private JGNServer server;
	private JGNClient client;
	
	private Queue<Short> used;				// Only used by server
	private Queue<SyncWrapper> idQueue;		// Client only - when waiting for a syncObjectId
	
	@SuppressWarnings("unchecked")
	private GraphicalController controller;
	
	private Queue<SyncWrapper> queue;
	private Queue<SyncWrapper> disabled;
	private Queue<SyncWrapper> passive;
	
	private Queue<SynchronizeCreateMessage> createQueue;
	private Queue<SynchronizeRemoveMessage> removeQueue;
	
	private Queue<SyncObjectManager> objectManagers;
	
	private boolean keepAlive;
	
	@SuppressWarnings("unchecked")
	public SynchronizationManager(JGNServer server, GraphicalController controller) {
		this.server = server;
		this.controller = controller;
		server.addMessageListener(this);
		server.addClientConnectionListener(this);
		
		used = new ConcurrentLinkedQueue<Short>();
		
		init();
	}
	
	@SuppressWarnings("unchecked")
	public SynchronizationManager(JGNClient client, GraphicalController controller) {
		this.client = client;
		this.controller = controller;
		client.addMessageListener(this);
		
		idQueue = new ConcurrentLinkedQueue<SyncWrapper>();
		
		init();
	}
	
	private void init() {
		keepAlive = true;
		queue = new ConcurrentLinkedQueue<SyncWrapper>();
		disabled = new ConcurrentLinkedQueue<SyncWrapper>();
		passive = new ConcurrentLinkedQueue<SyncWrapper>();
		objectManagers = new ConcurrentLinkedQueue<SyncObjectManager>();
		
		createQueue = new ConcurrentLinkedQueue<SynchronizeCreateMessage>();
		removeQueue = new ConcurrentLinkedQueue<SynchronizeRemoveMessage>();
	}
	
	/**
	 * Register an object authoritative from this peer.
	 * 
	 * @param object
	 * @param createMessage
	 * @param updateRate
	 * @throws IOException 
	 */
	public void register(Object object, SynchronizeCreateMessage createMessage, long updateRate) throws IOException {
		// Get player id
		short playerId = 0;
		if (client != null) {
			playerId = client.getPlayerId();
		}
		System.out.println("PlayerID: " + playerId);
		
		// Create SyncWrapper
		SyncWrapper wrapper = new SyncWrapper(object, updateRate, createMessage, playerId);
		if (server != null) {		// Server registering - we have the ids
			short id = serverNextId();
			wrapper.setId(id);
			wrapperFinished(wrapper);
		} else {					// Client registering - ask the server for an id
			// Create a request message for an id
			SynchronizeRequestIDMessage request = new SynchronizeRequestIDMessage();
			request.setRequestType(SynchronizeRequestIDMessage.REQUEST_ID);
			long id = client.sendToServer(request);
			wrapper.setWaitingId(id);
			System.out.println("Sent request to server for id");
			
			// Add it to the waiting queue
			idQueue.add(wrapper);
		}
	}
	
	private void wrapperFinished(SyncWrapper wrapper) {
		// Set object id
		wrapper.getCreateMessage().setSyncObjectId(wrapper.getId());
		
		// Send create message onward
		if (client != null) {
			client.broadcast(wrapper.getCreateMessage());
		} else {
			server.sendToAll(wrapper.getCreateMessage());
		}
		
		// Add to manager to be updated
		queue.add(wrapper);
	}
	
	/**
	 * Unregister an object and remove from remote clients
	 * 
	 * @param object
	 * @return
	 * 		true if removed
	 */
	public boolean unregister(Object object) {
		// Find SyncWrapper
		SyncWrapper wrapper = findWrapper(object);
		if (wrapper == null) return false;

		boolean sendRemove = queue.contains(wrapper);
		
		// Remove remotely
		SynchronizeRemoveMessage remove = new SynchronizeRemoveMessage();
		remove.setSyncObjectId(wrapper.getId());
		if (client != null) {
			if (sendRemove) {
				client.broadcast(remove);
				
				// Release id
				SynchronizeRequestIDMessage release = new SynchronizeRequestIDMessage();
				release.setRequestType(SynchronizeRequestIDMessage.RELEASE_ID);
				release.setSyncObjectId(wrapper.getId());
				client.sendToServer(release);
			}
		} else {
			server.sendToAll(remove);
			
			// Release id
			serverReleaseId(wrapper.getId());
		}
		
		// Remove from self
		if (queue.remove(wrapper)) {
			return true;
		} else if (passive.remove(wrapper)) {
			return true;
		}
		return disabled.remove(wrapper);
	}
	
	/**
	 * Re-enable a disabled object.
	 * 
	 * @param object
	 */
	public void enable(Object object) {
		SyncWrapper wrapper = findWrapper(object);
		disabled.remove(wrapper);
		queue.add(wrapper);
	}
	
	/**
	 * Stop sending updates for a sync object.
	 * 
	 * @param object
	 */
	public void disable(Object object) {
		SyncWrapper wrapper = findWrapper(object);
		queue.remove(wrapper);
		disabled.add(wrapper);
	}
	
	private final synchronized short serverNextId() {
		short id = (short)1;
		while (used.contains(id)) {
			id++;
		}
		used.add(id);
		return id;
	}
	
	private final void serverReleaseId(short id) {
		used.remove(id);
	}
	
	private SyncWrapper findWrapper(Object object) {
		SyncWrapper wrapper = null;
		for (SyncWrapper sync : queue) {
			if (sync.getObject() == object) {
				wrapper = sync;
				break;
			}
		}
		if (wrapper == null) {
			for (SyncWrapper sync : disabled) {
				if (sync.getObject() == object) {
					wrapper = sync;
					break;
				}
			}
		}
		if (wrapper == null) {
			for (SyncWrapper sync : passive) {
				if (sync.getObject() == object) {
					wrapper = sync;
					break;
				}
			}
		}
		return wrapper;
	}
	
	private SyncWrapper findWrapper(short syncObjectId) {
		SyncWrapper wrapper = null;
		for (SyncWrapper sync : queue) {
			if (sync.getId() == syncObjectId) {
				wrapper = sync;
				break;
			}
		}
		if (wrapper == null) {
			for (SyncWrapper sync : disabled) {
				if (sync.getId() == syncObjectId) {
					wrapper = sync;
					break;
				}
			}
		}
		if (wrapper == null) {
			for (SyncWrapper sync : passive) {
				if (sync.getId() == syncObjectId) {
					wrapper = sync;
					break;
				}
			}
		}
		return wrapper;
	}
	
	public void addSyncObjectManager(SyncObjectManager som) {
		objectManagers.add(som);
	}
	
	public boolean removeSyncObjectManager(SyncObjectManager som) {
		return objectManagers.remove(som);
	}
	
	/**
	 * Called internally when a SynchronizeCreateMessage is received
	 * 
	 * @param message
	 */
	public Object create(SynchronizeCreateMessage message) {
		for (SyncObjectManager manager : objectManagers) {
			Object obj = manager.create(message);
			if (obj != null) {
				// Create SyncWrapper
				SyncWrapper wrapper = new SyncWrapper(obj, 0, message, message.getPlayerId());
				wrapper.setId(message.getSyncObjectId());
				passive.add(wrapper);
				return obj;
			}
		}
		return null;
	}
	
	/**
	 * Called internally when a SynchronizeRemoveMessage is received
	 * 
	 * @param message
	 */
	public Object remove(SynchronizeRemoveMessage message) {
		SyncWrapper wrapper = findWrapper(message.getSyncObjectId());
		for (SyncObjectManager manager : objectManagers) {
			if (manager.remove(message, wrapper.getObject())) {
				unregister(wrapper.getObject());
				return true;
			}
		}
		return false;
	}

	public boolean isAlive() {
		return keepAlive;
	}

	public void update() throws Exception {
		// Create objects
		SynchronizeCreateMessage createMessage;
		while ((createMessage = createQueue.poll()) != null) {
			create(createMessage);
		}

		// Remove objects
		SynchronizeRemoveMessage removeMessage;
		while ((removeMessage = removeQueue.poll()) != null) {
			remove(removeMessage);
		}
		
		for (SyncWrapper sync : queue) {
			if (server != null) {
				sync.update(server, controller);
			} else {
				try { 
					if (client.isAlive() && client != null) {
						sync.update(client, controller);
					}
				} catch (ConnectionException ce) {
					// server or cliente disconnected... ignore
				} catch (NullPointerException ne) {
					// server or cliente disconnected... ignore
				}
			}
		}
	}
	
	public void shutdown() {
		keepAlive = false;
	}

	public void messageCertified(Message message) {
	}

	public void messageFailed(Message message) {
	}

	@SuppressWarnings("unchecked")
	public void messageReceived(Message message) {
		if (message instanceof SynchronizeCreateMessage) {
			createQueue.add((SynchronizeCreateMessage)message);
		} else if (message instanceof SynchronizeRemoveMessage) {
			removeQueue.add((SynchronizeRemoveMessage)message);
		} else if (message instanceof SynchronizeMessage) {
			SynchronizeMessage m = (SynchronizeMessage)message;
			SyncWrapper wrapper = findWrapper(m.getSyncObjectId());
			if (wrapper == null) {
				System.out.println("Unable to find object: " + m.getSyncObjectId() + " on " + (server != null ? "Server" : "Client " + client.getPlayerId()));
				return;
			}
			Object obj = wrapper.getObject();
			if (controller.validateMessage(m, obj)) {
				// Successfully validated synchronization message
				controller.applySynchronizationMessage(m, obj);
			} else {
				// Failed validation, so we ignore the message and send back our own
				m = controller.createSynchronizationMessage(obj);
				m.setSyncObjectId(((SynchronizeMessage)message).getSyncObjectId());
				message.getMessageClient().sendMessage(m);
			}
		} else if (message instanceof SynchronizeRequestIDMessage) {
			SynchronizeRequestIDMessage request = (SynchronizeRequestIDMessage)message;
			if (request.getRequestType() == SynchronizeRequestIDMessage.REQUEST_ID) {
				short id = serverNextId();
				request.setRequestType(SynchronizeRequestIDMessage.RESPONSE_ID);
				request.setSyncObjectId(id);
				request.getMessageClient().sendMessage(request);
				System.out.println("Server provided id: " + id);
			} else if (request.getRequestType() == SynchronizeRequestIDMessage.RESPONSE_ID) {
				for (SyncWrapper wrapper : idQueue) {
					if (wrapper.getWaitingId() == request.getId()) {
						wrapper.setId(request.getSyncObjectId());
						System.out.println("Received id from server: " + request.getSyncObjectId());
						wrapperFinished(wrapper);
						idQueue.remove(wrapper);
						break;
					}
				}
			} else if (request.getRequestType() == SynchronizeRequestIDMessage.RESPONSE_ID) {
				serverReleaseId(request.getSyncObjectId());
			}
		}
	}

	public void messageSent(Message message) {
	}

	public void connected(JGNConnection connection) {
		// Client connected to server - send creation messages to new connection
		System.out.println("**************** CONNECTED!");
		for (SyncWrapper wrapper : queue) {
			connection.sendMessage(wrapper.getCreateMessage());
		}
		for (SyncWrapper wrapper : disabled) {
			connection.sendMessage(wrapper.getCreateMessage());
		}
		for (SyncWrapper wrapper : passive) {
			connection.sendMessage(wrapper.getCreateMessage());
		}
	}

	public void disconnected(JGNConnection connection) {
		// Remove all connections associated with this player
		short playerId = connection.getPlayerId();
		for (SyncWrapper wrapper : passive) {
			if (wrapper.getOwnerPlayerId() == playerId) {
				SynchronizeRemoveMessage removeMessage = new SynchronizeRemoveMessage();
				removeMessage.setSyncObjectId(wrapper.getId());
				removeQueue.add(removeMessage);
			}
		}
	}
}