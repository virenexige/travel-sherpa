alter table travel_watches
    add column finish_days_early integer not null default 0,
    add column finish_days_late integer not null default 0,
    add column duration_increase_days integer not null default 0;
