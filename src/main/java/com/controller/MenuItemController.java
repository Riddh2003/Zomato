package com.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.bean.MenuItemBean;
import com.entity.MenuEntity;
import com.entity.MenuItemEntity;
import com.entity.RestaurantEntity;
import com.repository.MenuItemRepository;
import com.repository.MenuRepository;
import com.repository.RestaurantRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/menuitem")
public class MenuItemController {
	/*
	 * create menuitem
	 * 		Integer menuId;
			Integer restaurantId;
			String title;
			String description;
			String itemaImagePath;
			Integer price;
			Integer offerQty;
			Integer offerPercentage;
			Integer offerQtyCount;
			Integer uptoAmt;
			Integer uptoAmtCondition;
	 * 
	 * 
	 * */
	
	@Autowired
	MenuItemRepository menuItemRepository;
	
	@Autowired
	MenuRepository menuRepository;
	
	@Autowired
	RestaurantRepository restaurantRepository;
	
	@PostMapping("{menuId}")
	public ResponseEntity<?> addmenuItem(@PathVariable Integer menuId,
			@RequestBody MenuItemBean menuItemBean,
			HttpSession session){
		Integer loginRestaurantId = (Integer)session.getAttribute("restaurantId");
		if(!menuItemBean.getRestaurantId().equals(loginRestaurantId)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to add menuItem.");
		}
		if(!menuItemBean.getMenuId().equals(menuId)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can't add menuItem in this menu. Please, Check menuId: "+menuId+".");
		}
		Optional<MenuEntity> menu = menuRepository.findById(menuId);
		Optional<RestaurantEntity> restaurant = restaurantRepository.findById(loginRestaurantId);
		if(menu.isEmpty() || restaurant.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found.");
		}
		MenuItemEntity menuItemEntity = new MenuItemEntity();
		
		menuItemEntity.setMenuEntity(menu.get());
		menuItemEntity.setRestaurantEntity(restaurant.get());
		menuItemEntity.setTitle(menuItemBean.getTitle());
		menuItemEntity.setDescription(menuItemBean.getDescription());
		menuItemEntity.setItemaImagePath(menuItemBean.getItemaImagePath());
		menuItemEntity.setPrice(menuItemBean.getPrice());
		menuItemEntity.setOfferQty(menuItemBean.getOfferQty());
		menuItemEntity.setOfferQtyCount(menuItemBean.getOfferQtyCount());
		menuItemEntity.setOfferPercentage(menuItemBean.getOfferPercentage());
		menuItemEntity.setUptoAmt(menuItemBean.getUptoAmt());
		menuItemEntity.setUptoAmtCondition(menuItemBean.getUptoAmtCondition());
		
		menuItemRepository.save(menuItemEntity);
		return ResponseEntity.ok("Item successfully added in menu.");
	}
	
