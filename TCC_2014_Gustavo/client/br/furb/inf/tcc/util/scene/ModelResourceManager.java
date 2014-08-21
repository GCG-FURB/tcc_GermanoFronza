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
package br.furb.inf.tcc.util.scene;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.jme.util.TextureKey;
import com.jmex.model.XMLparser.XMLtoBinary;
import com.jmex.model.XMLparser.Converters.ObjToJme;

/**
 * This class manage all the model resources of the game.
 * Each model is once loaded from the disk and then cached. 
 * @author Germano Fronza
 */
public class ModelResourceManager {

	/**
	 * Singleton instance
	 */
	private static ModelResourceManager instance;
	
	/**
	 * Resources map.
	 */
	private Map<String, ByteArrayOutputStream> resources;
	
	/**
	 * Creates the resource map.
	 */
	private ModelResourceManager() {
		super();
		resources = new HashMap<String, ByteArrayOutputStream>();
	}
	
	private static ModelResourceManager getInstance() {
		// lazy instantiation
		if (instance == null) {
			instance = new ModelResourceManager();
		}
		
		return instance;
	}
	
	/**
	 * Gets a resource (from the disk or from the cache).
	 * @param filePath
	 * @return ByteArrayOutputStream
	 */
	public static ByteArrayOutputStream getBinaryResourceByXML(String filePath) {
		ModelResourceManager instance = getInstance();
		if (instance.resources.containsKey(filePath)) {
			return instance.resources.get(filePath);
		}
		else {
			URL modelURL = ModelUtils.class.getClassLoader().getResource(filePath);
			XMLtoBinary converter = new XMLtoBinary();
			ByteArrayOutputStream binaryOutput = new ByteArrayOutputStream();
			try {
			    converter.sendXMLtoBinary(modelURL.openStream(), binaryOutput);
			    
			    instance.resources.put(filePath, binaryOutput);
			    return binaryOutput;
			} catch (IOException ex) {
			    ex.printStackTrace();
			    return null;
			}
		}
	}
	
	/**
	 * Gets a resource (from the disk or from the cache).
	 * @param filePath
	 * @return ByteArrayOutputStream
	 */
	public static ByteArrayOutputStream getBinaryResourceByObj(String filePath) {
		ModelResourceManager instance = getInstance();
		if (instance.resources.containsKey(filePath)) {
			return instance.resources.get(filePath);
		}
		else {
			String rsrcPath = filePath.substring(0, filePath.lastIndexOf("/")+1);
			
			URL textureFolderPath = ModelUtils.class.getClassLoader().getResource("./" + rsrcPath);
			TextureKey.setOverridingLocation(textureFolderPath);
			
			ObjToJme converter = new ObjToJme();
			try {
				URL objFile = ModelUtils.class.getClassLoader().getResource(filePath);			
				converter.setProperty("mtllib", ModelUtils.class.getClassLoader().getResource(rsrcPath));
				converter.setProperty("texdir", ModelUtils.class.getClassLoader().getResource(rsrcPath));
				ByteArrayOutputStream binaryOutput = new ByteArrayOutputStream();
				converter.convert(objFile.openStream(), binaryOutput);
	            
				instance.resources.put(filePath, binaryOutput);
				return binaryOutput;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
