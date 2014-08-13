"use strict";

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
	text +=	date.getFullYear() + " ";
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
$('script').each(function(i, e) { 
	var path = $(e).attr('src');
	if (path && path.substring(path.length - 13 ) == '/commenter.js') {
		srcpath = path.substring(0, path.length - 13);
	}
});

// load require with main stuff of this application
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
			
			var FormView = Backbone.View.extend({
				
				initialize : function() {
					this.listenTo(this.model, "change", this.render);
				},
				render : function() {
					this.$el.html(Underscore.template(form, {comment : this.model}));
					$('#comment-form').submit(function(e) {
						alert(e);
					})
					return this;
				}
			});
			
			var formComment = new Comment({ objectId : 'testobject' });
			var formView = new FormView({model : formComment});
			
			var ListView = Backbone.View.extend({
				
				initialize : function() {
					this.listenTo(this.collection, "change", this.render());
				},
				
				render : function() {
					this.$el.html(this.doRender(this.collection.sortBy(function(comment) {
							return comment.get('createdAt');
						}),
						[]
					));
					return this;
				},
				
				doRender : function(comments, alreadyProcessed) {
					return Underscore.template(list, { 
						comments : comments,
						commentsByParentId : _.groupBy(comments, function(comment) {
							return comment.get('parentId');
						}),
						render : this.doRender,
						alreadyProcessed : alreadyProcessed
					});
				}
				
			});
			
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
						var view = new ListView({ collection : comments, el : container[0] });
					}
				},
				error : function(jqHXR, textStatus, e) {
					throw "Error executing request: " + textStatus + " : " + e;
				}
			});
			
			var comment = new Comment({ objectId : 'newsArticle_513', text : 'A test comment'});
			var commentJSON = comment.toJSON();
			
			//container.html(container.html() + Underscore.template(form, comment.toJSON()));
			
			//alert(JSON.stringify({ comment : commentJSON }));
			
		});
	})
	.fail(function( jqxhr, settings, exception ) {
		throw "Error loading require.js"
	});