alter table QUARIUM_TEST_CASE rename column checklist_id to checklist_id__u00074 ;
drop index IDX_QUARIUM_TEST_CASE_ON_CHECKLIST ;
alter table QUARIUM_TEST_CASE add column CHECKLIST_ID uuid ;
