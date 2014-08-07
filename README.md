commenter
========

Application to serve and store comments on websites.

(C) Copyright 2014 Bastiaan Welmers 

how to build and run
--------------------

Requirements:

* java-openjdk 6 or higher
* java-openjdk-devel
* tomcat 6 or higher to run the application  
  another servlet engine implementing servlet api 2.5 or higher
  should work as well (not tested)
* database engine working with JDBC. I use postgresql for this.
* apache-ant to build the application
* apache-ivy to resolve dependencies for the application

How to build:

* Create the tables in your database engine with the SQL scripts
  in the directory db/
* Find a proper JDBC connector for the database engine you use.
  For postgresql see http://jdbc.postgresql.org/download.html
  Place the jar in the lib/ subdirectory.
* create a new settings.properties in the src/ directory with
  information how to connect to the database. See 
  settings-example.properties what is need.

Then use ant to resolve dependencies and build the project:

* `$ ant resolve`
* `$ ant build`

Then place the generated war file in the tomcat webapps directory:

* `$ cp commenter.war /var/lib/tomcat/webapps/`

The application should then be available under
 http://localhost:8080/commenter/ !

URLs to use
-----------

I use the RestClient firefox plugin for testing this application.

**Creating new comment**

POST http://localhost:8080/commenter/savecomment.action  
Content-type: application/json; charset=UTF-8  
`{ "comment" : { "objectId" : "newsArticle_451", "text" : "This is a great news article!" } }`  

Result is the new comment with added field id and parent_id :

`
 {
    "comment":
    {
        "id": 1,
        "objectId": "newsArticle_451",
        "parentId": null,
        "text": "This is a great news article!"
    }
 }
`

**Listing comments**

POST http://localhost:8080/commenter/listcomments.action  
Content-type: application/json; charset=UTF-8  
`{ "objectId" : "newsArticle_451" }`

Result is a list of comments belonging to `newsArticle_451`:

`
{
   "comments":
   [
       {
           "id": 1,
           "objectId": "newsArticle_451",
           "parentId": null,
           "text": "This is a great news article!"
       }
   ]
}
`


