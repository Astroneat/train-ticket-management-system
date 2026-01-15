-- MySQL dump 10.13  Distrib 9.4.0, for macos15 (arm64)
--
-- Host: localhost    Database: TrainTicketManagement
-- ------------------------------------------------------
-- Server version	9.4.0-commercial

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cities`
--

DROP TABLE IF EXISTS `cities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cities` (
  `city_id` int NOT NULL AUTO_INCREMENT,
  `city_name` varchar(100) NOT NULL,
  PRIMARY KEY (`city_id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reset_password_tokens`
--

DROP TABLE IF EXISTS `reset_password_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reset_password_tokens` (
  `user_id` int NOT NULL,
  `token` varchar(6) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `reset_password_tokens_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `route_stations`
--

DROP TABLE IF EXISTS `route_stations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `route_stations` (
  `route_station_id` int NOT NULL AUTO_INCREMENT,
  `route_id` int NOT NULL,
  `station_id` int NOT NULL,
  `sequence_no` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`route_station_id`),
  UNIQUE KEY `route_id` (`route_id`,`sequence_no`),
  KEY `station_id` (`station_id`),
  CONSTRAINT `route_stations_ibfk_1` FOREIGN KEY (`route_id`) REFERENCES `train_routes` (`route_id`) ON DELETE CASCADE,
  CONSTRAINT `route_stations_ibfk_2` FOREIGN KEY (`station_id`) REFERENCES `stations` (`station_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stations`
--

DROP TABLE IF EXISTS `stations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stations` (
  `station_id` int NOT NULL AUTO_INCREMENT,
  `station_code` varchar(100) NOT NULL,
  `station_name` varchar(100) NOT NULL,
  `city_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`station_id`),
  UNIQUE KEY `station_code` (`station_code`),
  KEY `city_id_idx` (`city_id`),
  CONSTRAINT `city_id` FOREIGN KEY (`city_id`) REFERENCES `cities` (`city_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tickets`
--

DROP TABLE IF EXISTS `tickets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tickets` (
  `ticket_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `schedule_id` int NOT NULL,
  `car_no` int NOT NULL,
  `seat_index` int NOT NULL,
  `status` enum('booked','boarded','cancelled','expired') NOT NULL DEFAULT 'booked',
  `booked_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `scanned_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`ticket_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `tickets_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `tickets_ibfk_2` FOREIGN KEY (`schedule_id`) REFERENCES `train_schedules` (`schedule_id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `train_routes`
--

DROP TABLE IF EXISTS `train_routes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `train_routes` (
  `route_id` int NOT NULL AUTO_INCREMENT,
  `route_code` varchar(100) NOT NULL,
  `origin_station_id` int NOT NULL,
  `destination_station_id` int NOT NULL,
  `status` enum('active','inactive') NOT NULL DEFAULT 'active',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`route_id`),
  UNIQUE KEY `route_code` (`route_code`),
  KEY `origin_station_id` (`origin_station_id`),
  KEY `destination_station_id` (`destination_station_id`),
  CONSTRAINT `train_routes_ibfk_1` FOREIGN KEY (`origin_station_id`) REFERENCES `stations` (`station_id`),
  CONSTRAINT `train_routes_ibfk_2` FOREIGN KEY (`destination_station_id`) REFERENCES `stations` (`station_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `train_schedules`
--

DROP TABLE IF EXISTS `train_schedules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `train_schedules` (
  `schedule_id` int NOT NULL AUTO_INCREMENT,
  `train_id` int NOT NULL,
  `route_id` int NOT NULL,
  `departure_utc` timestamp NOT NULL,
  `arrival_utc` timestamp NOT NULL,
  `status` enum('scheduled','completed','cancelled') NOT NULL DEFAULT 'scheduled',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`schedule_id`),
  KEY `train_id` (`train_id`),
  KEY `route_id` (`route_id`),
  CONSTRAINT `train_schedules_ibfk_1` FOREIGN KEY (`train_id`) REFERENCES `trains` (`train_id`),
  CONSTRAINT `train_schedules_ibfk_2` FOREIGN KEY (`route_id`) REFERENCES `train_routes` (`route_id`),
  CONSTRAINT `chk_time_valid` CHECK ((`departure_utc` < `arrival_utc`))
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `trains`
--

DROP TABLE IF EXISTS `trains`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trains` (
  `train_id` int NOT NULL AUTO_INCREMENT,
  `train_code` varchar(20) NOT NULL,
  `seat_capacity` int NOT NULL,
  `status` enum('active','maintenance','retired') NOT NULL DEFAULT 'active',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`train_id`),
  UNIQUE KEY `train_code` (`train_code`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(45) NOT NULL,
  `full_name` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `password` varchar(45) NOT NULL,
  `role` enum('customer','admin') NOT NULL DEFAULT 'customer',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-10 17:54:31
