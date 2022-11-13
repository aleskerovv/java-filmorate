package ru.yandex.practicum.filmorate.model.enums;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SearchParam {

    TITLE,
    DIRECTOR;

    public static Set<String> searchParams() {
        return Stream.of(SearchParam.values())
                .map(SearchParam::toString)
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
