package com.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entity.CustomerEntity;
import com.bean.LoginBean;
import com.entity.RestaurantEntity;
import com.repository.CustomerRepository;
import com.repository.RestaurantRepository;
import com.service.Otpservice;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/session")
public class SessionController {
		
	@Autowired
	RestaurantRepository restaurantRepository;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	Otpservice otpservice;
	
	@Autowired
	JavaMailSender sender;
		
	//create restaurant in database or SignUp
	@PostMapping("/restaurant")
	public String addRestaurant(@RequestBody RestaurantEntity restaurantEntity) {
		restaurantEntity.setActive(1);
		restaurantRepository.save(restaurantEntity);
		return "Success";
	}
	
	//create customer or SignUp
	@PostMapping("/customer")
	public String addCustomer(@RequestBody CustomerEntity customerEntity) {
//		System.out.println(customerEntity.getFirstName());
//		System.out.println(customerEntity.getLastName());
		customerRepository.save(customerEntity);
		return "Success";
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginBean loginBean,HttpSession session) {
		String role = loginBean.getRole().toLowerCase();
		String email = loginBean.getEmail();
		String password = loginBean.getPassword();
		
		switch (role) {
			case "customer":
				return customerRepository.findByEmailAndPassword(email, password)
						.map(CustomerEntity->{
							session.setAttribute("customerId", CustomerEntity.getCustomerId());
							session.setAttribute("role", "customer");
							return ResponseEntity.ok("Login Successful as Customer"); 
						})
						.orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invaild Credentials for Customer"));
			case "restaurant":
				return restaurantRepository.findByEmailAndPassword(email, password)
						.map(RestaurantEntity->{
							session.setAttribute("restaurantId", RestaurantEntity.getRestaurantId());
							session.setAttribute("role", "restaurant");
							return ResponseEntity.ok("Login Successful as Restaurant");
						})
						.orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invaild Credentials for Restaurant"));
			default:
				return ResponseEntity.badRequest().body("Invaild Role");
		}
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpSession session){
		session.invalidate();
		return ResponseEntity.ok("Logout Sucessfully");
	}
	
	@PostMapping("/sendotp")
	public ResponseEntity<?> sendOtp(@RequestBody LoginBean loginBean,HttpSession session){
		String email= loginBean.getEmail();
		String role = loginBean.getRole();
		Object value = null;
		switch (role) {
			case "customer": 
				value = customerRepository.findByEmail(email);
				break;
			case "restaurant":
				value = (Object)restaurantRepository.findByEmail(email);
				break;
			default:
				return ResponseEntity.badRequest().body("Invalid role");
		}
		
		if(value == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User Not Found!!");
		}
		
		String otp = otpservice.otpGenerater();
		SimpleMailMessage message = new SimpleMailMessage();
	    message.setTo(email);
	    message.setSubject("OTP SEND BY ZOMATO");
	    message.setText("OTP is: "+otp);
	    sender.send(message);
	    
	    session.setAttribute("otp", otp);
	    session.setAttribute("role", role);
	   
	    return ResponseEntity.ok("OTP sent successfully");
	}
	
	@PostMapping("/updatepassword")
	public ResponseEntity<?> updatepassword(@RequestBody LoginBean loginBean,HttpSession session){
		String email = loginBean.getEmail();
		String role = loginBean.getRole().toLowerCase();
		String newpassword = loginBean.getPassword();
		String otp = loginBean.getOtp();
		
		String storerole = (String)session.getAttribute("role");
		String storeotp = (String)session.getAttribute("otp");
		
		if(storeotp == null || !storeotp.equals(otp)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP!!");
		}
		
		if(storerole == null || !storerole.equals(role)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Role not matched!!");
		}
		
		switch (role) {
			case "customer":
				Optional<CustomerEntity> op = customerRepository.findByEmail(email);
				if(op == null) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Not Found.");
				}
				CustomerEntity customer = op.get();
				customer.setPassword(newpassword);
				customerRepository.save(customer);
				break;
			case "restaurant":
				Optional<RestaurantEntity> op1 = restaurantRepository.findByEmail(email);
				if(op1 == null) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Not Found.");
				}
				RestaurantEntity restaurant = op1.get();
				restaurant.setPassword(newpassword);
				restaurantRepository.save(restaurant);
				break;
			default:
				return ResponseEntity.badRequest().body("Invalid Role.");
		}
		
		session.removeAttribute("otp");
		session.removeAttribute("role");
		
		return ResponseEntity.ok("Password updated successfully");
	}
}
