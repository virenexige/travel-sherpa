create table app_users (
    id uuid primary key,
    name varchar(160) not null,
    email varchar(320) not null unique,
    created_at timestamp with time zone not null
);

create table travel_watches (
    id uuid primary key,
    user_id uuid not null references app_users(id) on delete cascade,
    departure_location varchar(160) not null,
    destination varchar(160) not null,
    start_date date not null,
    end_date date not null,
    travellers integer not null,
    flexibility_days integer not null,
    max_budget numeric(12, 2),
    trip_type varchar(80) not null,
    preferred_hotel_rating integer,
    status varchar(32) not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create index idx_travel_watches_user_status on travel_watches(user_id, status);

create table travel_search_results (
    id uuid primary key,
    travel_watch_id uuid not null references travel_watches(id) on delete cascade,
    provider_name varchar(120) not null,
    destination varchar(160) not null,
    departure_airport varchar(32) not null,
    arrival_airport varchar(32) not null,
    start_date date not null,
    end_date date not null,
    flight_price numeric(12, 2) not null,
    hotel_price numeric(12, 2) not null,
    package_price numeric(12, 2) not null,
    currency varchar(8) not null,
    deal_score integer not null,
    result_url varchar(1000),
    searched_at timestamp with time zone not null
);

create index idx_search_results_watch_searched on travel_search_results(travel_watch_id, searched_at desc);

create table price_history (
    id uuid primary key,
    travel_watch_id uuid not null references travel_watches(id) on delete cascade,
    provider_name varchar(120) not null,
    package_price numeric(12, 2) not null,
    flight_price numeric(12, 2) not null,
    hotel_price numeric(12, 2) not null,
    currency varchar(8) not null,
    searched_at timestamp with time zone not null
);

create index idx_price_history_watch_searched on price_history(travel_watch_id, searched_at);

create table recommendations (
    id uuid primary key,
    travel_watch_id uuid not null references travel_watches(id) on delete cascade,
    title varchar(220) not null,
    explanation text not null,
    recommendation_type varchar(80) not null,
    confidence_score numeric(5, 2) not null,
    estimated_saving numeric(12, 2) not null,
    created_at timestamp with time zone not null
);

create index idx_recommendations_watch_created on recommendations(travel_watch_id, created_at desc);
