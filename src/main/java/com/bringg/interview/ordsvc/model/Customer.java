package com.bringg.interview.ordsvc.model;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;

/**
 * @author avim
 * @since 30/06/2018
 * @version $Id$
 */
@Data
public class Customer implements Serializable {

	private static final long serialVersionUID = 5133556856703975473L;

	@NonNull
	private String phone;
	@NonNull
	private String name;
	@NonNull
	private String address;
	private String email;

}
