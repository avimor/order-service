package com.bringg.interview.ordsvc;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import com.bringg.interview.ordsvc.model.Customer;
import com.bringg.interview.ordsvc.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.apache.commons.codec.digest.HmacAlgorithms.HMAC_SHA_1;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;

/**
 * @author avim
 * @since 03/07/2018
 * @version $Id$
 */
@Service
public class BringgApiService implements BringgService {

	private static final Logger log = LoggerFactory.getLogger(BringgApiService.class);
	private static final ParameterizedTypeReference<Map<String, ?>> RES_TYPE = new ParameterizedTypeReference<>() {};
	private static final String TASKS_PATH = "/tasks";

	private final WebClient webClient;
	private final ObjectMapper objectMapper;
	private final HmacUtils hmacUtils;

	public BringgApiService() {
		webClient = WebClient.builder().baseUrl("http://developer-api.bringg.com/partner_api").build();
		objectMapper = new ObjectMapper().setSerializationInclusion(NON_NULL).disable(FAIL_ON_EMPTY_BEANS);
		hmacUtils = new HmacUtils(HMAC_SHA_1, "V_-es-3JD82YyiNdzot7");
	}

	@Override
	public Mono<Map<String, ?>> createCustomer(Customer customer) {
		return webClient.post().uri("/customers").accept(APPLICATION_JSON_UTF8).syncBody(sign(customer)).retrieve().bodyToMono(RES_TYPE).single().doOnNext(this::validate);
	}

	@Override
	public Mono<Map<String, ?>> createOrder(Order order) {
		return createCustomer(order.getCustomer()).flatMap(c -> createOrder(order, c));
	}

	@Override
	public Flux<Map<String, ?>> getOrders(String phone, int page) {
		Map<String, Object> params = Map.of("phone", phone, "page", page);
		return webClient.get().uri(b -> b.path(TASKS_PATH).queryParams(toMultiValueMap(sign(params))).build()).accept(APPLICATION_JSON_UTF8).retrieve().bodyToFlux(RES_TYPE)
			.filter(t -> customersLastWeek(t, phone));
	}

	@SuppressWarnings("unchecked")
	protected Map<String, ?> sign(Object request) {
		Map<String, Object> params;
		if(request == null) {
			params = new HashMap<>();
		} else if(Map.class.isAssignableFrom(request.getClass())) {
			params = new HashMap<>((Map<String, Object>)request);
		} else {
			params = objectMapper.convertValue(request, Map.class);
		}
		params.put("company_id", 11010);
		params.put("access_token", "ZtWsDxzfTTkGnnsjp8yC");
		params.put("timestamp", System.currentTimeMillis());
		StringBuilder query = new StringBuilder();
		params.forEach((k, v) -> {
			if(query.length() > 0) query.append('&');
			try {
				query.append(k).append('=').append(URLEncoder.encode(String.valueOf(v), "UTF-8"));
			} catch(UnsupportedEncodingException ex) {
				throw new RuntimeException("Error encoding value " + v + " for key " + k, ex);
			}
		});
		params.put("signature", hmacUtils.hmacHex(query.toString()));
		log.info("Generated params: {}", params);
		return params;
	}

	protected void validate(Map<String, ?> response) {
		if(!Boolean.parseBoolean(String.valueOf(response.get("success")))) throw new RuntimeException("Response didn't return success: " + response);
	}

	protected Mono<Map<String, ?>> createOrder(Order order, Map<String, ?> bringgCustomer) {
		String customerId = null;
		try {
			customerId = PropertyUtils.getProperty(bringgCustomer, "customer.id").toString();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		if(StringUtils.isBlank(customerId)) throw new IllegalArgumentException("Error extracting customer id from " + bringgCustomer);
		order.setCustomer_id(customerId);
		order.setCustomer(null);
		return webClient.post().uri(TASKS_PATH).accept(APPLICATION_JSON_UTF8).syncBody(sign(order)).retrieve().bodyToMono(RES_TYPE).single().doOnNext(this::validate);
	}

	protected static MultiValueMap<String, String> toMultiValueMap(Map<String, ?> params) {
		MultiValueMap<String, String> result = new LinkedMultiValueMap<>();
		params.forEach((k, v) -> result.add(k, String.valueOf(v)));
		return result;
	}

	protected static boolean customersLastWeek(Map<String, ?> task, String phone) {
		String taskPhone = null;
		ZonedDateTime createdAt = null;
		try {
			taskPhone = (String)PropertyUtils.getProperty(task, "customer.phone");
			createdAt = ZonedDateTime.parse((CharSequence)task.get("created_at"));
		} catch(Exception ex) {
			log.warn("Error extracting phone/created_at from {}", task, ex);
			return false;
		}
		return taskPhone != null && taskPhone.contains(phone) && createdAt != null && createdAt.isAfter(ZonedDateTime.now().minusDays(7));
	}

}
