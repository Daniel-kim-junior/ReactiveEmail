package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.example.demo.domain.EmailToken;

@Configuration
public class ReactiveRedisConfig {
	@Bean
	ReactiveRedisOperations<String, EmailToken> redisOperations(ReactiveRedisConnectionFactory factory) {
		Jackson2JsonRedisSerializer<EmailToken> serializer = new Jackson2JsonRedisSerializer<>(EmailToken.class);

		RedisSerializationContext.RedisSerializationContextBuilder<String, EmailToken> builder =
			RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

		RedisSerializationContext<String, EmailToken> context = builder.value(serializer).build();

		return new ReactiveRedisTemplate<>(factory, context);
	}

}
