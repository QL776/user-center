package com.xql.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Q27
 * @description 用户登录请求体
 * @createDate
 */
@Data
public class UserLoginRequest implements Serializable {
  private static final long serialVersionUID = -2075865345789243727L;
  /**
   * 用户账号
   */
  private String account;
  /**
   * 用户密码
   */
  private String password;
}
