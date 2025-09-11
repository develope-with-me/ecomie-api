package org.csbf.security.utils.commons;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Documented
@Constraint(validatedBy = {})
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = PatternConstant.OWASP_EMAIL_REGEX,
        message = "Invalid email address. Please provide a valid email address eg. example@email.com")
@Target({
        ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR,
        ElementType.PARAMETER, ElementType.TYPE_USE
})

public @interface ExtendedEmailValidator {
    String message() default "Please provide a valid email address";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Pattern.Flag[] flags() default {};

}


interface PatternConstant {
    String OWASP_EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
}
