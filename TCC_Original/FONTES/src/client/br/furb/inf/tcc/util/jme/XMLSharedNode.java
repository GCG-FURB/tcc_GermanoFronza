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
package br.furb.inf.tcc.util.jme;

import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.RenderState;

/**
 * Started Date: Jun 5, 2004
 * Dummy class only for use with jME's file system.
 * 
 * @author Jack Lindamood
 */
class XMLSharedNode extends Node {
    private static final long serialVersionUID = 1L;
	String myIdent;
    Object whatIReallyAm;
    XMLSharedNode(String ident){
        super();
        myIdent=ident;
    }
    public int attachChild(Spatial c){
        whatIReallyAm=c;
        return 0;
    }

    public RenderState setRenderState(RenderState r){
        whatIReallyAm=r;
        return null;
    }

    public void addController(Controller c){
        whatIReallyAm=c;
    }
}