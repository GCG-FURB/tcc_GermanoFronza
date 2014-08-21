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
package br.furb.inf.tcc.tankcoders.jason;

import jason.environment.Environment;
import jason.infra.centralised.RunCentralisedMAS;
import jason.mas2j.AgentParameters;
import jason.mas2j.ClassParameters;

import java.util.List;

import br.furb.inf.tcc.tankcoders.TankCoders;
import br.furb.inf.tcc.util.exception.NotCentralisedInfraException;

/**
 * Custom implementation of Jason environment.
 * @author Germano Fronza
 */
public class TankCodersEnvironment extends Environment {

	/**
	 * This method is called before the MAS execution with the args informed in .mas2j
	 */
    @SuppressWarnings("unchecked")
	public void init(String[] args) {
    	
    	String agsParam = null;
    	RunCentralisedMAS runner = RunCentralisedMAS.getRunner();
    	if (runner != null) {
	    	List<AgentParameters> agParameters = runner.getProject().getAgents();
	    	
	    	// prepare a comma-separated list of agentNames
	    	for (AgentParameters agParameter : agParameters) {
	    		ClassParameters classPar = agParameter.archClass;
	    		if (classPar != null) {
	    			try {
						Class c = Class.forName(classPar.className);
						if (c.equals(TankAgArch.class)) {
							if (agParameter.qty == 1) {
				    			String agName = agParameter.name;
				    			agsParam = (agsParam == null) ? agName : agsParam.concat(",").concat(agName);
				    		}
				    		else {
				    			for (int i = 1; i <= agParameter.qty; i++) {
				    				String agName = agParameter.name.concat(String.valueOf(i));
					    			agsParam = (agsParam == null) ? agName : agsParam.concat(",").concat(agName);
				    			}
				    		}
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
	    		}
			}
	    	
	    	boolean debug = false;
	    	for (String arg : args) {
				if (arg.equals("-debug")) {
					debug = true;
					break;
				}
			}
	    	
	    	String[] tcArguments;
	    	if (debug) {
	    		tcArguments = new String[] {"-debug", "-mas", agsParam};
	    	}
	    	else {
	    		tcArguments = new String[] {"-mas", agsParam};
	    	}
	    	
	    	// invoke the TankCoders simulator with the -mas agents argument
	    	TankCoders.main(tcArguments);
    	}
    	else {
    		throw new NotCentralisedInfraException();
    	}
    }
}

