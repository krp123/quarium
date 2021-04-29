alter table QUARIUM_CHECKLIST rename column sub_card_id to sub_card_id__u07052 ;
alter table QUARIUM_CHECKLIST drop constraint FK_QUARIUM_CHECKLIST_ON_SUB_CARD ;
drop index IDX_QUARIUM_CHECKLIST_ON_SUB_CARD ;
