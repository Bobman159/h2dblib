/* 
 ******************************************************************************
 * H2DbLib provides a simple connection pool for establishing connections to 
 * an embedded H2 database.
 * This file is part of H2DBLib.
 *  
 * Copyright (c) 2016-2017 Robert W. Anderson.
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
package net.bobs.own.db.h2.pool;

import java.sql.Clob;

import net.bobs.own.db.h2.db.H2AbstractDatabaseService;

/**
 * An API for the execution of a an SQL statement in an H2 database.  This class follows a 
 * Singleton pattern usage model, so that only 1 use of the service is active.
 * 
 * 
 * @author Robert Anderson
 *
 */
public class H2Database extends H2AbstractDatabaseService {

	public H2Database(IH2ConnectionPool pool) {
	   super(pool);
	}

//	@Override
//	public IH2ConnectionPool getPool(String poolId) {
//		return H2PoolController.getPool(poolId);
//	}

}
