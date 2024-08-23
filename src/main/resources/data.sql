DROP TABLE IF EXISTS run;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users
(
    id         SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    birth_date DATE         NOT NULL,
    sex        CHAR(1)      NOT NULL
);
CREATE TABLE IF NOT EXISTS run
(
    id               SERIAL PRIMARY KEY,
    user_id          BIGINT           NOT NULL,
    start_latitude   DOUBLE PRECISION NOT NULL,
    start_longitude  DOUBLE PRECISION NOT NULL,
    finish_latitude  DOUBLE PRECISION,
    finish_longitude DOUBLE PRECISION,
    distance         DOUBLE PRECISION,
    start_date_time  TIMESTAMP        NOT NULL,
    finish_date_time TIMESTAMP,
    status           varchar(20),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

INSERT INTO users (first_name, last_name, birth_date, sex)
VALUES ('John', 'Doe', '1990-01-01', 'M'),
       ('Jane', 'Smith', '1985-05-12', 'F'),
       ('Emily', 'Johnson', '1992-08-23', 'F'),
       ('Michael', 'Brown', '1988-03-15', 'M'),
       ('Emma', 'Williams', '1995-10-10', 'F');

INSERT INTO run (user_id, start_latitude, start_longitude, finish_latitude, finish_longitude, distance, start_date_time,
                 finish_date_time, status)
VALUES (1, 40.712776, -74.005974, 40.730610, -73.935242, 5.0, '2024-08-20 07:00:00', '2024-08-20 07:30:00',
        'completed'),
       (2, 34.052235, -118.243683, 34.073620, -118.400356, 10.0, '2024-08-21 06:30:00', '2024-08-21 07:30:00',
        'completed'),
       (3, 51.507351, -0.127758, 51.515419, -0.141099, 2.5, '2024-08-19 08:00:00', '2024-08-19 08:20:00', 'completed'),

       (4, 48.856613, 2.352222, NULL, NULL, NULL, '2024-08-22 06:00:00', NULL, 'ongoing'),
       (5, 35.689487, 139.691711, NULL, NULL, NULL, '2024-08-22 06:15:00', NULL, 'ongoing');

SELECT setval('public.users_id_seq', (SELECT MAX(id) FROM users) + 1);
SELECT setval('public.run_id_seq', (SELECT MAX(id) FROM run) + 1);
