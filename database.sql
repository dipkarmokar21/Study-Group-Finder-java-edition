-- ==============================================================================
-- STUDY GROUP FINDER - COMPLETE DATABASE SCHEMA & SCRIPT
-- For University DBMS Lab demonstration using MySQL / XAMPP (phpMyAdmin)
-- ==============================================================================

-- 1. Create and select Database
CREATE DATABASE IF NOT EXISTS `study_group_db`;
USE `study_group_db`;

-- Drop existing tables/procedures/triggers in correct foreign key order if re-running
DROP TRIGGER IF EXISTS `trg_after_member_insert`;
DROP TRIGGER IF EXISTS `trg_after_member_delete`;
DROP PROCEDURE IF EXISTS `GetGroupsBySubject`;
DROP TABLE IF EXISTS `group_members`;
DROP TABLE IF EXISTS `schedules`;
DROP TABLE IF EXISTS `study_groups`;
DROP TABLE IF EXISTS `users`;

-- ==============================================================================
-- TABLE 1: users
-- Represents student users in the application
-- Demonstrates: Primary Key, Auto Increment, Unique Constraint, Default Timestamp
-- ==============================================================================
CREATE TABLE `users` (
    `user_id` INT AUTO_INCREMENT PRIMARY KEY,
    `full_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==============================================================================
-- TABLE 2: study_groups
-- Represents study groups created by users
-- Demonstrates: Primary Key, Foreign Key (1-to-Many: User -> Study Groups), Check/Enum
-- ==============================================================================
CREATE TABLE `study_groups` (
    `group_id` INT AUTO_INCREMENT PRIMARY KEY,
    `owner_id` INT NOT NULL,
    `title` VARCHAR(150) NOT NULL,
    `subject` VARCHAR(100) NOT NULL,
    `max_members` INT NOT NULL,
    `status` ENUM('OPEN', 'FULL') DEFAULT 'OPEN',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_groups_owner` 
        FOREIGN KEY (`owner_id`) REFERENCES `users` (`user_id`) 
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ==============================================================================
-- TABLE 3: schedules
-- Represents the meeting day and time for each group
-- Demonstrates: 1-to-1 Relationship with study_groups via UNIQUE constraint on FK
-- ==============================================================================
CREATE TABLE `schedules` (
    `schedule_id` INT AUTO_INCREMENT PRIMARY KEY,
    `group_id` INT NOT NULL UNIQUE,
    `meeting_day` VARCHAR(20) NOT NULL,
    `meeting_time` VARCHAR(50) NOT NULL,
    CONSTRAINT `fk_schedules_group` 
        FOREIGN KEY (`group_id`) REFERENCES `study_groups` (`group_id`) 
        ON DELETE CASCADE ON UPDATE CASCADE
) ;

-- ==============================================================================
-- TABLE 4: group_members
-- Junction table for students joining study groups
-- Demonstrates: Many-to-Many Relationship, Composite Primary Key, Foreign Keys
-- ==============================================================================
CREATE TABLE `group_members` (
    `group_id` INT NOT NULL,
    `user_id` INT NOT NULL,
    `joined_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`group_id`, `user_id`),
    CONSTRAINT `fk_members_group` 
        FOREIGN KEY (`group_id`) REFERENCES `study_groups` (`group_id`) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_members_user` 
        FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) 
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ==============================================================================
-- TRIGGER 1: trg_after_member_insert
-- Automatically marks group status as 'FULL' when total members reach max_members
-- ==============================================================================
DELIMITER $$
CREATE TRIGGER `trg_after_member_insert`
AFTER INSERT ON `group_members`
FOR EACH ROW
BEGIN
    DECLARE member_count INT;
    DECLARE allowed_max INT;

    -- Calculate current member count for this group
    SELECT COUNT(*) INTO member_count 
    FROM `group_members` 
    WHERE `group_id` = NEW.group_id;

    -- Fetch maximum members allowed
    SELECT `max_members` INTO allowed_max 
    FROM `study_groups` 
    WHERE `group_id` = NEW.group_id;

    -- If capacity reached, update status to FULL
    IF member_count >= allowed_max THEN
        UPDATE `study_groups` 
        SET `status` = 'FULL' 
        WHERE `group_id` = NEW.group_id;
    END IF;
END$$
DELIMITER ;

-- ==============================================================================
-- TRIGGER 2: trg_after_member_delete
-- Automatically resets group status to 'OPEN' if member count drops below max_members
-- ==============================================================================
DELIMITER $$
CREATE TRIGGER `trg_after_member_delete`
AFTER DELETE ON `group_members`
FOR EACH ROW
BEGIN
    DECLARE member_count INT;
    DECLARE allowed_max INT;

    SELECT COUNT(*) INTO member_count 
    FROM `group_members` 
    WHERE `group_id` = OLD.group_id;

    SELECT `max_members` INTO allowed_max 
    FROM `study_groups` 
    WHERE `group_id` = OLD.group_id;

    IF member_count < allowed_max THEN
        UPDATE `study_groups` 
        SET `status` = 'OPEN' 
        WHERE `group_id` = OLD.group_id;
    END IF;
END$$
DELIMITER ;

-- ==============================================================================
-- STORED PROCEDURE: GetGroupsBySubject
-- Fetches study groups with schedules and member counts, filtered by subject name
-- ==============================================================================
DELIMITER $$
CREATE PROCEDURE `GetGroupsBySubject`(IN `p_subject` VARCHAR(100))
BEGIN
    IF p_subject IS NULL OR p_subject = '' OR p_subject = 'ALL' THEN
        SELECT 
            sg.group_id,
            sg.title,
            sg.subject,
            sg.max_members,
            sg.status,
            sg.owner_id,
            u.full_name AS owner_name,
            s.meeting_day,
            s.meeting_time,
            COUNT(gm.user_id) AS current_members
        FROM `study_groups` sg
        JOIN `users` u ON sg.owner_id = u.user_id
        LEFT JOIN `schedules` s ON sg.group_id = s.group_id
        LEFT JOIN `group_members` gm ON sg.group_id = gm.group_id
        GROUP BY sg.group_id, s.meeting_day, s.meeting_time
        ORDER BY sg.created_at DESC;
    ELSE
        SELECT 
            sg.group_id,
            sg.title,
            sg.subject,
            sg.max_members,
            sg.status,
            sg.owner_id,
            u.full_name AS owner_name,
            s.meeting_day,
            s.meeting_time,
            COUNT(gm.user_id) AS current_members
        FROM `study_groups` sg
        JOIN `users` u ON sg.owner_id = u.user_id
        LEFT JOIN `schedules` s ON sg.group_id = s.group_id
        LEFT JOIN `group_members` gm ON sg.group_id = gm.group_id
        WHERE sg.subject = p_subject
        GROUP BY sg.group_id, s.meeting_day, s.meeting_time
        ORDER BY sg.created_at DESC;
    END IF;
END$$
DELIMITER ;

-- ==============================================================================
-- SAMPLE DATA INSERTION
-- Password for all sample users is: password123
-- (Bcrypt hash: $2y$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm)
-- ==============================================================================

-- 1. Insert Sample Users
INSERT INTO `users` (`user_id`, `full_name`, `email`, `password`) VALUES
(1, 'Alex Rivera', 'alex@university.edu', '$2y$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm'),
(2, 'Sarah Chen', 'sarah@university.edu', '$2y$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm'),
(3, 'David Kim', 'david@university.edu', '$2y$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm'),
(4, 'Emily Watson', 'emily@university.edu', '$2y$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm'),
(5, 'Marcus Johnson', 'marcus@university.edu', '$2y$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm');

-- 2. Insert Sample Study Groups (Demonstrating 1-to-Many Users -> Study Groups)
INSERT INTO `study_groups` (`group_id`, `owner_id`, `title`, `subject`, `max_members`, `status`) VALUES
(1, 1, 'Database Systems Lab & SQL Querying', 'Computer Science', 4, 'OPEN'),
(2, 2, 'Calculus II & Differential Equations Prep', 'Mathematics', 4, 'OPEN'),
(3, 3, 'AI & Machine Learning Concepts Discussion', 'Computer Science', 2, 'OPEN'),
(4, 4, 'Organic Chemistry Reaction Mechanisms', 'Chemistry', 5, 'OPEN'),
(5, 5, 'Classical Mechanics & Physics Problem Solving', 'Physics', 3, 'OPEN');

-- 3. Insert Schedules (Demonstrating 1-to-1 Study Groups -> Schedules)
INSERT INTO `schedules` (`schedule_id`, `group_id`, `meeting_day`, `meeting_time`) VALUES
(1, 1, 'Monday & Wednesday', '04:00 PM - 06:00 PM'),
(2, 2, 'Tuesday & Thursday', '05:30 PM - 07:00 PM'),
(3, 3, 'Friday', '02:00 PM - 04:00 PM'),
(4, 4, 'Saturday', '10:00 AM - 12:30 PM'),
(5, 5, 'Sunday', '03:00 PM - 05:00 PM');

-- 4. Insert Group Members (Demonstrating Many-to-Many via Junction Table)
-- Note: Group 3 has max_members = 2. When user 3 and user 1 join, trigger will fire!
INSERT INTO `group_members` (`group_id`, `user_id`) VALUES
(1, 1), -- Alex (owner) in Group 1
(1, 2), -- Sarah joined Group 1
(2, 2), -- Sarah (owner) in Group 2
(2, 3), -- David joined Group 2
(2, 4), -- Emily joined Group 2
(3, 3), -- David (owner) in Group 3
(3, 1), -- Alex joined Group 3 -> Hits max 2, trigger updates status to FULL!
(4, 4), -- Emily (owner) in Group 4
(4, 5), -- Marcus joined Group 4
(5, 5); -- Marcus (owner) in Group 5
