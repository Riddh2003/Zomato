package com.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.MenuEntity;
import com.repository.MenuRepository;

@Service
public class MenuService {
	
	@Autowired
	MenuRepository menuRepository;
	
	public boolean checkLoginOrNot(Integer restaurantId) {
		if(restaurantId == null) {
			return false;
		}
		Optional<MenuEntity> op = menuRepository.findById(restaurantId);
		if(op.isEmpty()) {
			return false;
		}
		
		MenuEntity menu = op.get();
		if(!menu.getRestaurantEntity().getRestaurantId().equals(restaurantId)) {
			return false;
		}
		return true;
	}
	
	public String softDeleteMenuService(Integer menuId, Integer restaurantId) {
		
		Optional<MenuEntity> op = menuRepository.findById(menuId);
		
		//check for login 
		MenuEntity menuEntity = op.get();
		if(!menuEntity.getRestaurantEntity().getRestaurantId().equals(restaurantId)) {
			return "Please Logged in first.";
		}
		
		if(op.isEmpty()) {
			return "Menu not found.";
		}
		
		menuEntity.setActive(false);
		menuRepository.save(menuEntity);
		return "success";
	}
	
}
