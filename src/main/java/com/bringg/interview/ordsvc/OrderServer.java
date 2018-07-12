package com.bringg.interview.ordsvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @author avim
 * @since 01/07/2018
 * @version $Id$
 */
@SpringBootApplication
public class OrderServer {

	public static void main(String[] args) {
		SpringApplication.run(OrderServer.class);
	}

	@Bean
	@SuppressWarnings("static-method")
	public RouterFunction<ServerResponse> monoRouterFunction(EchoHandler echoHandler) {
		return route(POST("/echo"), echoHandler::echo);
	}

}
