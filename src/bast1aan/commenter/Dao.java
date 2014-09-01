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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Dao {
	
	private static Dao instance;
	
	public static Dao getInstance() {
		if (instance == null) {
			instance = new Dao();
		}
		return instance;
	}
	
	private Connection conn;
	
	public Dao() {
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
	
	public List<Comment> getComments(String objectId) {
		
		List<Comment> comments = new ArrayList<Comment>();
		
		String query = "SELECT * FROM comments WHERE object_id = ?";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, objectId);
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				Comment comment = new Comment();
				comment.setId(result.getInt("id"));
				
				Integer parentId = result.getInt("parent_id");
				comment.setParentId(parentId == 0 ? null : parentId);
				
				comment.setObjectId(result.getString("object_id"));
				comment.setName(result.getString("name"));
				comment.setEmail(result.getString("email"));
				comment.setText(result.getString("text"));
				comment.setCreatedAt(result.getTimestamp("created_at"));
				comment.setUpdatedAt(result.getTimestamp("updated_at"));
				comments.add(comment);
			}
			result.close();
		} catch (SQLException e) {
			throw new CommenterException(String.format("Error executing query: %s", query), e);
		}
		
		return comments;
	}
	
	public void saveComment(Comment comment, String remoteAddr) {
		String query = "";
		Integer id = comment.getId();
		
		PreparedStatement stmt;
		
		try {
			if (id != null) {
				//.update needed
				query = "UPDATE comments SET parent_id = ?, object_id = ?, name = ?, email = ?, text = ?, ip = ?::inet, updated_at = now() WHERE id = ?";
				stmt = conn.prepareStatement(query);
			} else {
				query = "INSERT INTO comments (parent_id, object_id, name, email, text, ip, created_at, updated_at) VALUES ( ?, ?, ?, ?, ?, ?::inet, now(), now() )";
				stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			}
			
			setCommentToStatement(comment, stmt);
			stmt.setString(6, remoteAddr);
			if (id != null) { // update
				stmt.setLong(7, id);
			}
			int affected = stmt.executeUpdate();
			
			if (id == null && affected != 0) {
				// update id of comment
				ResultSet keys = stmt.getGeneratedKeys();
				if (keys.next()) {
					id = keys.getInt(1);
					comment.setId(id);
				}
			}
		} catch (SQLException e) {
			throw new CommenterException(String.format("Error executing query: %s", query), e);
		}
	}
	
	private void setCommentToStatement(Comment comment, PreparedStatement stmt) 
		throws SQLException {
		Integer parentId = comment.getParentId();
		if (parentId == null)
			stmt.setObject(1, null);
		else
			stmt.setInt(1, parentId);
		stmt.setString(2, comment.getObjectId());
		stmt.setString(3, comment.getName());
		stmt.setString(4, comment.getEmail());
		stmt.setString(5, comment.getText());
	}
	
	public int countComments(String objectId) {
		
		int amount = 0;
		
		String query = "SELECT COUNT(*) AS cnt FROM comments WHERE object_id = ?";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, objectId);
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				amount = result.getInt("cnt");
			}
			result.close();
		} catch (SQLException e) {
			throw new CommenterException(String.format("Error executing query: %s", query), e);
		}
		
		return amount;
	}

}
