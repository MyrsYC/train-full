package com.train.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.train.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user")
public class User extends BaseEntity {
    
    private String username;
    
    private String password;
    
    private String realName;
    
    private String idCard;
    
    private String phone;
    
    private String email;
    
    private Integer userType;
    
    private Integer status;
}
