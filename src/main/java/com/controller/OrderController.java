package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.repository.OrderRepository;

@RestController
@RequestMapping("/api/private/order")
public class OrderController {
	
	@Autowired
	OrderRepository orderRepository;
	
	public ResponseEntity<?> getAllOrder(){
		return ResponseEntity.ok(orderRepository.findAll());
	}
}
