package com.yz.pferestapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UpdateProfileDto {
    @NotEmpty(message = "Name should not be null or empty")
    private String firstName;

    @NotEmpty(message = "Name should not be null or empty")
    private String lastName;

    @NotEmpty(message = "Email should not be null or empty")
    @Email
    private String email;

    @NotNull(message = "Phone number should not be null")
    @Pattern(regexp = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$", message = "Phone number is invalid") // source: https://ihateregex.io/expr/phone
    private String phoneNumber;
}
