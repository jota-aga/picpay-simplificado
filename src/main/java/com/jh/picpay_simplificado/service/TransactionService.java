package com.jh.picpay_simplificado.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jh.picpay_simplificado.entity.Role;
import com.jh.picpay_simplificado.entity.User;

import jakarta.transaction.Transactional;

@Service
public class TransactionService {
	@Autowired
	private SecurityService securityService;
		
	@Transactional
	public void realizarDeposito(BigDecimal valor) {
		User user = securityService.getCurrentUser();
		
		if(user.getRole().getNome().equals(Role.Value.COMPRADOR.name())) {
			
		}
		else if(user.getRole().getNome().equals(Role.Value.LOJISTA.name())) {
			
		}
	}
}
