package ru.yandex.practicum.filmorate.model.enums;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public enum SearchParam {

    TITLE("title"),
    DIRECTOR("director");

    public final String label;
    public static final Set<String> SEARCH_PARAMS = new HashSet<>();

    SearchParam(String label) {
        this.label = label;
    }

    static {
        Stream.of(SearchParam.values())
                .forEach(v -> SEARCH_PARAMS.add(v.label));
    }

    public static SearchParam valueOfLabel(String value) {
        return Stream.of(SearchParam.values())
                .filter(v -> v.label.equals(value))
                .findFirst().orElse(null);
    }
}
