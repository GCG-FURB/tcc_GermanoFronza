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
package br.furb.inf.tcc.tankcoders.scene.battle;

import com.jme.scene.Node;

/**
 * Used to show the current time in the Panel.
 * @author Germano Fronza 
 */
public class Stopwatch extends Node {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Singleton
	 */
	private static Stopwatch instance;
	
	private long accumulatedTime, start;
	private boolean isCounting;
	private StringBuffer formattedTime;
	
	/**
	 * Singleton factory method
	 * @return
	 */
	public static Stopwatch getInstance(){
		if (instance == null)
			instance = new Stopwatch();
		return instance;
	}
	
	/**
	 * Private constructor.
	 */
	private Stopwatch(){
		isCounting = false;
		formattedTime = new StringBuffer(10);
	}
	/**
	 * Updates accumulated time in milli
	 */
	private void update(){
		if (isCounting){
			long actualTime = System.currentTimeMillis();
			accumulatedTime = actualTime - start;
		}
	}
	
	/**
	 * Start the counter.
	 */
	public void startCounter() {
		if (!isCounting){
			isCounting = true;
			start = System.currentTimeMillis();
		}
	}

	public void stopCounter() {
		isCounting = false;
	}
	
	public StringBuffer getFormattedTime(){
		update();
		int dec = (int) accumulatedTime % 1000;
		int sec = (int) (accumulatedTime/1000) % 60;
		int min =  (int) accumulatedTime/60000;
		formattedTime.setLength(0);
		if (min < 10) {
			formattedTime.append("0");
		}
		
		formattedTime.append(min+":");
		
		if (sec < 10) {
			formattedTime.append("0");
		}
		
		formattedTime.append(sec+":"+dec);
		
		return formattedTime;
	}
	
}
