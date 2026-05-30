alter table travel_watches
    add column range2_start_date date,
    add column range2_end_date date,
    add column range3_start_date date,
    add column range3_end_date date,
    add column start_days_early integer not null default 0,
    add column start_days_late integer not null default 0;

update travel_watches
set start_days_early = flexibility_days,
    start_days_late = flexibility_days
where flexibility_days is not null;
