DROP TABLE IF EXISTS public.issue_priorities CASCADE;
DROP TABLE IF EXISTS public.issue_states CASCADE;
DROP TABLE IF EXISTS public.issue_types CASCADE;
DROP TABLE IF EXISTS public.workflows CASCADE;
DROP TABLE IF EXISTS public.workflow_transitions CASCADE;
DROP TABLE IF EXISTS public.users CASCADE;
DROP TABLE IF EXISTS public.roles CASCADE;
DROP TABLE IF EXISTS public.projects CASCADE;
DROP TABLE IF EXISTS public.sprints CASCADE;
DROP TABLE IF EXISTS public.project_teams CASCADE;
DROP TABLE IF EXISTS public.projects_team_members CASCADE;
DROP TABLE IF EXISTS public.users_roles CASCADE;
DROP TABLE IF EXISTS public.issues CASCADE;

-- Sequences

DROP SEQUENCE IF EXISTS public.issue_priorities_id_seq;

CREATE SEQUENCE public.issue_priorities_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

DROP SEQUENCE IF EXISTS public.issue_state_id_seq;

CREATE SEQUENCE public.issue_state_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

DROP SEQUENCE IF EXISTS public.issue_types_id_seq;

CREATE SEQUENCE public.issue_types_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

DROP SEQUENCE IF EXISTS public.issues_id_seq;

CREATE SEQUENCE public.issues_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

DROP SEQUENCE IF EXISTS public.project_teams_id_seq;

CREATE SEQUENCE public.project_teams_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

DROP SEQUENCE IF EXISTS public.projects_id_seq;

CREATE SEQUENCE public.projects_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

DROP SEQUENCE IF EXISTS public.roles_id_seq;

CREATE SEQUENCE public.roles_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

DROP SEQUENCE IF EXISTS public.sprints_id_seq;

CREATE SEQUENCE public.sprints_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

DROP SEQUENCE IF EXISTS public.users_id_seq;

CREATE SEQUENCE public.users_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

DROP SEQUENCE IF EXISTS public.workflow_id_seq;

CREATE SEQUENCE public.workflow_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;


-- Tables

