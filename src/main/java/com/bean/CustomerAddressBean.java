package com.bean;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerAddressBean {
	
	Integer customerId;
	String title;
	String addressLine;
	String pincode;
	String lat;
	String log;
}
