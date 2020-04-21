DROP TABLE IF EXISTS tasks;

CREATE TABLE tasks
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(100) NOT NULL,
    done        BIT
)