package com.jh.picpay_simplificado.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.jh.picpay_simplificado.entity.User;
import com.jh.picpay_simplificado.exceptions.NotFoundException;
import com.jh.picpay_simplificado.repository.UserRepository;

@Service
public class SecurityService {
	
	@Autowired
	private UserRepository userRepository;
	
	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext()
			.getAuthentication();
		
		Long userId = Long.valueOf(authentication.getName());
		
		return userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("usuário por id do token"));
	}
}
