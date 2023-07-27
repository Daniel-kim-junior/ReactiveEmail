package com.example.demo.loader;

import static com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat.*;

import java.util.Random;
import java.util.random.RandomGenerator;

import javax.annotation.PostConstruct;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;

import com.example.demo.config.ReactiveRedisConfig;
import com.example.demo.domain.EmailToken;

import reactor.core.publisher.Flux;

@Component
public class EmailTokenLoader {

	private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	private ReactiveRedisConnectionFactory factory;

	private ReactiveRedisOperations<String, EmailToken> emailTokenOps;

	public EmailTokenLoader(ReactiveRedisConnectionFactory factory,
		ReactiveRedisOperations<String, EmailToken> emailTokenOps) {
		this.factory = factory;
		this.emailTokenOps = emailTokenOps;
	}

	@PostConstruct
	public void loadData() {
		factory.getReactiveConnection().serverCommands().flushAll()
			.thenMany(Flux.just("Jet Black Redis", "Darth Redis", "Black Alert Redis")
				.map(name -> EmailToken.builder().email(name).token(makeRandomString(6)).build())
				.flatMap(emailToken -> emailTokenOps.opsForValue().set(emailToken.getEmail(), emailToken)))
			.thenMany(emailTokenOps.keys("*")
				.flatMap(emailTokenOps.opsForValue()::get))
			.subscribe(System.out::println);
	}

	public String makeRandomString(int length) {
		Random random = new Random();
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			int randomIndex = random.nextInt(CHARACTERS.length());
			char randomChar = CHARACTERS.charAt(randomIndex);
			sb.append(randomChar);
		}

		return sb.toString();
	}
}
