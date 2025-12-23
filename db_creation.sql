CREATE DATABASE 

CREATE TABLE IF NOT EXISTS users (
	user_id int AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(45) UNIQUE NOT NULL,
    full_name VARCHAR(45) NOT NULL,
    password VARCHAR(45) NOT NULL,
    role ENUM('customer', 'admin') NOT NULL DEFAULT 'customer',
    created_at TIMESTAMP DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS trains (
	train_id int AUTO_INCREMENT PRIMARY KEY,
    train_code varchar(20) UNIQUE NOT NULL,
    seat_capacity int NOT NULL,
    status enum('active', 'maintenance', 'retired') NOT NULL DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS stations (
	station_id int AUTO_INCREMENT PRIMARY KEY,
    station_code VARCHAR(100) UNIQUE NOT NULL,
    station_name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS train_routes (
    route_id INT AUTO_INCREMENT PRIMARY KEY,
    route_code VARCHAR(100) UNIQUE NOT NULL,
    origin_station_id INT NOT NULL,
    destination_station_id INT NOT NULL,
    distance_km DECIMAL(6,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (origin_station_id) REFERENCES stations(station_id),
    FOREIGN KEY (destination_station_id) REFERENCES stations(station_id)
);

CREATE TABLE IF NOT EXISTS route_stations (
    route_station_id INT AUTO_INCREMENT PRIMARY KEY,
    route_id INT NOT NULL,
    station_id INT NOT NULL,
    sequence_no INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (route_id) REFERENCES train_routes(route_id)
        ON DELETE CASCADE,
    FOREIGN KEY (station_id) REFERENCES stations(station_id),
    UNIQUE (route_id, sequence_no)
);

CREATE TABLE IF NOT EXISTS train_route_assignments (
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    train_id INT NOT NULL,
    route_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (train_id) REFERENCES trains(train_id)
        ON DELETE CASCADE,
    FOREIGN KEY (route_id) REFERENCES train_routes(route_id)
        ON DELETE CASCADE,
    UNIQUE (train_id, route_id)
);

CREATE TABLE IF NOT EXISTS train_schedules (
    schedule_id INT AUTO_INCREMENT PRIMARY KEY,
    assignment_id INT NOT NULL,
    departure_datetime DATETIME NOT NULL,
    arrival_datetime DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (assignment_id) REFERENCES train_route_assignments(assignment_id)
        ON DELETE CASCADE
);

INSERT INTO users (email, full_name, role, password) VALUES
('nguyen.anh@gmail.com', 'Nguyen Minh Anh', 'customer', 'Anh@4821'),
('tran.huy@gmail.com', 'Tran Quang Huy', 'customer', 'Huy#7392'),
('le.thao@yahoo.com', 'Le Phuong Thao', 'customer', 'Thao!1056'),
('pham.tuan@gmail.com', 'Pham Duc Tuan', 'customer', 'Tuan$8843'),
('hoang.linh@gmail.com', 'Hoang Thu Linh', 'customer', 'Linh@2719'),
('vu.hai@gmail.com', 'Vu Thanh Hai', 'customer', 'Hai#6602'),
('bui.ngoc@gmail.com', 'Bui Ngoc Han', 'customer', 'Han!9430'),
('doan.khanh@gmail.com', 'Doan Minh Khanh', 'customer', 'Khanh$5128'),

('admin@mrt.vn', 'MRT System Admin', 'admin', 'Admin@999'),
('it.admin@mrt.vn', 'Nguyen Tuan IT', 'admin', 'IT#2025'),
('operations@mrt.vn', 'Tran Operations Lead', 'admin', 'Ops!7788');

INSERT INTO stations (station_code, station_name, city) VALUES
('DN', 'Da Nang Central', 'Da Nang'),
('HU', 'Hue Station', 'Hue'),
('DH', 'Dong Hoi Station', 'Dong Hoi'),
('VI', 'Vinh Station', 'Vinh'),
('HN', 'Hanoi Central', 'Hanoi'),
('HP', 'Hai Phong Station', 'Hai Phong');

INSERT INTO trains (train_code, seat_capacity, status) VALUES
('MRT-T01', 300, 'active'),
('MRT-T02', 280, 'active'),
('MRT-T03', 220, 'maintenance'),
('MRT-T04', 320, 'active'),
('MRT-T05', 200, 'retired');

INSERT INTO train_routes (route_code, origin_station_id, destination_station_id, distance_km)
VALUES
('DN-HU', 1, 2, 102.5),
('DN-HN', 1, 5, 764.0),
('HU-HN', 2, 5, 661.5),
('HN-HP', 5, 6, 120.0);

INSERT INTO route_stations (route_id, station_id, sequence_no) VALUES
(1, 1, 1), -- Da Nang
(1, 2, 2); -- Hue

INSERT INTO route_stations (route_id, station_id, sequence_no) VALUES
(2, 1, 1), -- Da Nang
(2, 2, 2), -- Hue
(2, 3, 3), -- Dong Hoi
(2, 4, 4), -- Vinh
(2, 5, 5); -- Hanoi

INSERT INTO route_stations (route_id, station_id, sequence_no) VALUES
(3, 2, 1), -- Hue
(3, 3, 2), -- Dong Hoi
(3, 4, 3), -- Vinh
(3, 5, 4); -- Hanoi

INSERT INTO route_stations (route_id, station_id, sequence_no) VALUES
(4, 5, 1), -- Hanoi
(4, 6, 2); -- Hai Phong

INSERT INTO train_route_assignments (train_id, route_id, active) VALUES
(1, 1, TRUE),  -- MRT-T01 on DN-HU
(1, 2, TRUE),  -- MRT-T01 on DN-HN
(2, 3, TRUE),  -- MRT-T02 on HU-HN
(4, 4, TRUE);  -- MRT-T04 on HN-HP

INSERT INTO train_schedules (assignment_id, departure_time, arrival_time) VALUES
(1, '07:00', '09:30'),
(2, '06:00', '18:30'),
(3, '08:15', '17:45'),
(5, '09:00', '11:00');
