package com.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bean.CustomerAddressBean;
import com.entity.CustomerAddressEntity;
import com.entity.CustomerEntity;
import com.repository.CustomerAddressRepository;
import com.repository.CustomerRepository;
import com.service.CustomerAddressService;
import com.service.CustomerService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/customeraddress")
public class CustomerAddressController{
	
	
	@Autowired
	CustomerAddressRepository customerAddressRepository;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	CustomerAddressService customerAddressService;
	
	
	//create address for customer
	@PostMapping
	public ResponseEntity<?> addCustomerAddress(@RequestBody CustomerAddressBean customerAddressBean,HttpSession session){
		Integer loginCustomerId = (Integer)session.getAttribute("customerId");
		String str = (String)customerService.checkLoginorNot((Integer)customerAddressBean.getCustomerId(),loginCustomerId);
		if(!str.equalsIgnoreCase("success")) {
			return ResponseEntity.badRequest().body(str);
		}
		Optional<CustomerEntity> op = customerRepository.findById(loginCustomerId);
		CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
		customerAddressEntity.setCustomerEntity(op.get());
		customerAddressEntity.setTitle(customerAddressBean.getTitle());
		customerAddressEntity.setAddressLine(customerAddressBean.getAddressLine());
		customerAddressEntity.setPincode(customerAddressBean.getPincode());
		customerAddressEntity.setLog(customerAddressBean.getLog());
		customerAddressEntity.setLat(customerAddressBean.getLat());
		
		customerAddressRepository.save(customerAddressEntity);
		return ResponseEntity.ok("Address added successfully.");
	}
	
	//read all the Address
	@GetMapping("{customerId}")
	public ResponseEntity<?> getAllAddress(@PathVariable Integer customerId,HttpSession session){
		Integer loginCustomerId = (Integer)session.getAttribute("customerId");
		if(!customerId.equals(loginCustomerId)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized. Please log in first.");
		}
		Optional<CustomerEntity> op = customerRepository.findById(customerId);
		if(op.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found.");
		}
		CustomerEntity customerEntity = op.get();
		List<CustomerAddressEntity> addresses = customerEntity.getAddresses();
		
		if(addresses.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Address found for this customer.");
		}
		
		return ResponseEntity.ok(addresses);
	}
	
	@GetMapping("customer/{customerId}/address/{addressId}")
	public ResponseEntity<?> getAddressById(@PathVariable Integer addressId,@PathVariable Integer customerId, HttpSession session) {
		Integer loginCustomerId = (Integer) session.getAttribute("customerId");
		String authenticateString = customerAddressService.checkCustomerandAddress(addressId, loginCustomerId);
		if(!authenticateString.equalsIgnoreCase("success")) {
			return ResponseEntity.badRequest().body(authenticateString);
		}
		Optional<CustomerAddressEntity> op = customerAddressRepository.findById(addressId);
	    CustomerAddressEntity customerAddress = op.get();
	    return ResponseEntity.ok(customerAddress);
	}
	
	@PutMapping("/customer/{customerId}/address/{addressId}")
	public ResponseEntity<?> updateAddressById(@PathVariable Integer customerId,
			@PathVariable Integer addressId,
			@RequestBody CustomerAddressBean customerAddressBean, 
			HttpSession session){
		Integer loginCustomerId = (Integer) session.getAttribute("customerId");
		String str = customerAddressService.updateCustomerAddress(customerAddressBean,customerId, loginCustomerId,addressId);
		if(!str.equalsIgnoreCase("success")) {
			return ResponseEntity.badRequest().body(str);
		}
		return ResponseEntity.ok("Address updated successfully.");
	}
	
	@DeleteMapping("/address/{addressId}")
	public ResponseEntity<?> softDeleteAddress(@PathVariable Integer addressId, HttpSession session){
		Integer loginCustomerId = (Integer) session.getAttribute("customerId");
		String authenticateString = customerAddressService.checkCustomerandAddress(addressId, loginCustomerId);
		if(!authenticateString.equalsIgnoreCase("success")) {
			return ResponseEntity.badRequest().body(authenticateString);
		}
		Optional<CustomerAddressEntity> op = customerAddressRepository.findById(addressId);
	    CustomerAddressEntity customerAddress = op.get();
	    customerAddress.setIsactive(false);
	    customerAddressRepository.save(customerAddress);
		return ResponseEntity.ok("Address is soft-deleted successfully.");
	}
}
