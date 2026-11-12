package com.english.learning.dto;

import lombok.Data;

@Data
public class UserProfileRequest {
    private String realName;
    private String email;
    private String phone;
    private String avatar;
    private Integer gender;
    private String birthday;
    private String school;
    private String currentLevel;
    private String targetLevel;
}
