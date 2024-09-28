package com.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bean.MenuBean;
import com.entity.MenuEntity;
import com.entity.RestaurantEntity;
import com.repository.MenuRepository;
import com.repository.RestaurantRepository;
import com.service.MenuService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/menu")
public class MenuController {
	
	@Autowired
	MenuRepository menuRepository;
	
	@Autowired
	RestaurantRepository restaurantRepository;
	
	@Autowired
	MenuService menuService;
	
	//create menu 
	@PostMapping
	public ResponseEntity<?> addmenu(@RequestBody MenuBean menuBean,HttpSession session){
		if(menuBean.getRestaurantId() == null) {
			return ResponseEntity.badRequest().body("Restaurant ID is requried.");
		}
		Integer restaurantId = (Integer)session.getAttribute("restaurantId");
		
		if(restaurantId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No Restaurant ID found in session. Please log in.");
		}
		
		//verify that the restaurant Id in the request matches the one in the session
		if(!restaurantId.equals(menuBean.getRestaurantId())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only add menus to your own restaurant.");
		}
		
		Optional<RestaurantEntity> op = restaurantRepository.findById(restaurantId);
		if(op.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Restaurant not found");
		}
		MenuEntity menuEntity = new MenuEntity();
		menuEntity.setTitle(menuBean.getTitle());
		menuEntity.setMenuImagePath(menuBean.getMenuImagePath());
		menuEntity.setRestaurantEntity(op.get());
		
		menuRepository.save(menuEntity);
		return ResponseEntity.ok("Menu is created.");
	}
	
	//read list of menus
	@GetMapping
	public ResponseEntity<?> getAllMenu(HttpSession session){
		Integer restaurantId = (Integer)session.getAttribute("restaurantId");
		if(restaurantId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No restaurantId found in session.Please log in.");
		}
		Optional<List<MenuEntity>> menus = menuRepository.findByRestaurantEntityRestaurantId(restaurantId);
		List<MenuBean> menuDetails = menus.get().stream()
				.filter(MenuEntity::isActive)
				.map(menu -> new MenuBean(
					restaurantId,
					menu.getTitle(),
					menu.getMenuImagePath(),
					menu.getMenuId(),
					menu.isActive()
					))
				.collect(Collectors.toList());
		return ResponseEntity.ok(menuDetails);
	}
	
	//read menu by ID
	@GetMapping("{menuId}")
	public ResponseEntity<?> getMenuById(@PathVariable Integer menuId,HttpSession session){
		Integer restaurantId = (Integer)session.getAttribute("restaurantId");
		if(!menuService.checkLoginOrNot(restaurantId)) {
			return ResponseEntity.badRequest().body("Please Login First.");
		}
		Optional<MenuEntity> menu = menuRepository.findById(menuId);
		if(menu.isEmpty()) {
			return ResponseEntity.badRequest().body("Menu not found.");
		}
		MenuEntity menuEntity = menu.get();
		if(menuEntity.isActive()) {			
			MenuBean menuBean = new MenuBean(
					restaurantId,
					menuEntity.getTitle(),
					menuEntity.getMenuImagePath(),
					menuEntity.getMenuId(),
					menuEntity.isActive()
					);
			return ResponseEntity.ok(menuBean);
		}
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("That Menu is not active.");
		}
	}
	
	//update menu by id
	@PutMapping("{menuId}")
	public ResponseEntity<?> updateMenu(@PathVariable Integer menuId,@RequestBody MenuEntity menuEntity,HttpSession session) {
		Integer restaurantId = (Integer)session.getAttribute("restaurantId");
		if(!menuService.checkLoginOrNot(restaurantId)) {
			return ResponseEntity.badRequest().body("Please Login First.");
		}
		Optional<MenuEntity> op = menuRepository.findById(menuId);
		if(op.isEmpty()) {
			return ResponseEntity.badRequest().body("Menu not found.");
		}
		
		MenuEntity menu = op.get();
		
		if(!menu.isActive()) {
			return ResponseEntity.badRequest().body("Menu is not active.");
		}
		if(menuEntity.getRestaurantEntity() != null) {
			Integer newRestaurantId = menuEntity.getRestaurantEntity().getRestaurantId();
			
			Optional<RestaurantEntity> newRestaurant = restaurantRepository.findById(newRestaurantId);
			if(newRestaurant.isEmpty()) {
				return ResponseEntity.badRequest().body("Invalid RestaurantId.");
			}
			menu.setRestaurantEntity(newRestaurant.get());
		}
		
		//other wise these works
		menu.setTitle(menuEntity.getTitle());
		menu.setMenuImagePath(menuEntity.getMenuImagePath());
		
		//save applied
		menuRepository.save(menu);
		
		MenuBean menuBean = new MenuBean(
				restaurantId,
				menu.getTitle(),
				menu.getMenuImagePath(),
				menu.getMenuId(),
				menu.isActive()
				);
		return ResponseEntity.ok(menuBean);
	}
	
	//soft delete menu
	@DeleteMapping("{menuId}")
	public ResponseEntity<?> softDeleteMenu(@PathVariable Integer menuId,HttpSession session){
		Integer restaurantId = (Integer)session.getAttribute("restaurantId");
		if(!menuService.checkLoginOrNot(restaurantId)) {
			return ResponseEntity.badRequest().body("Please Login First.");
		}
		String result = menuService.softDeleteMenuService(menuId, restaurantId);
		if(result.toLowerCase().equals("success")) {
			return ResponseEntity.ok("Menu soft deleted successfully.");
		}
		else {
			return ResponseEntity.badRequest().body(result);
		}
	}
	
}
