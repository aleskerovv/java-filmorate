package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Genre implements Comparable<Genre> {
    private int id;
    private String name;

    @Override
    public int compareTo(Genre genre) {
        return this.id - genre.id;
    }
}
