-- CREATE ZAEHLERSTAENDE TABLE
CREATE TABLE IF NOT EXISTS zaehlerstaende (
       id INT AUTO_INCREMENT PRIMARY KEY,
       kunden_id CHAR(36),
       zaehler_nr CHAR(36) NOT NULL,
       type ENUM('WASSER', 'STROM', 'HEIZUNG') NOT NULL,
       Zaehlerstand INT NOT NULL,
       Comment VARCHAR(255),
       Date DATE NOT NULL
   );

-- CREATE KUNDEN TABLE
CREATE TABLE IF NOT EXISTS kunde (
   id INT AUTO_INCREMENT PRIMARY KEY,
   kunden_id UUID NOT NULL,
   Anrede VARCHAR(255) NOT NULL,
   Vorname VARCHAR(255) NOT NULL,
   Geburtsdatum date NOT NULL);