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
    START_DATE timestamp,
    FINISH_DATE timestamp,
    STATUS varchar(50),
    DESCRIPTION varchar(1000),
    PROJECT_ID uuid,
    --
    primary key (ID)
);