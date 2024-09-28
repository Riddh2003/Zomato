package com.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
		menuItemEntity.setOfferPercentage(menuItemBean.getOfferPercentage());
		menuItemEntity.setOfferQtyCount(menuItemBean.getOfferQtyCount());
		menuItemEntity.setUptoAmt(menuItemBean.getUptoAmt());
		menuItemEntity.setUptoAmtCondition(menuItemBean.getUptoAmtCondition());
		
		menuItemRepository.save(menuItemEntity);
		return ResponseEntity.ok("Item successfully added in menu.");
	}
}
