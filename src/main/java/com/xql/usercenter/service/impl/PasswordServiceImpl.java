package com.xql.usercenter.service.impl;

import com.xql.usercenter.service.PasswordService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class PasswordServiceImpl implements PasswordService {

    private static final String SALT = "THisIsASaltUsedToEncryptPassword";

    @Override
    public String encryptPassword(String password) {
        return DigestUtils.md5DigestAsHex((SALT + password).getBytes());
    }
}
