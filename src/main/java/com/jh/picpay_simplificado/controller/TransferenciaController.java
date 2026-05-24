package com.jh.picpay_simplificado.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jh.picpay_simplificado.dto.transferencia.TransferenciaRequest;
import com.jh.picpay_simplificado.service.TransferenciaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transferencia")
public class TransferenciaController {
	
	@Autowired
	private TransferenciaService transferenciaService;
	
	@PostMapping
	public void transferir(@Valid @RequestBody TransferenciaRequest transferenciaRequest) {
		transferenciaService.realizarTransferencia(transferenciaRequest);
	}
}
