package com.yz.pferestapi.dto;

import lombok.*;

import java.util.Map;

@Setter
@Getter
public class FormValidationErrorResponse extends ErrorResponse{
    private Map<String, String> details;

    public FormValidationErrorResponse(int status, String error, String message, Map<String, String> details) {
        super(status, error, message);
        this.details = details;
    }
}
