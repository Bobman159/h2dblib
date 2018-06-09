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

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bobs.own.db.h2.exceptions.NoPreferenceException;
import net.bobs.own.db.h2.resources.Messages;

/*
 *	I used http://javaprogramming.language-tutorial.com/2012/09/a-sample-java-code-for-connection.html
 * 	as the model for this connection pool.  The closeAllConnections method is 
 * 	my own creation.
 * 	
 * 	August 31,2016
 */

/**
 * A non thread safe simple connection pool for JDBC H2 database connections.
 * The pool creates and manages a list of n connections for a H2 database. The
 * maximum number of available connections can be configured, but if no value is
 * provided a default of 25 connections is used. One instance of the connection
 * pool is needed for each H2 database. Each occurrence of a connection pool
 * maintains a list of available and in use connections. These lists are updated
 * appropriately when a JDBC connection is obtained from the pool or released
 * back to the pool.
 * 
 * The class implements the runnable interface which manages the list of
 * available connections so that it does not exceed the maximum connections
 * specified at pool creation. The database name, database path, maximum
 * connections and other values are configurable using a .properties file. The
 * <code>H2DbPreferencesUtil</code> class should be used to provide assistance
 * for configuration of the connection pool.
 * 
 * 
 * 
 * @author Robert Anderson
 *
 */
class H2MyOwnConnectionPool extends Thread implements IH2ConnectionPool {

   /* Default value, may be overridden by db.max_connections property */
   private List<Connection> available_connections = null;
   private List<Connection> inuse_connections = null;
   private Thread cleanupThread = null;

   private Logger logger = LogManager.getLogger(H2MyOwnConnectionPool.class);
   private final String DRIVER_NAME = "org.h2.Driver";
   private boolean terminate = false;
   private H2ConnectionPoolPreferences prefs = null;
   private int max_connections;
   private String poolId = null;
   private boolean poolTracing = false;
   
   /**
    * Creates a connection pool using the database preferences identified by a key
    * values in a properties file.
    * 
    * 
    * @param url
    *           - .properties file URL
    */
   public H2MyOwnConnectionPool(URL url) throws IOException {

      logger.debug(Messages.bind(Messages.H2InitPool_Message, "url", url.toString()));
      prefs = new H2ConnectionPoolPreferences(url);
      try {
         max_connections = prefs.getdbMaxConnections();
         poolId = prefs.getdbPoolid();
      } catch (NoPreferenceException npex) {
         logger.error(npex.getMessage(), npex);
      }
      initConnectionPool();

   }

   public H2MyOwnConnectionPool(Properties dbProps) {
      prefs = new H2ConnectionPoolPreferences(dbProps);
      try {
         max_connections = prefs.getdbMaxConnections();
         logger.debug(Messages.bind(Messages.H2InitPool_Message, "properties", prefs.getdbPath()));
         poolId = prefs.getdbPoolid();
      } catch (NoPreferenceException npex) {
         logger.error(npex.getMessage(), npex);
      }
      initConnectionPool();
   }

   /**
    * Obtains an available <code>Connection</code> from the pool and marks it as in
    * use. If there are no available connections, a new connection is created and
    * added to the pool. The pool will reduce the number of connections if it
    * exceeds the maximum number of connections
    * 
    * @return - a JDBC <code>Connection</code> object
    */
   @Override
   public synchronized Connection getConnection() throws SQLException {

      Connection conn = null;
      final String DEBUG_STATUS = "PoolId {0} get connection available= {1} inuse= {2})";
      String dbgOut = " ";

      if (available_connections.size() == 0) {
         conn = createConnection(buildURL());
         available_connections.add(conn);
         dbgOut = MessageFormat.format(DEBUG_STATUS, poolId, available_connections.size(),   
                                       inuse_connections.size());
         logConnectionTrace(dbgOut);
      } else {
         conn = available_connections.remove(available_connections.size() - 1);
         inuse_connections.add(conn);
         dbgOut = MessageFormat.format(DEBUG_STATUS, poolId, available_connections.size(), 
                                       inuse_connections.size());
         logConnectionTrace(dbgOut);
      }

      return conn;
   }

   /**
    * Returns a <code>Connection</code> back to the pool, and indicates it is
    * available.
    * 
    * @param conn
    *           - the <code>Connection</code> to be returned
    */
   @Override
   public synchronized void releaseConnection(Connection conn) {
      /*
       * It's a bit of a kludge, BUT make sure the connection being released is in the
       * inuse_connections before removing it AND make sure it's NOT in the
       * available_connections before adding it.
       * 
       * If I DON'T do this then the current logic ends up adding duplicate
       * connections to the available connections list.
       */

      final String DEBUG_OUT = "poolid {0} close connection available= {1} inuse= {2}";
      String dbgOut = " ";

      if (conn != null) {
         if (inuse_connections.contains(conn)) {
            inuse_connections.remove(conn);
         }
         if (available_connections.contains(conn) == false) {
            logConnectionTrace("poolId " + poolId + " release connections addded connection");
            
            // dbgOut = MessageFormat.format(DEBUG_OUT, available_connections.size(),
            // inuse_connections.size());
            available_connections.add(conn);
         }
         dbgOut = MessageFormat.format(DEBUG_OUT, poolId, available_connections.size(), 
                                       inuse_connections.size());
         logConnectionTrace(dbgOut);
      }
   }

