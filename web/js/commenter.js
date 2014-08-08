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
			"text!templates/form.html?v=2"
		], function(Underscore, Backbone, form) {
			
			var Comment = Backbone.Model.extend({});
			
			var comment = new Comment({ 'objectId' : 'newsArticle_513', 'text' : 'A test comment'});
			var commentJSON = comment.toJSON();
			
			$("#commenter-container").html(Underscore.template(form, comment.toJSON()));
			
			alert(JSON.stringify({ 'comment' : commentJSON }));
			
		});
	})
	.fail(function( jqxhr, settings, exception ) {
		throw "Error loading require.js"
	});