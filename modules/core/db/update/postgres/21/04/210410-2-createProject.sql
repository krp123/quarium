create unique index IDX_QUARIUM_PROJECT_UK_PROJECT_NAME on QUARIUM_PROJECT (PROJECT_NAME) where DELETE_TS is null ;
create index IDX_QUARIUM_PROJECT on QUARIUM_PROJECT (PROJECT_NAME, DESCRIPTION);
