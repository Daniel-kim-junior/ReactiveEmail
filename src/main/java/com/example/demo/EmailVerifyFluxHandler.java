package com.example.demo;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.demo.domain.EmailToken;
import com.example.demo.domain.EmailVerifyReqDto;
import com.example.demo.service.EmailService;

import reactor.core.publisher.Mono;

@Component
public class EmailVerifyFluxHandler {

	private final ReactiveRedisOperations<String, EmailToken> redisOperations;

	private final EmailService emailService;

	public EmailVerifyFluxHandler(ReactiveRedisOperations<String, EmailToken> redisOperations, EmailService emailService) {
		this.redisOperations = redisOperations;
		this.emailService = emailService;
	}

	public Mono<ServerResponse> emailVerify(ServerRequest request) {
		Mono<EmailVerifyReqDto> dto = request.bodyToMono(EmailVerifyReqDto.class);

		return dto.flatMap(emailDto -> {
			Mono<Void> sendMono = emailService.sendEmailAndVerifyReady(Mono.just(emailDto));
			return sendMono.then(Mono.just(emailDto));
		}).doOnNext(emailDto -> {
			validateEmailToken(emailDto);
		}).flatMap(emailDto -> ServerResponse.ok().bodyValue("Email verification is in progress."));
			// .onErrorResume(ConstraintViolationException.class, e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
	}

	private void validateEmailToken(@Valid EmailVerifyReqDto dto) {}
}
