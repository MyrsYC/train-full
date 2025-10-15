package com.train.user.service;

import com.train.user.dto.LoginDTO;
import com.train.user.dto.RegisterDTO;
import com.train.user.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     */
    UserVO register(RegisterDTO registerDTO);
    
    /**
     * 用户登录
     */
    UserVO login(LoginDTO loginDTO);
    
    /**
     * 根据用户名查询用户
     */
    UserVO getUserByUsername(String username);
    
    /**
     * 根据ID查询用户
     */
    UserVO getUserById(Long id);
}
