package com.yz.pferestapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDto {
    @NotEmpty(message = "Name should not be null or empty")
    private String firstName;

    @NotEmpty(message = "Name should not be null or empty")
    private String lastName;

    @NotEmpty(message = "Email should not be null or empty")
    @Email
    private String email;

    @NotEmpty
    @Size(min = 4, message = "Comment body must be minimum 4 characters")
    private String password;
}
