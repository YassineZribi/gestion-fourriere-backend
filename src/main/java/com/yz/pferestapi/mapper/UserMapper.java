package com.yz.pferestapi.mapper;

import com.yz.pferestapi.dto.UserDto;
import com.yz.pferestapi.entity.User;

import java.util.ArrayList;

public class UserMapper {
    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .position(user.getPosition())
                .photoPath(user.getPhotoPath())
                .subordinates(new ArrayList<>())
                .build();
    };
}
