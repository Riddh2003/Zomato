package com.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entity.CartEntity;
import com.entity.CartItemEntity;
import com.entity.CustomerEntity;
import com.entity.MenuEntity;
import com.entity.MenuItemEntity;
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
	
	@Autowired
	BCryptPasswordEncoder encoder;
		
	//create restaurant in database or SignUp
	@PostMapping("/restaurant")
	public String addRestaurant(@RequestBody RestaurantEntity restaurantEntity) {
		restaurantEntity.setPassword(encoder.encode(restaurantEntity.getPassword()));
		restaurantRepository.save(restaurantEntity);
		return "Success";
	}
	
	//create customer or SignUp
	@PostMapping("/customer")
	public String addCustomer(@RequestBody CustomerEntity customerEntity) {
//		System.out.println(customerEntity.getFirstName());
//		System.out.println(customerEntity.getLastName());
		customerEntity.setPassword(encoder.encode(customerEntity.getPassword()));
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
						.map((CustomerEntity customerEntity) -> {
						    session.setAttribute("customerId", customerEntity.getCustomerId());
						    session.setAttribute("role", "customer");
						    
						    // Fetch active restaurants
						    Optional<List<RestaurantEntity>> op = restaurantRepository.findByActive(true);
						    List<RestaurantEntity> restaurants = op.get();
						    
						    // Prepare data for each restaurant
						    List<Map<String, Object>> restaurantDataList = new ArrayList<>();
						    for (RestaurantEntity restaurant : restaurants){
						        
						        // Filter active menus
						        List<MenuEntity> menus = restaurant.getMenus().stream()
						                .filter(MenuEntity::isActive)
						                .collect(Collectors.toList());
						        
						        List<Map<String, Object>> menuDataList = new ArrayList<>();
						        for (MenuEntity menu : menus) {
						            
						            // Filter active menu items
						            List<MenuItemEntity> menuItems = menu.getMenuItems().stream()
						                    .filter(MenuItemEntity::getActive)
						                    .collect(Collectors.toList());
						            
						            if (!menuItems.isEmpty()) {
						                Map<String, Object> menuData = new HashMap<>();
						                menuData.put("menu", menu);
						                menuData.put("menuItems", menuItems);
						                menuDataList.add(menuData);
						            }
						        }

						        // Prepare restaurant data
						        Map<String, Object> restaurantData = new HashMap<>();
						        if (menuDataList.isEmpty()) {
						            restaurantData.put("MenuMessage", "Menu is Empty. So, you can't order from " + restaurant.getTitle() + " Restaurant.");
						        }
						        restaurantData.put("restaurant", restaurant);
						        
						        // Include cart and cart items for the customer and restaurant
						        List<CartEntity> customerCarts = restaurant.getCarts().stream()
						                .filter(cart -> cart.getCustomerEntity().getCustomerId().equals(customerEntity.getCustomerId()))
						                .collect(Collectors.toList());
						        
						        List<Map<String, Object>> cartDataList = new ArrayList<>();
						        for (CartEntity cart : customerCarts) {
						            List<CartItemEntity> cartItems = cart.getCartItems();
						            if (!cartItems.isEmpty()) {
						                Map<String, Object> cartData = new HashMap<>();
						                cartData.put("cartId", cart.getCartId());
						                cartData.put("cartItems", cartItems);
						                cartData.put("customerId", cart.getCustomerEntity().getCustomerId());
						                cartData.put("restaurantId", cart.getRestaurantEntity().getRestaurantId());
						                cartDataList.add(cartData);
						            }
						        }

						        // Add cart data to the restaurant data if exists
						        if (cartDataList.isEmpty()) {
						            restaurantData.put("CartMessage", "No cart items for " + restaurant.getTitle() + " Restaurant.");
						        } else {
						            restaurantData.put("carts", cartDataList);
						        }
						        
						        restaurantDataList.add(restaurantData);
						    }
						    
						    // Prepare final response
						    Map<String, Object> response = new HashMap<>();
						    response.put("message", "Login Successful as customer.");
						    response.put("restaurants", restaurantDataList);
						    return ResponseEntity.ok(response);
						})
						.orElseGet(()->{
								Map<String,Object> errorResponse = new HashMap<>();
								errorResponse.put("message", "Invaild Credentials for Customer.");
								return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
						});
			case "restaurant":
				return restaurantRepository.findByEmailAndPassword(email, password)
						.map(RestaurantEntity -> {
						    // Set restaurant session attributes
						    session.setAttribute("restaurantId", RestaurantEntity.getRestaurantId());
						    session.setAttribute("role", "restaurant");

						    // Get active menus of the restaurant
						    List<MenuEntity> activeMenus = RestaurantEntity.getMenus().stream()
						        .filter(MenuEntity::isActive) // Only active menus
						        .collect(Collectors.toList());

						    // Prepare data for active menus and their active items
							List<Map<String, Object>> menuDataList = activeMenus.stream().map(menu -> {
						        List<MenuItemEntity> activeMenuItems = menu.getMenuItems().stream()
						            .filter(MenuItemEntity::getActive) // Only active items
						            .collect(Collectors.toList());

						        Map<String, Object> menuData = new HashMap<>();
						        menuData.put("menu", menu);
						        menuData.put("menuItems", activeMenuItems);
						        return menuData;
						    }).collect(Collectors.toList());

						    // Prepare restaurant data without cart details
							Map<String, Object> restaurantData = new HashMap<>();
						    restaurantData.put("LoginMessage", "Login successfully as Restaurant.");
						    if(menuDataList.isEmpty()) {
						    	restaurantData.put("MenuMessage","Menu is Empty.So, You can add the Menu Details.");
						    }
						    restaurantData.put("restaurant", RestaurantEntity);

						    return ResponseEntity.ok(restaurantData);
						})
						.orElseGet(()->{
							Map<String,Object> errorResponse = new HashMap<>();
							errorResponse.put("message", "Invaild Credentials for Restaurant.");
							return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
					});
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
