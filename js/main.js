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

"use strict";

if (typeof $ == "undefined") {
"include ../dependencies/js/jquery.js";
}
if (typeof _ == "undefined") {
"include ../dependencies/js/underscore.js";
}
if (typeof Backbone == "undefined") {
"include ../dependencies/js/backbone.js";
}

var form;
"read templates/form.html into form";

var list;
"read templates/list.html into list";

/**
 * Display date
 * @param Date Date
 * @returns string text
 */
var displayDate = function(date) {
	var zeroFill = function(value) {
		value = value.toString();
		if (value.length < 2)
			value = '0' + value;
		return value;
	}
	var text;
	text  = zeroFill(date.getDate()) + "-";
	text +=	zeroFill(date.getMonth() + 1) + "-";
	text +=	date.getFullYear() + " at ";
	text +=	zeroFill(date.getHours()) + ":";
	text +=	zeroFill(date.getMinutes()) + ":";
	text +=	zeroFill(date.getSeconds());
	return text;
};

/*
 * Filter for comment text to make them viewable.
 * Strips html tags from text
 * EXCEPT u, i, em, br, strong, b.
 * and creates link tags
 */
var filterCommentText = function(text) {
	if (!text) {
		return ;
	}
	// create <br /> tags at line endings
	text = text.replace(/\n/g, "<br />\n");
	
	// remove a tags but preserve link in href is it starts with http(s)
	text = text.replace(/<a.*?href="(https?:.*?)".*?>.*?<\/a.*?>/ig, "$1");
	
	// strip html comments
	text = text.replace(/<!--(.*?)-->/ig, '');
	
	// strip html attributes
	text = text.replace(/<([a-z][a-z0-9]*)[^>]*?(\/?)>/ig, '<$1$2>');
	
	// underline
	text = text.replace(/<u\s*?>(.*?)<\/u\s*>/mig, '<span class="underline">$1</span>');
	
	// bold
	text = text.replace(/<b\s*?>(.*?)<\/b\s*>/mig, '<strong>$1</strong>');
	
	// italic
	text = text.replace(/<i\s*?>(.*?)<\/i\s*>/mig, '<em>$1</em>');
	
	// strip everything except allowed tags
	// TODO: validate if open tags have corresponding close tags
	text = text.replace(/<\/?(?:(?!strong|em|span class="underline"|span|br).)*?\/?>/ig, "");
	
	// detect links and create tag for them
	var pattern;
	pattern  = '[ \\t\\r\\n]';              // whitespace
	pattern += '(([A-Za-z]{3,9}):\\/\\/';   // protocol
	pattern += '([-;:&=\\+\\$,\\w]+@{1})?';  // User info (optional)
	pattern += '([-A-Za-z0-9\\.]+)+';      // FQDN or IP
	pattern += ':?(\\d+)?';                // port (optional)
	pattern += '(';                       // start to capture complete REQUEST_URI
	pattern += '\\/([-\\+~%\\/\\.\\w]+)?';     // path and filename (optional)
	pattern += '\\??([-\\+=&;%@\\.\\w]+)?';   // query string (optional)
	pattern += '#?([\\w]+)?';              // anchor (optional)
	pattern += ')?)';                     // end capturing REQUEST_URI and close pattern

	text = text.replace(new top.RegExp(pattern), " <a href=\"$1\">$1</a>");

	// email
	text = text.replace(/[ \t\r\n]([A-Z0-9._%+-]+\@[A-Z0-9.-]+\.[A-Z]{2,6})/i, ' <a href=\"mailto:$1\">$1</a>');
	
	return text;
};

// find URL of current script
var srcpath = "";
var commenterPath = "";
var $script;
$('script').each(function(i, e) { 
	var $e = $(e);
	var path = $e.attr('src');
	var match;
	if (path && (match = path.match(/js\/commenter\.js/)) !== null && match.length > 0) {
		commenterPath = path.replace(/(.*)js\/commenter\.js.*/, "$1");
		if (commenterPath.length > 0) {
			commenterPath = commenterPath.substring(0, commenterPath.length - 1); // remove trailing slash
		} else {
			commenterPath = '.';
		}
		srcpath = commenterPath + "/js";
		$script = $e; 
	}
});

