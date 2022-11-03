--films insert
insert into films(name, description, release_date, duration, mpa_rate_id)
values ('first Film', 'test desc of first film', '20220101', 100, 1);
insert into films(name, description, release_date, duration, mpa_rate_id)
values ('second film', 'test desc of second film', '20220102', 100, 2);
insert into films(name, description, release_date, duration, mpa_rate_id)
values ('third film', 'test desc of third film', '20220102', 100, 2);
--users insert
insert into users(email, login, name, birthday)
values('user@mail.ru', 'user1', 'user1', '19850101');
insert into users(email, login, name, birthday)
values('user2@gmail.com', 'user2', 'user2', '19950101');
insert into users(email, login, name, birthday)
values('user3@mail.ru', 'user3', 'user3', '19650101');
--reviews insert
insert into reviews(content, is_positive, user_id, film_id)
values('positive review for film 1 from user 1', true, 1, 1);
insert into reviews(content, is_positive, user_id, film_id)
values('negative review for film 2 from user 2', false, 2, 2);