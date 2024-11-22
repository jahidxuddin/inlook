CREATE DATABASE email_server_db;

USE email_server_db;

CREATE TABLE Emails (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender VARCHAR(255),
    recipient VARCHAR(255),
    subject VARCHAR(255),
    body VARCHAR(255),
    sentAt DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255),
    password VARCHAR(255)
);

CREATE TABLE UserEmails (
    user_id INT,
    email_id INT,
    PRIMARY KEY (user_id, email_id),
    FOREIGN KEY (user_id) REFERENCES Users(id),
    FOREIGN KEY (email_id) REFERENCES Emails(id)
);
