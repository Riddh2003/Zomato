package com.bean;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponseBean {
	String transactionId;
    String authCode;
    String responseCode;
    String message;
    boolean success;
}
