package com.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entity.CustomerEntity;
import com.repository.CustomerRepository;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
	
	@Autowired
	CustomerRepository customerRepository;
	
	//read all customer
	@GetMapping
	public ResponseEntity<List<CustomerEntity>> getAllCustomer() {
	 List<CustomerEntity> customers = customerRepository.findAll();
	 return ResponseEntity.ok(customers);
	}
	
	//customer get by it id
	@GetMapping("{customerId}")
	public ResponseEntity<CustomerEntity> getCustomerById(@PathVariable("customerId") Integer customerId) {
		Optional<CustomerEntity> op = customerRepository.findById(customerId);
		if(op.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		else {
			CustomerEntity customerEntity = op.get();
			return ResponseEntity.ok(customerEntity);
//			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}
	}
	
	//customer delete by it id
	@DeleteMapping("{customerId}")
	public String deleteCustomer(@PathVariable("customerId") Integer customerId) {
		customerRepository.deleteById(customerId);
		return "sucess";
	}
	
	
	//update customer by it id
	@PutMapping
	public ResponseEntity<?> updateCustomer(@RequestBody CustomerEntity customerEntity){
		Optional<CustomerEntity> op = customerRepository.findById(customerEntity.getCustomerId());
		if(op.isEmpty()) {
			return ResponseEntity.ok("Invalid CustomerId");
		}
		else {
			customerRepository.save(customerEntity);
			return ResponseEntity.ok(customerEntity);
		}
	}
	
}
