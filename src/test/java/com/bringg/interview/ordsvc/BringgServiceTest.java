package com.bringg.interview.ordsvc;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bringg.interview.ordsvc.model.Customer;
import com.bringg.interview.ordsvc.model.Order;

/**
 * @author avim
 * @since 03/07/2018
 * @version $Id$
 */
public class BringgServiceTest {

	static final Logger log = LoggerFactory.getLogger(BringgServiceTest.class);
	static final Customer TEST_CUSTOMER = new Customer("7894561", "Test Name", "Test Address");
	static final Order TEST_ORDER = Order.builder().title("Test Title").details("Test Details").customer(TEST_CUSTOMER).build();

	BringgService bringgService = new BringgApiService();

	@Test
	public void testCreateCustomer() {
		bringgService.createCustomer(TEST_CUSTOMER).log().block();
	}

	@Test
	public void testCreateOrder() {
		bringgService.createOrder(TEST_ORDER).log().block();
	}

	@Test
	public void testGetOrders() {
		bringgService.getOrders(TEST_CUSTOMER.getPhone(), 0).log().blockLast();
	}

}
