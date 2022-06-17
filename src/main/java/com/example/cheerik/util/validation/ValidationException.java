package com.example.cheerik.util.validation;

import java.util.Set;

public class ValidationException extends RuntimeException {
    public <T> ValidationException(Set<String> errors) {
        super(String.join("\n", errors));
    }

    public <T> ValidationException(String error) {
        super(error);
    }
}
