package com.example.webfluxsessiondemo.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.RedisFlushMode;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

/**
 * @author wangxing
 * @create 2019/5/15
 */
@Configuration
@EnableRedisWebSession(redisFlushMode = RedisFlushMode.ON_SAVE)
public class SpringSessionConfig {

	@Bean
	RedisSerializer<Object> springSessionDefaultRedisSerializer() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
		GenericJackson2JsonRedisSerializer jackson = new GenericJackson2JsonRedisSerializer(mapper);

		return jackson;
	}
}