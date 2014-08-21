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
package br.furb.inf.tcc.util.jgn;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.furb.inf.tcc.tankcoders.TankCoders;

import com.captiveimagination.jgn.JGN;

/**
 * Central point to register class messages.
 * @author Germano Fronza
 */
public class JGNRegisterUtil {

	/**
	 * Register all the class messages of the package br.furb.inf.tcc.tankcoders.message
	 */
	@SuppressWarnings("unchecked")
	public static void registerTankCodersClassMessages() {
		try {
			List<Class> classMessages = getClasses("br.furb.inf.tcc.tankcoders.message");
			for (Class classMsg : classMessages) {
				JGN.register(classMsg);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets all the classes from a given package name.
	 * @param pckgname
	 * @return
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static List<Class> getClasses(String pckgname) throws ClassNotFoundException {  
		List<Class> classes = new ArrayList();
		
		// Get a file object for the package  
		File directory = null;  
		try {  
			directory = new File(Thread.currentThread().getContextClassLoader().getResource(pckgname.replace('.', '/')).getFile());  
		} catch(NullPointerException x) {  
			throw new ClassNotFoundException(pckgname + " does not appear to be a valid package!");  
		}  
		
		if(directory.exists()) {  
			// Get the list of the files contained in the package  
			String[] files = directory.list();  
			for(int i = 0; i < files.length; i++) {  
				// we are only interested in .class files  
				if(files[i].endsWith(".class")) {  	        
					classes.add(Class.forName(pckgname + '.' + files[i].substring(0, files[i].length()-6)));  
				}  
			}  
		} else {  
			TankCoders.getLogger().info(pckgname + " does not appear to be a valid package, the messages won't be registered");  
		}  
		  
		return classes;
	}  
}
