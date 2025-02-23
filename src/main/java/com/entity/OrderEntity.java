package com.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "orders")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderEntity {
	
	@Id
	Integer orderId;
	
//	customerId;
	@ManyToOne
	@JoinColumn(name = "customerId")
	@JsonBackReference
	CustomerEntity customer;
	
//	cartId;
	@ManyToOne
	@JoinColumn(name = "cartId")
	@JsonBackReference
	CartEntity cart;
	
	Double totalPaid;
	String authCode;
	String transactionId;
	Integer status;
	String paymentType;
	String orderDate;
}
