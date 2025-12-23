SELECT * FROM users;

SELECT COUNT(*) cnt FROM users;

DESCRIBE users;

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

CREATE TABLE IF NOT EXISTS trains (
	train_id int AUTO_INCREMENT PRIMARY KEY,
    train_code varchar(20) UNIQUE NOT NULL,
    seat_capacity int NOT NULL,
    status enum('active', 'maintenance', 'retired') NOT NULL DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS stations (
	station_id int PRIMARY KEY,
    station_code VARCHAR(100) UNIQUE NOT NULL,
    station_name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
DESCRIBE stations;

CREATE TABLE IF NOT EXISTS train_routes (
	route_id int PRIMARY KEY,
    route_code VARCHAR(100) UNIQUE NOT NULL,
    origin_station_id INT REFERENCES stations(station_id),
    destination_station_id INT REFERENCES stations(station_id),
    distance_km DECIMAL(6, 2)
);
DESCRIBE train_routes;

CREATE TABLE IF NOT EXISTS route_stations (
	route_station_id int PRIMARY KEY,
    route_id int REFERENCES train_routes(route_id),
    station_id int REFERENCES stations(station_id),
    
);

