CREATE TABLE comments
(
   id serial, 
   parent_id integer, 
   object_id character varying(255), 
   text text, 
   CONSTRAINT comments_pk PRIMARY KEY (id)
) ;

ALTER TABLE comments
  ADD CONSTRAINT comments_fk_parent_id FOREIGN KEY (parent_id) REFERENCES comments (id)
   ON UPDATE NO ACTION ON DELETE NO ACTION;
CREATE INDEX fki_comments_fk_parent_id
  ON comments(parent_id);
  
CREATE INDEX idx_comments_object_id
   ON comments (object_id ASC NULLS LAST);