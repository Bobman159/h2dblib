package net.bobs.own.db.h2.pool;

import java.sql.Connection;
import java.sql.SQLException;

public interface IH2ConnectionPool {

	public Connection getConnection() throws SQLException;
	public void releaseConnection(Connection conn) throws SQLException;
	public void closePool();
	public void setPoolConnectionTrace(boolean trace);
	
}
