/* 
 ******************************************************************************
 * H2DbLib provides a simple connection pool for establishing connections to 
 * an embedded H2 database.
 * This file is part of H2DBLib.
 *  
 * Copyright (c) 2016 Robert W. Anderson.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert W. Anderson - initial API and implementation and/or initial documentation
 *    
 * EXCEPT AS EXPRESSLY SET FORTH IN THIS AGREEMENT, THE PROGRAM IS PROVIDED ON 
 * AN "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER 
 * EXPRESS OR IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR 
 * CONDITIONS OF TITLE, NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A 
 * PARTICULAR PURPOSE.   
 ******************************************************************************
 */
package net.bobs.own.db.h2.resources;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	
	private static final String BUNDLE_NAME = "net.bobs.own.db.h2.resources.messages";
	
	static public  	String	h2database_jdbc_url;
	static public  	String	h2database_jdbc_url_nopass;
	static public  	String	errormsg_sauserid_invalid;
	static public	String	errormsg_preference_exist;
	static public	String	errormsg_nopreference_found;
	static public	String	errormsg_noproperty_exist;  
	static public	String	errormsg_noconnections;
	static public	String	H2InitPool_Message;
	static public	String	HikariInitPool_Message;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
//	public static ResourceBundle loadBundle() {
//		return ResourceBundle.getBundle(BUNDLE_NAME);
//	}
//	
//	public static String getString(String id) {
//
//		String msg = null;
//		
//		try {
//			ResourceBundle bundle = loadBundle();
//			msg = bundle.getString(id);
//		} catch (MissingResourceException mex) {
//			msg = "!" + id + "!";
//			System.out.println("msg=" + msg);
//		}
//		return msg;
//
//	}
//	
//	public static String getString(String id,Object... parms) {
//
//		String msg = null;
//		
//		try {
//			ResourceBundle bundle = loadBundle();
//			msg = MessageFormat.format(bundle.getString(id),parms);
//		} catch (MissingResourceException mex) {
//			msg = "!" + id + "!";
//			System.out.println("msg=" + msg);
//		}
//		return msg;
//	}

}
