package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    //设置一个bean 然后再添加如下参数 会让该参数的bean也注入进来
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        //实例化bean
        RedisTemplate<String,Object> template=new RedisTemplate<>();
        //将工厂设置给template
        template.setConnectionFactory(factory);

        //因为我们写的是java程序所以我们需要指定的序列化方式
        //设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        //设置value的序列化方式
        template.setValueSerializer(RedisSerializer.json());
        //设置hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        //设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());
        //设置完需要激活一下
        template.afterPropertiesSet();
        return template;
    }
}
