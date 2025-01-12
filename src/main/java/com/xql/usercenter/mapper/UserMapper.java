package com.xql.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xql.usercenter.model.domain.User;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Q27
* @description 针对表【user(用户信息表)】的数据库操作Mapper
* @createDate 2024-11-27 21:11:26
* @Entity generator.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




