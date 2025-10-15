package com.train.user.vo;

import lombok.Data;

/**
 * 用户响应VO
 */
@Data
public class UserVO {
    
    private Long id;
    
    private String username;
    
    private String realName;
    
    private String idCard;
    
    private String phone;
    
    private String email;
    
    private String token;
}
