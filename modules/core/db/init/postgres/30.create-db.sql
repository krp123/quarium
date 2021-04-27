--begin insert QUARIUM_STATEMENT
insert into QUARIUM_STATEMENT (ID, VERSION, CREATE_TS, CREATED_BY, NAME) values ('31c599f1-c1b0-30ae-add1-5c6e4b354276', 1, now(), 'admin', 'Не приступали')^
insert into QUARIUM_STATEMENT (ID, VERSION, CREATE_TS, CREATED_BY, NAME) values ('80d7e75a-9df1-96b4-c2d8-b78faffdaa53', 1, now(), 'admin', 'На проверке')^
insert into QUARIUM_STATEMENT (ID, VERSION, CREATE_TS, CREATED_BY, NAME) values ('cd85906d-6fbe-3e8d-8602-bf1af8e1ea53', 1, now(), 'admin', 'Баг')^
insert into QUARIUM_STATEMENT (ID, VERSION, CREATE_TS, CREATED_BY, NAME) values ('d9d8fd34-068d-99db-5adc-9d95731bc419', 1, now(), 'admin', 'Проверен')^
--end insert QUARIUM_STATEMENT

--begin insert QUARIUM_PRIORITY
insert into QUARIUM_PRIORITY (ID, VERSION, CREATE_TS, CREATED_BY, NAME) values (newid(), 1, now(), 'admin', 'Высокий')^
insert into QUARIUM_PRIORITY (ID, VERSION, CREATE_TS, CREATED_BY, NAME) values (newid(), 1, now(), 'admin', 'Средний')^
insert into QUARIUM_PRIORITY (ID, VERSION, CREATE_TS, CREATED_BY, NAME) values (newid(), 1, now(), 'admin', 'Низкий')^
--end insert QUARIUM_PRIORITY
