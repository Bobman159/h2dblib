package net.bobs.own.db.h2.pool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import net.bobs.own.db.h2.exceptions.NoPreferenceException;
import net.bobs.own.db.h2.resources.Messages;

 class H2ConnectionPoolPreferences {
	
	 private Properties prefs = null;
	
	/**
	 * Create a new connection pool database preferences definition.  The database preference is 
	 * used by the db.h2 plug in to define a database path, user id and password
	 * information.
	 * 
	 * @param propStream - IO stream for a properties file
	 * @throws NoPreferenceException - Specified preference was not defined
	 * @throws IOException
	 */
	public H2ConnectionPoolPreferences(URL url) throws IOException {

		/* Based on the idea of Reading Resources from a plug-in @h2
		 * http://blog.vogella.com/2010/07/06/reading-resources-from-plugin/
		 */
		InputStream inputStream = url.openConnection().getInputStream();
		Properties prop = new Properties();
		prop.load(inputStream);
		prefs = prop;

		
		 BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		    String inputLine;
		 
		    while ((inputLine = in.readLine()) != null) {
		        System.out.println(inputLine);
		    }
		 
		    in.close();
		 
	}
	
	/**
	 * Createa new Database preferences definition.  The database preference is 
	 * used by the db.h2 plug in to define a database path, user id and password
	 * information.
	 * 
	 * @param dbProp - <code>Properties</code> containing preferences
	 * @throws NoPreferenceException - Specified preference was not defined
	 */
	public H2ConnectionPoolPreferences(Properties dbProp) {

		prefs = dbProp;

	}
	
	/**
	 * Retrieve the database path to be used by the database service.
	 * 
	 * @return - the absolute or relative path for the database
	 */
	public String getdbPath() throws NoPreferenceException {
		
		checkPropertyExist("db.path");
		return prefs.getProperty("db.path");

 	}
		
	/**
	 * Retrieve the user id to be by the database service
	 * 
	 * @return - the user id to be used
	 */
	public String getdbUser() throws NoPreferenceException {
		
		checkPropertyExist("db.user");
		return prefs.getProperty("db.user");

	}

	/**
	 * Retrieve the password for the userid to be used by the database service
	 * 
	 * @return - the password to be used, null if no password was specified.
	 */
	public String getdbPassword() throws NoPreferenceException {
		String password = " ";
		
		try {
			checkPropertyExist("db.password");
			password = prefs.getProperty("db.password");
		} catch (NoPreferenceException npex) {
			password = null;
		}
		return password;
	}
	
	/**
	 * Retrieve the maximum number of connections to be allowed for the h2 
	 * database.  
	 * 
	 * @return - the maximum number of connections or -1 if no preference is specified.
	 * @throws NoPreferenceException
	 */
	public int getdbMaxConnections() throws NoPreferenceException {

		int max_connections = -1;

		checkPropertyExist("db.maxconnections");
		max_connections = Integer.valueOf(prefs.getProperty("db.maxconnections"));
		return max_connections;
		
	}
	
	/**
	 * Retrieve the pool identifier for the h2 database connection pool.
	 *   
	 * @return - the pool identifier.
	 * @throws NoPreferenceException
	 */
	public String getdbPoolid() throws NoPreferenceException {
		
		checkPropertyExist("db.poolid");
		return prefs.getProperty("db.poolid");

	}
	
	/**
	 * Returns an indication of if the h2 database connection pool tracing is on or off
	 * 
	 * @return - true if tracing is on, false otherwise
	 * @throws NoPreferenceException
	 */
	public boolean getdbTrace() throws NoPreferenceException {
	   boolean trace = false;
	   checkPropertyExist("db.trace");
	   if (prefs.getProperty("db.trace").equalsIgnoreCase("true")) {
	      trace = true;
	   }
	   return trace;
	}
	
	private void checkPropertyExist(String propKey) throws NoPreferenceException {
		if (prefs.containsKey(propKey) == false) {
			throw new NoPreferenceException(Messages.bind(Messages.errormsg_noproperty_exist,propKey));
		}
	}	

}
