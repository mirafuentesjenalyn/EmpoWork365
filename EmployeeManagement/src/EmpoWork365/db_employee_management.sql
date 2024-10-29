-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 29, 2024 at 10:58 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_employee_management`
--

-- --------------------------------------------------------

--
-- Table structure for table `tbl_attendance`
--

CREATE TABLE `tbl_attendance` (
  `fld_attendance_id` int(11) NOT NULL,
  `fld_employee_id` int(11) DEFAULT NULL,
  `fld_time_in` timestamp NULL DEFAULT current_timestamp(),
  `fld_time_out` timestamp NULL DEFAULT NULL,
  `fld_attendance_date` date DEFAULT NULL,
  `fld_job_title_id` int(11) DEFAULT NULL,
  `fld_department_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbl_attendance`
--

INSERT INTO `tbl_attendance` (`fld_attendance_id`, `fld_employee_id`, `fld_time_in`, `fld_time_out`, `fld_attendance_date`, `fld_job_title_id`, `fld_department_id`) VALUES
(1, 3, '2024-01-01 01:00:00', '2024-01-01 09:00:00', '2024-01-01', NULL, NULL),
(2, 3, '2024-01-02 01:00:00', '2024-01-02 09:00:00', '2024-01-02', NULL, NULL),
(3, 3, '2024-01-03 01:00:00', '2024-01-03 09:00:00', '2024-01-03', NULL, NULL),
(4, 3, '2024-01-04 01:00:00', '2024-01-04 09:00:00', '2024-01-04', NULL, NULL),
(5, 3, '2024-01-05 01:00:00', '2024-01-05 09:00:00', '2024-01-05', NULL, NULL),
(6, 3, '2024-01-08 01:00:00', '2024-01-08 09:00:00', '2024-01-08', NULL, NULL),
(7, 3, '2024-01-09 01:00:00', '2024-01-09 09:00:00', '2024-01-09', NULL, NULL),
(8, 3, '2024-01-10 01:00:00', '2024-01-10 09:00:00', '2024-01-10', NULL, NULL),
(9, 3, '2024-01-11 01:00:00', '2024-01-11 09:00:00', '2024-01-11', NULL, NULL),
(10, 3, '2024-01-12 01:00:00', '2024-01-12 09:00:00', '2024-01-12', NULL, NULL),
(11, 3, '2024-01-15 01:00:00', '2024-01-15 09:00:00', '2024-01-15', NULL, NULL),
(12, 3, '2024-01-16 01:00:00', '2024-01-16 09:00:00', '2024-01-16', NULL, NULL),
(13, 3, '2024-01-17 01:00:00', '2024-01-17 09:00:00', '2024-01-17', NULL, NULL),
(14, 3, '2024-01-18 01:00:00', '2024-01-18 09:00:00', '2024-01-18', NULL, NULL),
(15, 3, '2024-01-19 01:00:00', '2024-01-19 09:00:00', '2024-01-19', NULL, NULL),
(16, 3, '2024-01-22 01:00:00', '2024-01-22 09:00:00', '2024-01-22', NULL, NULL),
(17, 3, '2024-01-22 17:00:00', '2024-01-23 01:00:00', '2024-01-23', NULL, NULL),
(21, 3, '2024-02-01 01:00:00', '2024-02-01 09:00:00', '2024-02-01', NULL, NULL),
(22, 3, '2024-02-02 01:00:00', '2024-02-02 09:00:00', '2024-02-02', NULL, NULL),
(23, 3, '2024-02-05 01:00:00', '2024-02-05 09:00:00', '2024-02-05', NULL, NULL),
(24, 3, '2024-02-06 01:00:00', '2024-02-06 09:00:00', '2024-02-06', NULL, NULL),
(25, 3, '2024-02-07 01:00:00', '2024-02-07 09:00:00', '2024-02-07', NULL, NULL),
(26, 3, '2024-02-08 01:00:00', '2024-02-08 09:00:00', '2024-02-08', NULL, NULL),
(27, 3, '2024-02-12 01:00:00', '2024-02-12 09:00:00', '2024-02-12', NULL, NULL),
(28, 3, '2024-02-13 01:00:00', '2024-02-13 09:00:00', '2024-02-13', NULL, NULL),
(29, 3, '2024-02-14 01:00:00', '2024-02-14 09:00:00', '2024-02-14', NULL, NULL),
(30, 3, '2024-02-15 01:00:00', '2024-02-15 09:00:00', '2024-02-15', NULL, NULL),
(31, 3, '2024-02-16 01:00:00', '2024-02-16 09:00:00', '2024-02-16', NULL, NULL),
(32, 3, '2024-02-19 01:00:00', '2024-02-19 09:00:00', '2024-02-19', NULL, NULL),
(33, 3, '2024-02-20 01:00:00', '2024-02-20 09:00:00', '2024-02-20', NULL, NULL),
(34, 3, '2024-02-21 01:00:00', '2024-02-21 09:00:00', '2024-02-21', NULL, NULL),
(35, 3, '2024-02-22 01:00:00', '2024-02-22 09:00:00', '2024-02-22', NULL, NULL),
(36, 3, '2024-02-23 01:00:00', '2024-02-23 09:00:00', '2024-02-23', NULL, NULL),
(37, 3, '2024-02-26 01:00:00', '2024-02-26 09:00:00', '2024-02-26', NULL, NULL),
(38, 3, '2024-02-27 01:00:00', '2024-02-27 09:00:00', '2024-02-27', NULL, NULL),
(39, 3, '2024-02-28 01:00:00', '2024-02-28 09:00:00', '2024-02-28', NULL, NULL),
(40, 3, '2024-03-01 01:00:00', '2024-03-01 09:00:00', '2024-03-01', NULL, NULL),
(41, 3, '2024-03-04 01:00:00', '2024-03-04 09:00:00', '2024-03-04', NULL, NULL),
(42, 3, '2024-03-05 01:00:00', '2024-03-05 09:00:00', '2024-03-05', NULL, NULL),
(43, 3, '2024-03-06 01:00:00', '2024-03-06 09:00:00', '2024-03-06', NULL, NULL),
(44, 3, '2024-03-07 01:00:00', '2024-03-07 09:00:00', '2024-03-07', NULL, NULL),
(45, 3, '2024-03-08 01:00:00', '2024-03-08 09:00:00', '2024-03-08', NULL, NULL),
(46, 3, '2024-03-11 01:00:00', '2024-03-11 09:00:00', '2024-03-11', NULL, NULL),
(47, 3, '2024-03-12 01:00:00', '2024-03-12 09:00:00', '2024-03-12', NULL, NULL),
(48, 3, '2024-03-13 01:00:00', '2024-03-13 09:00:00', '2024-03-13', NULL, NULL),
(49, 3, '2024-03-14 01:00:00', '2024-03-14 09:00:00', '2024-03-14', NULL, NULL),
(50, 3, '2024-03-15 01:00:00', '2024-03-15 09:00:00', '2024-03-15', NULL, NULL),
(51, 3, '2024-03-18 01:00:00', '2024-03-18 09:00:00', '2024-03-18', NULL, NULL),
(52, 3, '2024-03-19 01:00:00', '2024-03-19 09:00:00', '2024-03-19', NULL, NULL),
(53, 3, '2024-03-20 01:00:00', '2024-03-20 09:00:00', '2024-03-20', NULL, NULL),
(54, 3, '2024-03-21 01:00:00', '2024-03-21 09:00:00', '2024-03-21', NULL, NULL),
(55, 3, '2024-03-22 01:00:00', '2024-03-22 09:00:00', '2024-03-22', NULL, NULL),
(56, 3, '2024-03-25 01:00:00', '2024-03-25 09:00:00', '2024-03-25', NULL, NULL),
(57, 3, '2024-03-26 01:00:00', '2024-03-26 09:00:00', '2024-03-26', NULL, NULL),
(58, 3, '2024-03-27 01:00:00', '2024-03-27 09:00:00', '2024-03-27', NULL, NULL),
(59, 3, '2024-03-28 01:00:00', '2024-03-28 09:00:00', '2024-03-28', NULL, NULL),
(61, 3, '2024-04-01 01:00:00', '2024-04-01 09:00:00', '2024-04-01', NULL, NULL),
(62, 3, '2024-04-02 01:00:00', '2024-04-02 09:00:00', '2024-04-02', NULL, NULL),
(63, 3, '2024-04-03 01:00:00', '2024-04-03 09:00:00', '2024-04-03', NULL, NULL),
(64, 3, '2024-04-04 01:00:00', '2024-04-04 09:00:00', '2024-04-04', NULL, NULL),
(65, 3, '2024-04-05 01:00:00', '2024-04-05 09:00:00', '2024-04-05', NULL, NULL),
(66, 3, '2024-04-08 01:00:00', '2024-04-08 09:00:00', '2024-04-08', NULL, NULL),
(67, 3, '2024-04-09 01:00:00', '2024-04-09 09:00:00', '2024-04-09', NULL, NULL),
(68, 3, '2024-04-10 01:00:00', '2024-04-10 09:00:00', '2024-04-10', NULL, NULL),
(69, 3, '2024-04-11 01:00:00', '2024-04-11 09:00:00', '2024-04-11', NULL, NULL),
(70, 3, '2024-04-12 01:00:00', '2024-04-12 09:00:00', '2024-04-12', NULL, NULL),
(71, 3, '2024-04-15 01:00:00', '2024-04-15 09:00:00', '2024-04-15', NULL, NULL),
(72, 3, '2024-04-16 01:00:00', '2024-04-16 09:00:00', '2024-04-16', NULL, NULL),
(73, 3, '2024-04-17 01:00:00', '2024-04-17 09:00:00', '2024-04-17', NULL, NULL),
(74, 3, '2024-04-18 01:00:00', '2024-04-18 09:00:00', '2024-04-18', NULL, NULL),
(75, 3, '2024-04-19 01:00:00', '2024-04-19 09:00:00', '2024-04-19', NULL, NULL),
(76, 3, '2024-04-22 01:00:00', '2024-04-22 09:00:00', '2024-04-22', NULL, NULL),
(77, 3, '2024-04-23 01:00:00', '2024-04-23 09:00:00', '2024-04-23', NULL, NULL),
(78, 3, '2024-04-24 01:00:00', '2024-04-24 09:00:00', '2024-04-24', NULL, NULL),
(79, 3, '2024-04-25 01:00:00', '2024-04-25 09:00:00', '2024-04-25', NULL, NULL),
(80, 3, '2024-04-26 01:00:00', '2024-04-26 09:00:00', '2024-04-26', NULL, NULL),
(83, 3, '2024-05-01 01:00:00', '2024-05-01 09:00:00', '2024-05-01', NULL, NULL),
(84, 3, '2024-05-02 01:00:00', '2024-05-02 09:00:00', '2024-05-02', NULL, NULL),
(85, 3, '2024-05-03 01:00:00', '2024-05-03 09:00:00', '2024-05-03', NULL, NULL),
(86, 3, '2024-05-06 01:00:00', '2024-05-06 09:00:00', '2024-05-06', NULL, NULL),
(87, 3, '2024-05-07 01:00:00', '2024-05-07 09:00:00', '2024-05-07', NULL, NULL),
(88, 3, '2024-05-08 01:00:00', '2024-05-08 09:00:00', '2024-05-08', NULL, NULL),
(89, 3, '2024-05-09 01:00:00', '2024-05-09 09:00:00', '2024-05-09', NULL, NULL),
(90, 3, '2024-05-10 01:00:00', '2024-05-10 09:00:00', '2024-05-10', NULL, NULL),
(91, 3, '2024-05-13 01:00:00', '2024-05-13 09:00:00', '2024-05-13', NULL, NULL),
(92, 3, '2024-05-14 01:00:00', '2024-05-14 09:00:00', '2024-05-14', NULL, NULL),
(93, 3, '2024-05-15 01:00:00', '2024-05-15 09:00:00', '2024-05-15', NULL, NULL),
(94, 3, '2024-05-16 01:00:00', '2024-05-16 09:00:00', '2024-05-16', NULL, NULL),
(95, 3, '2024-05-17 01:00:00', '2024-05-17 09:00:00', '2024-05-17', NULL, NULL),
(96, 3, '2024-05-20 01:00:00', '2024-05-20 09:00:00', '2024-05-20', NULL, NULL),
(97, 3, '2024-05-21 01:00:00', '2024-05-21 09:00:00', '2024-05-21', NULL, NULL),
(98, 3, '2024-05-22 01:00:00', '2024-05-22 09:00:00', '2024-05-22', NULL, NULL),
(99, 3, '2024-05-23 01:00:00', '2024-05-23 09:00:00', '2024-05-23', NULL, NULL),
(100, 3, '2024-05-24 01:00:00', '2024-05-24 09:00:00', '2024-05-24', NULL, NULL),
(101, 3, '2024-05-26 17:00:00', '2024-05-27 01:00:00', '2024-05-27', NULL, NULL),
(102, 3, '2024-05-27 17:00:00', '2024-05-28 01:00:00', '2024-05-28', NULL, NULL),
(106, 3, '2024-06-03 01:00:00', '2024-06-03 09:00:00', '2024-06-03', NULL, NULL),
(107, 3, '2024-06-04 01:00:00', '2024-06-04 09:00:00', '2024-06-04', NULL, NULL),
(108, 3, '2024-06-05 01:00:00', '2024-06-05 09:00:00', '2024-06-05', NULL, NULL),
(109, 3, '2024-06-06 01:00:00', '2024-06-06 09:00:00', '2024-06-06', NULL, NULL),
(110, 3, '2024-06-07 01:00:00', '2024-06-07 09:00:00', '2024-06-07', NULL, NULL),
(111, 3, '2024-06-10 01:00:00', '2024-06-10 09:00:00', '2024-06-10', NULL, NULL),
(112, 3, '2024-06-11 01:00:00', '2024-06-11 09:00:00', '2024-06-11', NULL, NULL),
(113, 3, '2024-06-12 01:00:00', '2024-06-12 09:00:00', '2024-06-12', NULL, NULL),
(114, 3, '2024-06-13 01:00:00', '2024-06-13 09:00:00', '2024-06-13', NULL, NULL),
(115, 3, '2024-06-14 01:00:00', '2024-06-14 09:00:00', '2024-06-14', NULL, NULL),
(116, 3, '2024-06-17 01:00:00', '2024-06-17 09:00:00', '2024-06-17', NULL, NULL),
(117, 3, '2024-06-18 01:00:00', '2024-06-18 09:00:00', '2024-06-18', NULL, NULL),
(118, 3, '2024-06-19 01:00:00', '2024-06-19 09:00:00', '2024-06-19', NULL, NULL),
(119, 3, '2024-06-20 01:00:00', '2024-06-20 09:00:00', '2024-06-20', NULL, NULL),
(120, 3, '2024-06-21 01:00:00', '2024-06-21 09:00:00', '2024-06-21', NULL, NULL),
(121, 3, '2024-06-24 01:00:00', '2024-06-24 09:00:00', '2024-06-24', NULL, NULL),
(122, 3, '2024-06-25 01:00:00', '2024-06-25 09:00:00', '2024-06-25', NULL, NULL),
(123, 3, '2024-06-26 01:00:00', '2024-06-26 09:00:00', '2024-06-26', NULL, NULL),
(124, 3, '2024-06-27 01:00:00', '2024-06-27 09:00:00', '2024-06-27', NULL, NULL),
(125, 3, '2024-06-28 01:00:00', '2024-06-28 09:00:00', '2024-06-28', NULL, NULL),
(126, 3, '2024-07-01 01:00:00', '2024-07-01 09:00:00', '2024-07-01', NULL, NULL),
(127, 3, '2024-07-02 01:00:00', '2024-07-02 09:00:00', '2024-07-02', NULL, NULL),
(128, 3, '2024-07-03 01:00:00', '2024-07-03 09:00:00', '2024-07-03', NULL, NULL),
(129, 3, '2024-07-04 01:00:00', '2024-07-04 09:00:00', '2024-07-04', NULL, NULL),
(130, 3, '2024-07-05 01:00:00', '2024-07-05 09:00:00', '2024-07-05', NULL, NULL),
(131, 3, '2024-07-08 01:00:00', '2024-07-08 09:00:00', '2024-07-08', NULL, NULL),
(132, 3, '2024-07-09 01:00:00', '2024-07-09 09:00:00', '2024-07-09', NULL, NULL),
(133, 3, '2024-07-10 01:00:00', '2024-07-10 09:00:00', '2024-07-10', NULL, NULL),
(134, 3, '2024-07-11 01:00:00', '2024-07-11 09:00:00', '2024-07-11', NULL, NULL),
(135, 3, '2024-07-12 01:00:00', '2024-07-12 09:00:00', '2024-07-12', NULL, NULL),
(136, 3, '2024-07-15 01:00:00', '2024-07-15 09:00:00', '2024-07-15', NULL, NULL),
(137, 3, '2024-07-16 01:00:00', '2024-07-16 09:00:00', '2024-07-16', NULL, NULL),
(138, 3, '2024-07-17 01:00:00', '2024-07-17 09:00:00', '2024-07-17', NULL, NULL),
(139, 3, '2024-07-18 01:00:00', '2024-07-18 09:00:00', '2024-07-18', NULL, NULL),
(140, 3, '2024-07-19 01:00:00', '2024-07-19 09:00:00', '2024-07-19', NULL, NULL),
(141, 3, '2024-07-22 01:00:00', '2024-07-22 09:00:00', '2024-07-22', NULL, NULL),
(142, 3, '2024-07-23 01:00:00', '2024-07-23 09:00:00', '2024-07-23', NULL, NULL),
(143, 3, '2024-07-24 01:00:00', '2024-07-24 09:00:00', '2024-07-24', NULL, NULL),
(144, 3, '2024-07-25 01:00:00', '2024-07-25 09:00:00', '2024-07-25', NULL, NULL),
(145, 3, '2024-07-26 01:00:00', '2024-07-26 09:00:00', '2024-07-26', NULL, NULL),
(149, 3, '2024-08-01 01:00:00', '2024-08-01 09:00:00', '2024-08-01', NULL, NULL),
(150, 3, '2024-08-02 01:00:00', '2024-08-02 09:00:00', '2024-08-02', NULL, NULL),
(151, 3, '2024-08-05 01:00:00', '2024-08-05 09:00:00', '2024-08-05', NULL, NULL),
(152, 3, '2024-08-06 01:00:00', '2024-08-06 09:00:00', '2024-08-06', NULL, NULL),
(153, 3, '2024-08-07 01:00:00', '2024-08-07 09:00:00', '2024-08-07', NULL, NULL),
(154, 3, '2024-08-08 01:00:00', '2024-08-08 09:00:00', '2024-08-08', NULL, NULL),
(155, 3, '2024-08-09 01:00:00', '2024-08-09 09:00:00', '2024-08-09', NULL, NULL),
(156, 3, '2024-08-12 01:00:00', '2024-08-12 09:00:00', '2024-08-12', NULL, NULL),
(157, 3, '2024-08-13 01:00:00', '2024-08-13 09:00:00', '2024-08-13', NULL, NULL),
(158, 3, '2024-08-14 01:00:00', '2024-08-14 09:00:00', '2024-08-14', NULL, NULL),
(159, 3, '2024-08-15 01:00:00', '2024-08-15 09:00:00', '2024-08-15', NULL, NULL),
(160, 3, '2024-08-16 01:00:00', '2024-08-16 09:00:00', '2024-08-16', NULL, NULL),
(161, 3, '2024-08-19 01:00:00', '2024-08-19 09:00:00', '2024-08-19', NULL, NULL),
(162, 3, '2024-08-20 01:00:00', '2024-08-20 09:00:00', '2024-08-20', NULL, NULL),
(163, 3, '2024-08-21 01:00:00', '2024-08-21 09:00:00', '2024-08-21', NULL, NULL),
(164, 3, '2024-08-22 01:00:00', '2024-08-22 09:00:00', '2024-08-22', NULL, NULL),
(165, 3, '2024-08-23 01:00:00', '2024-08-23 09:00:00', '2024-08-23', NULL, NULL),
(166, 3, '2024-08-26 01:00:00', '2024-08-26 09:00:00', '2024-08-26', NULL, NULL),
(167, 3, '2024-08-27 01:00:00', '2024-08-27 09:00:00', '2024-08-27', NULL, NULL),
(168, 3, '2024-08-28 01:00:00', '2024-08-28 09:00:00', '2024-08-28', NULL, NULL),
(171, 3, '2024-09-02 01:00:00', '2024-09-02 09:00:00', '2024-09-02', NULL, NULL),
(172, 3, '2024-09-03 01:00:00', '2024-09-03 09:00:00', '2024-09-03', NULL, NULL),
(173, 3, '2024-09-04 01:00:00', '2024-09-04 09:00:00', '2024-09-04', NULL, NULL),
(174, 3, '2024-09-05 01:00:00', '2024-09-05 09:00:00', '2024-09-05', NULL, NULL),
(175, 3, '2024-09-06 01:00:00', '2024-09-06 09:00:00', '2024-09-06', NULL, NULL),
(176, 3, '2024-09-09 01:00:00', '2024-09-09 09:00:00', '2024-09-09', NULL, NULL),
(177, 3, '2024-09-10 01:00:00', '2024-09-10 09:00:00', '2024-09-10', NULL, NULL),
(178, 3, '2024-09-11 01:00:00', '2024-09-11 09:00:00', '2024-09-11', NULL, NULL),
(179, 3, '2024-09-12 01:00:00', '2024-09-12 09:00:00', '2024-09-12', NULL, NULL),
(180, 3, '2024-09-13 01:00:00', '2024-09-13 09:00:00', '2024-09-13', NULL, NULL),
(181, 3, '2024-09-16 01:00:00', '2024-09-16 09:00:00', '2024-09-16', NULL, NULL),
(182, 3, '2024-09-17 01:00:00', '2024-09-17 09:00:00', '2024-09-17', NULL, NULL),
(183, 3, '2024-09-18 01:00:00', '2024-09-18 09:00:00', '2024-09-18', NULL, NULL),
(184, 3, '2024-09-19 01:00:00', '2024-09-19 09:00:00', '2024-09-19', NULL, NULL),
(185, 3, '2024-09-20 01:00:00', '2024-09-20 09:00:00', '2024-09-20', NULL, NULL),
(186, 3, '2024-09-23 01:00:00', '2024-09-23 09:00:00', '2024-09-23', NULL, NULL),
(187, 3, '2024-09-24 01:00:00', '2024-09-24 09:00:00', '2024-09-24', NULL, NULL),
(188, 3, '2024-09-25 01:00:00', '2024-09-25 09:00:00', '2024-09-25', NULL, NULL),
(189, 3, '2024-09-26 01:00:00', '2024-09-26 09:00:00', '2024-09-26', NULL, NULL),
(190, 3, '2024-09-27 01:00:00', '2024-09-27 09:00:00', '2024-09-27', NULL, NULL),
(192, 3, '2024-10-01 01:00:00', '2024-10-01 09:00:00', '2024-10-01', NULL, NULL),
(193, 3, '2024-10-02 01:00:00', '2024-10-02 09:00:00', '2024-10-02', NULL, NULL),
(194, 3, '2024-10-03 01:00:00', '2024-10-03 09:00:00', '2024-10-03', NULL, NULL),
(195, 3, '2024-10-04 01:00:00', '2024-10-04 09:00:00', '2024-10-04', NULL, NULL),
(196, 3, '2024-10-07 01:00:00', '2024-10-07 09:00:00', '2024-10-07', NULL, NULL),
(197, 3, '2024-10-08 01:00:00', '2024-10-08 09:00:00', '2024-10-08', NULL, NULL),
(198, 3, '2024-10-09 01:00:00', '2024-10-09 09:00:00', '2024-10-09', NULL, NULL),
(199, 3, '2024-10-10 01:00:00', '2024-10-10 09:00:00', '2024-10-10', NULL, NULL),
(200, 3, '2024-10-11 01:00:00', '2024-10-11 09:00:00', '2024-10-11', NULL, NULL),
(201, 3, '2024-10-14 01:00:00', '2024-10-14 09:00:00', '2024-10-14', NULL, NULL),
(202, 3, '2024-10-15 01:00:00', '2024-10-15 09:00:00', '2024-10-15', NULL, NULL),
(203, 3, '2024-10-16 01:00:00', '2024-10-16 09:00:00', '2024-10-16', NULL, NULL),
(204, 3, '2024-10-17 01:00:00', '2024-10-17 09:00:00', '2024-10-17', NULL, NULL),
(205, 3, '2024-10-18 01:00:00', '2024-10-18 09:00:00', '2024-10-18', NULL, NULL),
(206, 3, '2024-10-21 01:00:00', '2024-10-21 09:00:00', '2024-10-21', NULL, NULL),
(207, 3, '2024-10-22 01:00:00', '2024-10-22 09:00:00', '2024-10-22', NULL, NULL),
(208, 3, '2024-10-23 01:00:00', '2024-10-23 09:00:00', '2024-10-23', NULL, NULL),
(209, 3, '2024-10-24 01:00:00', '2024-10-24 09:00:00', '2024-10-24', NULL, NULL),
(210, 3, '2024-10-25 01:00:00', '2024-10-25 09:00:00', '2024-10-25', NULL, NULL),
(211, 3, '2024-10-28 01:00:00', '2024-10-28 09:00:00', '2024-10-28', NULL, NULL),
(216, 3, '2024-11-04 01:00:00', '2024-11-04 09:00:00', '2024-11-04', NULL, NULL),
(217, 3, '2024-11-05 01:00:00', '2024-11-05 09:00:00', '2024-11-05', NULL, NULL),
(218, 3, '2024-11-06 01:00:00', '2024-11-06 09:00:00', '2024-11-06', NULL, NULL),
(219, 3, '2024-11-07 01:00:00', '2024-11-07 09:00:00', '2024-11-07', NULL, NULL),
(220, 3, '2024-11-08 01:00:00', '2024-11-08 09:00:00', '2024-11-08', NULL, NULL),
(221, 3, '2024-11-11 01:00:00', '2024-11-11 09:00:00', '2024-11-11', NULL, NULL),
(222, 3, '2024-11-12 01:00:00', '2024-11-12 09:00:00', '2024-11-12', NULL, NULL),
(223, 3, '2024-11-13 01:00:00', '2024-11-13 09:00:00', '2024-11-13', NULL, NULL),
(224, 3, '2024-11-14 01:00:00', '2024-11-14 09:00:00', '2024-11-14', NULL, NULL),
(225, 3, '2024-11-15 01:00:00', '2024-11-15 09:00:00', '2024-11-15', NULL, NULL),
(226, 3, '2024-11-18 01:00:00', '2024-11-18 09:00:00', '2024-11-18', NULL, NULL),
(227, 3, '2024-11-19 01:00:00', '2024-11-19 09:00:00', '2024-11-19', NULL, NULL),
(228, 3, '2024-11-20 01:00:00', '2024-11-20 09:00:00', '2024-11-20', NULL, NULL),
(229, 3, '2024-11-21 01:00:00', '2024-11-21 09:00:00', '2024-11-21', NULL, NULL),
(230, 3, '2024-11-22 01:00:00', '2024-11-22 09:00:00', '2024-11-22', NULL, NULL),
(231, 3, '2024-11-25 01:00:00', '2024-11-25 09:00:00', '2024-11-25', NULL, NULL),
(232, 3, '2024-11-26 01:00:00', '2024-11-26 09:00:00', '2024-11-26', NULL, NULL),
(233, 3, '2024-11-27 01:00:00', '2024-11-27 09:00:00', '2024-11-27', NULL, NULL),
(234, 3, '2024-11-28 01:00:00', '2024-11-28 09:00:00', '2024-11-28', NULL, NULL),
(235, 3, '2024-11-29 01:00:00', '2024-11-29 09:00:00', '2024-11-29', NULL, NULL),
(236, 3, '2024-12-02 00:00:00', '2024-12-02 09:00:00', '2024-12-02', NULL, NULL),
(237, 3, '2024-12-03 00:30:00', '2024-12-03 09:00:00', '2024-12-03', NULL, NULL),
(238, 3, '2024-12-04 01:00:00', '2024-12-04 09:00:00', '2024-12-04', NULL, NULL),
(239, 3, '2024-12-05 00:00:00', '2024-12-05 10:00:00', '2024-12-05', NULL, NULL),
(240, 3, '2024-12-06 00:00:00', '2024-12-06 10:00:00', '2024-12-06', NULL, NULL),
(241, 3, '2024-12-09 01:00:00', '2024-12-09 09:00:00', '2024-12-09', NULL, NULL),
(242, 3, '2024-12-10 01:00:00', '2024-12-10 09:00:00', '2024-12-10', NULL, NULL),
(243, 3, '2024-12-11 01:00:00', '2024-12-11 09:00:00', '2024-12-11', NULL, NULL),
(244, 3, '2024-12-12 00:00:00', '2024-12-12 10:00:00', '2024-12-12', NULL, NULL),
(245, 3, '2024-12-13 00:00:00', '2024-12-13 10:00:00', '2024-12-13', NULL, NULL),
(246, 3, '2024-12-16 00:00:00', '2024-12-16 09:00:00', '2024-12-16', NULL, NULL),
(247, 3, '2024-12-17 00:00:00', '2024-12-17 09:00:00', '2024-12-17', NULL, NULL),
(248, 3, '2024-12-18 00:00:00', '2024-12-18 09:00:00', '2024-12-18', NULL, NULL),
(249, 3, '2024-12-19 00:00:00', '2024-12-19 09:00:00', '2024-12-19', NULL, NULL),
(250, 3, '2024-12-20 00:00:00', '2024-12-20 09:00:00', '2024-12-20', NULL, NULL),
(251, 3, '2024-12-23 00:00:00', '2024-12-23 09:00:00', '2024-12-23', NULL, NULL),
(252, 3, '2024-12-24 00:00:00', '2024-12-24 10:00:00', '2024-12-24', NULL, NULL),
(253, 3, '2024-12-25 00:00:00', '2024-12-25 10:00:00', '2024-12-25', NULL, NULL),
(254, 3, '2024-12-26 00:00:00', '2024-12-26 10:00:00', '2024-12-26', NULL, NULL),
(255, 3, '2024-12-27 00:00:00', '2024-12-27 10:00:00', '2024-12-27', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `tbl_department`
--

CREATE TABLE `tbl_department` (
  `fld_department_id` int(11) NOT NULL,
  `fld_department_name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbl_department`
--

INSERT INTO `tbl_department` (`fld_department_id`, `fld_department_name`) VALUES
(1, 'Engineering & IT Department'),
(3, 'Finance & Accounting Department'),
(2, 'Human Resources (HR) & Administration Department'),
(5, 'Operations & Management Department'),
(4, 'Sales & Marketing Department');

-- --------------------------------------------------------

--
-- Table structure for table `tbl_employees`
--

CREATE TABLE `tbl_employees` (
  `fld_employee_id` int(11) NOT NULL,
  `fld_first_name` varchar(50) NOT NULL,
  `fld_last_name` varchar(50) NOT NULL,
  `fld_email` varchar(100) NOT NULL,
  `fld_password` varchar(255) NOT NULL,
  `fld_gender` enum('Male','Female','Other') DEFAULT NULL,
  `fld_department_id` int(11) DEFAULT NULL,
  `fld_role_id` int(11) DEFAULT NULL,
  `fld_job_title_id` int(11) DEFAULT NULL,
  `fld_date_of_employment` date DEFAULT NULL,
  `fld_image_path` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbl_employees`
