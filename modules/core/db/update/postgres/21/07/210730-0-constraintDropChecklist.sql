alter table quarium_checklist drop constraint FK_QUARIUM_CHECKLIST_ON_PARENT_CARD ;
alter table quarium_test_case drop constraint FK_QUARIUM_TEST_CASE_ON_CHECKLIST ;
