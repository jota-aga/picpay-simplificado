package com.jh.picpay_simplificado.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jh.picpay_simplificado.entity.Comprador;
import com.jh.picpay_simplificado.entity.Lojista;
import com.jh.picpay_simplificado.entity.Role;
import com.jh.picpay_simplificado.entity.User;
import com.jh.picpay_simplificado.exceptions.NotFoundException;
import com.jh.picpay_simplificado.repository.CompradorRepository;
import com.jh.picpay_simplificado.repository.LojistaRepository;

import jakarta.transaction.Transactional;

@Service
public class TransactionService {
	@Autowired
	private SecurityService securityService;
	
	@Autowired
	private CompradorRepository compradorRepository;
	
	@Autowired
	private LojistaRepository lojistaRepository;
		
	@Transactional
	public void realizarDeposito(BigDecimal valor) {
		User user = securityService.getCurrentUser();
		
		if(user.getRole().getNome().equals(Role.Value.COMPRADOR.name())) {
			Comprador comprador = getCompradorByUser(user);
			BigDecimal balancoFinal = comprador.getBalanco().add(valor);
			comprador.setBalanco(balancoFinal);
		}
		else if(user.getRole().getNome().equals(Role.Value.LOJISTA.name())) {
			Lojista lojista = getLojistaByUser(user);
			BigDecimal balancoFinal = lojista.getBalanco().add(valor);
			lojista.setBalanco(balancoFinal);
		}
	}
	
	public Comprador getCompradorByUser(User user) {
		return compradorRepository.findByUser(user)
				.orElseThrow(() -> new NotFoundException("Comprador por usuário"));
	}
	
	public Lojista getLojistaByUser(User user) {
		return lojistaRepository.findByUser(user)
				.orElseThrow(() -> new NotFoundException("Lojista por usuário"));
	}
}
