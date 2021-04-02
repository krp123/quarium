-- begin QUARIUM_PROJECT
create unique index IDX_QUARIUM_PROJECT_UK_PROJECT_NAME on QUARIUM_PROJECT (PROJECT_NAME) where DELETE_TS is null ^
create index IDX_QUARIUM_PROJECT on QUARIUM_PROJECT (PROJECT_NAME, DESCRIPTION)^
-- end QUARIUM_PROJECT
-- begin QUARIUM_QA
create unique index IDX_QUARIUM_QA_UK_FULL_NAME on QUARIUM_QA (FULL_NAME) where DELETE_TS is null ^
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
alter table QUARIUM_CHECKLIST add constraint FK_QUARIUM_CHECKLIST_ON_PROJECT foreign key (PROJECT_ID) references QUARIUM_PROJECT(ID)^
create unique index IDX_QUARIUM_CHECKLIST_UK_NAME on QUARIUM_CHECKLIST (NAME) where DELETE_TS is null ^
create index IDX_QUARIUM_CHECKLIST_ON_PROJECT on QUARIUM_CHECKLIST (PROJECT_ID)^
-- end QUARIUM_CHECKLIST
-- begin QUARIUM_TEST_CASE
alter table QUARIUM_TEST_CASE add constraint FK_QUARIUM_TEST_CASE_ON_CHECKLIST foreign key (CHECKLIST_ID) references QUARIUM_CHECKLIST(ID)^
create unique index IDX_QUARIUM_TEST_CASE_UK_NAME on QUARIUM_TEST_CASE (NAME) where DELETE_TS is null ^
create index IDX_QUARIUM_TEST_CASE_ON_CHECKLIST on QUARIUM_TEST_CASE (CHECKLIST_ID)^
-- end QUARIUM_TEST_CASE
