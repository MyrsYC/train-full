package com.train.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.train.common.exception.BusinessException;
import com.train.common.result.ResultCode;
import com.train.user.dto.LoginDTO;
import com.train.user.dto.RegisterDTO;
import com.train.user.entity.User;
import com.train.user.mapper.UserMapper;
import com.train.user.service.UserService;
import com.train.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    
    @Override
    public UserVO register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, registerDTO.getUsername());
        User existUser = userMapper.selectOne(wrapper);
        if (existUser != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(DigestUtil.md5Hex(registerDTO.getPassword()));
        user.setRealName(registerDTO.getRealName());
        user.setIdCard(registerDTO.getIdCard());
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setUserType(0);
        user.setStatus(1);
        user.setDelFlag(0);
        
        userMapper.insert(user);
        
        // 返回用户信息
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }
    
    @Override
    public UserVO login(LoginDTO loginDTO) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // 验证密码
        if (!user.getPassword().equals(DigestUtil.md5Hex(loginDTO.getPassword()))) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        
        // 返回用户信息
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        userVO.setToken("token-" + user.getId()); // 简化实现，实际应使用JWT
        return userVO;
    }
    
    @Override
    public UserVO getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }
    
    @Override
    public UserVO getUserById(Long id) {
        User user = userMapper.selectById(id);
        
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }
}
