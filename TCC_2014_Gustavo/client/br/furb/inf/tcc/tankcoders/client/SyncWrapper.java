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

import com.captiveimagination.jgn.clientserver.JGNClient;
import com.captiveimagination.jgn.clientserver.JGNServer;
import com.captiveimagination.jgn.synchronization.GraphicalController;
import com.captiveimagination.jgn.synchronization.message.SynchronizeCreateMessage;
import com.captiveimagination.jgn.synchronization.message.SynchronizeMessage;

/**
 * Custom implementation of JGN SyncWrapper of the author Matthew D. Hicks. 
 * @author Germano Fronza
 */
class SyncWrapper {
	private short id;
	private long waitingId;
	
	private Object object;
	private long rate;
	private SynchronizeCreateMessage createMessage;
	private short ownerPlayerId;
	
	private long lastUpdate;
	
	public SyncWrapper(Object object, long rate, SynchronizeCreateMessage createMessage, short ownerPlayerId) {
		if (object == null) throw new RuntimeException("Object is null: " + id);
		
		this.object = object;
		this.rate = rate * 1000000;		// Convert to nanoseconds for better timing
		this.createMessage = createMessage;
		this.ownerPlayerId = ownerPlayerId;
		lastUpdate = System.nanoTime() - this.rate;		// Make ready for immediate update
	}
	
	public short getId() {
		return id;
	}

	public void setId(short id) {
		this.id = id;
	}
	
	public long getWaitingId() {
		return waitingId;
	}
	
	public void setWaitingId(long waitingId) {
		this.waitingId = waitingId;
	}
	
	public Object getObject() {
		return object;
	}

	public long getRate() {
		return rate;
	}

	public SynchronizeCreateMessage getCreateMessage() {
		return createMessage;
	}

	public short getOwnerPlayerId() {
		return ownerPlayerId;
	}
	
	@SuppressWarnings("unchecked")
	protected void update(JGNServer server, GraphicalController controller) {
		if (lastUpdate + rate < System.nanoTime()) {
			if (server.getConnections().length > 0) {
				SynchronizeMessage message = controller.createSynchronizationMessage(getObject());
				message.setSyncObjectId(getId());
				server.sendToAll(message);
			}
			
			lastUpdate = System.nanoTime();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void update(JGNClient client, GraphicalController controller) {
		if (lastUpdate + rate < System.nanoTime()) {
			SynchronizeMessage message = controller.createSynchronizationMessage(getObject());
			message.setSyncObjectId(getId());
			
			if (client.isAlive() && client != null) {
				client.broadcast(message);
			}
			
			lastUpdate = System.nanoTime();
		}
	}
}
