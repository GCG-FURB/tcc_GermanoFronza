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

import br.furb.inf.tcc.tankcoders.message.SynchronizeArticulatedObject3DMessage;
import br.furb.inf.tcc.tankcoders.scene.tank.AbstractTank;
import br.furb.inf.tcc.tankcoders.scene.tank.m1abrams.M1AbramsTank;
import br.furb.inf.tcc.tankcoders.scene.tank.panther.JadgPantherTank;

import com.captiveimagination.jgn.synchronization.*;
import com.captiveimagination.jgn.synchronization.message.*;
import com.jme.math.Vector3f;
import com.jme.scene.*;

/**
 * My Graphical controller.
 * This class is responsible for preparing messages to update objects (Tanks) and also 
 * apply the updates in objects.
 * @author Germano Fronza
 * 
 * Adapted from JMEGraphicalController of the author Matthew D. Hicks
 */
public class TankGraphicalController implements GraphicalController<Spatial> {
	
    public void applySynchronizationMessage(SynchronizeMessage message, Spatial spatial) {
    	Synchronize3DMessage m = (Synchronize3DMessage)message;
    	Spatial chassi = ((AbstractTank)spatial).getStaticChassi();
    	
    	chassi.getLocalTranslation().x = m.getPositionX();
    	chassi.getLocalTranslation().y = m.getPositionY();
    	chassi.getLocalTranslation().z = m.getPositionZ();
    	chassi.getLocalRotation().x = m.getRotationX();
    	chassi.getLocalRotation().y = m.getRotationY();
    	chassi.getLocalRotation().z = m.getRotationZ();
    	chassi.getLocalRotation().w = m.getRotationW();
        
        // M1Abrams also has a main gun (articulated object)
        if (spatial instanceof M1AbramsTank) {
        	Node mainGunNode = ((M1AbramsTank)spatial).getMainGun().getMainNode();
        	SynchronizeArticulatedObject3DMessage ma = (SynchronizeArticulatedObject3DMessage)message;
        	
        	mainGunNode.setLocalTranslation(chassi.getLocalTranslation().add(new Vector3f(-2.2f, 2.2f, -0.48f)));
        	
        	mainGunNode.getLocalRotation().x = ma.getMainGunRotationX();
        	mainGunNode.getLocalRotation().y = ma.getMainGunRotationY();
        	mainGunNode.getLocalRotation().z = ma.getMainGunRotationZ();
        	mainGunNode.getLocalRotation().w = ma.getMainGunRotationW();
        }
    }

    public SynchronizeMessage createSynchronizationMessage(Spatial spatial) {
        Synchronize3DMessage message = (spatial instanceof JadgPantherTank) ? 
        								new Synchronize3DMessage() : 
        								new SynchronizeArticulatedObject3DMessage();
        
        Spatial chassi = ((AbstractTank)spatial).getChassi();
        
        message.setPositionX(chassi.getLocalTranslation().x);
        message.setPositionY(chassi.getLocalTranslation().y);
        message.setPositionZ(chassi.getLocalTranslation().z);
        message.setRotationX(chassi.getLocalRotation().x);
        message.setRotationY(chassi.getLocalRotation().y);
        message.setRotationZ(chassi.getLocalRotation().z);
        message.setRotationW(chassi.getLocalRotation().w);
        
        // M1Abrams also has a main gun (articulated object)
        if ((spatial instanceof M1AbramsTank)) {
        	Node mainGunNode = ((M1AbramsTank)spatial).getMainGun().getMainNode();
        	SynchronizeArticulatedObject3DMessage ma = (SynchronizeArticulatedObject3DMessage)message;
        	
        	ma.setMainGunRotationX(mainGunNode.getLocalRotation().x);
        	ma.setMainGunRotationY(mainGunNode.getLocalRotation().y);
        	ma.setMainGunRotationZ(mainGunNode.getLocalRotation().z);
        	ma.setMainGunRotationW(mainGunNode.getLocalRotation().w);
        }
        
        return message;
    }

    /**
     * TODO: Change the return of this method provide better efficiency to synchronization.
     */
    public float proximity(Spatial spatial, short playerId) {
        return 1.0f;
    }

    /**
     * TODO: check this method to provide a layer of security.
     */
    public boolean validateMessage(SynchronizeMessage message, Spatial spatial) {
        return true;
    }

	public boolean validateCreate(SynchronizeCreateMessage message) {
		return true;
	}

	public boolean validateRemove(SynchronizeRemoveMessage message) {
		return true;
	}
}
