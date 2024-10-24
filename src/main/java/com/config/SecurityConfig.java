package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.filter.TokenFilter;

import jakarta.servlet.Filter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, TokenFilter tokenFilter) throws Exception {
		return http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(
						auth -> auth.requestMatchers("/api/public/**").permitAll().requestMatchers("/api/private/**")
								.hasAnyAuthority("restaurant", "customer").anyRequest().authenticated())
				.addFilterBefore((Filter) tokenFilter, UsernamePasswordAuthenticationFilter.class).build();
	}
	
	@Bean
    public TokenFilter tokenFilter() {
        return new TokenFilter();
    }
}
