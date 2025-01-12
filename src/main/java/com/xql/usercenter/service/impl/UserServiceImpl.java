package com.xql.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xql.usercenter.service.UserService;
import com.xql.usercenter.service.UserValidationService;
import com.xql.usercenter.service.PasswordService;
import com.xql.usercenter.model.domain.User;
import com.xql.usercenter.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @description 用户实现类
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final int ERROR_CODE = -1;

    @Resource
    private UserValidationService userValidationService;

    @Resource
    private PasswordService passwordService;


    /**
     * 用户注册方法。
     *
     * 该方法首先验证用户输入的账号、密码和确认密码是否合法。接着检查账号是否已存在，
     * 如果账号不存在，说明可以注册，则对密码进行加密，创建新的用户对象并保存到数据库中，最后返回新用户的 ID。
     *
     * @param account 用户注册的账号
     * @param password 用户注册的密码
     * @param checkPassword 用户确认的密码
     * @return 如果注册成功，返回新创建用户的 ID；如果发生错误，返回错误码 ERROR_CODE
     */
    @Override
    public long userRegister(String account, String password, String checkPassword) {
        // 1. 校验用户输入
        if (userValidationService.isInvalidInput(account, password, checkPassword)) {
            return ERROR_CODE;
        }
        if (userValidationService.containSpecialChar(account)) {
            return ERROR_CODE;
        }
        // 密码和确认密码是否一致
        if (!password.equals(checkPassword)) {
            return ERROR_CODE;
        }
        // 2. 校验账号是否存在
        if (isAccountExist(account)) {
            return ERROR_CODE;
        }
        // 3. 加密密码
        String encryptedPassword = passwordService.encryptPassword(password);
        // 4. 创建用户对象并插入
        User user = createUser(account, encryptedPassword);
        if (!this.save(user)) {
            return ERROR_CODE;
        }
        return user.getId();
    }


    /**
     * 用户登录方法。
     * <p>
     * 该方法首先验证用户输入的账号和密码是否合法。如果输入无效或账号包含特殊字符，登录失败。
     * 然后对密码进行加密，查找数据库中是否有匹配的用户账号和密码。如果找到匹配的用户，
     * 返回脱敏后的用户信息，并将登录态记录在会话中。如果账号或密码不匹配，则返回 `null`。
     *
     * @param account 用户输入的账号
     * @param password 用户输入的密码
     * @param request HTTP 请求对象，用于获取会话信息
     * @return 如果登录成功，返回脱敏后的用户信息；如果登录失败，返回 `null`
     */
    @Override
    public User userLogin(String account, String password, HttpServletRequest request) {
        if (userValidationService.isInvalidInput(account, password)) {
            return null;
        }
        if (userValidationService.containSpecialChar(account)) {
            return null;
        }

        String encryptedPassword = passwordService.encryptPassword(password);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account).eq("password", encryptedPassword);
        User user = this.getOne(queryWrapper);

        if (user == null) {
            log.info("User login failed, account or password mismatch for account: {}", account);
            return null;
        }

        // 脱敏用户信息
        User userSafety = getOriginalUser(user);
        // 记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, userSafety);
        return userSafety;
    }

    @Override
    public User getCurrentUser(HttpServletRequest request) {
        // 从Session中获取当前登录用户
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);

        // 如果Session中没有用户信息，返回null
        if (user == null) {
            return null;
        }

        // 如果需要，可以对用户信息进行脱敏处理
        User userSafety = getOriginalUser(user);

        return userSafety;
    }


    /**
     * 检查指定账号是否已存在。
     *
     * 该方法根据给定的账号查询数据库，判断该账号是否已经存在。如果存在，则返回 `true`，
     * 否则返回 `false`。
     *
     * @param account 要检查的账号
     * @return 如果账号存在，返回 `true`；如果账号不存在，返回 `false`
     */
    private boolean isAccountExist(String account) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        return this.count(queryWrapper) > 0;
    }

    /**
     * 创建一个新的用户对象并初始化相关字段。
     * <p>
     * 该方法用于创建一个新的 `User` 对象，并为其设置必要的字段。包括：账号、加密后的密码、用户名、性别、状态等。
     * 用户对象会被赋予默认值，且创建和更新时间会设置为当前时间。
     *
     * @param account  用户账号
     * @param encryptedPassword 加密后的密码
     * @return 创建并初始化的 `User` 对象
     */
    private User createUser(String account, String encryptedPassword) {
        User user = new User();
        user.setAccount(account);
        user.setPassword(encryptedPassword);
        user.setUsername(account);  // 使用 account 作为 username
        user.setGender(0);  // 默认性别为0
        user.setStatus(1);  // 默认状态为正常
        user.setIsDelete(0);  // 默认不删除
        user.setCreateTime(new Date());  // 设置创建时间
        user.setUpdateTime(new Date());  // 设置更新时间
        return user;
    }

    /**
     * 将原始用户信息脱敏，返回一个只包含部分安全信息的用户对象。
     * <p>
     * 该方法用于将原始用户对象中的敏感信息（如密码等）进行脱敏处理，并返回一个只包含必要信息的 `User` 对象。
     * 该方法通常用于返回给前端时，避免暴露敏感数据。
     *
     * @param originUser 原始的用户对象，包含所有用户信息（包括敏感信息）
     * @return 脱敏后的用户对象，仅包含非敏感信息（如用户名、头像、邮箱等）
     */
    @Override
    public User getOriginalUser(User originUser) {
        User userSafety = new User();
        userSafety.setId(originUser.getId());
        userSafety.setUsername(originUser.getUsername());
        userSafety.setAccount(originUser.getAccount());
        userSafety.setAvatarUrl(originUser.getAvatarUrl());
        userSafety.setGender(originUser.getGender());
        userSafety.setPhone(originUser.getPhone());
        userSafety.setEmail(originUser.getEmail());
        userSafety.setRole(originUser.getRole());
        userSafety.setStatus(originUser.getStatus());
        userSafety.setCreateTime(originUser.getCreateTime());
        return userSafety;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        if(request == null || request.getSession() == null) {
            return 0;
        }
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}
