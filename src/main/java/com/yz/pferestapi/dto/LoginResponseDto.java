package com.yz.pferestapi.dto;

import com.yz.pferestapi.entity.User;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    private User user;
    private String accessToken;
}
