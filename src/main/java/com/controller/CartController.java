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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.entity.CartEntity;
import com.entity.CartItemEntity;
import com.entity.CustomerEntity;
import com.entity.MenuItemEntity;
import com.entity.RestaurantEntity;
import com.repository.CartRepository;
import com.repository.CustomerRepository;
import com.repository.MenuItemRepository;
import com.repository.RestaurantRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/cart")
public class CartController {
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	RestaurantRepository restaurantRepository;
	
	@Autowired
	MenuItemRepository menuItemRepository;
	
	@Autowired
	CartRepository cartRepository;
	
	@PostMapping
	public ResponseEntity<?> addtoCart(@RequestParam Integer itemId,
			@RequestParam Integer qty,
			HttpSession session){
		Integer loginCustomerId = (Integer)session.getAttribute("customerId");
		if(loginCustomerId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please log in as a customer.");
		}
		
		//fetch Customer Data
		Optional<CustomerEntity> customerOptional = customerRepository.findById(loginCustomerId);
		if(customerOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found.");
		}
		CustomerEntity customerEntity = customerOptional.get();
		
		//fetch menuItem Data
		Optional<MenuItemEntity> menuItemOptional = menuItemRepository.findById(itemId);
		if(menuItemOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Menu item not found.");
		}
		MenuItemEntity menuItemEntity = menuItemOptional.get();
		
		//check if menu item is active
		if(!menuItemEntity.getActive()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot add inactive menu item.");
		}
		
		//fetch the restaurant associated with the menu item
		RestaurantEntity restaurantEntity = menuItemEntity.getRestaurantEntity();
		if(!restaurantEntity.getActive()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The restaurant is not active.");
		}
		
		//find or create cart for current customer
		Optional<CartEntity> cart = Optional.of(cartRepository.findByCustomerEntityAndRestaurantEntity(customerEntity,restaurantEntity)
				.orElseGet(()->{
					CartEntity newCartEntity = new CartEntity();
					newCartEntity.setCustomerEntity(customerEntity);
					newCartEntity.setRestaurantEntity(restaurantEntity);
					newCartEntity.setCartItems(new ArrayList<>());
//					newCartEntity.setTotalQty(qty);
					return newCartEntity;					
				}));
		CartEntity cartEntity = cart.get();
		//check if the item already in the cart
		Optional<CartItemEntity> cartItemOptional = cartEntity.getCartItems().stream()
				.filter(cartItem -> cartItem.getMenuItemEntity().getItemId().equals(itemId))
				.findFirst();
		if(cartItemOptional.isPresent()) {
			CartItemEntity cartItemEntity = cartItemOptional.get();
			cartItemEntity.setQty(cartItemEntity.getQty() + qty);
		}
		else {
			//add new item to the cart
			CartItemEntity newCartItemEntity = new CartItemEntity();
			newCartItemEntity.setMenuItemEntity(menuItemEntity);
			newCartItemEntity.setCartEntity(cartEntity);
			newCartItemEntity.setMenuEntity(menuItemEntity.getMenuEntity());
			newCartItemEntity.setQty(qty);
			
			cartEntity.getCartItems().add(newCartItemEntity);
		}
		//save to the cart
		cartRepository.save(cartEntity);
		
		
		// Build a simplified response
	    Map<String, Object> cartResponse = new HashMap<>();
	    cartResponse.put("cartId", cartEntity.getCartId());
	    cartResponse.put("restaurantId", cartEntity.getRestaurantEntity().getRestaurantId());

	    List<Map<String, Object>> cartItemsResponse = cartEntity.getCartItems().stream().map(cartItem -> {
	        Map<String, Object> cartItemData = new HashMap<>();
	        cartItemData.put("cartItemId", cartItem.getCartItemId());
	        cartItemData.put("menuId", cartItem.getMenuEntity().getMenuId());
	        cartItemData.put("menuItemId", cartItem.getMenuItemEntity().getItemId());
	        cartItemData.put("menuItemTitle", cartItem.getMenuItemEntity().getTitle());
	        cartItemData.put("quantity", cartItem.getQty());
	        cartItemData.put("price", cartItem.getMenuItemEntity().getPrice() * cartItem.getQty());
	        return cartItemData;
	    }).collect(Collectors.toList());

	    cartResponse.put("cartItems", cartItemsResponse);

	    // Return the response with relevant cart data
	    Map<String, Object> response = new HashMap<>();
	    response.put("message", "Item added to cart successfully.");
	    response.put("cart", cartResponse);

		return ResponseEntity.ok(response);
	}
}
