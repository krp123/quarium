alter table QUARIUM_STEP add constraint FK_QUARIUM_STEP_ON_TEST_CASE foreign key (TEST_CASE_ID) references QUARIUM_TEST_CASE(ID);
create index IDX_QUARIUM_STEP_ON_TEST_CASE on QUARIUM_STEP (TEST_CASE_ID);