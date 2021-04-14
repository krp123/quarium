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
);