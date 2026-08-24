package com.libraryapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented //Indica que la anotación puede aparecer en la documentación generada por JavaDoc
@Constraint(validatedBy = CurrentOrPastYearValidator.class) //Le dice a Bean Validation que esta anotación representa una regla de validación.
@Target(ElementType.FIELD)//Indica dónde se puede usar la anotación.( solo en atributos)
@Retention(RetentionPolicy.RUNTIME) //Indica cuánto tiempo Java conserva la anotación.
public @interface CurrentOrPastYear {

    String message() default "El año no puede ser posterior al año actual";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
