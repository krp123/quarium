alter table QUARIUM_TEST_RUN add constraint FK_QUARIUM_TEST_RUN_ON_PROJECT foreign key (PROJECT_ID) references QUARIUM_PROJECT(ID);
create index IDX_QUARIUM_TEST_RUN_ON_PROJECT on QUARIUM_TEST_RUN (PROJECT_ID);