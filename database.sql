
CREATE DATABASE IF NOT EXISTS `study_group_db`;
USE `study_group_db`;
DROP TRIGGER IF EXISTS `trg_after_member_insert`;
DROP TRIGGER IF EXISTS `trg_after_member_delete`;
DROP PROCEDURE IF EXISTS `GetGroupsBySubject`;
DROP TABLE IF EXISTS `group_members`;
DROP TABLE IF EXISTS `schedules`;
DROP TABLE IF EXISTS `study_groups`;
DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
    `user_id` INT AUTO_INCREMENT PRIMARY KEY,
    `full_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


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

CREATE TABLE `schedules` (
    `schedule_id` INT AUTO_INCREMENT PRIMARY KEY,
    `group_id` INT NOT NULL UNIQUE,
    `meeting_day` VARCHAR(20) NOT NULL,
    `meeting_time` VARCHAR(50) NOT NULL,
    CONSTRAINT `fk_schedules_group` 
        FOREIGN KEY (`group_id`) REFERENCES `study_groups` (`group_id`) 
        ON DELETE CASCADE ON UPDATE CASCADE
) ;

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

DELIMITER $$
CREATE TRIGGER `trg_after_member_insert`
AFTER INSERT ON `group_members`
FOR EACH ROW
BEGIN
    DECLARE member_count INT;
    DECLARE allowed_max INT;


    SELECT COUNT(*) INTO member_count 
    FROM `group_members` 
    WHERE `group_id` = NEW.group_id;


    SELECT `max_members` INTO allowed_max 
    FROM `study_groups` 
    WHERE `group_id` = NEW.group_id;

    IF member_count >= allowed_max THEN
        UPDATE `study_groups` 
        SET `status` = 'FULL' 
        WHERE `group_id` = NEW.group_id;
    END IF;
END$$
DELIMITER ;

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


INSERT INTO `users` (`user_id`, `full_name`, `email`, `password`) VALUES
(1, 'Alex Rivera', 'alex@university.edu', '$2y$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm'),
(2, 'Sarah Chen', 'sarah@university.edu', '$2y$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm'),
(3, 'David Kim', 'david@university.edu', '$2y$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm'),
(4, 'Emily Watson', 'emily@university.edu', '$2y$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm'),
(5, 'Marcus Johnson', 'marcus@university.edu', '$2y$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIVFlYg7B77UdFm');


INSERT INTO `study_groups` (`group_id`, `owner_id`, `title`, `subject`, `max_members`, `status`) VALUES
(1, 1, 'Database Systems Lab & SQL Querying', 'Computer Science', 4, 'OPEN'),
(2, 2, 'Calculus II & Differential Equations Prep', 'Mathematics', 4, 'OPEN'),
(3, 3, 'AI & Machine Learning Concepts Discussion', 'Computer Science', 2, 'OPEN'),
(4, 4, 'Organic Chemistry Reaction Mechanisms', 'Chemistry', 5, 'OPEN'),
(5, 5, 'Classical Mechanics & Physics Problem Solving', 'Physics', 3, 'OPEN');


INSERT INTO `schedules` (`schedule_id`, `group_id`, `meeting_day`, `meeting_time`) VALUES
(1, 1, 'Monday & Wednesday', '04:00 PM - 06:00 PM'),
(2, 2, 'Tuesday & Thursday', '05:30 PM - 07:00 PM'),
(3, 3, 'Friday', '02:00 PM - 04:00 PM'),
(4, 4, 'Saturday', '10:00 AM - 12:30 PM'),
(5, 5, 'Sunday', '03:00 PM - 05:00 PM');


INSERT INTO `group_members` (`group_id`, `user_id`) VALUES
(1, 1), 
(1, 2), 
(2, 2), 
(2, 3), 
(2, 4), 
(3, 3),
(3, 1), 
(4, 4), 
(4, 5),
(5, 5);
