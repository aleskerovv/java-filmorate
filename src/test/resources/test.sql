DROP TABLE IF EXISTS USERS CASCADE;
DROP TABLE IF EXISTS FRIENDSHIPS CASCADE;
DROP TABLE IF EXISTS FILMS CASCADE;
DROP TABLE IF EXISTS FILMS_LIKES CASCADE;
DROP TABLE IF EXISTS FILMS_GENRES CASCADE;
DROP TABLE IF EXISTS MPA_RATING CASCADE;
DROP TABLE IF EXISTS GENRES CASCADE;

create table USERS
(
    ID       INTEGER auto_increment
        primary key,
    EMAIL    CHARACTER VARYING(64) not null,
    LOGIN    CHARACTER VARYING(64) not null,
    NAME     CHARACTER VARYING(64),
    BIRTHDAY DATE
);

create table FRIENDSHIPS
(
    USER_ID   INTEGER not null,
    FRIEND_ID INTEGER not null,
    primary key (USER_ID, FRIEND_ID),
    constraint FRIENDS_FRIENDSHIP_ID
        foreign key (FRIEND_ID) references USERS,
    constraint USERS_FRIENDSHIP_ID
        foreign key (USER_ID) references USERS
);

create table MPA_RATING
(
    MPA_RATE_ID int,
    NAME        CHARACTER VARYING(64) not null,
    primary key (MPA_RATE_ID)
);

create table FILMS
(
    ID           INTEGER auto_increment
        primary key,
    NAME         CHARACTER VARYING(100) not null,
    DESCRIPTION  CHARACTER VARYING(200),
    RELEASE_DATE DATE,
    DURATION     INTEGER,
    RATE         INTEGER,
    MPA_RATE_ID  INTEGER,
    constraint FILMS_FK
        foreign key (MPA_RATE_ID) references MPA_RATING
);

create table GENRES
(
    GENRE_ID INTEGER  primary key,
    NAME     CHARACTER VARYING(64) not null
);

create table FILMS_LIKES
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    primary key (FILM_ID, USER_ID),
    constraint USERS_LIKES_FK
        foreign key (USER_ID) references USERS,
    constraint FILMS_LIKES_FK
        foreign key (FILM_ID) references FILMS
);

create table FILMS_GENRES
(
    FILM_ID  INTEGER not null,
    GENRE_ID INTEGER not null,
    primary key (FILM_ID, GENRE_ID),
    constraint FILM_FILM_FK_1
        foreign key (FILM_ID) references FILMS,
    constraint FILM_GENRE_FK_1
        foreign key (GENRE_ID) references GENRES
);

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

insert into films(name, description, release_date, duration, rate, mpa_rate_id)
values ('test', 'test desc', '20220101', 100, 15, 1);
insert into films(name, description, release_date, duration, rate, mpa_rate_id)
values ('another test', 'test 2', '20220102', 100, 15, 2);

insert into users(email, login, name, birthday)
values('user@mail.ru', 'user1', 'user1', '19850101');
insert into users(email, login, name, birthday)
values('user2@gmail.com', 'user2', 'user2', '19950101');
insert into users(email, login, name, birthday)
values('user3@mail.ru', 'user3', 'user3', '19650101');