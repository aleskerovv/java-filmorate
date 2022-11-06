--directors insert
MERGE INTO DIRECTORS (ID, NAME)
    VALUES ('1', 'Steven Spielberg'),
    ('2', 'Quentin Tarantino');
--films_directors insert
MERGE INTO FILMS_DIRECTORS(FILM_ID, DIRECTOR_ID)
VALUES ('1', '1'),
    ('1', '2'),
    ('3', '2');