// determine container for storing comments
var $commenterContainer;
if (typeof commenterContainer != 'undefined') {
	$commenterContainer = commenterContainer;
}

if (!$commenterContainer) {
	$commenterContainer = $script.data('container');
}

if (typeof $commenterContainer == "string") {
	if ($commenterContainer.substring(0, 1) != '#') {
		$commenterContainer = '#' + $commenterContainer;
	}
	$commenterContainer = $($commenterContainer);
} else if (typeof $commenterContainer == "object" && !($commenterContainer instanceof jQuery)) {
	$commenterContainer = $($commenterContainer);
}

var countComments = function() {
	
	/**
	 * Returns the common prefix of an array of strings
	 * 
	 * @param Array array
	 * @returns string
	 */
	function commonPrefix(array) {
		var A = array.concat().sort(), 
		a1 = A[0], a2 = A[A.length-1], L = a1.length, i= 0;
		while(i < L && a1.charAt(i) === a2.charAt(i)) i++;
		return a1.substring(0, i);
	}
	
	var countElements = {};
	$('.commenter-count').each(function() {
		var countContainer = $(this);
		var objectId = countContainer.data('objectId');
		if (!objectId) {
			// find objectId on its first parent
			countContainer.parents().each(function() {
				if (typeof objectId == 'string' && objectId.length > 0) return;
				var tmpObjectId = $(this).data('objectId');
				if (typeof tmpObjectId == 'string' && tmpObjectId.length > 0) {
					objectId = tmpObjectId;
				}
			});
		}
		if (typeof objectId == 'string' && objectId.length > 0) {
			if (typeof countElements[objectId] == 'undefined')
				countElements[objectId] = [];
			countElements[objectId].push(countContainer);
		}
	});
	
	var countElementsQStrArr = []
	var countElementsKeys = Object.keys(countElements)
	
	// pass the objectIDs with a common prefix in prefix(oipr)/postfixes(oipf) format,
	// so the URI becomes much shorter
	var oipr = commonPrefix(countElementsKeys);
	if (oipr && countElementsKeys.length > 2) { // common prefix found, use the oipr/oipf syntax
		countElementsQStrArr.push("oipr=" + oipr);
		var len = oipr.length;
		var oipfArr = [];
		for (var i in countElementsKeys) {
			oipfArr.push(countElementsKeys[i].substring(len));
		}
		countElementsQStrArr.push("oipf=" + oipfArr.join(','));
	} else { // use separate objectId elements
		for (var i in countElementsKeys) {
			countElementsQStrArr.push("objectId=" + countElementsKeys[i])
		}
	}
	jQuery.ajax({
		url : commenterPath + "/countcomments.action?" + countElementsQStrArr.join('&'),
		type : 'GET',
		crossDomain : true,
		success : function(data){
			if (data.amounts && typeof data.amounts == 'object') {
				$.each(countElements, function(k) {
					for (var i = 0; i < countElements[k].length; ++i) {
						if (typeof data.amounts[k] == 'number') {
							countElements[k][i].text(data.amounts[k]);
						} else {
							countElements[k][i].text('0');
						}
						countElements[k][i].change();
					}
				});
			}
		},
		error : function(jqHXR, textStatus, e) {
			throw new Error("Error executing request: " + textStatus + " : " + e);
		}
	});

};


