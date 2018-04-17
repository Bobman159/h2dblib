package net.bobs.own.db.h2.pool;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class H2ConnectionPoolFactory {

	public enum PoolTypes {HIKARICP,MYOWN};
	private static Logger logger = LogManager.getLogger(H2ConnectionPoolFactory.class);
	private static H2ConnectionPoolFactory factory = null;
	private Map<String,IH2ConnectionPool> poolMap = null;
	
	/**
	 * Obtain the instance of the connection pool factory.
	 * 
	 * @return - the connection pool factory
	 */
	static public H2ConnectionPoolFactory getInstance() {
	   if (factory == null) {
	      factory = new H2ConnectionPoolFactory();
	   }
	   return factory;
 	}
	
	
	/**
	 * Define a connection pool using the specified information for configuration. 
	 * <b>Do not specify the file extension <code>.h2.db</code> for the dbPath parameter.</b>
	 * It will result in database not found messages in h2 1.3.176.
	 * 
	 * @param type - type of pool HIKARICP or MYOWN
	 * @param dbPath - full path to the specified H2 database 
	 * @param userId - the user Id for logon information
	 * @param password - password for the user Id, null if no password
	 * @param numberConnections - the number of connections to be defined in the pool
	 * @param poolId - unique identifier for the pool being created
	 */
	 public IH2ConnectionPool makePool(PoolTypes type, String dbPath,
	                            String userId, String password, String numberConnections,String poolId) {
	
		IH2ConnectionPool pool = null;
		Properties dbProps = null;
		
		switch (type) {
			case HIKARICP:
			   dbProps = createProperties(type,dbPath,userId,password,numberConnections);
				pool = new H2HikariConnectionPool(dbProps);
				poolMap.put(poolId,pool);
				logger.debug("HikariConnectionPool with poolid= " + poolId + " added to connection pool map");
				break;
			case MYOWN:
			   dbProps = createProperties(type,dbPath,userId,password,numberConnections);
			   pool = new H2MyOwnConnectionPool(dbProps);
			   poolMap.put(poolId, pool);
				logger.debug("H2MyOwnConnectionPool with poolid= " + poolId + " added to connection pool map");
				break;
		}
		
		return pool;

	}

	 /**
	  * Create a connection pool using a .properties file with the necessary information for defining the pool.
	  * 
	  * @param type - type of pool HIKARICP or MYOWN
	  * @param poolId - unique identifier for the pool
	  * @param propFile - full path to .properties file.
	  * 
	  * For .properties file parameters, see the Usage section of the README.md file.
	  * 
	  * @return - an <code>IH2ConnectionPool</code> pool
	  */
	public IH2ConnectionPool makePool(PoolTypes type, String poolId, String propFile)  {
		
		IH2ConnectionPool pool = null;

		switch (type) {
			case HIKARICP:
				pool = new H2HikariConnectionPool(propFile);
	         poolMap.put(poolId,pool);
				logger.debug("HikariConnectionPool with poolid= " + poolId + " added to connection pool map");
				break;
			case MYOWN:
   			pool = makeMyOwnConnectionPool(propFile);
            poolMap.put(poolId,pool);
				logger.debug("H2MyOwnConnectionPool with poolid= " + poolId + " added to connection pool map");
				break;
		}
		
		return pool;
	}
	
	/**
	 * Locate an <code>IH2ConnectionPool</code> object for the specified poolId.  
	 *   
	 * @param poolId - unique identifier for a <code>IH2ConnectionPool</code> 
	 * 
	 * @return - the connection pool object if created, null if no pool was found for the poolId
	 */
	public IH2ConnectionPool findPool(String poolId) {
	   
	   IH2ConnectionPool pool = null;
	   
	   if (poolMap.containsKey(poolId)) {
	      pool = poolMap.get(poolId);	      
	   }
	   
	   return pool;

	}
	
	private H2ConnectionPoolFactory () {
	   poolMap = new HashMap<String,IH2ConnectionPool>();
	}
	
	private H2MyOwnConnectionPool makeMyOwnConnectionPool(String path) {

	   URL poolURL = null;
	   H2MyOwnConnectionPool pool = null;
	   
      try {
         if (containsURL(path) == false) {
            poolURL = new File(path).toURI().toURL();
         } else {
            poolURL = new URL(path);
         }
         pool = new H2MyOwnConnectionPool(poolURL);
      } catch (IOException ex) {
         logger.debug(ex.getMessage(),ex);
      }	   
      
      return pool;
      
	}
	
	private Properties createProperties(PoolTypes type,String dbPath, String userId,String password,
	                                    String numberConnections ) {
	   
	   logger.debug("Create configuration using properties object");
	   Properties prop = new Properties();
	   switch (type) {
   	   case HIKARICP:
   	      prop.put("dataSourceClassName", "org.h2.jdbcx.JdbcDataSource");
   	      prop.put("dataSource.url","jdbc:h2:" + dbPath);
   	      prop.put("dataSource.user",userId);
   	      if (password != null) {
   	           prop.put("dataSource.password",password);
   	      }
   	      prop.put("maximumPoolSize",numberConnections);
   	      break;
   	   case MYOWN:
   	      
   	      prop.put("db.maxconnections",numberConnections);
   	      prop.put("db.path",dbPath);
   	      prop.put("db.user",userId);
   	      if (password != null) {
   	         prop.put("db.password",password);
   	      }
   	      break;
	   }
	   
	   return prop;
	   
	}

	
	/*
	 * Test if the string contains URL syntax 
	 * returns true if URL (file:/, or platform:/ is found, false otherwise
	 * 
	 */
	private boolean containsURL(String text) {
		boolean isURL = false;
//		final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

		if (text.substring(0,10).equalsIgnoreCase("platform:/")) {
			isURL = true;
		} else if (text.substring(0,6).equalsIgnoreCase("file:/")) {
			isURL = true;
		}

//		Pattern p = Pattern.compile(URL_REGEX);
//		Matcher m = p.matcher("example.com");//replace with string to compare
//		if(m.find()) {
//		    System.out.println("String contains URL");
//		}

		return isURL;
		
	}
}
