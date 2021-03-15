alter table QUARIUM_QA_PROJECT_RELATIONSHIP add constraint FK_QUARIUM_QA_PROJECT_RELATIONSHIP_ON_PROJECT foreign key (PROJECT_ID) references QUARIUM_PROJECT(ID);
alter table QUARIUM_QA_PROJECT_RELATIONSHIP add constraint FK_QUARIUM_QA_PROJECT_RELATIONSHIP_ON_QA foreign key (QA_ID) references QUARIUM_QA(ID);
create index IDX_QUARIUM_QA_PROJECT_RELATIONSHIP_ON_PROJECT on QUARIUM_QA_PROJECT_RELATIONSHIP (PROJECT_ID);
create index IDX_QUARIUM_QA_PROJECT_RELATIONSHIP_ON_QA on QUARIUM_QA_PROJECT_RELATIONSHIP (QA_ID);
