package com.bean;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentBean {
	Integer paymentId;
    Integer customerId;
    Integer cartId;
    String authCode;
    String paymentType;
    String transactionId;
    String cardNumber;
    String expiryDate;
    Double totalAmount;
}
