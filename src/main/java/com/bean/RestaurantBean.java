package com.bean;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestaurantBean {
	
	Integer restaurantId;
    String title;
    String category;
    String description;
    String timings;
    String address;
    String contactNum;
    String lat;
    String log;
    Integer pincode;
    Integer online;
    String email;
    String password;
    Boolean active;
    String restaurantImagePath;
}
