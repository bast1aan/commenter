/*
 * Commenter
 * Copyright (C) 2014 Bastiaan Welmers, bastiaan@welmers.net
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * version 2 along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package bast1aan.commenter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The connection manager makes sure there is a working
 * non-stale java.sql.Connection available, instantiated
 * with the data provided with Settings
 */
public class ConnectionManager {
	private Connection conn;
	
	private void init() {
		if (conn == null) {
			setupConnection();
		}
		try {
			ResultSet result = conn.prepareStatement("SELECT 1").executeQuery();
			result.next();
			if (result.getInt(1) != 1) {
				throw new Exception("No valid result from SQL backend");
			}
		} catch(Exception e) {
			try {
				conn.close();
			} catch (SQLException ex) {}
			
			setupConnection();
			
		}
	}
	
	private void setupConnection() {
		Settings settings = Settings.getInstance();
		try {
			Class.forName(settings.get(Settings.DRIVER));
			conn = DriverManager.getConnection(
					settings.get(Settings.DSN),
					settings.get(Settings.USERNAME),
					settings.get(Settings.PASSWORD));
			
		} catch (ClassNotFoundException e) {
			throw new CommenterException("Wrong driver provided or library not present", e);
		} catch (SQLException e) {
			throw new CommenterException("Not able to connect with configuration to DB", e);
		}
	}
	
	public Connection getConnection() {
		init();
		return conn;
	}
}