   /**
    * Closes and removes <b>all available and in use connections</b>. In use
    * connections will issue a commit prior to closing the connection. This method
    * should be called to prevent resource leaks from connections which are not
    * properly closed.
    */
   /*
    * I AM SURE closing in use connections is a bad idea. BUT since I don't
    * currently have a need to wait for in flight transactions to finish, I am not
    * going to try and figure out how to wait until all the in use connections are
    * available...
    */
   @Override
   public void closePool() {

      for (int count = 0; count < available_connections.size(); count++) {
         Connection conn = available_connections.get(count);
         try {
            if (conn.isClosed()) {
               available_connections.remove(count);
            } else {
               conn.close();
               available_connections.remove(count);
            }
         } catch (SQLException sqex) {
            logger.debug(sqex.getMessage(), sqex);
         }
      }

      for (int count = 0; count < inuse_connections.size(); count++) {
         Connection conn = inuse_connections.get(count);
         try {
            if (conn.isClosed()) {
               inuse_connections.remove(count);
            } else {
               conn.commit();
               conn.close();
               inuse_connections.remove(count);
            }
         } catch (SQLException sqex) {
            logger.debug(sqex.getMessage(), sqex);
         }
      }

      String poolId = " ";
      try {
         poolId = prefs.getdbPoolid();
         // H2PoolController.closePool(poolId);
      } catch (NoPreferenceException npex) {
         // Do nothing...
      }
      logger.debug(MessageFormat.format("Close pool {0} available= {1} inuse={2}", poolId, 
                                        available_connections.size(),
      inuse_connections.size()));
      cleanupThread.interrupt();
      terminate = true;
      interrupt();

   }

   /**
    * Returns the number of available connections in the pool currently.
    * 
    * @return - the number of available connections
    */
   private int availableCount() {
      return available_connections.size();
   }

   /**
    * Responsible for creation of a separate thread to close and remove JDBC
    * connections. When the count of available connections is greater than the
    * maximum number of connections the excess connections are closed and removed
    * from the list of available connections.
    */
   public void run() {
      try {
         while (terminate == false) {
            synchronized (this) {

               while (available_connections.size() > max_connections) {
                  // Clean up extra available connections.
                  Connection conn = (Connection) available_connections.remove(available_connections.size() - 1);
                  available_connections.remove(conn);

                  // Close the connection to the database.
                  if (conn.isClosed()) {
                     available_connections.remove(conn);
                     conn = getConnection();
                     available_connections.add(conn);
                  } else {
                     conn.close();
                  }
               }

               // Clean up is done
            }

            logger.debug("PoolId " + poolId + " CLEANUP : Available Connections : " + availableCount());

            // Now sleep for 1 minute
            logger.debug("Sleep for one minute");
            Thread.sleep(60000 * 1);
         }
      } catch (SQLException sqle) {
         logger.error(sqle.getMessage(), sqle);
      } catch (Exception e) {
         logger.error(e.getMessage(), e);
      }
   }

   private Connection createConnection(String url) throws SQLException {

      Connection conn = null;
      try {
         Class.forName(DRIVER_NAME);
         conn = DriverManager.getConnection(url);
      } catch (ClassNotFoundException | SQLException ex) {
         logger.error(ex.getMessage(), ex);
      }
      return conn;

   }

   private void initConnectionPool() {

      // max_connections = prefs.getdbMaxConnections();
      if (max_connections == -1) {
         // No preference value specified, set a default value.
         max_connections = 10;
      }
      
      available_connections=Collections.synchronizedList(new ArrayList<Connection>());
      inuse_connections=Collections.synchronizedList(new ArrayList<Connection>());
      try {
         logger.debug(MessageFormat.format("poolid= {0}",prefs.getdbPoolid()));
         logger.debug(MessageFormat.format("database path= {0}", prefs.getdbPath()));
         logger.debug(MessageFormat.format("database userid= {0}", prefs.getdbUser()));
         logger.debug(MessageFormat.format("user id password= {0}", "xxxxxx"));
         logger.debug(MessageFormat.format("maxiumum connections= {0}", max_connections));
         String url = buildURL();

         for(int ix = 0;ix<max_connections;ix++) {
            available_connections.add(createConnection(buildURL()));
         }
         logConnectionTrace("There are " + available_connections.size() + 
                      " available connections using the jdbc url " + url);
      } catch (SQLException | NoPreferenceException ex) {
         logger.debug(ex.getMessage(), ex);
      }
      
      /* Create and start the cleanup thread */
      cleanupThread=new Thread(this);cleanupThread.start();
      }

   private String buildURL() {
      
		   String url = null;
		   try {
            Object[] parms = new Object[] {prefs.getdbPath(),prefs.getdbUser(),prefs.getdbPassword()};
//            Class.forName(DRIVER_NAME);
         if (prefs.getdbPassword() != null) {
               //TODO: Develop a way to specify ";IFEXISTS=TRUE" when needed or leave it off when not needed
               //Possibly use a H2ConnectStringBuilder class to build/specify different options?
               //Use Interface IConnectStringHook to be called when specified by user....
               url = Messages.bind(Messages.h2database_jdbc_url, parms);
         } else {
            url = Messages.bind(Messages.h2database_jdbc_url_nopass,parms[0],parms[1]);
         }
//         }
		   } catch (NoPreferenceException npex) {
            logger.debug(npex.getMessage(), npex);
         }
            
         return url;
          
   }
   
   /**
    * Set indicator for whether connections pool trace information should be logged when connections 
    * are obtained and released.  Logging shows 1) the total number of available connections, 2) the
    * number of active connections and 3) number of idle connections. Connection tracing is off by default.
    * 
    * @param - trace indicator true turns connection tracing on, false turns connection tracing off.
    *  
    */
   @Override 
   public void setPoolConnectionTrace(boolean trace) {
      this.poolTracing = trace;
   }
   
   private void logConnectionTrace(String msg) {
      
      if (poolTracing) {
         logger.debug(msg);
      }
      
   }
         
}
