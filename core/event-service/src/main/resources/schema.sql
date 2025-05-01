DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS compilations_events CASCADE;
DROP TABLE IF EXISTS locations CASCADE;
DROP TABLE IF EXISTS comments CASCADE;


-- Создание таблицы locations
CREATE TABLE IF NOT EXISTS locations (
    location_id SERIAL PRIMARY KEY,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL
);

-- Создание таблицы categories
CREATE TABLE IF NOT EXISTS categories (
    category_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Создание таблицы events
CREATE TABLE IF NOT EXISTS events (
    event_id SERIAL PRIMARY KEY,
    annotation TEXT,
    category_id INTEGER REFERENCES categories(category_id),
    confirmed_requests INTEGER,
    created_on TIMESTAMP,
    description TEXT,
    event_date TIMESTAMP,
    user_id INTEGER,  -- Связь с таблицей users (не предоставлена)
    location_id INTEGER REFERENCES locations(location_id),
    paid BOOLEAN,
    participant_limit INTEGER,
    published_on TIMESTAMP,
    request_moderation BOOLEAN,
    state VARCHAR(50),  -- Enum EventState преобразуется в VARCHAR
    title VARCHAR(255),
    views INTEGER
);

CREATE TABLE IF NOT EXISTS LIKES_EVENTS
(
    EVENT_ID BIGINT NOT NULL,
    USER_ID BIGINT NOT NULL,
    CONSTRAINT LIKES_EVENTS_PK
        PRIMARY KEY (EVENT_ID, USER_ID)
);

-- Создание таблицы compilations
CREATE TABLE IF NOT EXISTS compilations (
    compilation_id SERIAL PRIMARY KEY,
    pinned BOOLEAN DEFAULT FALSE,
    title VARCHAR(255)
);

-- Создание связующей таблицы compilations_events
CREATE TABLE IF NOT EXISTS compilations_events (
    compilation_id INTEGER REFERENCES compilations(compilation_id),
    event_id INTEGER REFERENCES events(event_id),
    PRIMARY KEY (compilation_id, event_id)
);

-- Создание таблицы comments
CREATE TABLE IF NOT EXISTS comments (
    comment_id SERIAL PRIMARY KEY,
    author_id INTEGER,  -- Связь с таблицей users (не предоставлена)
    event_id INTEGER REFERENCES events(event_id),
    text TEXT,
    created TIMESTAMP,
    updated TIMESTAMP,
    parent_id INTEGER REFERENCES comments(comment_id)
);