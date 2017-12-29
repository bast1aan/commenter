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
import static com.opensymphony.xwork2.Action.SUCCESS;
import static com.opensymphony.xwork2.Action.ERROR;
import java.math.BigInteger;
import java.util.Random;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.interceptor.ServletResponseAware;

public class SaveCommentAction extends BaseAction implements ServletResponseAware {

	private Comment comment;

	private HttpServletResponse response;
	
	public String execute() throws Exception {
		String indent = getIndent();
		
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
			
			if (indent == null) {
				indent = generateNewString();
				dao.saveComment(comment, remoteAddr, indent);
				writeNewIndent(indent);
			} else {
				dao.saveComment(comment, remoteAddr, indent);
			}
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
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	private void writeNewIndent(String indent) {
		Cookie cookie = new Cookie(COOKIE_NAME, indent);
		if (request.getScheme().equals("https"))
			cookie.setSecure(true);
		cookie.setMaxAge(31536000); // 365 * 24 * 3600, one year
		response.addCookie(cookie);
	}
	
	/**
	 * generate random 32 character hex code string
	 * 
	 * @return 32 char String with random code 
	 */
	private String generateNewString() {
	
		// TODO can this code simpler? Is it random enough?
		
		// generate two random 64bit longs, 
		Random rnd = new Random();
		long val = rnd.nextLong();
		long val2 = rnd.nextLong();
		
		// turn the two random longs into a 128 bit / 16 byte array
		byte[] codeB = new byte[16];
		for (int i = 7; i >= 0; --i) {
			codeB[i] = (byte)(val & 0xff);
			val >>= 8;
		}
		for (int i = 15; i >= 8; --i) {
			codeB[i] = (byte)(val2 & 0xff);
			val2 >>= 8;
		}
		// return 16 byte array as 32 char hex notation
		return new BigInteger(1, codeB).toString(16);
		
	}

}
