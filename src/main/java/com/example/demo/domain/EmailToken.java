package com.example.demo.domain;

import static lombok.AccessLevel.*;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@RedisHash
@Getter
@ToString
@NoArgsConstructor(access = PROTECTED)
public class EmailToken {
	@Id
	private String email;

	private String token;

	@Builder
	public EmailToken(String email, String token) {
		this.email = email;
		this.token = token;
	}
}
