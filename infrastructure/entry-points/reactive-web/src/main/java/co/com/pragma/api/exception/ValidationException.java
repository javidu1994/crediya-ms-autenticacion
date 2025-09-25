package co.com.pragma.api.exception;

import co.com.pragma.api.dto.ValidationError;

import java.util.Arrays;
import java.util.List;

public class ValidationException extends RuntimeException {

    private final List<ValidationError> errors;

    public ValidationException(ValidationError... errors) {
        this(Arrays.asList(errors));
    }

    public ValidationException(List<ValidationError> errors) {
        this.errors = errors;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
