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
('Fly Bondi'); -- d

INSERT INTO airports (name) VALUES 
('JFK International Airport'),
('Los Angeles International Airport'),
('Ezeiza Airport'),
('Tokyo Airport'),
('Cancun Airport'); -- d

INSERT INTO routes (id_from, id_to, airline_id, km, price) VALUES 
(101, 102, 201, 4000, 500.00),  
(102, 103, 202, 8750, 750.00),  
(103, 104, 203, 9600, 880.00), 
(104, 105, 204, 9700, 900.00),  
(101, 105, 202, 5800, 650.00); -- d

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
