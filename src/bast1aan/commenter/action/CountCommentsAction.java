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

import bast1aan.commenter.Dao;
import static com.opensymphony.xwork2.Action.SUCCESS;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

public class CountCommentsAction implements ServletRequestAware {

	private HttpServletRequest request;
	
	private Map<String, Integer> amounts;
	
	public String execute() throws Exception {
		Dao dao = Dao.getInstance();
		String[] objectIds = request.getParameterValues("objectId");
		
		// ability to pass the object ids with prefix and postfixes. 
		// This makes the required query string and corresponding URI much shorter.
		String objectPrefix = request.getParameter("oipr");
		String objectPostfix = request.getParameter("oipf");
		if (objectPrefix != null && objectPostfix != null) {
			String[] newObjectIds = objectPostfix.split(",");
			// add the prefix to the ids
			for (int i = 0; i < newObjectIds.length; ++i)
				newObjectIds[i] = objectPrefix + newObjectIds[i];
			
			if (objectIds != null)
				// concatenate the arrays
				objectIds = ArrayUtils.addAll(objectIds, newObjectIds);
			else
				// or replace the original
				objectIds = newObjectIds;
		}
		if (objectIds != null && objectIds.length > 0) {
			amounts = dao.countComments(objectIds);
		}
		return SUCCESS;
	}

	public Map<String, Integer> getAmounts() {
		return amounts;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
}
