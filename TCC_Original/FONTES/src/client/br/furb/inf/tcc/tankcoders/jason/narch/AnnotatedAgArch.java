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
package br.furb.inf.tcc.tankcoders.jason.narch;

import jason.architecture.AgArch;
import jason.asSemantics.ActionExec;
import jason.asSyntax.Structure;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom agent architecture to use annotated methods as agent actions.<br>
 * Example:
 * <code>
 * 	  	@Action
 *  	public void moveUp(Term[] terms) {
 *  		System.out.println("Called when agent needs to execute moveUp");
 *      }
 *      
 *      @Action("moveDown")
 *      public void moveDownImpl2(Term[] terms) {
 *      	System.out.println("The annotation @Action also allows inform the action name");
 *      }
 * </code>
 * @author Germano Fronza
 * @version 1.0.0.0 (03/05/2008)
 */
public class AnnotatedAgArch extends AgArch {

	/**
	 * Map of all annotated methods of the subclass.
	 */
	private Map<String, Method> annotatedMethods;
	
	/**
	 * Creates a new AnnotatedAgArch.
	 * Loads into the map all the annotated methods of the subclass.
	 */
	public AnnotatedAgArch() {
		annotatedMethods = new HashMap<String, Method>();
		
		Method[] allMethods = this.getClass().getDeclaredMethods();
		for (Method method : allMethods) {
			if (method.isAnnotationPresent(Action.class)) {
				String actionName = method.getAnnotation(Action.class).value();
				if (actionName.equals("")) {
					actionName = method.getName();
				}
				annotatedMethods.put(actionName, method);
			}
		}
	}
	
	/**
	 * This method is called when agent wants to execute an action.
	 */
	public void act(ActionExec action, List<ActionExec> feedback) {
		super.act(action, feedback);
		Structure actionTerm = action.getActionTerm();
		String functor = actionTerm.getFunctor();
		Method m = annotatedMethods.get(functor);
		if (m != null) {
			try {
				m.invoke(this, (Object)actionTerm.getTermsArray());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}	
}