CREATE TABLE public.issue_priorities
(
    id integer NOT NULL DEFAULT nextval('issue_priorities_id_seq'::regclass),
    name character varying(50) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT issue_priorities_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

CREATE TABLE public.issue_states
(
    id integer NOT NULL DEFAULT nextval('issue_state_id_seq'::regclass),
    name character varying(50) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT issue_state_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

CREATE TABLE public.issue_types
(
    id integer NOT NULL DEFAULT nextval('issue_types_id_seq'::regclass),
    name character varying(50) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT issue_types_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

CREATE TABLE public.workflows
(
    id integer NOT NULL DEFAULT nextval('workflow_id_seq'::regclass),
    name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    description character varying(512) COLLATE pg_catalog."default",
    issue_type_id integer,
    first_state_id integer,
    CONSTRAINT workflow_pkey PRIMARY KEY (id),
    CONSTRAINT fk_workflows_issue_state FOREIGN KEY (first_state_id)
        REFERENCES public.issue_states (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT fk_workflows_issue_type FOREIGN KEY (issue_type_id)
        REFERENCES public.issue_types (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)

TABLESPACE pg_default;

CREATE TABLE public.workflow_transitions
(
    workflow_id integer NOT NULL,
    from_state_id integer NOT NULL,
    to_state_id integer NOT NULL,
    CONSTRAINT workflow_transitions_pkey PRIMARY KEY (workflow_id, from_state_id, to_state_id),
    CONSTRAINT fk_workflow_transitions_issue_states_from FOREIGN KEY (from_state_id)
        REFERENCES public.issue_states (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    CONSTRAINT fk_workflow_transitions_issue_states_to FOREIGN KEY (to_state_id)
        REFERENCES public.issue_states (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    CONSTRAINT fk_workflow_transitions_workflows FOREIGN KEY (workflow_id)
        REFERENCES public.workflows (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
)

TABLESPACE pg_default;

CREATE TABLE public.users
(
    id integer NOT NULL DEFAULT nextval('users_id_seq'::regclass),
    first_name character varying(50) COLLATE pg_catalog."default" NOT NULL,
    middle_name character varying(50) COLLATE pg_catalog."default",
    last_name character varying(50) COLLATE pg_catalog."default" NOT NULL,
    username character varying(50) COLLATE pg_catalog."default" NOT NULL,
    password integer NOT NULL,
    phone character varying(20) COLLATE pg_catalog."default" NOT NULL,
    email character varying(255) COLLATE pg_catalog."default" NOT NULL,
    employee_code character varying(20) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

CREATE TABLE public.roles
(
    id integer NOT NULL DEFAULT nextval('roles_id_seq'::regclass),
    role_name character varying(50) COLLATE pg_catalog."default" NOT NULL,
    description character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT roles_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

CREATE TABLE public.projects
(
    id integer NOT NULL DEFAULT nextval('projects_id_seq'::regclass),
    name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    description character varying(512) COLLATE pg_catalog."default",
    biz_unit character varying(255) COLLATE pg_catalog."default",
    admin_id integer,
    owner_id integer,
    CONSTRAINT projects_pkey PRIMARY KEY (id),
    CONSTRAINT fk_projects_projects_users FOREIGN KEY (owner_id)
        REFERENCES public.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)

TABLESPACE pg_default;

CREATE TABLE public.sprints
(
    id integer NOT NULL DEFAULT nextval('sprints_id_seq'::regclass),
    name character varying(50) COLLATE pg_catalog."default" NOT NULL,
    capacity smallint NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    project_id integer NOT NULL,
    CONSTRAINT sprints_pkey PRIMARY KEY (id),
    CONSTRAINT fk_sprints_projects FOREIGN KEY (project_id)
        REFERENCES public.projects (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID
)

TABLESPACE pg_default;

CREATE TABLE public.project_teams
(
    id integer NOT NULL DEFAULT nextval('project_teams_id_seq'::regclass),
    project_id integer NOT NULL,
    name character varying(50) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT project_teams_pkey PRIMARY KEY (id),
    CONSTRAINT fk_project_teams_projects FOREIGN KEY (project_id)
        REFERENCES public.projects (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID
)

TABLESPACE pg_default;

CREATE TABLE public.projects_team_members
(
    projects_team_id integer NOT NULL,
    user_id integer NOT NULL,
    CONSTRAINT projects_team_members_pkey PRIMARY KEY (projects_team_id, user_id),
    CONSTRAINT fk_project_team_members_project_teams FOREIGN KEY (projects_team_id)
        REFERENCES public.project_teams (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID,
    CONSTRAINT fk_project_team_members_users FOREIGN KEY (user_id)
        REFERENCES public.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID
)

TABLESPACE pg_default;

CREATE TABLE public.users_roles
(
    project_team_id integer NOT NULL,
    user_id integer NOT NULL,
    role_id integer NOT NULL,
    CONSTRAINT users_roles_pkey PRIMARY KEY (project_team_id, user_id),
    CONSTRAINT fk_users_roles_roles FOREIGN KEY (role_id)
        REFERENCES public.roles (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    CONSTRAINT fk_users_roles_team_members FOREIGN KEY (project_team_id, user_id)
        REFERENCES public.projects_team_members (projects_team_id, user_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
)

TABLESPACE pg_default;

CREATE TABLE public.issues
(
    id integer NOT NULL DEFAULT nextval('issues_id_seq'::regclass),
    issue_type_id integer NOT NULL,
    project_id integer NOT NULL,
    issue_state_id integer NOT NULL,
    issue_priority_id integer NOT NULL,
    sprint_id integer,
    parent_id integer,
    assignee_id integer,
    reporter_id integer,
    code character varying(50) COLLATE pg_catalog."default",
    story_points smallint,
    title character varying(255) COLLATE pg_catalog."default",
    description character varying(4096) COLLATE pg_catalog."default",
    created timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT issues_pkey PRIMARY KEY (id),
    CONSTRAINT fk_issue_issue FOREIGN KEY (parent_id)
        REFERENCES public.issues (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_issue_issue_priority FOREIGN KEY (issue_priority_id)
        REFERENCES public.issue_priorities (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_issue_issue_state FOREIGN KEY (issue_state_id)
        REFERENCES public.issue_states (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_issue_issue_type FOREIGN KEY (issue_type_id)
        REFERENCES public.issue_types (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_issue_project FOREIGN KEY (project_id)
        REFERENCES public.projects (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
)

TABLESPACE pg_default;


-- Insert initial data
-- issue_types
INSERT INTO public.issue_types VALUES (NEXTVAL('issue_types_id_seq'), 'Epic');
INSERT INTO public.issue_types VALUES (NEXTVAL('issue_types_id_seq'), 'Story');
INSERT INTO public.issue_types VALUES (NEXTVAL('issue_types_id_seq'), 'Task');
INSERT INTO public.issue_types VALUES (NEXTVAL('issue_types_id_seq'), 'Bug');

-- issue_priorities
INSERT INTO public.issue_priorities VALUES (NEXTVAL('issue_priorities_id_seq'), 'High');
INSERT INTO public.issue_priorities VALUES (NEXTVAL('issue_priorities_id_seq'), 'Normal');
INSERT INTO public.issue_priorities VALUES (NEXTVAL('issue_priorities_id_seq'), 'Low');

-- issue_states
INSERT INTO public.issue_states VALUES (NEXTVAL('issue_state_id_seq'), 'OPEN');
INSERT INTO public.issue_states VALUES (NEXTVAL('issue_state_id_seq'), 'IN PROGRESS');
INSERT INTO public.issue_states VALUES (NEXTVAL('issue_state_id_seq'), 'REVIEW');
INSERT INTO public.issue_states VALUES (NEXTVAL('issue_state_id_seq'), 'TEST');
INSERT INTO public.issue_states VALUES (NEXTVAL('issue_state_id_seq'), 'RESOLVED');
INSERT INTO public.issue_states VALUES (NEXTVAL('issue_state_id_seq'), 'REOPENED');
INSERT INTO public.issue_states VALUES (NEXTVAL('issue_state_id_seq'), 'CLOSED');

-- Insert test workflows
INSERT INTO workflows VALUES (NEXTVAL('workflow_id_seq'), 'Epic test workflow', NULL,
	(SELECT id FROM issue_types WHERE name = 'Epic'),
	(SELECT id FROM issue_states WHERE name = 'OPEN')
);

WITH wf_id_statement AS (
    SELECT id
    FROM workflows
    WHERE name = 'Epic test workflow'
)
INSERT INTO workflow_transitions (workflow_id, from_state_id, to_state_id)
VALUES
	(
		(SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'OPEN'),
        (SELECT id FROM issue_states WHERE name = 'CLOSED')),
	(
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'OPEN'),
        (SELECT id FROM issue_states WHERE name = 'IN PROGRESS')),
	(
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'IN PROGRESS'),
        (SELECT id FROM issue_states WHERE name = 'REVIEW')),
    (
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'REVIEW'),
        (SELECT id FROM issue_states WHERE name = 'TEST')),
    (
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'TEST'),
        (SELECT id FROM issue_states WHERE name = 'RESOLVED')),
    (
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'RESOLVED'),
        (SELECT id FROM issue_states WHERE name = 'CLOSED')),
    (
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'CLOSED'),
        (SELECT id FROM issue_states WHERE name = 'OPEN'));

INSERT INTO workflows VALUES (NEXTVAL('workflow_id_seq'), 'Story test workflow', NULL,
	(SELECT id FROM issue_types WHERE name = 'Story'),
	(SELECT id FROM issue_states WHERE name = 'OPEN')
);

WITH wf_id_statement AS (
    SELECT id
    FROM workflows
    WHERE name = 'Story test workflow'
)
INSERT INTO workflow_transitions (workflow_id, from_state_id, to_state_id)
VALUES
	(
		(SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'OPEN'),
        (SELECT id FROM issue_states WHERE name = 'CLOSED')),
	(
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'OPEN'),
        (SELECT id FROM issue_states WHERE name = 'IN PROGRESS')),
	(
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'IN PROGRESS'),
        (SELECT id FROM issue_states WHERE name = 'REVIEW')),
    (
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'REVIEW'),
        (SELECT id FROM issue_states WHERE name = 'TEST')),
    (
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'TEST'),
        (SELECT id FROM issue_states WHERE name = 'RESOLVED')),
    (
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'RESOLVED'),
        (SELECT id FROM issue_states WHERE name = 'CLOSED')),
    (
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'CLOSED'),
        (SELECT id FROM issue_states WHERE name = 'OPEN'));

INSERT INTO workflows VALUES (NEXTVAL('workflow_id_seq'), 'Task test workflow', NULL,
	(SELECT id FROM issue_types WHERE name = 'Task'),
	(SELECT id FROM issue_states WHERE name = 'OPEN')
);

WITH wf_id_statement AS (
    SELECT id
    FROM workflows
    WHERE name = 'Task test workflow'
)
INSERT INTO workflow_transitions (workflow_id, from_state_id, to_state_id)
VALUES
	(
		(SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'OPEN'),
        (SELECT id FROM issue_states WHERE name = 'CLOSED')),
	(
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'OPEN'),
        (SELECT id FROM issue_states WHERE name = 'IN PROGRESS')),
	(
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'IN PROGRESS'),
        (SELECT id FROM issue_states WHERE name = 'REVIEW')),
    (
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'REVIEW'),
        (SELECT id FROM issue_states WHERE name = 'TEST')),
    (
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'TEST'),
        (SELECT id FROM issue_states WHERE name = 'RESOLVED')),
    (
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'RESOLVED'),
        (SELECT id FROM issue_states WHERE name = 'CLOSED')),
    (
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'CLOSED'),
        (SELECT id FROM issue_states WHERE name = 'OPEN'));

INSERT INTO workflows VALUES (NEXTVAL('workflow_id_seq'), 'Bug test workflow', NULL,
	(SELECT id FROM issue_types WHERE name = 'Bug'),
	(SELECT id FROM issue_states WHERE name = 'OPEN')
);

WITH wf_id_statement AS (
    SELECT id
    FROM workflows
    WHERE name = 'Bug test workflow'
)
INSERT INTO workflow_transitions (workflow_id, from_state_id, to_state_id)
VALUES
	(
		(SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'OPEN'),
        (SELECT id FROM issue_states WHERE name = 'CLOSED')),
	(
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'OPEN'),
        (SELECT id FROM issue_states WHERE name = 'IN PROGRESS')),
	(
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'IN PROGRESS'),
        (SELECT id FROM issue_states WHERE name = 'REVIEW')),
    (
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'REVIEW'),
        (SELECT id FROM issue_states WHERE name = 'TEST')),
    (
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'TEST'),
        (SELECT id FROM issue_states WHERE name = 'RESOLVED')),
    (
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'RESOLVED'),
        (SELECT id FROM issue_states WHERE name = 'CLOSED')),
    (
        (SELECT id FROM wf_id_statement),
        (SELECT id FROM issue_states WHERE name = 'CLOSED'),
        (SELECT id FROM issue_states WHERE name = 'OPEN'));