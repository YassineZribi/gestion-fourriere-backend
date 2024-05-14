package com.yz.pferestapi.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertUserDto {
    @NotEmpty(message = "First name should not be null or empty")
    private String firstName;

    @NotEmpty(message = "Last name should not be null or empty")
    private String lastName;

    @NotEmpty(message = "Email should not be null or empty")
    @Email
    private String email;

    @NotNull(message = "Phone number should not be null")
    @Pattern(regexp = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$", message = "Phone number is invalid") // source: https://ihateregex.io/expr/phone
    private String phoneNumber;

    @NotEmpty(message = "Role name should not be null or empty")
    private String roleName;
}
