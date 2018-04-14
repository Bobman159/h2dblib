package net.bobs.own.db.h2.pool;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class H2ConnectionPoolFactory {

	public enum PoolTypes {HIKARICP,MYOWN};
	private static Logger logger = LogManager.getLogger(H2ConnectionPoolFactory.class);
	private static H2ConnectionPoolFactory factory = null;
	private Map<String,IH2ConnectionPool> poolMap = null;
	
	/**
	 * Obtain the instance of the connection pool factory.
	 * @return
	 */
	static public H2ConnectionPoolFactory getInstance() {
	   if (factory == null) {
	      factory = new H2ConnectionPoolFactory();
	   }
	   return factory;
 	}
	
	
	/**
	 * Define a connection pool using a <code>Properties</code> object with configuration information.
	 * @param type - connection pool type 
	 * @param poolId - unique identifier for the pool being created
	 * @param props - property object for configuration
	 */
	 public IH2ConnectionPool makePool(PoolTypes type, String resourcePath,
	                            String userId, String password, String numberConnections,String poolId) {
	
		IH2ConnectionPool pool = null;
		
		switch (type) {
			case HIKARICP:
				pool = new H2HikariConnectionPool(resourcePath);
//				H2PoolController.addPool(pool, poolId);
				logger.debug("HikariConnectionPool with poolid= " + poolId + " added to connection pool map");
				break;
			case MYOWN:
			   pool = makeMyOwnConnectionPool(resourcePath);
//				H2PoolController.addPool(pool, poolId);
				logger.debug("H2MyOwnConnectionPool with poolid= " + poolId + " added to connection pool map");
				break;
		}
		
		return pool;

	}
	
	public IH2ConnectionPool makePool(PoolTypes type, String poolId, String path)  {
		
		IH2ConnectionPool pool = null;

		switch (type) {
			case HIKARICP:
				pool = new H2HikariConnectionPool(path);
//				H2PoolController.addPool(pool, poolId);
				logger.debug("HikariConnectionPool with poolid= " + poolId + " added to connection pool map");
				break;
			case MYOWN:
   			pool = makeMyOwnConnectionPool(path);
//			   H2PoolController.addPool(pool, poolId);
				logger.debug("H2MyOwnConnectionPool with poolid= " + poolId + " added to connection pool map");
				break;
		}
		
		return pool;
	}
	
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
