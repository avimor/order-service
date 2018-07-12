package com.bringg.interview.ordsvc.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * @author avim
 * @since 30/06/2018
 * @version $Id$
 */
@Data
@Builder
public class Order implements Serializable {

	private static final long serialVersionUID = -2613102874946972848L;

	@NonNull
	private String title;
	@NonNull
	private String details;
	private Customer customer;
	private String customer_id;

}
