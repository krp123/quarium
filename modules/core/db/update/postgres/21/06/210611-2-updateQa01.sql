alter table QUARIUM_QA add constraint FK_QUARIUM_QA_ON_USER foreign key (USER_ID) references SEC_USER(ID);
create index IDX_QUARIUM_QA_ON_USER on QUARIUM_QA (USER_ID);
