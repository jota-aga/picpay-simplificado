package com.jh.picpay_simplificado.enums;

public enum StatusDaTransferencia {
	REALIZADA("Realizada"),
	NAO_AUTORIZADA("Não autorizada");
	
	private String legenda;

	private StatusDaTransferencia(String legenda) {
		this.legenda = legenda;
	}

	public String getLegenda() {
		return legenda;
	}
}
