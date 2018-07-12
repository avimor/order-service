package com.bringg.interview.ordsvc;

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bringg.interview.ordsvc.model.Order;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author avim
 * @since 01/07/2018
 * @version $Id$
 */
@RestController
public class OrderController {

	private static final Logger log = LoggerFactory.getLogger(OrderController.class);
	protected static final String ORDER_PATH = "/order";

	@Resource
	private BringgService bringgService;

	@GetMapping("/")
	@SuppressWarnings("static-method")
	public String welcome() {
		return "Hello World";
	}

	@PostMapping(ORDER_PATH)
	public Mono<Map<String, ?>> createOrder(@RequestBody Order order) {
		log.info("Got request: {}", order);
		return bringgService.createOrder(order).log();
	}

	@GetMapping(ORDER_PATH)
	public Flux<Map<String, ?>> getOrders(@RequestParam String phone, @RequestParam(defaultValue = "0") int page) {
		log.info("Got request: {}", phone);
		return bringgService.getOrders(phone, page).log();
	}

}
