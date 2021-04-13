--begin insert QUARIUM_STATEMENT
insert into QUARIUM_STATEMENT (ID, VERSION, CREATE_TS, CREATED_BY, NAME) values (newid(), 1, now(), 'admin', 'Не приступали')^
insert into QUARIUM_STATEMENT (ID, VERSION, CREATE_TS, CREATED_BY, NAME) values (newid(), 1, now(), 'admin', 'На проверке')^
insert into QUARIUM_STATEMENT (ID, VERSION, CREATE_TS, CREATED_BY, NAME) values (newid(), 1, now(), 'admin', 'Баг')^
insert into QUARIUM_STATEMENT (ID, VERSION, CREATE_TS, CREATED_BY, NAME) values (newid(), 1, now(), 'admin', 'Проверен')^
--end insert QUARIUM_STATEMENT

--begin insert QUARIUM_PRIORITY
insert into QUARIUM_PRIORITY (ID, VERSION, CREATE_TS, CREATED_BY, NAME) values (newid(), 1, now(), 'admin', 'Высокий')^
insert into QUARIUM_PRIORITY (ID, VERSION, CREATE_TS, CREATED_BY, NAME) values (newid(), 1, now(), 'admin', 'Средний')^
insert into QUARIUM_PRIORITY (ID, VERSION, CREATE_TS, CREATED_BY, NAME) values (newid(), 1, now(), 'admin', 'Низкий')^
--end insert QUARIUM_PRIORITY
