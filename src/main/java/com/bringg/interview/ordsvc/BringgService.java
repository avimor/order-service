package com.bringg.interview.ordsvc;

import java.util.Map;

import com.bringg.interview.ordsvc.model.Customer;
import com.bringg.interview.ordsvc.model.Order;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author avim
 * @since 03/07/2018
 * @version $Id$
 */
public interface BringgService {

	Mono<Map<String, ?>> createCustomer(Customer customer);

	Mono<Map<String, ?>> createOrder(Order order);

	Flux<Map<String, ?>> getOrders(String phone, int page);

}
