alter table QUARIUM_TEST_CASE add constraint FK_QUARIUM_TEST_CASE_ON_PRIORITY foreign key (PRIORITY_ID) references QUARIUM_PRIORITY(ID);
create index IDX_QUARIUM_TEST_CASE_ON_PRIORITY on QUARIUM_TEST_CASE (PRIORITY_ID);
