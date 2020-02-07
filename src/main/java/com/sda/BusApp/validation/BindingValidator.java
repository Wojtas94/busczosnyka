package com.sda.BusApp.validation;

import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

public class BindingValidator {

    public static void validate(BindingResult result) {
        if (!result.getFieldErrors().isEmpty()) {
            List<String> errors = new ArrayList<>();
            result
                    .getFieldErrors()
                    .forEach(e -> errors.add(errors.size()+ 1 + ": " + e.getDefaultMessage()));
            throw new RuntimeException(errors.toString());
        }
    }
}
