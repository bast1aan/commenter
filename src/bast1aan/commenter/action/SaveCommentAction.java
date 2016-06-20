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
import bast1aan.commenter.Settings;
import com.opensymphony.xwork2.ActionSupport;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;

public class SaveCommentAction extends ActionSupport implements ServletRequestAware {

	private Comment comment;

	private HttpServletRequest request;
	
	@Override
	public String execute() throws Exception {
		
		if (comment != null) {
                        Integer id = comment.getId();
                        if (id != null) {
                            // prevent unauthorized update of existing comments
                            return ERROR;
                        }
			Dao dao = Dao.getInstance();
			String remoteAddrHeader = Settings.getInstance().get(Settings.REMOTE_ADDR_HEADER);
			//LOG.info(String.format("remoteAddrHeader: %s", remoteAddrHeader));
			//LOG.info(String.format("request: %s", request.toString()));
			String remoteAddr;
			if (remoteAddrHeader == null) {
				remoteAddr = request.getRemoteAddr();
			} else {
				remoteAddr = request.getHeader(remoteAddrHeader);
			}
			
			dao.saveComment(comment, remoteAddr);
		}
		return SUCCESS;
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	
}
