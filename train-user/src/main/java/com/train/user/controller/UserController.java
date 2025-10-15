package com.train.user.controller;

import com.train.common.result.Result;
import com.train.user.dto.LoginDTO;
import com.train.user.dto.RegisterDTO;
import com.train.user.service.UserService;
import com.train.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody RegisterDTO registerDTO) {
        UserVO userVO = userService.register(registerDTO);
        return Result.success(userVO);
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody LoginDTO loginDTO) {
        UserVO userVO = userService.login(loginDTO);
        return Result.success(userVO);
    }
    
    /**
     * 根据用户名查询用户
     */
    @GetMapping("/username/{username}")
    public Result<UserVO> getUserByUsername(@PathVariable String username) {
        UserVO userVO = userService.getUserByUsername(username);
        return Result.success(userVO);
    }
    
    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        UserVO userVO = userService.getUserById(id);
        return Result.success(userVO);
    }
}
