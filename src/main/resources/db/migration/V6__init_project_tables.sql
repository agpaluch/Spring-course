CREATE TABLE projects
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(100) NOT NULL
);


ALTER TABLE task_groups
    ADD COLUMN project_id INT NULL;
ALTER TABLE task_groups
    ADD FOREIGN KEY (project_id) REFERENCES projects (id);


CREATE TABLE project_steps
(
    id               INT PRIMARY KEY AUTO_INCREMENT,
    description      VARCHAR(100) NOT NULL,
    days_to_deadline INT       NOT NULL,
    project_id       INT          NOT NULL,
    FOREIGN KEY (project_id) REFERENCES projects (id)
);

