package com.jh.picpay_simplificado.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jh.picpay_simplificado.client.AuthorizationClient;
import com.jh.picpay_simplificado.client.NotificationService;
import com.jh.picpay_simplificado.dto.transferencia.TransferenciaRequest;
import com.jh.picpay_simplificado.entity.Carteira;
import com.jh.picpay_simplificado.entity.Transferencia;
import com.jh.picpay_simplificado.entity.User;
import com.jh.picpay_simplificado.enums.Roles;
import com.jh.picpay_simplificado.exceptions.ConflictException;
import com.jh.picpay_simplificado.exceptions.NotAuthorizedException;
import com.jh.picpay_simplificado.exceptions.NotFoundException;
import com.jh.picpay_simplificado.repository.TransferenciaRepository;
import com.jh.picpay_simplificado.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class TransferenciaService {
	@Autowired
	private SecurityService securityService;

	@Autowired
	private AuthorizationClient authorizationClient;
	
	@Autowired
	private NotificationService notificationService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TransferenciaRepository transferenciaRepository;

	@Transactional
	public void realizarTransferencia(TransferenciaRequest transferenciaRequest) {
		User pagador = securityService.getCurrentUser();
		User recebedor = findUserById(transferenciaRequest.recebedor());
		BigDecimal valor = transferenciaRequest.valor();
		Transferencia transferencia;

		validarTransferencia(pagador, transferenciaRequest.valor());

		transferencia = Transferencia.builder()
				.pagador(pagador)
				.recebedor(recebedor)
				.valor(valor)
				.createdAt(LocalDateTime.now())
				.build();
		
		if(authorizationClient.autorizarTransferencia()) {
			editarCarteiras(pagador, recebedor, valor);
			
			transferenciaRepository.save(transferencia);
			
			notificationService.notificar();
		}
	}

	private void editarCarteiras(User pagador, User recebedor, BigDecimal valor) {
		Carteira carteiraPagador = pagador.getCarteira();
		Carteira carteiraRecebedor = recebedor.getCarteira();

		carteiraPagador.setBalanco(carteiraPagador.getBalanco().subtract(valor));
		carteiraRecebedor.setBalanco(carteiraRecebedor.getBalanco().add(valor));

		userRepository.saveAll(List.of(pagador, recebedor));
	}

	private void validarTransferencia(User pagador, BigDecimal valorTransferencia) {
		if (pagador.getRole().getNome().equals(Roles.COMPRADOR.name())) {
			Carteira carteira = pagador.getCarteira();

			if (carteira.getBalanco().compareTo(valorTransferencia) == -1)
				throw new ConflictException("Balanço não suficiente para realizar a transaferñecia");

		} else if (pagador.getRole().getNome().equals(Roles.LOJISTA.name())) {
			throw new NotAuthorizedException("Apenas Compradores podem realizar transferência");
		}
	}

	private User findUserById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new NotFoundException("usuário por id"));
	}
}
