-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.30 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.1.0.6537
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for db_people
DROP DATABASE IF EXISTS `db_people`;
CREATE DATABASE IF NOT EXISTS `db_people` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `db_people`;

-- Dumping structure for table db_people.person
DROP TABLE IF EXISTS `person`;
CREATE TABLE IF NOT EXISTS `person` (
  `id` varchar(10) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `thn_lahir` int NOT NULL,
  `kategori` varchar(20) NOT NULL,
  `mbti` varchar(4) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table db_people.person: ~7 rows (approximately)
INSERT INTO `person` (`id`, `nama`, `thn_lahir`, `kategori`, `mbti`) VALUES
	('P001', 'Rina Amelia', 1998, 'Pasien', 'INFJ'),
	('P002', 'Dr. Budi Santoso', 1980, 'Psikiater', 'INTJ'),
	('P003', 'Sinta Maharani', 1990, 'Psikolog', 'ENFP'),
	('P004', 'Andi Pratama', 2001, 'Pasien', 'ISTP'),
	('P005', 'Lestari Dewi', 1993, 'Perawat', 'ISFJ'),
	('P006', 'Fajar Nugroho', 1987, 'Konselor', 'ENFJ'),
	('P007', 'A', 2007, 'Pasien', 'INTJ');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
