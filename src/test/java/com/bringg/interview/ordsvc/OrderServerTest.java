package com.bringg.interview.ordsvc;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.annotation.Timed;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.bringg.interview.ordsvc.model.Order;

import reactor.core.publisher.Mono;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.TEXT_PLAIN;

import static com.bringg.interview.ordsvc.BringgServiceTest.TEST_CUSTOMER;
import static com.bringg.interview.ordsvc.BringgServiceTest.TEST_ORDER;
import static com.bringg.interview.ordsvc.OrderController.ORDER_PATH;

/**
 * @author avim
 * @since 06/07/2018
 * @version $Id$
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class OrderServerTest {

	@Resource
	private WebTestClient webClient;

	@Test
	public void testWelcome() {
		this.webClient.get().uri("/").accept(TEXT_PLAIN).exchange().expectBody(String.class).isEqualTo("Hello World");
	}

	@Test
	public void testEcho() {
		this.webClient.post().uri("/echo").contentType(TEXT_PLAIN).accept(TEXT_PLAIN).body(Mono.just("Hello WebFlux!"), String.class).exchange().expectBody(String.class)
			.isEqualTo("Hello WebFlux!");
	}

	@Test
	public void testActuatorStatus() {
		this.webClient.get().uri("/actuator/health").accept(APPLICATION_JSON_UTF8).exchange().expectStatus().isOk().expectBody().json("{\"status\":\"UP\"}");
	}

	@Test
	@Timed(millis = 1200) // 400 for boot + 800 for request
	public void testCreateOrder() {
		this.webClient.post().uri(ORDER_PATH).contentType(APPLICATION_JSON_UTF8).accept(APPLICATION_JSON_UTF8).body(Mono.just(TEST_ORDER), Order.class).exchange().expectStatus().isOk();
	}

	@Test
	@Repeat(3)
	public void testGetOrders() {
		this.webClient.get().uri(b -> b.path(ORDER_PATH).queryParam("phone", TEST_CUSTOMER.getPhone()).build()).accept(APPLICATION_JSON_UTF8).exchange().expectStatus().isOk();
	}

}
