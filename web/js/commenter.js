"use strict";

// find URL of current script
var srcpath = "";
$('script').each(function(i, e) { 
	var path = $(e).attr('src');
	if (path && path.substring(path.length - 13 ) == '/commenter.js') {
		srcpath = path.substring(0, path.length - 13);
	}
});

// load require with rest of the application
$.getScript(srcpath + "/require.js")
	.done(function(script, textStatus) {
		requirejs.config({ 'baseUrl' : srcpath});
		require([
			"underscore", 
			"backbone", 
			"text!templates/form.html?v=2",
			"text!templates/list.html?v=1"
		], function(Underscore, Backbone, form, list) {
			
			var Comment = Backbone.Model.extend({
				id : null,
				parentId : null,
				objectId : null,
				name : null,
				email : null,
				text : null,
				createdAt : null,
				updatedAt : null
			});
			
			var CommentCollection = Backbone.Collection.extend({model : Comment});
			
			var container = jQuery("#commenter-container");
			
			jQuery.ajax({
				url : "/commenter/listcomments.action",
				cache : false,
				data : "{ 'objectId' : 'testobject' }",
				contentType : 'application/json',
				type : 'POST',
				dataType : 'json',
				crossDomain : true,
				success : function(data){
					if (data.comments) {
						var comments = new CommentCollection(data.comments);
						container.html(container.html() + 
								Underscore.template(list, {comments : comments}));
					}
				},
				error : function(jqHXR, textStatus, e) {
					throw "Error executing request: " + textStatus + " : " + e;
				}
			});
			
			var comment = new Comment({ objectId : 'newsArticle_513', text : 'A test comment'});
			var commentJSON = comment.toJSON();
			
			container.html(container.html() + Underscore.template(form, comment.toJSON()));
			
			//alert(JSON.stringify({ comment : commentJSON }));
			
		});
	})
	.fail(function( jqxhr, settings, exception ) {
		throw "Error loading require.js"
	});