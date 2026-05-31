ALTER TABLE venues ADD COLUMN ciudad VARCHAR(180) DEFAULT 'Bogota' NOT NULL;
ALTER TABLE events ADD COLUMN active BOOLEAN DEFAULT TRUE NOT NULL;

CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    description VARCHAR(500)
);

CREATE TABLE events_categories (
    event_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    CONSTRAINT pk_events_categories PRIMARY KEY (event_id, category_id),
    CONSTRAINT fk_events_categories_event FOREIGN KEY (event_id) REFERENCES events(id),
    CONSTRAINT fk_events_categories_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

ALTER TABLE events MODIFY COLUMN venue_id BIGINT NOT NULL;
ALTER TABLE events ADD CONSTRAINT fk_events_venue FOREIGN KEY (venue_id) REFERENCES venues(id);
CREATE INDEX idx_events_fecha ON events(fecha DESC);
CREATE INDEX idx_venues_ciudad ON venues(ciudad);
CREATE INDEX idx_venues_capacidad ON venues(capacidad);
