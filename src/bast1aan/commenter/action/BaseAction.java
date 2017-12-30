/*
 * Commenter
 * Copyright (C) 2017 Bastiaan Welmers, bastiaan@welmers.net
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;

abstract public class BaseAction implements ServletRequestAware {

	protected static final String COOKIE_NAME = "indent";
	
	protected HttpServletRequest request;
	
	protected String indent;
	
	protected String getIndent() {
		String indent = null;
		
		// first try the GET parameter
		
		indent = request.getParameter(COOKIE_NAME);

		if (validateIndent(indent))
			return indent;
		
		// try the json parameter in case of POST/PUT
		
		indent = this.indent;

		if (validateIndent(indent))
			return indent;
		
		// try the cookie
		
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (COOKIE_NAME.equals(cookie.getName())) {
					indent = cookie.getValue();
				}
			}
		}
		if (validateIndent(indent))
			return indent;
		
		// in all other cases; not found
		return null;
	}
	
	private boolean validateIndent(String indent) {
		return indent != null && indent.length() > 10 && indent.length() <= 32;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setIndent(String indent) {
		this.indent = indent;
	}
	
}
