package com.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class TokenGeneraterService {
	public String tokenGenerater() {
		return UUID.randomUUID().toString();
	}
}
