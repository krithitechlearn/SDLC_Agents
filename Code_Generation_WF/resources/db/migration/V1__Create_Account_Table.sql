CREATE TABLE account (
         account_id VARCHAR(255) PRIMARY KEY,
         account_number VARCHAR(34) NOT NULL UNIQUE,
         currency CHAR(3) NOT NULL
     );
