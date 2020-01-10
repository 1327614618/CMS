package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UcenterAuthApplication.class)
public class RedisTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedis() {
//定义key
        String key = "user_token:9734b68f‐cf5e‐456f‐9bd6‐df578c711390";
//定义Map
        Map<String, String> mapValue = new HashMap<>();
        mapValue.put("id", "101");
        mapValue.put("username", "itcast");
        String value = JSON.toJSONString(mapValue);
//向redis中存储字符串
        stringRedisTemplate.boundValueOps(key).set(value, 60, TimeUnit.SECONDS);
//读取过期时间，已过期返回‐2
        Long expire = stringRedisTemplate.getExpire(key);
//根据key获取value
        String s = stringRedisTemplate.opsForValue().get(key);
        System.out.println(s);
    }
    
    @Test
    public void testPasswrodEncoder() {
        String password = "111111";
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    for (int i = 0; i < 10; i++) {
            //每个计算出的Hash值都不一样
                        String hashPass = passwordEncoder.encode(password);
                        System.out.println(hashPass);
            //虽然每次计算的密码Hash值不一样但是校验是通过的
            boolean f = passwordEncoder.matches(password, hashPass);
            System.out.println(f);
        }
    }
}
