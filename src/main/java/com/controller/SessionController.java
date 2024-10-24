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
import com.bean.CustomerBean;
import com.bean.LoginBean;
import com.bean.RestaurantBean;
import com.entity.RestaurantEntity;
import com.repository.CustomerRepository;
import com.repository.RestaurantRepository;
import com.service.Otpservice;
import com.service.TokenGeneraterService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/public/session")
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
	
	@Autowired
	TokenGeneraterService tokenGeneraterService;
	
	//create restaurant in database or SignUp
	@PostMapping("/restaurant")
	public ResponseEntity<?> addRestaurant(@RequestBody RestaurantBean restaurantBean) {
		//set in restaurantEntity
		RestaurantEntity restaurantEntity = new RestaurantEntity();
		
		restaurantEntity.setTitle(restaurantBean.getTitle());
		restaurantEntity.setCategory(restaurantBean.getCategory());
		restaurantEntity.setDescription(restaurantBean.getDescription());
		restaurantEntity.setTimings(restaurantBean.getTimings());
		restaurantEntity.setAddress(restaurantBean.getAddress());
		restaurantEntity.setContactNum(restaurantBean.getContactNum());
		restaurantEntity.setLat(restaurantBean.getLat());
		restaurantEntity.setLog(restaurantBean.getLog());
		restaurantEntity.setPincode(restaurantBean.getPincode());
		restaurantEntity.setOnline(restaurantBean.getOnline());
		restaurantEntity.setPassword(encoder.encode(restaurantBean.getPassword()));
		restaurantEntity.setEmail(restaurantBean.getEmail());
		restaurantEntity.setRestaurantImagePath(restaurantBean.getRestaurantImagePath());
		
		restaurantRepository.save(restaurantEntity);
		return ResponseEntity.ok("Successfully SignUp as Restaurant.");
	}
	
	//create customer or SignUp
	@PostMapping("/customer")
	public ResponseEntity<?> addCustomer(@RequestBody CustomerBean customerBean) {
		CustomerEntity customerEntity = new CustomerEntity();
		
		customerEntity.setFirstName(customerBean.getFirstName());
		customerEntity.setLastName(customerBean.getLastName());
		customerEntity.setEmail(customerBean.getEmail());
		customerEntity.setPassword(encoder.encode(customerBean.getPassword()));
		customerEntity.setProfilePicPath(customerBean.getProfilePicPath());
		customerEntity.setOtp(customerBean.getOtp());
		customerEntity.setGender(customerBean.getGender());
		customerEntity.setBornYear(customerBean.getBornYear());
		customerEntity.setContactNum(customerBean.getContactNum());
		
		customerRepository.save(customerEntity);
		return ResponseEntity.ok("Successfully SignUp as Customer.");
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginBean loginBean) {
		String role = loginBean.getRole().toLowerCase();
		String email = loginBean.getEmail();
		String password = loginBean.getPassword();
		String token = tokenGeneraterService.tokenGenerater();
		
		switch (role) {
			case "customer":
				return customerRepository.findByEmail(email)
						.map((CustomerEntity customerEntity) -> {
							String enpwd = customerEntity.getPassword();
							if(encoder.matches(password, enpwd) == true && email == customerEntity.getEmail()) {								
								//session.setAttribute("customerId", customerEntity.getCustomerId());
								//session.setAttribute("role", "customer");
								// Fetch active restaurants
								customerEntity.setToken(token);
								customerRepository.save(customerEntity);
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
							                cartData.put("customerId", cart.getCustomerEntity().getCustomerId());
							                cartData.put("restaurantId", cart.getRestaurantEntity().getRestaurantId());
							                
							                // Create a list to hold cart item details
							                List<Map<String, Object>> cartItemDetailsList = new ArrayList<>();
							                
							                for (CartItemEntity cartItem : cartItems) {
							                    Map<String, Object> cartItemDetails = new HashMap<>();
							                    cartItemDetails.put("cartItemId", cartItem.getCartItemId());
							                    cartItemDetails.put("menuId", cartItem.getMenuEntity().getMenuId());
							                    cartItemDetails.put("itemId", cartItem.getMenuItemEntity().getItemId());
							                    cartItemDetails.put("itemTitle", cartItem.getMenuItemEntity().getTitle());
							                    cartItemDetails.put("quantity", cartItem.getQty());
							                    cartItemDetails.put("price", cartItem.getMenuItemEntity().getPrice() * cartItem.getQty());
							                    
							                    // Add this item to the cartItemDetailsList
							                    cartItemDetailsList.add(cartItemDetails);
							                }       
							                // Add the list of cart item details to the cart data
							                cartData.put("cartItems", cartItemDetailsList);
							                
							                // Add the cart data to the main cartDataList
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
							    response.put("login customer", restaurantDataList);
							    return ResponseEntity.ok()
							    		.header("Authorization","Bearer "+token)
							    		.body(response);

							}else {
								Map<String,Object> error = new HashMap<>();
								error.put("message", "Invaild Credentials for Customer.");
								return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
							}
						})
						.orElseGet(()->{
								Map<String,Object> errorResponse = new HashMap<>();
								errorResponse.put("message", "Invaild Credentials for Customer.");
								return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
						});
			case "restaurant":
				return restaurantRepository.findByEmail(email)
						.map((RestaurantEntity restaurantEntity) -> {
							String enpwd = restaurantEntity.getPassword();
							if(encoder.matches(password, enpwd) == true && email == restaurantEntity.getEmail()) {								
								// Set restaurant session attributes
//								session.setAttribute("restaurantId", restaurantEntity.getRestaurantId());
//								session.setAttribute("role", "restaurant");	
								// Get active menus of the restaurant
								restaurantEntity.setToken(token);
								restaurantRepository.save(restaurantEntity);
							    List<MenuEntity> activeMenus = restaurantEntity.getMenus().stream()
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
							    restaurantData.put("restaurant", restaurantEntity);

							    return ResponseEntity.ok()
							    		.header("Authorization","Bearer " +token)
							    		.body(restaurantData);

							}else {
								Map<String,Object> error = new HashMap<>();
								error.put("message", "Invaild Credentials for Restaurant.");
								return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
							}
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
				value = (Object)customerRepository.findByEmail(email);
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
				customer.setPassword(encoder.encode(newpassword));
				customerRepository.save(customer);
				break;
			case "restaurant":
				Optional<RestaurantEntity> op1 = restaurantRepository.findByEmail(email);
				if(op1 == null) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Not Found.");
				}
				RestaurantEntity restaurant = op1.get();
				restaurant.setPassword(encoder.encode(newpassword));
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
