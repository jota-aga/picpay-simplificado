package com.jh.picpay_simplificado.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jh.picpay_simplificado.dto.transferencia.TransferenciaRequest;
import com.jh.picpay_simplificado.entity.Role;
import com.jh.picpay_simplificado.entity.Transferencia;
import com.jh.picpay_simplificado.entity.User;
import com.jh.picpay_simplificado.enums.StatusDaTransferencia;
import com.jh.picpay_simplificado.exceptions.NotFoundException;
import com.jh.picpay_simplificado.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class TransferenciaService {
	@Autowired
	private SecurityService securityService;
	
	@Autowired
	private UserRepository userRepository;
		
	@Transactional
	public void realizarDeposito(TransferenciaRequest transferenciaRequest) {
		User pagador = securityService.getCurrentUser();
		
		if(pagador.getRole().getNome().equals(Role.Value.COMPRADOR.name())) {
			User recebedor = findUserById(transferenciaRequest.recebedor());
			
		}
		else if(pagador.getRole().getNome().equals(Role.Value.LOJISTA.name())) {
			
		}
	}
	
	private User findUserById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("usuário por id"));
	}
}
