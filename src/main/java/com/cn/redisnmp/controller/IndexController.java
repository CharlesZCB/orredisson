package com.cn.redisnmp.controller;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/demo")
public class IndexController {

    @Autowired
    private Redisson redisson;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping("/hello")
    public  void  demo(){
       String flag = "lockKey";
       RLock readLock = redisson.getLock(flag);
       if(readLock.isLocked()){
          System.err.println("系统繁忙，请稍后再试...");
       }
       try {
           readLock.lock();
           int stock =  Integer.parseInt(String.valueOf(redisTemplate.opsForValue().get("num")));
           if ( stock > 0 ){
               stock = stock - 1;
               redisTemplate.opsForValue().set("num",String.valueOf(stock));
               System.out.println("剩余库存：" + String.valueOf(stock));
           } else {
               System.out.println("库存不足...");
           }
       } finally {
            readLock.unlock();
       }
    }
}
