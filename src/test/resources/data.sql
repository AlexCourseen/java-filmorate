MERGE INTO rating (name) KEY(name) VALUES ('G');
MERGE INTO rating (name) KEY(name) VALUES ('PG');
MERGE INTO rating (name) KEY(name) VALUES ('PG-13');
MERGE INTO rating (name) KEY(name) VALUES ('R');
MERGE INTO rating (name) KEY(name) VALUES ('NC-17');


MERGE INTO genre (name) KEY(name) VALUES ('Комедия');
MERGE INTO genre (name) KEY(name) VALUES ('Драма');
MERGE INTO genre (name) KEY(name) VALUES ('Мультфильм');
MERGE INTO genre (name) KEY(name) VALUES ('Триллер');
MERGE INTO genre (name) KEY(name) VALUES ('Документальный');
MERGE INTO genre (name) KEY(name) VALUES ('Боевик');


MERGE INTO users (name, email, login, birthday) KEY(email)
VALUES ('Testov1', 'test1@test.com', 'test1', '2001-01-01');
MERGE INTO users (name, email, login, birthday) KEY(email)
VALUES ('Testov2', 'test2@test.com', 'test2', '2002-01-01');
MERGE INTO users (name, email, login, birthday) KEY(email)
VALUES ('Testov3', 'test3@test.com', 'test3', '2003-01-01');
MERGE INTO users (name, email, login, birthday) KEY(email)
VALUES ('Testov4', 'test4@test.com', 'test4', '2004-01-01');


MERGE INTO film (name, description, releaseDate, duration, rating_id) KEY(name)
VALUES ('Титаник', 'Фильм про корабль', '2000-03-31', 136, 4);
MERGE INTO film (name, description, releaseDate, duration, rating_id) KEY(name)
VALUES ('Король лев', 'Мультфильм', '1999-04-22', 90, 1);
MERGE INTO film (name, description, releaseDate, duration, rating_id) KEY(name)
VALUES ('Пляж', 'Драма', '1989-04-22', 120, 3);
MERGE INTO film (name, description, releaseDate, duration, rating_id) KEY(name)
VALUES ('Оно', 'Триллер', '1985-03-15', 90, 5);

MERGE INTO likes (film_id, user_id) VALUES (1, 1);
MERGE INTO likes (film_id, user_id) VALUES (1, 2);
MERGE INTO likes (film_id, user_id) VALUES (1, 3);
MERGE INTO likes (film_id, user_id) VALUES (1, 4);
MERGE INTO likes (film_id, user_id) VALUES (2, 1);
MERGE INTO likes (film_id, user_id) VALUES (2, 2);
MERGE INTO likes (film_id, user_id) VALUES (3, 4);
MERGE INTO likes (film_id, user_id) VALUES (3, 2);
MERGE INTO likes (film_id, user_id) VALUES (3, 1);

