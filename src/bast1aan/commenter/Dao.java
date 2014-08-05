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
				comment.setText(result.getString("text"));
				comments.add(comment);
			}
			result.close();
		} catch (SQLException e) {
			throw new CommenterException(String.format("Error executing query: %s", query), e);
		}
		
		return comments;
	}
	
}
