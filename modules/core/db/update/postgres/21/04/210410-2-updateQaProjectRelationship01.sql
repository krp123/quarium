alter table QUARIUM_QA_PROJECT_RELATIONSHIP add constraint FK_QUARIUM_QA_PROJECT_RELATIONSHIP_ON_PROJECT foreign key (PROJECT_ID) references QUARIUM_PROJECT(ID);
create index IDX_QUARIUM_QA_PROJECT_RELATIONSHIP_ON_PROJECT on QUARIUM_QA_PROJECT_RELATIONSHIP (PROJECT_ID);
