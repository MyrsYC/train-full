package com.train.user.dto;

import lombok.Data;

/**
 * 注册请求DTO
 */
@Data
public class RegisterDTO {
    
    private String username;
    
    private String password;
    
    private String realName;
    
    private String idCard;
    
    private String phone;
    
    private String email;
}
