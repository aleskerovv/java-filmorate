package ru.yandex.practicum.filmorate.model.enums;

public enum MpaRating {
    G("G"),
    PG("PG"),
    PG13("PG-13"),
    R("R"),
    NC17("NC-17");

    private String code;

    private MpaRating(String code) {
        this.code=code;
    }
}
