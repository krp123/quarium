alter table QUARIUM_QA add constraint FK_QUARIUM_QA_ON_PROJECT foreign key (PROJECT_ID) references QUARIUM_PROJECT(ID);
create unique index IDX_QUARIUM_QA_UK_FULL_NAME on QUARIUM_QA (FULL_NAME) where DELETE_TS is null ;
create index IDX_QUARIUM_QA_ON_PROJECT on QUARIUM_QA (PROJECT_ID);
