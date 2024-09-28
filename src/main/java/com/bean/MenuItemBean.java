package com.bean;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuItemBean {
	
	Integer menuId;
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
}
