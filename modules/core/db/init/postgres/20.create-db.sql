-- begin QUARIUM_PROJECT
alter table QUARIUM_PROJECT add constraint FK_QUARIUM_PROJECT_ON_DBMS foreign key (DBMS_ID) references QUARIUM_DBMS(ID)^
alter table QUARIUM_PROJECT add constraint FK_QUARIUM_PROJECT_ON_THESIS_VERSION foreign key (THESIS_VERSION_ID) references QUARIUM_THESIS_VERSION(ID)^
alter table QUARIUM_PROJECT add constraint FK_QUARIUM_PROJECT_ON_VERSION_OF foreign key (VERSION_OF_ID) references QUARIUM_PROJECT(ID)^
create index IDX_QUARIUM_PROJECT_ON_DBMS on QUARIUM_PROJECT (DBMS_ID)^
create index IDX_QUARIUM_PROJECT_ON_THESIS_VERSION on QUARIUM_PROJECT (THESIS_VERSION_ID)^
create index IDX_QUARIUM_PROJECT_ON_VERSION_OF on QUARIUM_PROJECT (VERSION_OF_ID)^
create index IDX_QUARIUM_PROJECT on QUARIUM_PROJECT (PROJECT_NAME, DESCRIPTION)^
-- end QUARIUM_PROJECT
-- begin QUARIUM_QA
alter table QUARIUM_QA add constraint FK_QUARIUM_QA_ON_USER foreign key (USER_ID) references SEC_USER(ID)^
create unique index IDX_QUARIUM_QA_UK_FULL_NAME on QUARIUM_QA (FULL_NAME) where DELETE_TS is null ^
create index IDX_QUARIUM_QA_ON_USER on QUARIUM_QA (USER_ID)^
-- end QUARIUM_QA