--

INSERT INTO `tbl_employees` (`fld_employee_id`, `fld_first_name`, `fld_last_name`, `fld_email`, `fld_password`, `fld_gender`, `fld_department_id`, `fld_role_id`, `fld_job_title_id`, `fld_date_of_employment`, `fld_image_path`) VALUES
(1, 'Chairman', 'Netero', 'admin@gmail.com', '123', 'Male', 1, 1, 1, '2024-10-12', 'src/Users/Eduardo.jpg'),
(2, 'Jenalyn', 'Mirafuentes', 'jm@gmail.com', '123', 'Female', 1, 4, 1, '2024-10-12', 'src/Users/UsersJen.jpg'),
(3, 'Jessica', 'Alvarez', 'Jessica@gmail.com', '123', 'Female', 1, 4, 9, '2024-10-12', 'src/Users/Jessica.jpg'),
(4, 'Charlie', 'Puth', 'Charlie@gmail.com', '123', 'Male', 1, 4, 6, '2024-10-12', 'src/Users/Charlie.jpg'),
(5, 'Diana', 'Garcia', 'Diana@gmail.com', '1234', 'Female', 3, 3, 22, '2024-10-12', 'src/Users/Diana.jpg'),
(6, 'Eduardo', 'Dela Cruz', 'Eduardo@gmail.com', '123', 'Male', 5, 3, 19, '2024-10-12', 'src/Users/Eduardo.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `tbl_job_titles`
--

CREATE TABLE `tbl_job_titles` (
  `fld_job_title_id` int(11) NOT NULL,
  `fld_job_title` varchar(100) NOT NULL,
  `fld_department_id` int(11) NOT NULL,
  `fld_rate_per_hour` decimal(10,4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbl_job_titles`
--

INSERT INTO `tbl_job_titles` (`fld_job_title_id`, `fld_job_title`, `fld_department_id`, `fld_rate_per_hour`) VALUES
(1, 'Software Engineer', 1, 180.7700),
(2, 'Frontend Developer', 1, 168.2700),
(3, 'Backend Developer', 1, 180.7700),
(4, 'Full Stack Developer', 1, 204.3300),
(5, 'Data Analyst', 1, 150.2400),
(6, 'Data Scientist', 1, 240.3800),
(7, 'System Administrator', 1, 144.2300),
(8, 'Network Engineer', 1, 168.2700),
(9, 'DevOps Engineer', 1, 220.2200),
(10, 'Technical Support Specialist', 1, 96.1500),
(11, 'IT Manager', 1, 240.3800),
(12, 'Chief Technology Officer (CTO)', 1, 480.7700),
(13, 'HR Specialist', 2, 120.1900),
(14, 'Recruiter', 2, 108.1700),
(15, 'Training & Development Specialist', 2, 120.1900),
(16, 'HR Manager', 2, 180.7700),
(17, 'Office Administrator', 2, 96.1500),
(18, 'Administrative Assistant', 2, 78.1300),
(19, 'Chief People Officer (CPO)', 2, 300.4800),
(20, 'Executive Assistant', 2, 108.1700),
(21, 'Office Manager', 2, 120.1900),
(22, 'Receptionist', 2, 78.1300),
(23, 'Finance Manager', 3, 204.3300),
(24, 'Accountant', 3, 120.1900),
(25, 'Payroll Specialist', 3, 96.1500),
(26, 'Financial Analyst', 3, 156.2500),
(27, 'Chief Financial Officer (CFO)', 3, 420.6700),
(28, 'Budget Analyst', 3, 120.1900),
(29, 'Bookkeeper', 3, 96.1500),
(30, 'Credit Analyst', 3, 120.1900),
(31, 'Internal Auditor', 3, 156.2500),
(32, 'Marketing Specialist', 4, 120.1900),
(33, 'Digital Marketing Manager', 4, 180.7700),
(34, 'Content Writer', 4, 96.1500),
(35, 'Sales Representative', 4, 96.1500),
(36, 'Sales Manager', 4, 180.7700),
(37, 'Business Development Manager', 4, 180.7700),
(38, 'Social Media Manager', 4, 120.1900),
(39, 'Market Research Analyst', 4, 120.1900),
(40, 'Public Relations Specialist', 4, 120.1900),
(41, 'Customer Relationship Manager', 4, 120.1900),
(42, 'Operations Manager', 5, 204.3300),
(43, 'Project Manager', 5, 180.7700),
(44, 'Product Manager', 5, 204.3300),
(45, 'Logistics Coordinator', 5, 120.1900),
(46, 'Supply Chain Manager', 5, 180.7700),
(47, 'Procurement Specialist', 5, 120.1900),
(48, 'Team Leader', 5, 120.1900),
(49, 'Operations Analyst', 5, 156.2500),
(50, 'Chief Operating Officer (COO)', 5, 420.6700),
(51, 'Security Manager', 5, 156.2500);

-- --------------------------------------------------------

--
-- Table structure for table `tbl_leave_applications`
--

CREATE TABLE `tbl_leave_applications` (
  `fld_application_id` int(11) NOT NULL,
  `fld_employee_id` int(11) NOT NULL,
  `fld_leave_type_id` int(11) NOT NULL,
  `fld_date_leave_request` date DEFAULT NULL,
  `fld_reason` text DEFAULT NULL,
  `fld_status` enum('Pending','Approved','Rejected') DEFAULT 'Pending',
  `fld_request_date` datetime NOT NULL DEFAULT current_timestamp(),
  `fld_is_unpaid` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbl_leave_applications`
--

INSERT INTO `tbl_leave_applications` (`fld_application_id`, `fld_employee_id`, `fld_leave_type_id`, `fld_date_leave_request`, `fld_reason`, `fld_status`, `fld_request_date`, `fld_is_unpaid`) VALUES
(1, 3, 1, '2024-10-17', '\"I am sick.\"', 'Pending', '2024-10-17 11:53:54', 0),
(2, 3, 2, '2024-10-29', '\"I have a meeting abroad.\"', 'Pending', '2024-10-17 12:45:14', 0),
(3, 3, 1, '2024-11-12', '\"Family Reunion\"', 'Pending', '2024-10-17 12:53:24', 0),
(5, 3, 1, '2024-10-18', '\"I have a fever.\"', 'Pending', '2024-10-17 13:53:54', 0),
(6, 4, 3, '2024-10-19', 'Dear Admin,\n\nI hope this message finds you well. I regret to inform you that I will not be able to come to work tomorrow as I need to return to my hometown for a family matter. My grandmother has an important medical check-up, and she needs my assistance during this time. Given the situation, I will be on leave starting tomorrow. I apologize for any inconvenience this may cause and will ensure that my responsibilities are covered in my absence. Please let me know if there are any urgent tasks that need attention before I leave. \n\nThank you for your understanding.', 'Rejected', '2024-10-18 10:19:21', 0),
(8, 5, 1, '2024-10-19', '\"My apologies. I can\'t work at this day because I need to do a checkup for my injuries.\"', 'Pending', '2024-10-18 13:39:51', 0),
(35, 3, 3, '2024-10-30', '\"I have a flu.\"', 'Pending', '2024-10-19 15:11:52', 0),
(36, 3, 3, '2024-10-29', '\"I have a family reunion.\"', 'Pending', '2024-10-19 15:25:51', 0),
(63, 6, 1, '2024-10-28', '\"I have an appointment.\"', 'Rejected', '2024-10-20 16:52:22', 0),
(64, 6, 2, '2024-10-29', '\"I have an appointment.\"', 'Rejected', '2024-10-20 16:52:29', 0),
(65, 6, 3, '2024-10-30', '\"I have an appointment.\"', 'Rejected', '2024-10-20 16:52:35', 0),
(67, 3, 2, '2024-10-28', '\"I have an urgent meeting.\"', 'Pending', '2024-10-26 13:19:48', 0);

-- --------------------------------------------------------

--
-- Table structure for table `tbl_leave_balances`
--

CREATE TABLE `tbl_leave_balances` (
  `fld_employee_id` int(11) NOT NULL,
  `fld_leave_type_id` int(11) NOT NULL,
  `fld_remaining_days` int(11) DEFAULT NULL,
  `fld_application_id` int(11) NOT NULL
) ;

-- --------------------------------------------------------

--
-- Table structure for table `tbl_leave_types`
--

CREATE TABLE `tbl_leave_types` (
  `fld_leave_type_id` int(11) NOT NULL,
  `fld_leave_type_name` varchar(50) NOT NULL,
  `fld_max_days` decimal(10,4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbl_leave_types`
--

INSERT INTO `tbl_leave_types` (`fld_leave_type_id`, `fld_leave_type_name`, `fld_max_days`) VALUES
(1, 'Sick Leave', 5.0000),
(2, 'Emergency Leave', 5.0000),
(3, 'Vacation Leave', 15.0000);

-- --------------------------------------------------------

--
-- Table structure for table `tbl_rejected_leave_applications`
--

CREATE TABLE `tbl_rejected_leave_applications` (
  `fld_rejected_id` int(11) NOT NULL,
  `fld_application_id` int(11) NOT NULL,
  `fld_employee_id` int(11) NOT NULL,
  `fld_leave_type_id` int(11) NOT NULL,
  `fld_date_leave_request` date NOT NULL,
  `fld_reason` text DEFAULT NULL,
  `fld_request_date` datetime NOT NULL,
  `fld_status` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tbl_roles`
--

CREATE TABLE `tbl_roles` (
  `fld_role_id` int(11) NOT NULL,
  `fld_role_name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbl_roles`
--

INSERT INTO `tbl_roles` (`fld_role_id`, `fld_role_name`) VALUES
(1, 'Admin'),
(2, 'HR Manager'),
(3, 'Department Manager'),
(4, 'Employee');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tbl_attendance`
--
ALTER TABLE `tbl_attendance`
  ADD PRIMARY KEY (`fld_attendance_id`),
  ADD KEY `fld_employee_id` (`fld_employee_id`),
  ADD KEY `fk_attendance_job_title` (`fld_job_title_id`),
  ADD KEY `fk_attendance_department` (`fld_department_id`);

--
-- Indexes for table `tbl_department`
--
ALTER TABLE `tbl_department`
  ADD PRIMARY KEY (`fld_department_id`),
  ADD UNIQUE KEY `fld_department_name` (`fld_department_name`);

--
-- Indexes for table `tbl_employees`
--
ALTER TABLE `tbl_employees`
  ADD PRIMARY KEY (`fld_employee_id`),
  ADD KEY `fld_department_id` (`fld_department_id`),
  ADD KEY `fld_role_id` (`fld_role_id`),
  ADD KEY `fld_job_title_id` (`fld_job_title_id`);

--
-- Indexes for table `tbl_job_titles`
--
ALTER TABLE `tbl_job_titles`
  ADD PRIMARY KEY (`fld_job_title_id`),
  ADD KEY `fld_department_id` (`fld_department_id`);

--
-- Indexes for table `tbl_leave_applications`
--
ALTER TABLE `tbl_leave_applications`
  ADD PRIMARY KEY (`fld_application_id`),
  ADD KEY `fld_leave_type_id` (`fld_leave_type_id`),
  ADD KEY `tbl_leave_applications_ibfk_1` (`fld_employee_id`);

--
-- Indexes for table `tbl_leave_balances`
--
ALTER TABLE `tbl_leave_balances`
  ADD PRIMARY KEY (`fld_employee_id`,`fld_leave_type_id`),
  ADD KEY `fld_leave_type_id` (`fld_leave_type_id`),
  ADD KEY `fk_leave_balances_application` (`fld_application_id`);

--
-- Indexes for table `tbl_leave_types`
--
ALTER TABLE `tbl_leave_types`
  ADD PRIMARY KEY (`fld_leave_type_id`);

--
-- Indexes for table `tbl_rejected_leave_applications`
--
ALTER TABLE `tbl_rejected_leave_applications`
  ADD PRIMARY KEY (`fld_rejected_id`),
  ADD KEY `fld_application_id` (`fld_application_id`),
  ADD KEY `fld_employee_id` (`fld_employee_id`),
  ADD KEY `fld_leave_type_id` (`fld_leave_type_id`);

--
-- Indexes for table `tbl_roles`
--
ALTER TABLE `tbl_roles`
  ADD PRIMARY KEY (`fld_role_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tbl_attendance`
--
ALTER TABLE `tbl_attendance`
  MODIFY `fld_attendance_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=380;

--
-- AUTO_INCREMENT for table `tbl_department`
--
ALTER TABLE `tbl_department`
  MODIFY `fld_department_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `tbl_employees`
--
ALTER TABLE `tbl_employees`
  MODIFY `fld_employee_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=42;

--
-- AUTO_INCREMENT for table `tbl_job_titles`
--
ALTER TABLE `tbl_job_titles`
  MODIFY `fld_job_title_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=52;

--
-- AUTO_INCREMENT for table `tbl_leave_applications`
--
ALTER TABLE `tbl_leave_applications`
  MODIFY `fld_application_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=72;

--
-- AUTO_INCREMENT for table `tbl_leave_types`
--
ALTER TABLE `tbl_leave_types`
  MODIFY `fld_leave_type_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `tbl_rejected_leave_applications`
--
ALTER TABLE `tbl_rejected_leave_applications`
  MODIFY `fld_rejected_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `tbl_roles`
--
ALTER TABLE `tbl_roles`
  MODIFY `fld_role_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `tbl_attendance`
--
ALTER TABLE `tbl_attendance`
  ADD CONSTRAINT `fk_attendance_department` FOREIGN KEY (`fld_department_id`) REFERENCES `tbl_department` (`fld_department_id`),
  ADD CONSTRAINT `fk_attendance_employee` FOREIGN KEY (`fld_employee_id`) REFERENCES `tbl_employees` (`fld_employee_id`),
  ADD CONSTRAINT `fk_attendance_job_title` FOREIGN KEY (`fld_job_title_id`) REFERENCES `tbl_job_titles` (`fld_job_title_id`),
  ADD CONSTRAINT `tbl_attendance_fk_department` FOREIGN KEY (`fld_department_id`) REFERENCES `tbl_department` (`fld_department_id`) ON DELETE SET NULL,
  ADD CONSTRAINT `tbl_attendance_fk_job_title` FOREIGN KEY (`fld_job_title_id`) REFERENCES `tbl_job_titles` (`fld_job_title_id`) ON DELETE SET NULL,
  ADD CONSTRAINT `tbl_attendance_ibfk_1` FOREIGN KEY (`fld_employee_id`) REFERENCES `tbl_employees` (`fld_employee_id`) ON DELETE CASCADE;

--
-- Constraints for table `tbl_employees`
--
ALTER TABLE `tbl_employees`
  ADD CONSTRAINT `fk_employee_department` FOREIGN KEY (`fld_department_id`) REFERENCES `tbl_department` (`fld_department_id`),
  ADD CONSTRAINT `fk_employee_job_title` FOREIGN KEY (`fld_job_title_id`) REFERENCES `tbl_job_titles` (`fld_job_title_id`),
  ADD CONSTRAINT `fk_employee_role` FOREIGN KEY (`fld_role_id`) REFERENCES `tbl_roles` (`fld_role_id`),
  ADD CONSTRAINT `tbl_employees_ibfk_2` FOREIGN KEY (`fld_department_id`) REFERENCES `tbl_department` (`fld_department_id`) ON DELETE SET NULL,
  ADD CONSTRAINT `tbl_employees_ibfk_3` FOREIGN KEY (`fld_role_id`) REFERENCES `tbl_roles` (`fld_role_id`) ON DELETE SET NULL,
  ADD CONSTRAINT `tbl_employees_ibfk_4` FOREIGN KEY (`fld_job_title_id`) REFERENCES `tbl_job_titles` (`fld_job_title_id`) ON DELETE SET NULL;

--
-- Constraints for table `tbl_job_titles`
--
ALTER TABLE `tbl_job_titles`
  ADD CONSTRAINT `fk_job_title_department` FOREIGN KEY (`fld_department_id`) REFERENCES `tbl_department` (`fld_department_id`),
  ADD CONSTRAINT `tbl_job_titles_ibfk_1` FOREIGN KEY (`fld_department_id`) REFERENCES `tbl_department` (`fld_department_id`) ON DELETE CASCADE;

--
-- Constraints for table `tbl_leave_applications`
--
ALTER TABLE `tbl_leave_applications`
  ADD CONSTRAINT `tbl_leave_applications_ibfk_1` FOREIGN KEY (`fld_employee_id`) REFERENCES `tbl_employees` (`fld_employee_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `tbl_leave_applications_ibfk_2` FOREIGN KEY (`fld_leave_type_id`) REFERENCES `tbl_leave_types` (`fld_leave_type_id`);

--
-- Constraints for table `tbl_leave_balances`
--
ALTER TABLE `tbl_leave_balances`
  ADD CONSTRAINT `fk_leave_balances_application` FOREIGN KEY (`fld_application_id`) REFERENCES `tbl_leave_applications` (`fld_application_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `tbl_leave_balances_ibfk_1` FOREIGN KEY (`fld_employee_id`) REFERENCES `tbl_employees` (`fld_employee_id`),
  ADD CONSTRAINT `tbl_leave_balances_ibfk_2` FOREIGN KEY (`fld_leave_type_id`) REFERENCES `tbl_leave_types` (`fld_leave_type_id`);

--
-- Constraints for table `tbl_rejected_leave_applications`
--
ALTER TABLE `tbl_rejected_leave_applications`
  ADD CONSTRAINT `tbl_rejected_leave_applications_ibfk_1` FOREIGN KEY (`fld_application_id`) REFERENCES `tbl_leave_applications` (`fld_application_id`),
  ADD CONSTRAINT `tbl_rejected_leave_applications_ibfk_2` FOREIGN KEY (`fld_employee_id`) REFERENCES `tbl_employees` (`fld_employee_id`),
  ADD CONSTRAINT `tbl_rejected_leave_applications_ibfk_3` FOREIGN KEY (`fld_leave_type_id`) REFERENCES `tbl_leave_types` (`fld_leave_type_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
