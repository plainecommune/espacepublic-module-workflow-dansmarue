--
-- Table structure for table signalement_workflow
--
CREATE TABLE signalement_workflow (
    id_workflow integer,
    CONSTRAINT id_workflow_pkey PRIMARY KEY (id_workflow)
);


--
-- Table structure for table signalement_workflow_notification_config
--
CREATE TABLE signalement_workflow_notification_config
(
  id_task integer NOT NULL,
  subject character varying(255),
  message text,
  sender character varying(255),
  CONSTRAINT signalement_workflow_notification_config_pkey PRIMARY KEY (id_task )
);

--
-- Table structure for table signalement_workflow_notification_user_config
--
CREATE TABLE signalement_workflow_notification_user_config
(
  id_task integer NOT NULL,
  subject character varying(255),
  sender character varying(255),
  title character varying(255),
  message integer,
  CONSTRAINT signalement_workflow_notification_user_config_pkey PRIMARY KEY (id_task),
  CONSTRAINT signalement_workflow_notification_user_config_fkey FOREIGN KEY (id_message)
      REFERENCES signalement_message_creation (message) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

--
-- Table structure for table signalement_workflow_notification_config_unit
--
CREATE TABLE signalement_workflow_notification_config_unit
(
  id_task integer NOT NULL,
  destinataires character varying(255),
  id_unit bigint NOT NULL,
  CONSTRAINT signalement_workflow_notification_config_unit_pkey PRIMARY KEY (id_task , id_unit )
);


--
-- Table structure for table signalement_workflow_notification_user_value
--
CREATE TABLE signalement_workflow_notification_user_value
(
  id_history integer NOT NULL DEFAULT 0,
  id_task integer NOT NULL,
  notification_value text,
  CONSTRAINT signalement_workflow_notification_user_value_pkey PRIMARY KEY (id_history, id_task),
  CONSTRAINT fk_notification_user_value_id_history FOREIGN KEY (id_history)
      REFERENCES workflow_resource_history (id_history) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_notification_user_id_task FOREIGN KEY (id_task)
      REFERENCES workflow_task (id_task) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT
);

--
-- Table structure for table signalement_workflow_notifuser_multi_contents_config
--
CREATE SEQUENCE seq_signalement_workflow_notifuser_multi_contents_config;
CREATE TABLE signalement_workflow_notifuser_multi_contents_config (
	id_message int8 NOT NULL,
	subject varchar(255) NULL,
	sender varchar(255) NULL,
	title varchar(255) NULL,
	message text NULL,
	CONSTRAINT id_message_pk PRIMARY KEY (id_message)
)
WITH (
	OIDS=FALSE
) ;

--
-- Table structure for table signalement_workflow_notifuser_multi_contents_task
--
CREATE TABLE signalement_workflow_notifuser_multi_contents_task (
	id_task int8 NOT NULL,
	id_message int8 NOT NULL,
	CONSTRAINT signalement_workflow_notifuser_multi_contents_config_message_fk FOREIGN KEY (id_message) REFERENCES signalement_workflow_notifuser_multi_contents_config(id_message)
)
WITH (
	OIDS=FALSE
) ;

--
-- Table structure for table signalement_workflow_notifuser_multi_contents_value
--
CREATE TABLE signalement_workflow_notifuser_multi_contents_value (
	id_history int4 NOT NULL DEFAULT 0,
	id_task int4 NOT NULL,
	notification_value text NULL,
	id_message int8 NULL,
	CONSTRAINT signalement_workflow_notifuser_3contents_value_pkey PRIMARY KEY (id_history, id_task),
	CONSTRAINT fk_notification_user_id_task FOREIGN KEY (id_task) REFERENCES workflow_task(id_task) ON UPDATE RESTRICT ON DELETE RESTRICT,
	CONSTRAINT fk_notifuser_3contents_value_id_history FOREIGN KEY (id_history) REFERENCES workflow_resource_history(id_history) ON UPDATE RESTRICT ON DELETE RESTRICT
)
WITH (
	OIDS=FALSE
) ;


--
-- Table structure for table signalement_workflow_rac_unit
--
CREATE TABLE signalement_workflow_rac_unit
(
  id_config_unit integer NOT NULL,
  id_task integer NOT NULL,
  id_unit_source integer NOT NULL,
  id_type_signalement integer NOT NULL,
  id_unit_target integer NOT NULL,
  id_state_after integer NOT NULL,
  CONSTRAINT signalement_workflow_rac_unit_pkey PRIMARY KEY (id_config_unit),
  CONSTRAINT fk_signalement_rac_id_task FOREIGN KEY (id_task)
      REFERENCES workflow_task (id_task) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT
);

--
-- Table structure for table signalement_workflow_webservice_config
--
CREATE TABLE signalement_workflow_webservice_config (
	id_task int4 NOT NULL,
	id_state_withws_success int4 NULL,
	id_state_withws_failure int4 NULL,
	id_state_withoutws int4 NULL,
	CONSTRAINT signalement_workflow_webservice_config_pkey PRIMARY KEY (id_task)
)
WITH (
	OIDS=FALSE
) ;

--
-- Table structure for table signalement_workflow_webservice_config_unit
--
CREATE TABLE signalement_workflow_webservice_config_unit (
	id_task int4 NOT NULL,
	id_unit int4 NOT NULL,
	prestatairesansws varchar(8) NULL,
	urlprestataire varchar(255) NULL
)
WITH (
	OIDS=FALSE
) ;

--
-- Table structure for table signalement_workflow_webservice_value
--
CREATE TABLE signalement_workflow_webservice_value (
	id_history int4 NOT NULL,
	id_task int4 NOT NULL,
	webservice_value text NULL,
	CONSTRAINT signalement_workflow_webservice_value_pkey PRIMARY KEY (id_history, id_task),
	CONSTRAINT fk_id_history FOREIGN KEY (id_history) REFERENCES workflow_resource_history(id_history) ON DELETE CASCADE
)
WITH (
	OIDS=FALSE
) ;

--
-- Table structure for table signalement_dashboard_user_preferences
--
CREATE TABLE signalement_dashboard_user_preferences (
	fk_id_user int4 NOT NULL,
	fk_id_state int4 NOT NULL,
	CONSTRAINT signalement_dashboard_user_preferences_pk PRIMARY KEY (fk_id_user, fk_id_state),
	CONSTRAINT signalement_dashboard_state_fk FOREIGN KEY (fk_id_state) REFERENCES workflow_state(id_state),
	CONSTRAINT signalement_dashboard_user_fk FOREIGN KEY (fk_id_user) REFERENCES core_admin_user(id_user) ON DELETE CASCADE
)
WITH (
	OIDS=FALSE
) ;
