alter table QUARIUM_QA rename column project_id to project_id__u67267 ;
alter table QUARIUM_QA alter column project_id__u67267 drop not null ;
alter table QUARIUM_QA drop constraint FK_QUARIUM_QA_ON_PROJECT ;
drop index IDX_QUARIUM_QA_ON_PROJECT ;
