package com.example.demo.domain;

import static lombok.AccessLevel.*;

import javax.validation.constraints.Email;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class EmailVerifyReqDto {
	@Email
	private String email;

	@Builder
	public EmailVerifyReqDto(String email) {
		this.email = email;
	}
}
