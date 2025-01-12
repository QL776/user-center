package com.xql.usercenter.service.impl;

import com.xql.usercenter.service.UserValidationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserValidationServiceImpl implements UserValidationService {

    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

    @Override
    public boolean isInvalidInput(String... params) {
        if (params.length < 2 || params.length > 3) {
            throw new IllegalArgumentException("Invalid number of parameters");
        }

        String account = params[0];
        String password = params[1];
        String checkPassword = params.length == 3 ? params[2] : null;

        if(StringUtils.isAnyBlank(account, password) || account.length() < 4 || password.length() < 8){
            return true;
        }
        if(checkPassword != null){
            return StringUtils.isBlank(checkPassword) || !password.equals(checkPassword);
        }
        return false;
    }

    @Override
    public boolean containSpecialChar(String account) {
        return SPECIAL_CHAR_PATTERN.matcher(account).matches();
    }
}
