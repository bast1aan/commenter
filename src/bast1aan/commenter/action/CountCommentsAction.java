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
import com.opensymphony.xwork2.ActionSupport;
import java.util.List;
import java.util.Map;

public class CountCommentsAction extends ActionSupport {

	private int amount;
	
	private Map<String, Integer> amounts;
	
	private List<String> objectIds = null;
	
	private String objectId;

	@Override
	public String execute() throws Exception {
		Dao dao = Dao.getInstance();
		if (objectIds != null && objectIds.size() > 0) {
			amounts = dao.countComments(objectIds);
		} else if (objectId != null) {
			amount = dao.countComments(objectId);
		}
		return SUCCESS;
	}

	public int getAmount() {
		return amount;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	public void setObjectIds(List<String> objectIds) {
		this.objectIds = objectIds;
	}
	
	public Map<String, Integer> getAmounts() {
		return amounts;
	}

}
