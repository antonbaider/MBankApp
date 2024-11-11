-- MySQL dump 10.13  Distrib 9.0.1, for macos15.1 (arm64)
--
-- Host: localhost    Database: bankmthree
-- ------------------------------------------------------
-- Server version	9.1.0

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
-- Table structure for table `account_services`
--

DROP TABLE IF EXISTS `account_services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account_services` (
  `account_id` bigint NOT NULL,
  `service_id` bigint NOT NULL,
  PRIMARY KEY (`account_id`,`service_id`),
  KEY `FKf3q48n2uoxxgck7it6ppb6fku` (`service_id`),
  CONSTRAINT `FKf3q48n2uoxxgck7it6ppb6fku` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`),
  CONSTRAINT `FKi95jwi2nf3i1o2hbndg4pkc67` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_services`
--

LOCK TABLES `account_services` WRITE;
/*!40000 ALTER TABLE `account_services` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `accounts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `balance` decimal(38,2) NOT NULL,
  `card_number` varchar(16) NOT NULL,
  `creation_date` date NOT NULL,
  `currency` enum('EUR','GBP','INR','PLN','USD') NOT NULL,
  `expiration_date` date NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6p8wnyww1i4s0oygiw1erctlh` (`card_number`),
  KEY `FKnjuop33mo69pd79ctplkck40n` (`user_id`),
  CONSTRAINT `FKnjuop33mo69pd79ctplkck40n` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (1,0.00,'4000275910275502','2024-11-07','USD','2029-11-07',1),(3,6183.00,'4000255884449440','2024-11-07','USD','2029-11-07',2),(5,9987567.00,'4000229905609795','2024-11-07','USD','2029-11-07',3),(8,0.00,'4000324792175362','2024-11-08','USD','2029-11-08',4),(10,336.00,'4000199204423075','2024-11-08','USD','2029-11-08',5),(12,0.00,'4000272373910451','2024-11-08','USD','2029-11-08',6),(13,0.00,'4000472960866500','2024-11-08','EUR','2029-11-08',6),(14,3914.00,'4000920232190075','2024-11-08','USD','2029-11-08',7),(17,1500.00,'4000731832684072','2024-11-08','USD','2029-11-08',8),(18,0.00,'4000543268015451','2024-11-08','EUR','2029-11-08',8),(19,0.00,'4000022188841494','2024-11-08','USD','2029-11-08',9),(20,0.00,'4000624721501814','2024-11-08','EUR','2029-11-08',9),(21,0.00,'4000206700431120','2024-11-09','USD','2029-11-09',10),(22,0.00,'4000803489519933','2024-11-09','EUR','2029-11-09',10),(23,0.00,'4000818316908182','2024-11-09','USD','2029-11-09',11),(24,0.00,'4000299536208275','2024-11-09','EUR','2029-11-09',11),(25,0.00,'4000194595716187','2024-11-09','USD','2029-11-09',12),(26,0.00,'4000626654337132','2024-11-09','EUR','2029-11-09',12),(27,0.00,'4000790249998145','2024-11-09','USD','2029-11-09',13),(28,0.00,'4000615716164709','2024-11-09','EUR','2029-11-09',13),(50,0.00,'4000269495789482','2024-11-09','EUR','2029-11-09',3),(51,0.00,'4000449852706770','2024-11-09','GBP','2029-11-09',3),(52,0.00,'4000220532282486','2024-11-09','PLN','2029-11-09',3),(53,500.00,'4000596690818066','2024-11-09','USD','2029-11-09',14),(54,0.00,'4000812545902413','2024-11-09','EUR','2029-11-09',14),(58,0.00,'4000855874406902','2024-11-09','EUR','2029-11-09',7),(59,0.00,'4000747958600635','2024-11-09','PLN','2029-11-09',7),(60,0.00,'4000876602662467','2024-11-09','GBP','2029-11-09',7),(61,0.00,'4000717654492439','2024-11-09','INR','2029-11-09',7),(62,0.00,'4000575628503099','2024-11-09','PLN','2029-11-09',14),(63,0.00,'4000390186618515','2024-11-09','GBP','2029-11-09',14),(64,0.00,'4000983439878900','2024-11-09','INR','2029-11-09',14);
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_accounts`
--

DROP TABLE IF EXISTS `group_accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_accounts` (
  `group_id` bigint NOT NULL,
  `account_id` bigint NOT NULL,
  PRIMARY KEY (`group_id`,`account_id`),
  KEY `FKm4adfpvyh0rduv9lqfqspeuaf` (`account_id`),
  CONSTRAINT `FKm4adfpvyh0rduv9lqfqspeuaf` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`),
  CONSTRAINT `FKsrwvm9r8xfl5ls6hbeieg011b` FOREIGN KEY (`group_id`) REFERENCES `user_groups` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_accounts`
--

LOCK TABLES `group_accounts` WRITE;
/*!40000 ALTER TABLE `group_accounts` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `services`
--

DROP TABLE IF EXISTS `services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `services` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `service_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `services`
--

LOCK TABLES `services` WRITE;
/*!40000 ALTER TABLE `services` DISABLE KEYS */;
/*!40000 ALTER TABLE `services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(38,2) DEFAULT NULL,
  `timestamp` datetime(6) DEFAULT NULL,
  `receiver_id` bigint DEFAULT NULL,
  `receiver_account_id` bigint DEFAULT NULL,
  `sender_id` bigint DEFAULT NULL,
  `sender_account_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5nn8ird7idyxyxki68gox2wbx` (`receiver_id`),
  KEY `FKrspclyipil8qrxop31nvc7rrc` (`receiver_account_id`),
  KEY `FK3ly4r8r6ubt0blftudix2httv` (`sender_id`),
  KEY `FK68i6gd4um71kdv786gc5exkme` (`sender_account_id`),
  CONSTRAINT `FK3ly4r8r6ubt0blftudix2httv` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK5nn8ird7idyxyxki68gox2wbx` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK68i6gd4um71kdv786gc5exkme` FOREIGN KEY (`sender_account_id`) REFERENCES `accounts` (`id`),
  CONSTRAINT `FKrspclyipil8qrxop31nvc7rrc` FOREIGN KEY (`receiver_account_id`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
INSERT INTO `transactions` VALUES (2,970.00,'2024-11-07 13:34:05.583781',2,3,3,5),(3,380.00,'2024-11-07 13:41:20.206506',2,3,3,5),(7,482.00,'2024-11-07 14:13:27.985919',2,3,3,5),(9,645.00,'2024-11-07 14:18:35.242705',2,3,3,5),(23,1000.00,'2024-11-08 02:30:08.762988',7,14,3,5),(24,20.00,'2024-11-08 02:31:25.189598',3,5,7,14),(25,2300.00,'2024-11-08 02:32:29.500315',7,14,3,5),(26,2030.00,'2024-11-08 02:33:09.202094',3,5,7,14),(27,957.00,'2024-11-08 02:43:52.592753',2,3,3,5),(28,2500.00,'2024-11-08 03:27:20.933029',7,14,3,5),(30,250.00,'2024-11-08 09:42:42.648275',7,14,3,5),(31,250.00,'2024-11-08 09:42:58.722429',7,14,3,5),(65,146.00,'2024-11-08 22:43:24.112925',2,3,3,5),(66,868.00,'2024-11-08 22:47:14.924044',2,3,3,5),(67,642.00,'2024-11-08 22:53:51.710669',2,3,3,5),(68,692.00,'2024-11-08 23:07:18.983454',2,3,3,5),(69,293.00,'2024-11-08 23:21:07.058097',2,3,3,5),(70,108.00,'2024-11-09 02:21:36.280471',2,3,3,5),(73,213.00,'2024-11-09 04:08:34.294085',5,10,7,14),(74,123.00,'2024-11-09 04:13:23.287220',5,10,7,14),(75,1500.00,'2024-11-09 12:16:25.099775',8,17,3,5),(76,1000.00,'2024-11-09 12:23:15.738383',14,53,3,5),(77,500.00,'2024-11-09 12:24:54.953367',3,5,14,53);
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_family`
--

DROP TABLE IF EXISTS `user_family`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_family` (
  `user_id` bigint NOT NULL,
  `family_member_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`family_member_id`),
  KEY `FKi93l6bsv0c3rvjbfvkv26rlgd` (`family_member_id`),
  CONSTRAINT `FKi93l6bsv0c3rvjbfvkv26rlgd` FOREIGN KEY (`family_member_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKmobwaqyg6wloh4yqb912j9smi` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_family`
--

LOCK TABLES `user_family` WRITE;
/*!40000 ALTER TABLE `user_family` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_family` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_group`
--

DROP TABLE IF EXISTS `user_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_group` (
  `user_id` bigint NOT NULL,
  `group_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`group_id`),
  KEY `FK9adfs48vhutm10yck0sf42aq3` (`group_id`),
  CONSTRAINT `FK7k9ade3lqbo483u9vuryxmm34` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK9adfs48vhutm10yck0sf42aq3` FOREIGN KEY (`group_id`) REFERENCES `user_groups` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_group`
--

LOCK TABLES `user_group` WRITE;
/*!40000 ALTER TABLE `user_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_groups`
--

DROP TABLE IF EXISTS `user_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_groups` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_groups`
--

LOCK TABLES `user_groups` WRITE;
/*!40000 ALTER TABLE `user_groups` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_profiles`
--

DROP TABLE IF EXISTS `user_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_profiles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `ssn` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKdqltqkaw58m11jbov0udx8xqg` (`email`),
  UNIQUE KEY `UK6hqj1dof69qhmgoxbyc6uws1t` (`ssn`),
  UNIQUE KEY `UK5vlt12tabpccuckq0e84nhs4c` (`username`),
  UNIQUE KEY `UKghihfr8rpwng616ynpu779f79` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_profiles`
--

LOCK TABLES `user_profiles` WRITE;
/*!40000 ALTER TABLE `user_profiles` DISABLE KEYS */;
INSERT INTO `user_profiles` VALUES (1,'Brisa.Gorczany55@hotmail.com','Jesse','Vandervort','$2a$10$ZpIN7prD5R0TVUeew/4eNO54CFU8/2ViuawKqhc.30Ik24Z8vh6zm','2437522834','221576724','Administrator'),(2,'Jaunita.Botsford64@hotmail.com','Murl','Buckridge','$2a$10$XUIho3P839Ed3dvtx6joQOnDLrukxosDWjmox9CFgiQMuMqJ8kI7y','2437222834','221576224','tester'),(3,'john.doe@example.com','Anton','Baider','$2a$10$AankUBs/vLk4ViSxLzzhkex.f.r6CWZivkDPHon2zUsianW8kUIQm','10767433101','231576224','admin'),(4,'Carolyn_Schimmel@hotmail.com','Candice','Littel','$2a$10$W0MmV/097Xmly8ALU/nmrOLkfVjwHp.RjHVW4a2I74nYLHeqWR8U2','2434212834','231576424','adminka'),(5,'antonbaider.dev@gmail.com','Anton','Baider','$2a$10$/hC7fUZ9Sz1uXkc8P9rGOeooWG3uIb.CZLip5gxq1NEmFrBIpjrj.','8804652473','123456789','username'),(6,'antonbaider.dev3@gmail.com','Anton','Baider','$2a$10$TqJJE9kfzPf9Y/.ANNjM9OiAC1YkmUYPEgDLKcF9jUKVcsB4lBIpa','8804652470','123456780','admin2'),(7,'antonbaide2r.dev@gmail.com','Anton','Baider','$2a$10$LxDZcpBf/WfNYOVaFVRrNO7jlxW51/E512ll9yEe3BloQQzMIpbRK','8804652472','123321123','testing'),(8,'test@test.com','Antonio','Bugatti','$2a$10$igtxS.RXSg7jxoVcs/6i9e/HBlMuxti2mjpmO./YhXp4dvCVuEE1u','1234567891','123456782','user name'),(9,'test23@test.com','Testing','Last','$2a$10$A5K6q4Ryjgq4FwW87bgKoO/LIl9UbzkzIbcdmADYbuUfLK0vg4HJW','1231231232','123123123','Last Name'),(10,'123@123.com','Heloo','Heloo','$2a$10$6/RALnMbjo0Q0rwyHwXlheXRyvwC47s26tWGERzgd8hQiAGovKxvy','4231231232','123123125','121232'),(11,'test@test','Anton','Baider','$2a$10$ZZmIWmypi0B47/B5QmBxl.mUKVdM1BBymXKMu1BTSw7eE1lr8NUg.','4526434534','234432233','sadasd'),(12,'antonbaider.dev2@gmail.com','Anton','Baider','$2a$10$/p26vjpem5DgLTGVxjn7dur7eNMcdmHm3I/arE031oVTyPo8.n9pi','28804652474','123123321','admin22'),(13,'antonbaider.dev34@gmail.com','asf','dsf','$2a$10$ebExyjT6/ip3S.8bfWlKF.tTx3xXebJw5qtDbT/2xtIzAF/JU3dsy','88046524372','321123425','sdfsdf'),(14,'antonbaider.dev64@gmail.com','First','Last','$2a$10$KyFpUsKgdOsVUyHIra11m.WQJJg2FwggpERn7u1f1G7NkpTC8QpWO','1231233212','345543453','login24');
/*!40000 ALTER TABLE `user_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_statuses`
--

DROP TABLE IF EXISTS `user_statuses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_statuses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_statuses`
--

LOCK TABLES `user_statuses` WRITE;
/*!40000 ALTER TABLE `user_statuses` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_statuses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_types`
--

DROP TABLE IF EXISTS `user_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_types` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_types`
--

LOCK TABLES `user_types` WRITE;
/*!40000 ALTER TABLE `user_types` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_date` datetime(6) NOT NULL,
  `role` enum('ROLE_ADMIN','ROLE_USER') NOT NULL,
  `status` enum('ACTIVE','INACTIVE','SUSPENDED') NOT NULL,
  `type` enum('PREMIUM','STANDARD','STUDENT') NOT NULL,
  `update_date` datetime(6) DEFAULT NULL,
  `profile_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK7s5nlreekaxdbfml4ofky7utw` (`profile_id`),
  CONSTRAINT `FK9ni9y01cgm4kt2lp4d8smxm45` FOREIGN KEY (`profile_id`) REFERENCES `user_profiles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2024-11-07 13:06:08.303059','ROLE_USER','ACTIVE','STANDARD','2024-11-07 13:06:08.352222',1),(2,'2024-11-07 13:06:46.452329','ROLE_USER','ACTIVE','STANDARD','2024-11-07 13:06:46.470758',2),(3,'2024-11-07 13:10:10.660877','ROLE_ADMIN','ACTIVE','STANDARD','2024-11-07 13:10:10.733546',3),(4,'2024-11-08 00:38:55.803600','ROLE_USER','ACTIVE','STANDARD','2024-11-08 00:38:55.985814',4),(5,'2024-11-08 00:57:59.241364','ROLE_USER','ACTIVE','STANDARD','2024-11-08 00:57:59.281298',5),(6,'2024-11-08 01:17:31.701064','ROLE_USER','ACTIVE','STANDARD','2024-11-08 01:17:31.765050',6),(7,'2024-11-08 01:20:27.796668','ROLE_USER','ACTIVE','STANDARD','2024-11-08 01:20:27.815113',7),(8,'2024-11-08 11:19:46.129478','ROLE_USER','ACTIVE','STANDARD','2024-11-08 11:19:46.167602',8),(9,'2024-11-08 11:28:10.244131','ROLE_USER','ACTIVE','STANDARD','2024-11-08 11:28:10.302303',9),(10,'2024-11-09 00:57:35.168499','ROLE_USER','ACTIVE','STANDARD','2024-11-09 00:57:35.248704',10),(11,'2024-11-09 01:12:04.013466','ROLE_USER','ACTIVE','STANDARD','2024-11-09 01:12:04.044220',11),(12,'2024-11-09 01:58:51.565225','ROLE_USER','ACTIVE','STANDARD','2024-11-09 01:58:51.601804',12),(13,'2024-11-09 02:43:54.121948','ROLE_USER','ACTIVE','STANDARD','2024-11-09 02:43:54.155307',13),(14,'2024-11-09 12:20:23.505901','ROLE_USER','ACTIVE','STANDARD','2024-11-09 12:20:23.535853',14);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'bankmthree'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-11-11 20:27:59
