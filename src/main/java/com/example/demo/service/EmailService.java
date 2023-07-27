package com.example.demo.service;

import java.util.Random;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.demo.domain.EmailToken;
import com.example.demo.domain.EmailVerifyReqDto;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class EmailService {
	private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	private static final String EMAIL_TITLE = "Email Verification";

	private ReactiveRedisConnectionFactory factory;

	private ReactiveRedisOperations<String, EmailToken> emailTokenOps;

	private JavaMailSender mailSender;


	public EmailService(ReactiveRedisConnectionFactory factory,
		ReactiveRedisOperations<String, EmailToken> emailTokenOps,JavaMailSender mailSender) {
		this.factory = factory;
		this.emailTokenOps = emailTokenOps;
		this.mailSender = mailSender;
	}

	public Mono<Void> sendEmailAndVerifyReady(Mono<EmailVerifyReqDto> dto) {
		return dto.flatMap(emailDto -> {
			String email = emailDto.getEmail();
			String verificationToken = makeRandomString(10);
			return sendMail(email, verificationToken)
				.then(saveEmailTokenToRedis(email, verificationToken));
		}).subscribeOn(Schedulers.boundedElastic());
	}
	public Mono<Void> saveEmailTokenToRedis(String email, String verificationToken) {
		EmailToken emailToken = EmailToken.builder()
			.email(email)
			.token(verificationToken)
			.build();
		return emailTokenOps.opsForValue().set(email, emailToken).then();
	}


	public Mono<Void> sendMail(String email, String sendEmailToken) throws MailSendException {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject(EMAIL_TITLE);
		message.setText(sendEmailToken);
		mailSender.send(message);
		return Mono.empty();
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
