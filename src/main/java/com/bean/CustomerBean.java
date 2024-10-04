package com.bean;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerBean {
	Integer customerId;
	String firstName;
	String lastName;
	String email;
	String password;
	String profilePicPath;
	String otp;
	String gender;
	String bornYear;
	Boolean active = true;
	String contactNum;
}
