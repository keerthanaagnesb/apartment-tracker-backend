-- Apartment Community Issue Tracker — MySQL schema + optional demo data
-- Run this once in MySQL Workbench / the Aiven query tool, OR skip it entirely:
-- the backend creates the same tables automatically on first start (spring.jpa.hibernate.ddl-auto=update).

CREATE DATABASE IF NOT EXISTS apartment_tracker;
USE apartment_tracker;

-- ---------- Tables ----------
CREATE TABLE IF NOT EXISTS users (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,          -- BCrypt hash, never plain text
    phone        VARCHAR(255),
    flat_number  VARCHAR(255),
    role         VARCHAR(20)  NOT NULL,          -- RESIDENT | ADMIN | TECHNICIAN
    department   VARCHAR(255),                   -- technicians only
    approved     BOOLEAN      NOT NULL DEFAULT TRUE   -- new residents are inserted as FALSE until an admin approves
);

CREATE TABLE IF NOT EXISTS complaints (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    complaint_code  VARCHAR(255) UNIQUE,         -- e.g. APT-0001
    title           VARCHAR(255),
    issue_type      VARCHAR(30),                 -- WATER, LIFT, PARKING, SECURITY, GARBAGE, ELECTRICAL, COMMON_AREA, OTHER
    description     VARCHAR(2000),
    location        VARCHAR(255),
    status          VARCHAR(20),                 -- PENDING | ASSIGNED | IN_PROGRESS | RESOLVED
    created_by      BIGINT NOT NULL,
    assigned_to     BIGINT NULL,
    created_at      DATETIME(6),
    updated_at      DATETIME(6),
    CONSTRAINT fk_complaint_creator  FOREIGN KEY (created_by)  REFERENCES users(id),
    CONSTRAINT fk_complaint_assignee FOREIGN KEY (assigned_to) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS chat_messages (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id  BIGINT NOT NULL,
    content    VARCHAR(1000),
    sent_at    DATETIME(6),
    CONSTRAINT fk_chat_sender FOREIGN KEY (sender_id) REFERENCES users(id)
);

-- ---------- Optional demo data (every password below is: password123) ----------
INSERT IGNORE INTO users (name, email, password, phone, flat_number, role, department, approved) VALUES
('Ravi Kumar (Admin)',   'admin@apartment.com',         '$2b$10$4EVXcibYHaCzL2wRLHNNHOpzYyp4JqX073V3.9yyjUMjuesG0dyL.', '9000000001', 'Office',        'ADMIN',      NULL,               TRUE),
('Suresh (Electrician)', 'tech.electric@apartment.com', '$2b$10$4EVXcibYHaCzL2wRLHNNHOpzYyp4JqX073V3.9yyjUMjuesG0dyL.', '9000000002', 'Maint. Block', 'TECHNICIAN', 'Electrical',      TRUE),
('Mahesh (Plumber)',     'tech.plumber@apartment.com',  '$2b$10$4EVXcibYHaCzL2wRLHNNHOpzYyp4JqX073V3.9yyjUMjuesG0dyL.', '9000000003', 'Maint. Block', 'TECHNICIAN', 'Plumbing / Water', TRUE),
('Agnes',                'resident1@apartment.com',     '$2b$10$4EVXcibYHaCzL2wRLHNNHOpzYyp4JqX073V3.9yyjUMjuesG0dyL.', '9000000004', 'A-101',        'RESIDENT',   NULL,               TRUE),
('Vikram Singh',         'resident2@apartment.com',     '$2b$10$4EVXcibYHaCzL2wRLHNNHOpzYyp4JqX073V3.9yyjUMjuesG0dyL.', '9000000005', 'B-202',        'RESIDENT',   NULL,               TRUE);

INSERT IGNORE INTO complaints (complaint_code, title, issue_type, description, location, status, created_by, assigned_to, created_at, updated_at) VALUES
('APT-0001', 'Water leakage near kitchen sink', 'WATER', 'Continuous water leakage under the kitchen sink since yesterday.', 'A-101', 'IN_PROGRESS',
    (SELECT id FROM users WHERE email='resident1@apartment.com'), (SELECT id FROM users WHERE email='tech.plumber@apartment.com'),
    DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
('APT-0002', 'Lift not working - Block B', 'LIFT', 'The lift in Block B has been stuck on the 3rd floor since this morning.', 'Block B', 'PENDING',
    (SELECT id FROM users WHERE email='resident2@apartment.com'), NULL,
    DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
('APT-0003', 'Street light not working', 'ELECTRICAL', 'The street light near the parking area does not turn on at night.', 'Parking Area', 'RESOLVED',
    (SELECT id FROM users WHERE email='resident1@apartment.com'), (SELECT id FROM users WHERE email='tech.electric@apartment.com'),
    DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY));
