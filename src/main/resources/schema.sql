create table if not exists USERS
(
    ID       INTEGER auto_increment
        primary key,
    EMAIL    CHARACTER VARYING(64) not null,
    LOGIN    CHARACTER VARYING(64) not null,
    NAME     CHARACTER VARYING(64),
    BIRTHDAY DATE
);

create table if not exists FRIENDSHIPS
(
    USER_ID   INTEGER not null,
    FRIEND_ID INTEGER not null,
    primary key (USER_ID, FRIEND_ID),
    constraint FRIENDS_FRIENDSHIP_ID
        foreign key (FRIEND_ID) references USERS,
    constraint USERS_FRIENDSHIP_ID
        foreign key (USER_ID) references USERS
);

create table if not exists MPA_RATING
(
    MPA_RATE_ID int,
    NAME        CHARACTER VARYING(64) not null,
    primary key (MPA_RATE_ID)
);

create table if not exists FILMS
(
    ID           INTEGER auto_increment
        primary key,
    NAME         CHARACTER VARYING(100) not null,
    DESCRIPTION  CHARACTER VARYING(200),
    RELEASE_DATE DATE,
    DURATION     INTEGER,
    RATE         INTEGER DEFAULT 0,
    MPA_RATE_ID  INTEGER,
    constraint FILMS_FK
        foreign key (MPA_RATE_ID) references MPA_RATING
);

create table if not exists GENRES
(
    GENRE_ID INTEGER  primary key,
    NAME     CHARACTER VARYING(64) not null
);

create table if not exists FILMS_LIKES
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    primary key (FILM_ID, USER_ID),
    constraint USERS_LIKES_FK
        foreign key (USER_ID) references USERS,
    constraint FILMS_LIKES_FK
        foreign key (FILM_ID) references FILMS
);

create table if not exists FILMS_GENRES
(
    FILM_ID  INTEGER not null,
    GENRE_ID INTEGER not null,
    primary key (FILM_ID, GENRE_ID),
    constraint FILM_FILM_FK_1
        foreign key (FILM_ID) references FILMS,
    constraint FILM_GENRE_FK_1
        foreign key (GENRE_ID) references GENRES
);

CREATE TABLE IF NOT EXISTS DIRECTORS
(
  ID INTEGER auto_increment PRIMARY KEY,
  NAME VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS FILMS_DIRECTORS
(
  FILM_ID INTEGER NOT NULL,
  DIRECTOR_ID INTEGER NOT NULL,
  PRIMARY KEY (FILM_ID, DIRECTOR_ID),
  CONSTRAINT FD_FILM
    FOREIGN KEY (FILM_ID) REFERENCES FILMS,
  CONSTRAINT FD_DIRECTOR
    FOREIGN KEY (DIRECTOR_ID) REFERENCES DIRECTORS
);