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
    PROJECT_NAME varchar(255) not null,
    CURRENT_RELEASE varchar(255),
    DESCRIPTION text,
    --
    -- from quarium_ProjectVersion
    VERSION_OF_ID uuid,
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
-- begin QUARIUM_CHECKLIST
create table QUARIUM_CHECKLIST (
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
    NAME varchar(255) not null,
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
    -- from quarium_RegressChecklist
    REGRESS_PROJECT_ID uuid,
    --
    primary key (ID)
)^
-- end QUARIUM_CHECKLIST
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
    NAME varchar(255) not null,
    CREATION_DATE timestamp,
    COMMENT_ varchar(1000),
    TICKET varchar(1000),
    HOURS integer,
    MINUTES integer,
    PRIORITY_ID uuid,
    STATE_ID uuid,
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
    CREATION_DATE timestamp,
    TEST_CASE_ID uuid,
    --
    primary key (ID)
)^
-- end QUARIUM_STEP
