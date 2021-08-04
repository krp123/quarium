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
);