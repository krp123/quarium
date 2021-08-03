alter table QUARIUM_TEST_CASE rename column result_ to result___u42555 ;
alter table QUARIUM_TEST_CASE rename column state_id to state_id__u55290 ;
alter table QUARIUM_TEST_CASE drop constraint FK_QUARIUM_TEST_CASE_ON_STATE ;
drop index IDX_QUARIUM_TEST_CASE_ON_STATE ;
alter table QUARIUM_TEST_CASE add column STATUS_ varchar(50) ;
