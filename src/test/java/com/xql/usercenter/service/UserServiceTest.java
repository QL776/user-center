package com.xql.usercenter.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xql.usercenter.mapper.UserMapper;
import com.xql.usercenter.model.domain.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Q27
 */
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;
    @Mock
    private UserMapper userMapper; // Mock UserMapper



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void userRegister_shouldReturnErrorCode_whenAccountIsBlank() {
        long result = userService.userRegister("", "Password123", "Password123");
        assertEquals(-1, result, "Account is blank, should return -1");
    }

    @Test
    void userRegister_shouldReturnErrorCode_whenPasswordTooShort() {
        long result = userService.userRegister("testAccount", "short", "short");
        assertEquals(-1, result, "Password is too short, should return -1");
    }

    @Test
    void userRegister_shouldReturnErrorCode_whenAccountContainsSpecialChars() {
        long result = userService.userRegister("test@account", "Password123", "Password123");
        assertEquals(-1, result, "Account contains special characters, should return -1");
    }

    @Test
    void userRegister_shouldReturnErrorCode_whenPasswordsDoNotMatch() {
        long result = userService.userRegister("testAccount", "Password123", "Password124");
        assertEquals(-1, result, "Passwords do not match, should return -1");
    }



    @Test
    void userRegister_shouldReturnUserId_whenRegistrationIsSuccessful() {
        // Mocking the behavior of `isAccountExist` (account does not exist)
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);

        // Mocking the save operation to return true
        when(userMapper.insert(any(User.class))).thenReturn(1);

        long result = userService.userRegister("newAccount", "Password123", "Password123");
        assertNotEquals(-1, result, "Registration should be successful");
    }
    @Test
    void userRegister_shouldReturnErrorCode_whenAccountAlreadyExists() {
        // Mocking the behavior of `isAccountExist`
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        long result = userService.userRegister("newAccount", "Password123", "Password123");
        assertEquals(-1, result, "Account already exists, should return -1");
    }



}