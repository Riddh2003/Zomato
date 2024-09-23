package com.bean;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuBean {

	public MenuBean(Integer restaurantId, String title, String menuImagePath, Integer menuId, boolean active) {
		this.title = title;
		this.restaurantId = restaurantId;
		this.menuImagePath = menuImagePath;
		this.active = active;
		this.menuId = menuId;
	}
	Integer restaurantId;
	String title;
	String menuImagePath;
	boolean active;
	Integer menuId;
}
