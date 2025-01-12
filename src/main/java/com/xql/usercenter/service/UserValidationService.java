package com.xql.usercenter.service;

public interface UserValidationService {

    boolean isInvalidInput(String... params);

    boolean containSpecialChar(String account);
}
