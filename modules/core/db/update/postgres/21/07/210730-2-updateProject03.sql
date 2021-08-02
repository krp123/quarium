alter table QUARIUM_PROJECT rename column version_of_id to version_of_id__u37052 ;
alter table QUARIUM_PROJECT drop constraint FK_QUARIUM_PROJECT_ON_VERSION_OF ;
drop index IDX_QUARIUM_PROJECT_ON_VERSION_OF ;