	@GetMapping("{menuId}")
	public ResponseEntity<?> getAllMenuItem(@PathVariable Integer menuId,HttpSession session){
		Optional<MenuEntity> op = menuRepository.findById(menuId);
		if(op.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Menu Not Found.");
		}
		MenuEntity menuEntity = op.get();
		Integer loginRestaurantId = (Integer)session.getAttribute("restaurantId");
		if(loginRestaurantId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please log in as a restaurant.");
		}
		if(!menuEntity.getRestaurantEntity().getRestaurantId().equals(loginRestaurantId)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to show the menu.");
		}
		if(!menuEntity.isActive()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Menu is not found.");
		}
		Optional<List<MenuItemEntity>> optional = menuItemRepository.findAllByMenuEntityAndActiveTrue(menuEntity);
		if(optional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No active menu items found for this menu.");
		}
		List<MenuItemEntity> menuItems = optional.get();
		
		Map<String, Object> response = new HashMap<>();
		response.put("items", menuItems);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/menu/{menuId}/item/{itemId}")
	public ResponseEntity<?> getMenuItemById(@PathVariable Integer menuId,
			@PathVariable Integer itemId, 
			HttpSession session) {
	    // Check if the restaurant is logged in
	    Integer restaurantId = (Integer) session.getAttribute("restaurantId");
	    if (restaurantId == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized. Please log in as a restaurant.");
	    }
	    // Fetch the menu item by itemId
	    Optional<MenuItemEntity> op = menuItemRepository.findById(itemId);
	    Optional<MenuEntity> menu = menuRepository.findById(menuId);
	    MenuEntity menuEntity = menu.get();
	    if(!menuEntity.isActive()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Menu is not found.");
		}
	    // If the menu item doesn't exist
	    if (!op.isPresent()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Menu item not found.");
	    }
	    MenuItemEntity menuItem = op.get();
	    // Check if the menu item belongs to the logged-in restaurant	    
	    if (!menuItem.getMenuEntity().getMenuId().equals(menuId)) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Menu ID does not match with the provided item.");
	    }
	    
	    if (!menuItem.getRestaurantEntity().getRestaurantId().equals(restaurantId)) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to view this menu item.");
	    }
	    if(menuItem.getActive()) {	    	
	    	return ResponseEntity.ok(menuItem);
	    }
	    return ResponseEntity.ok("MenuItem is not active.");
	    // Return the menu item
	}
	
	@PutMapping("/menu/{menuId}/item/{itemId}")
	public ResponseEntity<?> updateItemById(@PathVariable Integer menuId,
			@PathVariable Integer itemId,
			@RequestBody MenuItemBean menuItemBean,
			HttpSession session){
		Integer restaurantId = (Integer) session.getAttribute("restaurantId");
	    if (restaurantId == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized. Please log in as a restaurant.");
	    }
	    // Fetch the menu item by itemId
	    Optional<MenuItemEntity> op = menuItemRepository.findById(itemId);
	    Optional<MenuEntity> menu = menuRepository.findById(menuId);
	    MenuEntity menuEntity = menu.get();
	    if(!menuEntity.isActive()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Menu is not found.");
		}
	    // If the menu item doesn't exist
	    if (!op.isPresent()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Menu item not found.");
	    }
	    MenuItemEntity menuItem = op.get();
	    // Check if the menu item belongs to the logged-in restaurant	    
	    if (!menuItem.getMenuEntity().getMenuId().equals(menuId)) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Menu ID does not match with the provided item.");
	    }
	    if (!menuItem.getRestaurantEntity().getRestaurantId().equals(restaurantId)) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this menu item.");
	    }
	    if(!menuItem.getActive()) {
	    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item is not active.");
	    }
	    
	    menuItem.setTitle(menuItemBean.getTitle());
	    menuItem.setDescription(menuItemBean.getDescription());
	    menuItem.setPrice(menuItemBean.getPrice());
	    menuItem.setIsOffer(menuItemBean.getIsOffer());
	    menuItem.setOfferQty(menuItemBean.getOfferQty());
	    menuItem.setOfferPercentage(menuItemBean.getOfferPercentage());
	    menuItem.setOfferQtyCount(menuItemBean.getOfferQtyCount());
	    menuItem.setUptoAmt(menuItemBean.getUptoAmt());
	    menuItem.setUptoAmtCondition(menuItemBean.getUptoAmtCondition());
	    menuItem.setItemaImagePath(menuItemBean.getItemaImagePath());
	    
	    menuItemRepository.save(menuItem);
		return ResponseEntity.ok("Item is updated successfully.");
	}
	
	@DeleteMapping("/menu/{menuId}/item/{itemId}")
	public ResponseEntity<?> softDeleteofItemById(
	        @PathVariable Integer menuId,
	        @PathVariable Integer itemId,
	        HttpSession session) {

	    // Check if the restaurant is logged in
	    Integer restaurantId = (Integer) session.getAttribute("restaurantId");
	    if (restaurantId == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized. Please log in as a restaurant.");
	    }
	    // Fetch the menu item by itemId
	    Optional<MenuItemEntity> op = menuItemRepository.findById(itemId);
	    Optional<MenuEntity> menu = menuRepository.findById(menuId);
	    MenuEntity menuEntity = menu.get();
	    if(!menuEntity.isActive()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Menu is not found.");
		}
	    if (!op.isPresent()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Menu item not found.");
	    }
	    MenuItemEntity menuItem = op.get();
	    // Check if the menu item belongs to the specified menuId and restaurantId
	    if (!menuItem.getMenuEntity().getMenuId().equals(menuId)) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Menu ID does not match with the provided item.");
	    }
	    if (!menuItem.getRestaurantEntity().getRestaurantId().equals(restaurantId)) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this menu item.");
	    }
	    // Perform soft delete by setting active to false
	    menuItem.setActive(false);
	    // Save the updated menu item
	    menuItemRepository.save(menuItem);
	    // Return success message
	    return ResponseEntity.ok("Menu item has been soft deleted successfully.");
	}
}