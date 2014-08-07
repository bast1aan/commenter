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
