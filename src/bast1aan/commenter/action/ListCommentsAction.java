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

package bast1aan.commenter.action;

import bast1aan.commenter.Comment;
import bast1aan.commenter.Dao;
import static com.opensymphony.xwork2.Action.SUCCESS;
import java.util.List;

public class ListCommentsAction extends BaseAction {

	private List<Comment> comments;
	
	public String execute() throws Exception {
		
		String objectId = request.getParameter("objectId");
		Dao dao = Dao.getInstance();
		//LOG.info("object id : " + objectId, objectId);
		if (objectId != null && !"".equals(objectId.trim()))
			comments = dao.getComments(objectId, getIndent());
		//LOG.info("comments count : " + Integer.toString(comments.size()));
		//LOG.info("text : " + comments.get(0).getText());
		return SUCCESS;
	}

	public List<Comment> getComments() {
		return comments;
	}

}
