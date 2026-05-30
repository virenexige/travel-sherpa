alter table travel_watches
    add column bucket_list boolean not null default false,
    add column bucket_list_name varchar(180),
    add column earliest_start_date date,
    add column latest_end_date date,
    add column notes text;

create table search_activity_logs (
    id uuid primary key,
    travel_watch_id uuid not null references travel_watches(id) on delete cascade,
    provider_name varchar(120) not null,
    search_type varchar(80) not null,
    departure_location varchar(160) not null,
    destination varchar(160) not null,
    departure_airport varchar(32) not null,
    arrival_airport varchar(32) not null,
    start_date date not null,
    end_date date not null,
    status varchar(32) not null,
    offers_returned integer not null,
    cheapest_package_price numeric(12, 2),
    currency varchar(8),
    message text not null,
    searched_at timestamp with time zone not null
);

create index idx_search_activity_logs_watch_searched on search_activity_logs(travel_watch_id, searched_at desc);
