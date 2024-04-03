package com.yz.pferestapi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChangePasswordDto {
    @NotEmpty(message = "Current password should not be null or empty")
    private String currentPassword;

    @NotEmpty(message = "New password should not be null or empty")
    @Size(min = 4, message = "Password must be minimum 4 characters")
    private String newPassword;

    @NotEmpty(message = "Confirm new password should not be null or empty")
    private String confirmNewPassword;

}
