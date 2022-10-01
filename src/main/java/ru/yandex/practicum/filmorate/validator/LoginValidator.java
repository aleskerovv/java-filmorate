package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.annotations.CorrectLogin;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LoginValidator implements ConstraintValidator<CorrectLogin, String> {
    @Override
    public boolean isValid(String login, ConstraintValidatorContext constraintValidatorContext) {
        if (login == null) return true;
        return !(login.contains(" "));
    }
}
