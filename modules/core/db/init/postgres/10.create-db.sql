-- begin QUARIUM_PROJECT
create table QUARIUM_PROJECT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    DTYPE varchar(31),
    --
    PROJECT_NAME varchar(255),
    REGRESS_START_DATE date,
    REGRESS_FINISH_DATE date,
    DBMS_ID uuid,
    THESIS_VERSION_ID uuid,
    CREATION_DATE timestamp,
    CURRENT_RELEASE varchar(255),
    DESCRIPTION text,
    --
    primary key (ID)
)^
-- end QUARIUM_PROJECT
-- begin QUARIUM_QA
create table QUARIUM_QA (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    FULL_NAME varchar(255) not null,
    USER_ID uuid,
    --
    primary key (ID)
)^
-- end QUARIUM_QA

-- begin QUARIUM_QA_PROJECT_RELATIONSHIP
create table QUARIUM_QA_PROJECT_RELATIONSHIP (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PROJECT_ID uuid not null,
    QA_ID uuid not null,
    --
    primary key (ID)
)^
-- end QUARIUM_QA_PROJECT_RELATIONSHIP
-- begin QUARIUM_STATEMENT
create table QUARIUM_STATEMENT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    --
    primary key (ID)
)^
-- end QUARIUM_STATEMENT
-- begin QUARIUM_PRIORITY
create table QUARIUM_PRIORITY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    --
    primary key (ID)
)^
-- end QUARIUM_PRIORITY

-- begin QUARIUM_TEST_CASE
create table QUARIUM_TEST_CASE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    INITIAL_CONDITIONS varchar(1000),
    NUMBER_ integer,
    HOURS integer,
    MINUTES integer,
    PRIORITY_ID uuid,
    STATUS_ varchar(50),
    EXPECTED_RESULT text,
    CHECKLIST_ID uuid,
    --
    primary key (ID)
)^
-- end QUARIUM_TEST_CASE
-- begin QUARIUM_CONFIGURATION_PROJECT_RELATIONSHIP
create table QUARIUM_CONFIGURATION_PROJECT_RELATIONSHIP (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PROJECT_ID uuid not null,
    CONFIGURATION_ID uuid not null,
    --
    primary key (ID)
)^
-- end QUARIUM_CONFIGURATION_PROJECT_RELATIONSHIP
-- begin QUARIUM_CONFIGURATION
create table QUARIUM_CONFIGURATION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    CONFIGURATION varchar(255) not null,
    --
    primary key (ID)
)^
-- end QUARIUM_CONFIGURATION
-- begin QUARIUM_MODULE
create table QUARIUM_MODULE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    PROJECT_ID uuid,
    --
    primary key (ID)
)^
-- end QUARIUM_MODULE
-- begin QUARIUM_STEP
create table QUARIUM_STEP (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    STEP varchar(1000),
    NUMBER_ integer,
    CREATION_DATE timestamp,
    TEST_CASE_ID uuid,
    --
    primary key (ID)
)^
-- end QUARIUM_STEP
-- begin QUARIUM_DBMS
create table QUARIUM_DBMS (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    --
    primary key (ID)
)^
-- end QUARIUM_DBMS
-- begin QUARIUM_THESIS_VERSION
create table QUARIUM_THESIS_VERSION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NUMBER_ varchar(255),
    --
    primary key (ID)
)^
-- end QUARIUM_THESIS_VERSION
-- begin QUARIUM_MILESTONE
create table QUARIUM_MILESTONE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    START_DATE date,
    FINISH_DATE date,
    STATUS varchar(50),
    DESCRIPTION varchar(1000),
    PROJECT_ID uuid,
    --
    primary key (ID)
)^
-- end QUARIUM_MILESTONE
-- begin QUARIUM_TEST_RUN
create table QUARIUM_TEST_RUN (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    RUN_START_DATE date,
    RUN_FINISH_DATE date,
    MILESTONE_ID uuid,
    DESCRIPTION varchar(1000),
    PROJECT_ID uuid,
    --
    primary key (ID)
)^
-- end QUARIUM_TEST_RUN
-- begin QUARIUM_TESTSUIT
create table QUARIUM_TESTSUIT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    DTYPE varchar(31),
    --
    NAME varchar(255),
    INITIAL_CONDITIONS varchar(1000),
    PARENT_CARD_ID uuid,
    COMMENT_ varchar(1000),
    HOURS integer,
    MINUTES integer,
    TICKET varchar(1000),
    MODULE_ID uuid,
    IS_USED_IN_REGRESS boolean,
    ASSIGNED_QA_ID uuid,
    STATE_ID uuid,
    PROJECT_ID uuid,
    --
    -- from quarium_RunTestSuit
    REGRESS_PROJECT_ID uuid,
    TEST_RUN_ID uuid,
    --
    primary key (ID)
)^
-- end QUARIUM_TESTSUIT
-- begin QUARIUM_CASE_RESULT
create table QUARIUM_CASE_RESULT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    STATUS varchar(50),
    COMMENT_ varchar(1000),
    TEST_CASE_ID uuid,
    LINK varchar(1000),
    DATE_ADDED timestamp,
    EXECUTION_TIME varchar(255),
    --
    primary key (ID)
)^
-- end QUARIUM_CASE_RESULT
