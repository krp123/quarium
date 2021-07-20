alter table QUARIUM_TEST_RUN rename column run_finish_date to run_finish_date__u77142 ;
alter table QUARIUM_TEST_RUN rename column run_start_date to run_start_date__u21504 ;
alter table QUARIUM_TEST_RUN add column RUN_START_DATE date ;
alter table QUARIUM_TEST_RUN add column RUN_FINISH_DATE date ;
