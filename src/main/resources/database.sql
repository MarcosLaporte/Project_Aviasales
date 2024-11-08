CREATE TABLE airlines (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE
);
ALTER TABLE airlines AUTO_INCREMENT = 201;

CREATE TABLE airports (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE
);
ALTER TABLE airports AUTO_INCREMENT = 101;

CREATE TABLE routes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_from INT NOT NULL,
    id_to INT NOT NULL,
    airline_id INT NOT NULL,
    km INT UNSIGNED NOT NULL CHECK (km > 0),
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),

    FOREIGN KEY (id_from) REFERENCES airports(id) ON DELETE CASCADE,
    FOREIGN KEY (id_to) REFERENCES airports(id) ON DELETE CASCADE,
    FOREIGN KEY (airline_id) REFERENCES airlines(id) ON DELETE CASCADE
);
ALTER TABLE routes AUTO_INCREMENT = 1001;

CREATE TABLE passengers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(255) NOT NULL -- separar
);
ALTER TABLE passengers
DROP COLUMN full_name,
    ADD COLUMN name VARCHAR(255) NOT NULL,
    ADD COLUMN last_name VARCHAR(255) NOT NULL;


CREATE TABLE trips (
    id INT PRIMARY KEY AUTO_INCREMENT,
    pass_id INT NOT NULL,
    route_id INT NOT NULL,
    date DATE NOT NULL,
    FOREIGN KEY (pass_id) REFERENCES passengers(id) ON DELETE CASCADE,
    FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE
);

-- some inserts
INSERT INTO airlines (name) VALUES
('Aerolineas Argentinas'),
('Air Canada'),
('Copa Airlines'),
('Fly Bondi');

INSERT INTO airports (name) VALUES
('JFK International Airport'),
('Los Angeles International Airport'),
('Ezeiza Airport'),
('Tokyo Airport'),
('Cancun Airport');

INSERT INTO routes (id_from, id_to, airline_id, km, price) VALUES
(105, 103, 202, 2800, 320.00),
(105, 102, 201, 4500, 550.00),
(103, 104, 203, 11700, 600.00),
(103, 101, 201, 8500, 800.00),
(101, 105, 204, 2500, 2000.00),
(101, 102, 202, 4000, 420.00),
(102, 101, 203, 4000, 2500.00),
(102, 104, 201, 11500, 1200.00),
(104, 105, 204, 7200, 750.00),
(104, 102, 202, 11500, 1150.00),
(102, 104, 204, 11500, 200.00),
(104, 105, 204, 7200, 150.00),
(103, 101, 204, 8500, 730.00);

INSERT INTO passengers (name, last_name) VALUES
('Marcos', 'Laporte'),
('Jaco', 'Luna'),
('Ivan', 'Pineda'),
('Bruno', 'Pruzsi'),
('Anna', 'Taylor');

INSERT INTO trips (pass_id, route_id, date) VALUES
(1, 1001, '2024-11-10'),
(2, 1002, '2024-11-12'),
(3, 1003, '2024-12-05'),
(4, 1004, '2024-12-08'),
(5, 1001, '2024-11-15');

SELECT
    p.name AS passenger_name,
    p.last_name AS passenger_last_name,
    a1.name AS origin_airport,
    a2.name AS destination_airport,
    r.price AS ticket_price,
    t.date AS trip_date
FROM
    trips t
JOIN
    passengers p ON t.pass_id = p.id
JOIN
    routes r ON t.route_id = r.id
JOIN
    airports a1 ON r.id_from = a1.id
JOIN
    airports a2 ON r.id_to = a2.id;
