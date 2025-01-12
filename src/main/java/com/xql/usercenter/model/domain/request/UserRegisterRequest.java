package com.xql.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Q27
 * @description 用户注册请求体
 * @createDate
 */
@Data
public class UserRegisterRequest implements Serializable {
  private static final long serialVersionUID = 3191241716373120793L;
  /**
   * 用户账号
   */
  private String account;
  /**
   * 用户密码
   */
  private String password;
  /**
   * 确认密码
   */
  private String checkPassword;


}
