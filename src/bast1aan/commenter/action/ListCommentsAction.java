package bast1aan.commenter.action;

import bast1aan.commenter.Comment;
import bast1aan.commenter.Dao;
import com.opensymphony.xwork2.ActionSupport;
import java.util.List;

public class ListCommentsAction extends ActionSupport {

	private List<Comment> comments;
	
	private String objectId;
	
	@Override
	public String execute() throws Exception {
		Dao dao = Dao.getInstance();
		//LOG.info("object id : " + objectId, objectId);
		comments = dao.getComments(objectId);
		//LOG.info("comments count : " + Integer.toString(comments.size()));
		//LOG.info("text : " + comments.get(0).getText());
		return SUCCESS;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
}
