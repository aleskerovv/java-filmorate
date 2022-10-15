merge into GENRES(GENRE_ID, name)
    values (1, 'Комедия');

merge into GENRES(GENRE_ID, name)
    values (2, 'Драма');

merge into GENRES(GENRE_ID, name)
    values (3, 'Мультфильм');

merge into GENRES(GENRE_ID, name)
    values (4, 'Триллер');

merge into GENRES(GENRE_ID, name)
    values (5, 'Документальный');

merge into GENRES(GENRE_ID, name)
    values (6, 'Боевик');


merge into MPA_RATING(MPA_RATE_ID, NAME)
    VALUES (1, 'G');
merge into MPA_RATING(MPA_RATE_ID, NAME)
    VALUES (2, 'PG');
merge into MPA_RATING(MPA_RATE_ID, NAME)
    VALUES (3, 'PG-13');
merge into MPA_RATING(MPA_RATE_ID, NAME)
    VALUES (4, 'R');
merge into MPA_RATING(MPA_RATE_ID, NAME)
    VALUES (5, 'NC-17');