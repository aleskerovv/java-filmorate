# java-filmorate
Template repository for Filmorate project.  

Shown a filmorateDB er-diagram

<picture>
  <source media="(prefers-color-scheme: dark)" srcset="http://ipic.su/img/img7/fs/filmorate-er-diagram.1664635129.jpg">
  <source media="(prefers-color-scheme: light)" srcset="http://ipic.su/img/img7/fs/filmorate-er-diagram.1664635129.jpg">
  <img alt="Shows a filmorate-er-diagram" src="http://ipic.su/img/img7/fs/filmorate-er-diagram.1664635129.jpg">
</picture>

Several SQL requests:

SELECT * \
FROM film \
WHERE rating='PG-13'  

SELECT * FROM user \
WHERE birthday >='20200101'  

SELECT f.title, g.genre \
FROM film f \
INNER JOIN film_genre fg on f.film_id=fg.film_id \
INNER JOIN genre g on fg.genre_id=fg.genre_id \
WHERE g.name='Comedy'