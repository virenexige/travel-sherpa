alter table travel_watches
    add column trip_duration_days integer not null default 7;

update travel_watches
set trip_duration_days = greatest(1, end_date - start_date);
