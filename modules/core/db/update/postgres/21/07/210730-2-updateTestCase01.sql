alter table QUARIUM_TEST_CASE add constraint FK_QUARIUM_TEST_CASE_ON_CHECKLIST foreign key (CHECKLIST_ID) references QUARIUM_TESTSUIT(ID);
create index IDX_QUARIUM_TEST_CASE_ON_CHECKLIST on QUARIUM_TEST_CASE (CHECKLIST_ID);