if ($commenterContainer instanceof jQuery) {

	if (!$commenterContainer.hasClass('commenter-container')) {
		$commenterContainer.addClass('commenter-container');
	}

	$('head').append('<link rel="stylesheet" type="text/css" href="' + commenterPath + '/css/commenter.css" />');

	// determine object id

	var thisCommenterObjectId;
	if (typeof commenterObjectId != 'undefined') {
		thisCommenterObjectId = commenterObjectId;
	}
	if (!thisCommenterObjectId) {
		thisCommenterObjectId = $commenterContainer.data('objectId');
	}
	if (!thisCommenterObjectId) {
		thisCommenterObjectId = $script.data('objectId');
	}
	if (!thisCommenterObjectId) {
		thisCommenterObjectId = 'testobject';
	}

	var Underscore = _;

	var Comment = Backbone.Model.extend({
		id : null,
		parentId : null,
		objectId : null,
		name : null,
		email : null,
		text : null,
		createdAt : null,
		updatedAt : null,
		editable : null
	});

	var CommentCollection = Backbone.Collection.extend({model : Comment});

	var comments = new CommentCollection();

	// For IE8 and IE9
	jQuery.support.cors = true;

	function getCookie(cname) {
		var name = cname + "=";
		var decodedCookie = decodeURIComponent(document.cookie);
		var ca = decodedCookie.split(';');
		for(var i = 0; i <ca.length; i++) {
			var c = ca[i];
			while (c.charAt(0) == ' ') {
				c = c.substring(1);
			}
			if (c.indexOf(name) == 0) {
				return c.substring(name.length, c.length);
			}
		}
		return "";
	}
	var indent = getCookie('commenter_indent');

	/**
	 * Function to retrieve the comments and populate comment collection
	 */
	var readComments = function() {
		var uriComponentIndent = "";
		if (indent) {
			uriComponentIndent = "&indent=" + indent;
		}
		jQuery.ajax({
			url : commenterPath + "/listcomments.action?objectId=" + thisCommenterObjectId + uriComponentIndent,
			type : 'GET',
			crossDomain : true,
			success : function(data){
				if (data.comments) {
					comments.reset(data.comments);
				}
			},
			error : function(jqHXR, textStatus, e) {
				throw "Error executing request: " + textStatus + " : " + e;
			}
		});

	}

	/**
	 * Save the comment to the backend.
	 * @param Comment comment
	 */
	var saveComment = function(comment) {
		jQuery.ajax({
			url : commenterPath + "/savecomment.action",
			cache : false,
			data : JSON.stringify({ 'comment' : comment, 'indent' : indent }),
			contentType : 'application/json',
			type : comment.get('id') ? 'PUT' : 'POST',
			dataType : 'json',
			crossDomain : true,
			success : function(data){
				if (data.indent) {
					// set cookie
					var d = new Date();
					d.setTime(d.getTime() + (365*24*60*60*1000));
					var secure = "";
					if (location.protocol.substring(0, 5) == 'https') {
						secure = "; secure";
					}
					document.cookie = "commenter_indent=" + data.indent + "; expires=" + d.toUTCString() + "; path=/" + secure;
					// update our global indent as well
					indent = data.indent;
				}
				if (data.comment) {
					// re-read the comment list
					readComments();
				}
			},
			error : function(jqHXR, textStatus, e) {
				throw "Error executing request: " + textStatus + " : " + e;
			}
		});

	};

	var FormView = Backbone.View.extend({

		initialize : function() {
			this.listenTo(this.model, "change", this.render);
		},

		cancelButton : false,

		setCancelButton : function(enable) {
			this.cancelButton = enable;
			this.render();
		},

		cancelButtonListener : function(){},

		render : function() {
			var comment = this.model;
			this.$el.html(Underscore.template(form, {comment : comment}));
			var $form = this.$el.find('#commenter-form');
			if (this.cancelButton) {
				var cancelButtonJQ = $(document.createElement('button'));
				cancelButtonJQ.text("Cancel");
				var formNode = $form[0];
				for (var i = 0; i < formNode.length; i++) {
					if (formNode[i].type && formNode[i].type == 'submit') {
						formNode[i].parentNode.appendChild(cancelButtonJQ[0]);
					}
				}
				cancelButtonJQ.on('click', this.cancelButtonListener);
			}
			$form.submit(function(e) {
				// gather form values
				var formValues = {};
				for (var i = 0; i < e.currentTarget.length; i++) {
					var element = e.currentTarget[i];
					if (element.name && element.name.length > 0 && element.value && element.value.length > 0)
						formValues[element.name] = element.value;
				}
				// set model with gathered form values
				comment.set(formValues);
				saveComment(comment);
				comment.set('text', '');
				comment.set('id', null);
				readComments();
				return false;
			});
			return this;
		},
		
		onAttach : function() {},

		attachToContainer : function() {
			this.onAttach();
			formComment.set('parentId', null);
			$commenterContainer.append(this.$el);
		},
		
		insertBefore : function($el) {
			this.onAttach();
			this.$el.insertBefore($el);
		}
	});

	var formComment = new Comment({ objectId : thisCommenterObjectId });
	var formView = new FormView({model : formComment});

	var ListView = Backbone.View.extend({

		initialize : function() {
			this.listenTo(this.collection, "reset", this.resetListener);
		},

		resetListener : function() {
			this.render();
			countComments();
		},
		
		render : function() {
			// sort collection by creation date
			var sortedCollection = this.collection.sortBy(function(comment) {
					return comment.get('createdAt');
			});

			// group collection by parent ID to create tree structure
			var groupedCollection = _.groupBy(sortedCollection, function(comment) {
					return comment.get('parentId');
				});

			this.$el.html(this.doRender(sortedCollection, groupedCollection, []));

			// after rendering list, attach form to bottom of container
			formView.attachToContainer();

			return this;
		},

		doRender : function(comments, groupedComments, alreadyProcessed, render) {
			if (render == null) {
				render = this.doRender;
			}
			return Underscore.template(list, { 
				comments : comments,
				commentsByParentId : groupedComments,
				render : render,
				alreadyProcessed : alreadyProcessed
			});
		},

		events: {
			"click .reply" : 'replyOnComment',
			"click .edit" : 'editComment'
		},

		replyOnComment : function(e) {
			if (this.$el.find("textarea[name=text]").val() && !confirm("Do you want to cancel your current comment?")) {
				return false;
			}
			var buttonNode = e.currentTarget;
			var $allButtonsInNode = $(buttonNode.parentElement).find('.reply, .edit');
			var parentId = buttonNode.id.substring("replyOn".length);
			formComment.set('text', '');
			formComment.set('id', null);
			formComment.set('parentId', parentId);
			formView.cancelButton = true;
			formView.insertBefore($(buttonNode));
			formView.cancelButtonListener = function(e) {
				formView.cancelButton = false;
				formView.attachToContainer();
				return false;
			};
			formView.onAttach = function() {
				$allButtonsInNode.show();
				formView.onAttach = function(){};
			};

			$allButtonsInNode.hide();
			formView.render();
			formView.$el.find('h3').text('Reply');
		},
		
		editComment : function(e) {
			if (this.$el.find("textarea[name=text]").val() && !confirm("Do you want to cancel your current comment?")) {
				return false;
			}
			var buttonNode = e.currentTarget;
			var $allButtonsInNode = $(buttonNode.parentElement).find('.reply, .edit');
			var id = buttonNode.id.substring("edit".length);
			var oldFormComment = formComment.clone();
			formComment.set(this.collection.get(id).toJSON());
			formView.cancelButton = true;
			formView.insertBefore($(buttonNode));
			formView.cancelButtonListener = function(e) {
				formView.cancelButton = false;
				formView.attachToContainer();
				formComment.set(oldFormComment.toJSON());
				return false;
			};
			formView.onAttach = function() {
				$allButtonsInNode.show();
				formView.onAttach = function(){};
			};
			
			formView.render();

			$allButtonsInNode.hide();
		}



	});

	var listView = new ListView({ collection : comments});
	$commenterContainer.append(listView.$el);


	readComments();

	formView.attachToContainer();
} else {
	countComments();
}
