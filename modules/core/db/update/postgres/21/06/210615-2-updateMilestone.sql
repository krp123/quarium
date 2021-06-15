update QUARIUM_MILESTONE set NAME = '' where NAME is null ;
alter table QUARIUM_MILESTONE alter column NAME set not null ;
