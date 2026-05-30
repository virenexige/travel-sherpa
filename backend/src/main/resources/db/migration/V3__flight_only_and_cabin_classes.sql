alter table travel_watches
    add column travel_product_type varchar(40) not null default 'PACKAGE',
    add column cabin_class varchar(40) not null default 'ALL';
