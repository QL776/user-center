package com.xql.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xql.usercenter.model.domain.User;
import jakarta.servlet.http.HttpServletRequest;


/**
* @author Q27
* @description 用户服务
* @createDate 2024-11-27 21:11:26
*/
public interface UserService extends IService<User> {

    /**
     * 用户登录态的key
     */
    String USER_LOGIN_STATE = "userLoginState";
    /*
    * 用户注册方法
    *
    * @param account 注册账号
    * @param password 注册密码
    * @param checkPassword 确认密码
    * @return 注册成功返回用户id，注册失败返回-1
    */
    long userRegister(String account,String password, String checkPassword);

    /*
    * 用户登录方法
    *
    * @param account 登录账号
    * @param password 登录密码
      @return 返回用户脱敏信息
     */
    User userLogin(String account, String password, HttpServletRequest request);

    User getCurrentUser(HttpServletRequest request);


    User getOriginalUser(User originUser);

    /**
     * 用户注销方法
     *
     * @param request 请求
     * @return 注销成功返回1，注销失败返回0
     */
    int userLogout(HttpServletRequest request);
}

