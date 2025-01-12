package com.xql.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xql.usercenter.model.domain.User;
import com.xql.usercenter.model.domain.UserDeleteRequest;
import com.xql.usercenter.model.domain.request.UserLoginRequest;
import com.xql.usercenter.model.domain.request.UserRegisterRequest;
import com.xql.usercenter.service.UserService;
import com.xql.usercenter.service.UserValidationService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;

import static com.xql.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.xql.usercenter.constant.UserConstant.USER_LOGIN_STATE;
/**
 * @author Q27
 * @description 用户注册
 * @createDate 2024-11-27 21:11:26
 * */
@RestController
@Slf4j
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:3000"},allowCredentials = "true")
public class UserController {
    //依赖注入
    @Resource
    private UserService userService;
    @Resource
    private UserValidationService userValidationService;

    /**
     * 用户注册接口。
     *
     * 该方法处理用户的注册请求。首先验证请求体是否为空，如果为空，则返回 `BAD_REQUEST` 错误响应。
     * 然后对输入的账号、密码和确认密码进行校验，如果存在无效参数，返回 `BAD_REQUEST` 错误响应。
     * 接着调用 `userService.userRegister` 方法执行注册操作，如果注册失败，返回 `INTERNAL_SERVER_ERROR` 错误响应。
     * 如果注册成功，返回注册用户的 ID。
     *
     * @param userRegisterRequest 包含用户注册信息（账号、密码和确认密码）的请求体。
     * @return 如果注册成功，返回 `OK` 状态和新注册用户的 ID；如果注册失败，返回 `INTERNAL_SERVER_ERROR` 错误响应；如果请求参数无效，返回 `BAD_REQUEST` 错误响应。
     */
    @PostMapping("/register")
    public ResponseEntity<?> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return ResponseEntity.badRequest().body("Request body cannot be null");
        }

        String account = userRegisterRequest.getAccount();
        String password = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        // 参数校验
        if (userValidationService.isInvalidInput(account, password, checkPassword)) {
            return ResponseEntity.badRequest().body("Invalid input parameters");
        }

        long userId = userService.userRegister(account, password, checkPassword);
        if (userId == -1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
        }
        return ResponseEntity.ok(userId);
    }


    /**
     * 用户登录接口。
     *
     * 该方法用于处理用户的登录请求。首先验证请求体是否为空，如果为空，则返回 `BAD_REQUEST` 错误响应。
     * 然后检查用户名和密码是否有效（不能为空），如果无效，返回 `BAD_REQUEST` 错误响应。
     * 如果输入有效，调用 `userService.userLogin` 方法进行登录验证。如果登录失败，返回 `UNAUTHORIZED` 错误响应，并记录失败日志。
     * 如果登录成功，返回用户信息（经过脱敏处理）。
     *
     * @param userLoginRequest 包含用户登录信息（账号和密码）的请求体。
     * @param request 当前的 HTTP 请求对象，用于保存用户的登录状态。
     * @return 如果用户登录成功，返回 `OK` 状态和用户信息；如果登录失败，返回 `UNAUTHORIZED` 错误响应；如果请求参数无效，返回 `BAD_REQUEST` 错误响应。
     */
    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if(userLoginRequest == null){
            return ResponseEntity.badRequest().body("Request body cannot be null");
        }
        String account = userLoginRequest.getAccount();
        String password = userLoginRequest.getPassword();
        if(userValidationService.isInvalidInput(account, password)){
            return ResponseEntity.badRequest().body("Account and password cannot be blank");
        }
        User user = userService.userLogin(account, password, request);
        if (user == null) {
            log.error("Login failed for account: {}", account);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        User user = userService.getCurrentUser(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
        return ResponseEntity.ok(user);
    }


    @PostMapping("/logout")
    public ResponseEntity<?>  userLogout(HttpServletRequest request){

           if(request == null){
               return ResponseEntity.badRequest().body("Invalid request");
           }
           int result = userService.userLogout(request);
           if(result == 1){
               return ResponseEntity.ok("Logout successfully");
           }else{
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Logout failed");
           }
    }

    /**
     * 搜索用户列表。
     *
     * 该方法允许管理员根据用户名进行用户搜索。首先验证当前请求的用户是否为管理员，如果不是，则返回 `FORBIDDEN` 错误响应。
     * 然后根据请求参数中的用户名（如果提供）构造查询条件，查找匹配的用户。查询结果将进行脱敏处理（通过 `userService.getOriginalUser` 方法）。
     * 最终返回经过处理的用户列表。
     *
     * @param username 可选的查询参数，用于根据用户名进行模糊查询。
     * @param request 当前的 HTTP 请求对象，用于验证请求者是否为管理员。
     * @return 返回包含符合条件的用户信息的列表，如果请求的用户不是管理员，则返回 `FORBIDDEN` 错误响应。
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam(required = false) String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can perform this operation");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }

        List<User> userList = userService.list(queryWrapper);
        List<User> safeUserList = userList.stream()
                .map(userService::getOriginalUser)
                .collect(Collectors.toList());
        return ResponseEntity.ok(safeUserList);
    }





    @PostMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestBody UserDeleteRequest request, HttpServletRequest servletRequest) {
        if (!isAdmin(servletRequest)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can perform this operation");
        }

        if (request.getId() == null || request.getId() <= 0) {
            return ResponseEntity.badRequest().body("Invalid user ID");
        }
        boolean result = userService.removeById(request.getId());
        if (result) {
            return ResponseEntity.ok("User deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User deletion failed");
    }



    /**
     * 校验当前用户是否为管理员。
     *
     * 该方法通过检查 HTTP 请求中的会话（Session）来判断用户的角色是否为管理员。
     * 如果会话中存在用户信息，并且该用户的角色符合管理员角色标识，则返回 `true`。
     * 否则返回 `false`，表示该用户不是管理员。
     *
     * @param request 当前的 HTTP 请求对象，用于获取用户的登录状态和信息。
     * @return 如果当前用户是管理员，返回 `true`；否则返回 `false`。
     */
    private boolean isAdmin(HttpServletRequest request){
        //校验是否是管理员
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user!= null && user.getRole() == ADMIN_ROLE;
    }
}
