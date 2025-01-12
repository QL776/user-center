package com.xql.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("`user`")
public class User {
    // 用户ID，主键
    @TableId(type = IdType.AUTO)
    private Long id;
    // 用户名
    private String username;
    // 用户头像URL
    private String avatarUrl;
    // 用户性别，0：未知，1：男，2：女
    private int gender;
    // 用户账号
    private String account;
    // 用户密码
    private String password;
    // 用户电话号码
    private String phone;
    // 用户邮箱
    private String email;
    // 用户状态，0：禁用，1：正常
    private int status;
    // 用户创建时间
    private Date createTime;
    // 用户更新时间
    private Date updateTime;
    // 用户是否删除，0：未删除，1：已删除
    @TableLogic
    private int isDelete;

    private int role;

    // 无参构造函数
    public User() {}

    // 带参构造函数
    public User(Long id, String username, String avatarUrl, int gender, String account, String password,
                String phone, String email, int status, Date createTime, Date updateTime, int isDelete) {
        this.id = id;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.gender = gender;
        this.account = account;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.isDelete = isDelete;
    }

    // Getter 和 Setter 方法

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    // toString 方法 (可选)
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", gender=" + gender +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", isDelete=" + isDelete +
                '}';
    }
}
