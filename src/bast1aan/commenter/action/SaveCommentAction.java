package bast1aan.commenter.action;

import bast1aan.commenter.Comment;
import bast1aan.commenter.Dao;
import com.opensymphony.xwork2.ActionSupport;

public class SaveCommentAction extends ActionSupport {

	private Comment comment;
	
	@Override
	public String execute() throws Exception {
		Dao dao = Dao.getInstance();
		if (comment != null) {
			dao.saveComment(comment);
		}
		return SUCCESS;
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}
	
	
	
}
