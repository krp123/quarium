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
);