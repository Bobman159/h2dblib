package net.bobs.own.db.h2.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;

import net.bobs.own.db.h2.resources.Messages;

  class H2HikariConnectionPool implements IH2ConnectionPool {
		
	private Logger logger = LogManager.getLogger(H2HikariConnectionPool.class);
	private HikariDataSource ds;
	private String poolId = null;

	/**
	 * Creates a HikariCP backed connection pool using a properties file. 
	 * 
	 * @param prefKey - identifier for the database preferences to use
	 */

	public H2HikariConnectionPool(String path,String poolId)  {
		
		logger.debug(Messages.bind(Messages.HikariInitPool_Message, "properties file", path));
		String config_path = path;
		ds = null;
		HikariConfig config = null;
		config = new HikariConfig(config_path);
		ds = new HikariDataSource(config);
		this.poolId = poolId;
	}
	
	/**
	 * Creates a HikariCP backed connection pool using a java.util.Properties object
	 * 
	 * @param prop - properties of object of HikariCP configuration information
	 */
	public H2HikariConnectionPool(Properties prop,String poolId) {
		
		HikariConfig config = new HikariConfig(prop);
		ds = new HikariDataSource(config);
		this.poolId = poolId;
	}
	
	/**
	 * Creates a connection pool for the specified database path, user id 
	 * and maximum number of connections.
	 * 
	 * @param dbPath - the path identifying the JDBC database in the jdbc url
	 * @param userid - the user id to specify for each connection
	 * @param maxConnections - the maximum number of connections in the pool.
	 */
	/*
	 * Create a JDBC connection.
	 */
	@Override
	public Connection getConnection() throws SQLException {

		Connection conn = null;
		final String DEBUG_STATUS="PoolId {0} get connection total= {2} active= {2} idle= {3}";
		String dbgOut = " ";
		
		try {			
			conn = ds.getConnection();
			HikariPoolMXBean bean = ds.getHikariPoolMXBean();
			dbgOut = MessageFormat.format(DEBUG_STATUS,poolId, bean.getTotalConnections(),
					                        bean.getActiveConnections(),bean.getIdleConnections());			
			logger.debug(dbgOut);
		}
		catch (SQLException sqlex) {
         logger.error(sqlex.getMessage(), sqlex);
		   javax.swing.JOptionPane.showMessageDialog(null, 
                	" Connection to database failed ! See log for errors.", "Failed Connection!",
    						javax.swing.JOptionPane.ERROR_MESSAGE);
                System.exit(0);
		}
		
		return conn;

	}
			
	/**
	 * Returns a <code>Connection</code> back to the pool, and indicates 
	 * it is available.
	 * 
	 * @param conn - the <code>Connection</code> to be returned
	 */
	@Override
	public synchronized void releaseConnection(Connection conn) throws SQLException {
		
		final String DEBUG_STATUS="PoolId {0} close connection total= {1} active= {2} idle= {3}";
		String dbgOut = " ";
		
		conn.close();
		HikariPoolMXBean bean = ds.getHikariPoolMXBean();
		dbgOut = MessageFormat.format(DEBUG_STATUS,poolId,bean.getTotalConnections(),
				                        bean.getActiveConnections(),bean.getIdleConnections());			
		logger.debug(dbgOut);
	}
	
	/**
	 * Closes and removes <b>all available and in use connections</b>.
	 * In use connections will issue a commit prior to closing the connection.
	 * This method should be called to prevent resource leaks from 
	 * connections which are not properly closed.
	 */

	@Override
	public void closePool() {

		ds.close();
		
	}

}
