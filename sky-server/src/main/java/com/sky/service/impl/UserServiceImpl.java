package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName UserServiceImpl
 * @Description TODO
 * @Author msjoy
 * @Date 2024/9/16 16:58
 * @Version 1.0
 **/
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private final WeChatProperties weChatProperties;
    //微信服务接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session?";

    @Autowired
    private UserMapper userMapper;

    public UserServiceImpl(WeChatProperties weChatProperties) {
        this.weChatProperties = weChatProperties;
    }

    /**
     * 微信用户登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        Map<String, String> userInfo = getOpenId(userLoginDTO.getCode());
        String openId = userInfo.get("openId");
        String userName = userInfo.get("nickname");
        //判断openid是否为null，如果为空表示登录失败，抛出业务异常
        if (openId == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //判断当前用户是为新用户
        User user = userMapper.getUserByOpenId(openId);

        //如果是新用户，自动完成注册
        if (user == null) {
            user = User.builder()
                    .openid(openId)
                    .name(userName)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        //返回用户对象
        return user;
    }

    /**
     * 调用微信接口服务，获取当前微信用户的openid
     * @param code
     * @return
     */
    private Map<String, String> getOpenId(String code) {
        //调用微信接口服务，获取当前微信用户的openid
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("grant_type", "authorization_code");
        map.put("js_code", code);
        String json = HttpClientUtil.doGet(WX_LOGIN, map);

        JSONObject jsonObject = JSONObject.parseObject(json);
        String openId = jsonObject.getString("openid");
        map.put("openId", openId);
        String nickname = jsonObject.getString("nickname");
        map.put("nickname", nickname);
        return map;
    }
}
