alter table QUARIUM_CHECKLIST rename column assigned_qa_id to assigned_qa_id__u82717 ;
alter table QUARIUM_CHECKLIST drop constraint FK_QUARIUM_CHECKLIST_ON_ASSIGNED_QA ;
drop index IDX_QUARIUM_CHECKLIST_ON_ASSIGNED_QA ;
alter table QUARIUM_CHECKLIST add column ASSIGNED_QA_ID uuid ;
