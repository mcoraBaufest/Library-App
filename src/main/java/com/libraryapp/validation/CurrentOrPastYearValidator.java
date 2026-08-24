package com.libraryapp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Year;

public class CurrentOrPastYearValidator implements ConstraintValidator<CurrentOrPastYear, Integer> {

    @Override
    public boolean isValid(Integer year, ConstraintValidatorContext context) {
        return year != null && year <= Year.now().getValue();
    }
}
