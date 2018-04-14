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
package net.bobs.own.db.h2.db;

import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import net.bobs.own.db.h2.exceptions.NoPreferenceException;
import net.bobs.own.db.h2.pool.IH2ConnectionPool;

/**
 * Base class for H2 Database Services.  The main purpose of this service is to 
 * allow for simpler coding of JDBC code to connect, prepare, bind and execute
 * SQL statements.  The class uses <code>PreparedStatement</code> objects from 
 * the java.sql package but could be extended to support <code>CallableStatement</code>
 * if needed.
 * 
 * <b>Connections are NOT released by this class, it's is client (callers) responsibility to release 
 * connections.</b>
 * 
 * Adopted from code example from Thomas E. Davis article
 * 	URL: * http://www.javaworld.com/article/2076397/java-web-development/clever-facade-makes-jdbc-look-easy.html
 *  August 30, 2016
 *  
 * @author Robert Anderson
 *
 */

	/*	Adopted from code example from Thomas E. Davis article
	 * 	URL: * http://www.javaworld.com/article/2076397/java-web-development/clever-facade-makes-jdbc-look-easy.html
	 *  August 30, 2016
	 */
public abstract class H2AbstractDatabaseService {


	private PreparedStatement prep = null;
	private ResultSet  rset = null;
	private IH2ConnectionPool pool = null;

	/**
	 * Base Constructor for the service.
	 */
	public H2AbstractDatabaseService(IH2ConnectionPool pool) {
	   this.pool = pool;
		reset();
	}
	
	/**
	 * Create the <code>PreparedStatement</code> object for  the service.
	 * 
	 * 
	 * @param sqlText - The SQL statement text to be executed.
	 * @throws SQLException
	 * @throws NoPreferenceException 
	 */
	public void setSQL(String sqlText) throws SQLException, NoPreferenceException {
//		if (conn == null) {
//			conn = getConnection();
//		}
	   
		Connection conn = pool.getConnection();
		prep = conn.prepareStatement(sqlText);
	}
	
	/**
	 * Set a CLOB value for the SQL statement to be executed.
	 * 
	 * @param index - the parameter number index
	 * @param clob - the value to be used
	 * @throws SQLException
	 * @throws NoPreferenceException
	 */
	public void setClob(int index,Clob clob) throws SQLException, NoPreferenceException {
		prep.setClob(index, clob);
	}
	
	/**
	 * Set a string value for the SQL statement to be executed.
	 * 
	 * @param index - the parameter number index
	 * @param value - the value to be used.
	 * @throws SQLException
	 */
	public void setString(int index,String value) throws SQLException {
		prep.setString(index, value);
	}
	
	/**
	 * Set a decimal value for the SQL statement to be executed.
	 * 
	 * @param index - the parameter number index
	 * @param value - the value to be used.
	 * @throws SQLException
	 */
	public void setDecimal(int index,BigDecimal value) throws SQLException {
		prep.setBigDecimal(index, value);
	}
	
	/**
	 * Set a double value for the SQL statement to be executed.
	 * 
	 * @param index - the parameter number index
	 * @param value - the value to be used.
	 * @throws SQLException
	 */
	public void setDouble(int index, Double value) throws SQLException {
		prep.setDouble(index, value);
	}
	
	/**
	 * Set a Timestamp value for the SQL statement to be executed.
	 * 
	 * @param index - the parameter number index
	 * @param value - the value to be used.
	 * @throws SQLException
	 */
	public void setTimestamp(int index,Timestamp value) throws SQLException {
		
		prep.setTimestamp(index,value);
	}
	
	public void setDate(int index, Date value) throws SQLException {
		prep.setDate(index, value);
	}
	
	public void setInt(int index, int value) throws SQLException {
		prep.setInt(index, value);
	}
	
   public void setLong(int index, long value) throws SQLException {
      prep.setLong(index, value);
   }
	
	public void setNull(int index,int type) {
		try {
			prep.setNull(index, type);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ResultSet executeQuery() throws NoPreferenceException, SQLException {
		rset = prep.executeQuery();
//		releaseConnection(conn);
		return rset;
	}
	
	public int executeUpdate(Connection conn) throws NoPreferenceException, SQLException {
		int returnCode = prep.executeUpdate();
		prep.close();
//		releaseConnection(conn);
		return returnCode;
	}
	
	public AutoGeneratedKeysResult executeUpdate(Connection conn,boolean generatedKey) throws NoPreferenceException, SQLException {
		
		int returnCode = prep.executeUpdate(); 
		AutoGeneratedKeysResult keyResult = new AutoGeneratedKeysResult(returnCode);
		if (generatedKey) {
			AutoGeneratedKeysList keysList = keyResult.getAutoGeneratedKeys();
			ResultSet keys = prep.getGeneratedKeys();			
			while (keys.next()) {
				int autoKey = keys.getInt(1);
				keysList.add(autoKey);
			}
			keys.close();
		}
		prep.close();
//		releaseConnection(conn);
		return keyResult;
		
	}
	
	public boolean execute() throws SQLException {
		boolean hasResult = false;
		hasResult = prep.execute();
		prep.close();
		return hasResult;
	}
	
	public void commit(Connection conn) throws SQLException {
		PreparedStatement test = conn.prepareStatement("COMMIT");
		test.execute();
	}
 	
	public void reset() {

	}
	
//	protected abstract Connection getConnection(String poolId) throws SQLException, NoPreferenceException;
//	public abstract void releaseConnection(String poolId, Connection conn) throws SQLException, NoPreferenceException;
//	public abstract IH2ConnectionPool getPool(String poolId);	
	
}
