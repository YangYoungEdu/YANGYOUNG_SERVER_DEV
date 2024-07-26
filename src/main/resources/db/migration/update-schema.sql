CREATE SEQUENCE app_user_seq INCREMENT BY 50 START WITH 1;

CREATE TABLE app_user
(
    user_id  BIGINT       NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    CONSTRAINT pk_appuser PRIMARY KEY (user_id)
);

CREATE TABLE app_user_roles
(
    app_user_user_id BIGINT NOT NULL,
    roles            VARCHAR(255) NULL
);

CREATE TABLE exam
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    create_date datetime NULL,
    update_date datetime NULL,
    name        VARCHAR(255) NULL,
    grade       SMALLINT NULL,
    start_date  date NULL,
    end_date    date NULL,
    scope       VARCHAR(255) NULL,
    is_finished BIT(1) NOT NULL,
    school_id   BIGINT NULL,
    CONSTRAINT pk_exam PRIMARY KEY (id)
);

CREATE TABLE lecture_section
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    lecture_id BIGINT NULL,
    section_id BIGINT NULL,
    CONSTRAINT pk_lecturesection PRIMARY KEY (id)
);

CREATE TABLE lecture_task
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    lecture_id BIGINT NULL,
    task_id    BIGINT NULL,
    CONSTRAINT pk_lecturetask PRIMARY KEY (id)
);

CREATE TABLE school
(
    id     BIGINT AUTO_INCREMENT NOT NULL,
    name   VARCHAR(255) NULL,
    status VARCHAR(255) NULL,
    CONSTRAINT pk_school PRIMARY KEY (id)
);

CREATE TABLE student_lecture
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    student_id BIGINT NULL,
    lecture_id BIGINT NULL,
    CONSTRAINT pk_studentlecture PRIMARY KEY (id)
);

ALTER TABLE student
    ADD app_user_id BIGINT NULL;

ALTER TABLE student
    ADD is_enrolled BIT(1) NULL;

ALTER TABLE student
    ADD is_lecture_registered BIT(1) NULL;

ALTER TABLE student
    ADD school_id BIGINT NULL;

ALTER TABLE lecture
    ADD grade VARCHAR(255) NULL;

ALTER TABLE lecture
    ADD is_finished BIT(1) NULL;

ALTER TABLE lecture
    ADD lecture_type VARCHAR(255) NULL;

ALTER TABLE student
    MODIFY is_enrolled BIT (1) NOT NULL;

ALTER TABLE lecture
    MODIFY is_finished BIT (1) NOT NULL;

ALTER TABLE lecture_date
    ADD lecture_date date NULL;

ALTER TABLE lecture_day
    ADD lecture_day SMALLINT NULL;

ALTER TABLE attendance
    ADD lecture_id BIGINT NULL;

ALTER TABLE task
    ADD lecture_name VARCHAR(255) NULL;

ALTER TABLE student
    ADD CONSTRAINT uc_student_app_user UNIQUE (app_user_id);

ALTER TABLE attendance
    ADD CONSTRAINT FK_ATTENDANCE_ON_LECTURE FOREIGN KEY (lecture_id) REFERENCES lecture (id);

ALTER TABLE attendance
    ADD CONSTRAINT FK_ATTENDANCE_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE exam
    ADD CONSTRAINT FK_EXAM_ON_SCHOOL FOREIGN KEY (school_id) REFERENCES school (id);

ALTER TABLE lecture_date
    ADD CONSTRAINT FK_LECTUREDATE_ON_LECTURE FOREIGN KEY (lecture_id) REFERENCES lecture (id);

ALTER TABLE lecture_day
    ADD CONSTRAINT FK_LECTUREDAY_ON_LECTURE FOREIGN KEY (lecture_id) REFERENCES lecture (id);

ALTER TABLE lecture_section
    ADD CONSTRAINT FK_LECTURESECTION_ON_LECTURE FOREIGN KEY (lecture_id) REFERENCES lecture (id);

ALTER TABLE lecture_section
    ADD CONSTRAINT FK_LECTURESECTION_ON_SECTION FOREIGN KEY (section_id) REFERENCES section (id);

ALTER TABLE lecture_task
    ADD CONSTRAINT FK_LECTURETASK_ON_LECTURE FOREIGN KEY (lecture_id) REFERENCES lecture (id);

ALTER TABLE lecture_task
    ADD CONSTRAINT FK_LECTURETASK_ON_TASK FOREIGN KEY (task_id) REFERENCES task (id);

ALTER TABLE student_lecture
    ADD CONSTRAINT FK_STUDENTLECTURE_ON_LECTURE FOREIGN KEY (lecture_id) REFERENCES lecture (id);

ALTER TABLE student_lecture
    ADD CONSTRAINT FK_STUDENTLECTURE_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE student_section
    ADD CONSTRAINT FK_STUDENTSECTION_ON_SECTION FOREIGN KEY (section_id) REFERENCES section (id);

ALTER TABLE student_section
    ADD CONSTRAINT FK_STUDENTSECTION_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE student_task
    ADD CONSTRAINT FK_STUDENTTASK_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE student_task
    ADD CONSTRAINT FK_STUDENTTASK_ON_TASK FOREIGN KEY (task_id) REFERENCES task (id);

ALTER TABLE student
    ADD CONSTRAINT FK_STUDENT_ON_APP_USER FOREIGN KEY (app_user_id) REFERENCES app_user (user_id);

ALTER TABLE student
    ADD CONSTRAINT FK_STUDENT_ON_SCHOOL FOREIGN KEY (school_id) REFERENCES school (id);

ALTER TABLE app_user_roles
    ADD CONSTRAINT fk_appuser_roles_on_app_user FOREIGN KEY (app_user_user_id) REFERENCES app_user (user_id);

DROP TABLE date;

DROP TABLE day;

DROP TABLE lecture_material;

DROP TABLE material;

DROP TABLE section_lecture;

DROP TABLE section_task;

DROP TABLE student_material;

DROP TABLE time_table;

ALTER TABLE task
DROP
COLUMN assignment;

ALTER TABLE task
DROP
COLUMN assignment_date;

ALTER TABLE task
DROP
COLUMN task_type;

ALTER TABLE lecture
DROP
COLUMN create_date;

ALTER TABLE lecture
DROP
COLUMN home_room;

ALTER TABLE lecture
DROP
COLUMN lecture_room;

ALTER TABLE lecture
DROP
COLUMN lecture_seq;

ALTER TABLE lecture
DROP
COLUMN update_date;

ALTER TABLE section
DROP
COLUMN create_date;

ALTER TABLE section
DROP
COLUMN home_room;

ALTER TABLE section
DROP
COLUMN update_date;

ALTER TABLE student_task
DROP
COLUMN create_date;

ALTER TABLE student_task
DROP
COLUMN update_date;

ALTER TABLE student_task
DROP
COLUMN task_progress;

ALTER TABLE lecture_date
DROP
COLUMN date;

ALTER TABLE lecture_day
DROP
COLUMN day_id;

ALTER TABLE student
DROP
COLUMN school;

ALTER TABLE student
DROP
COLUMN section_id;

ALTER TABLE student
DROP
COLUMN grade;

ALTER TABLE attendance
DROP
COLUMN section_id;

ALTER TABLE attendance
DROP
COLUMN attendance_type;

ALTER TABLE attendance
    ADD attendance_type VARCHAR(255) NULL;

ALTER TABLE student
    ADD grade VARCHAR(255) NULL;

ALTER TABLE student_task
    ADD task_progress VARCHAR(255) NULL;

ALTER TABLE task
    ADD task_type VARCHAR(255) NULL;