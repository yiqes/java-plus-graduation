DROP TABLE IF EXISTS requests CASCADE;


CREATE TABLE IF NOT EXISTS requests (
    request_id SERIAL PRIMARY KEY,
    event_id INTEGER NOT NULL,
    requester_id INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    created TIMESTAMP NOT NULL
);