-- begin QUARIUM_QA_PROJECT_RELATIONSHIP
alter table QUARIUM_QA_PROJECT_RELATIONSHIP add constraint FK_QUARIUM_QA_PROJECT_RELATIONSHIP_ON_PROJECT foreign key (PROJECT_ID) references QUARIUM_PROJECT(ID)^
alter table QUARIUM_QA_PROJECT_RELATIONSHIP add constraint FK_QUARIUM_QA_PROJECT_RELATIONSHIP_ON_QA foreign key (QA_ID) references QUARIUM_QA(ID)^
create index IDX_QUARIUM_QA_PROJECT_RELATIONSHIP_ON_PROJECT on QUARIUM_QA_PROJECT_RELATIONSHIP (PROJECT_ID)^
create index IDX_QUARIUM_QA_PROJECT_RELATIONSHIP_ON_QA on QUARIUM_QA_PROJECT_RELATIONSHIP (QA_ID)^
-- end QUARIUM_QA_PROJECT_RELATIONSHIP
-- begin QUARIUM_STATEMENT
create unique index IDX_QUARIUM_STATEMENT_UK_NAME on QUARIUM_STATEMENT (NAME) where DELETE_TS is null ^
-- end QUARIUM_STATEMENT
-- begin QUARIUM_PRIORITY
create unique index IDX_QUARIUM_PRIORITY_UK_NAME on QUARIUM_PRIORITY (NAME) where DELETE_TS is null ^
-- end QUARIUM_PRIORITY
-- begin QUARIUM_CHECKLIST
alter table QUARIUM_CHECKLIST add constraint FK_QUARIUM_CHECKLIST_ON_PARENT_CARD foreign key (PARENT_CARD_ID) references QUARIUM_CHECKLIST(ID)^
alter table QUARIUM_CHECKLIST add constraint FK_QUARIUM_CHECKLIST_ON_MODULE foreign key (MODULE_ID) references QUARIUM_MODULE(ID)^
alter table QUARIUM_CHECKLIST add constraint FK_QUARIUM_CHECKLIST_ON_ASSIGNED_QA foreign key (ASSIGNED_QA_ID) references QUARIUM_QA_PROJECT_RELATIONSHIP(ID)^
alter table QUARIUM_CHECKLIST add constraint FK_QUARIUM_CHECKLIST_ON_STATE foreign key (STATE_ID) references QUARIUM_STATEMENT(ID)^
alter table QUARIUM_CHECKLIST add constraint FK_QUARIUM_CHECKLIST_ON_PROJECT foreign key (PROJECT_ID) references QUARIUM_PROJECT(ID)^
alter table QUARIUM_CHECKLIST add constraint FK_QUARIUM_CHECKLIST_ON_REGRESS_PROJECT foreign key (REGRESS_PROJECT_ID) references QUARIUM_PROJECT(ID)^
alter table QUARIUM_CHECKLIST add constraint FK_QUARIUM_CHECKLIST_ON_TEST_RUN foreign key (TEST_RUN_ID) references QUARIUM_TEST_RUN(ID)^
create index IDX_QUARIUM_CHECKLIST_ON_PARENT_CARD on QUARIUM_CHECKLIST (PARENT_CARD_ID)^
create index IDX_QUARIUM_CHECKLIST_ON_MODULE on QUARIUM_CHECKLIST (MODULE_ID)^
create index IDX_QUARIUM_CHECKLIST_ON_ASSIGNED_QA on QUARIUM_CHECKLIST (ASSIGNED_QA_ID)^
create index IDX_QUARIUM_CHECKLIST_ON_STATE on QUARIUM_CHECKLIST (STATE_ID)^
create index IDX_QUARIUM_CHECKLIST_ON_PROJECT on QUARIUM_CHECKLIST (PROJECT_ID)^
create index IDX_QUARIUM_CHECKLIST_ON_REGRESS_PROJECT on QUARIUM_CHECKLIST (REGRESS_PROJECT_ID)^
create index IDX_QUARIUM_CHECKLIST_ON_TEST_RUN on QUARIUM_CHECKLIST (TEST_RUN_ID)^
-- end QUARIUM_CHECKLIST
-- begin QUARIUM_TEST_CASE
alter table QUARIUM_TEST_CASE add constraint FK_QUARIUM_TEST_CASE_ON_PRIORITY foreign key (PRIORITY_ID) references QUARIUM_PRIORITY(ID)^
alter table QUARIUM_TEST_CASE add constraint FK_QUARIUM_TEST_CASE_ON_STATE foreign key (STATE_ID) references QUARIUM_STATEMENT(ID)^
alter table QUARIUM_TEST_CASE add constraint FK_QUARIUM_TEST_CASE_ON_CHECKLIST foreign key (CHECKLIST_ID) references QUARIUM_CHECKLIST(ID)^
create index IDX_QUARIUM_TEST_CASE_ON_PRIORITY on QUARIUM_TEST_CASE (PRIORITY_ID)^
create index IDX_QUARIUM_TEST_CASE_ON_STATE on QUARIUM_TEST_CASE (STATE_ID)^
create index IDX_QUARIUM_TEST_CASE_ON_CHECKLIST on QUARIUM_TEST_CASE (CHECKLIST_ID)^
-- end QUARIUM_TEST_CASE
-- begin QUARIUM_CONFIGURATION_PROJECT_RELATIONSHIP
alter table QUARIUM_CONFIGURATION_PROJECT_RELATIONSHIP add constraint FK_QUARIUM_CONFIGURATION_PROJECT_RELATIONSHIP_ON_PROJECT foreign key (PROJECT_ID) references QUARIUM_PROJECT(ID)^
alter table QUARIUM_CONFIGURATION_PROJECT_RELATIONSHIP add constraint FK_QUARIUM_CONFIGURATION_PROJECT_RELATIONSHIP_ON_CONFIGURATION foreign key (CONFIGURATION_ID) references QUARIUM_CONFIGURATION(ID)^
create index IDX_QUARIUM_CONFIGURATION_PROJECT_RELATIONSHIP_ON_PROJECT on QUARIUM_CONFIGURATION_PROJECT_RELATIONSHIP (PROJECT_ID)^
create index IDX_QUARIUM_CONFIGURAPROJECTRELATIONS_ON_CONFIGURATION on QUARIUM_CONFIGURATION_PROJECT_RELATIONSHIP (CONFIGURATION_ID)^
-- end QUARIUM_CONFIGURATION_PROJECT_RELATIONSHIP
-- begin QUARIUM_MODULE
alter table QUARIUM_MODULE add constraint FK_QUARIUM_MODULE_ON_PROJECT foreign key (PROJECT_ID) references QUARIUM_PROJECT(ID)^
create index IDX_QUARIUM_MODULE_ON_PROJECT on QUARIUM_MODULE (PROJECT_ID)^
-- end QUARIUM_MODULE
-- begin QUARIUM_STEP
alter table QUARIUM_STEP add constraint FK_QUARIUM_STEP_ON_TEST_CASE foreign key (TEST_CASE_ID) references QUARIUM_TEST_CASE(ID)^
create index IDX_QUARIUM_STEP_ON_TEST_CASE on QUARIUM_STEP (TEST_CASE_ID)^
-- end QUARIUM_STEP
-- begin QUARIUM_MILESTONE
alter table QUARIUM_MILESTONE add constraint FK_QUARIUM_MILESTONE_ON_PROJECT foreign key (PROJECT_ID) references QUARIUM_PROJECT(ID)^
create index IDX_QUARIUM_MILESTONE_ON_PROJECT on QUARIUM_MILESTONE (PROJECT_ID)^
-- end QUARIUM_MILESTONE
-- begin QUARIUM_TEST_RUN
alter table QUARIUM_TEST_RUN add constraint FK_QUARIUM_TEST_RUN_ON_PROJECT foreign key (PROJECT_ID) references QUARIUM_PROJECT(ID)^
create index IDX_QUARIUM_TEST_RUN_ON_PROJECT on QUARIUM_TEST_RUN (PROJECT_ID)^
-- end QUARIUM_TEST_RUN
