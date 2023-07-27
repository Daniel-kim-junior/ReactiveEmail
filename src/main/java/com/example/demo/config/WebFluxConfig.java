package com.example.demo.config;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.demo.EmailVerifyFluxHandler;

@Configuration
public class WebFluxConfig {
	@Bean
	public RouterFunction<ServerResponse> routes(EmailVerifyFluxHandler emailVerifyHandler) {
		return RouterFunctions.route(POST("/api/auth/email-verify"), emailVerifyHandler::emailVerify);
	}
}
