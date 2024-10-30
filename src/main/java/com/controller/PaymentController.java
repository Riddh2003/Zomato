package com.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bean.PaymentBean;
import com.bean.PaymentResponseBean;
import com.entity.CartEntity;
import com.entity.CustomerEntity;
import com.entity.OrderEntity;
import com.repository.CartRepository;
import com.repository.CustomerRepository;
import com.repository.OrderRepository;
import com.service.PaymentService;

@RestController
@RequestMapping("/api/private/payment")
public class PaymentController {
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	CartRepository cartRepository;
	
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	PaymentService paymentService;
	
	@PostMapping("/checkout")
	public ResponseEntity<?> checkout(@RequestBody PaymentBean paymentBean){
		if("COD".equalsIgnoreCase(paymentBean.getPaymentType())) {
			OrderEntity order = createOrder(paymentBean, null);
			orderRepository.save(order);
		}
		else if ("Credit Card".equalsIgnoreCase(paymentBean.getPaymentType())) {
			PaymentResponseBean paymentResponseBean = processPayment(paymentBean) ;
			if(paymentResponseBean.isSuccess()) {
				OrderEntity order = createOrder(paymentBean, paymentResponseBean);
				orderRepository.save(order);
				return ResponseEntity.ok("Payment successful and order created successfully...");
			}
			else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment Failed : "+paymentResponseBean.getMessage());
			}
		}
		return ResponseEntity.ok("Invalid Payment Method...");
	}
	
	private PaymentResponseBean processPayment(PaymentBean paymentBean) {
		return paymentService.run(paymentBean);
	}
	
	private OrderEntity createOrder(PaymentBean paymentBean,PaymentResponseBean paymentResponseBean) {
		Optional<CustomerEntity> cuOptional = customerRepository.findById(paymentBean.getCustomerId());
		CustomerEntity customer = cuOptional.get();
		Optional<CartEntity> cartOptional = cartRepository.findById(paymentBean.getCartId());
		CartEntity cart = cartOptional.get();
		if(cuOptional.isEmpty() && cartOptional.isEmpty()) {
			return null;
		}
		DateTimeFormatter date = DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy");
		
		OrderEntity order = new OrderEntity();
		order.setCustomer(customer);
		order.setCart(cart);
		order.setTotalPaid(paymentBean.getTotalAmount());
		order.setOrderDate(LocalDateTime.now().format(date));
		order.setStatus(1);
		
		if(paymentResponseBean != null) {
			order.setAuthCode(paymentResponseBean.getAuthCode());
			order.setTransactionId(paymentResponseBean.getTransactionId());
			order.setPaymentType("Credit Card");
		}
		else {
			order.setPaymentType("COD");
		}
		return order;
	}
}
