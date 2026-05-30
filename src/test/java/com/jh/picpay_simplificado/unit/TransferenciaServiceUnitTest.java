package com.jh.picpay_simplificado.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.jh.picpay_simplificado.client.AuthorizationClient;
import com.jh.picpay_simplificado.client.NotificationClient;
import com.jh.picpay_simplificado.creator.CarteiraCreator;
import com.jh.picpay_simplificado.creator.RoleCreator;
import com.jh.picpay_simplificado.creator.UserCreator;
import com.jh.picpay_simplificado.dto.transferencia.TransferenciaRequest;
import com.jh.picpay_simplificado.entity.Carteira;
import com.jh.picpay_simplificado.entity.Role;
import com.jh.picpay_simplificado.entity.User;
import com.jh.picpay_simplificado.exceptions.ConflictException;
import com.jh.picpay_simplificado.exceptions.NotAuthorizedException;
import com.jh.picpay_simplificado.repository.TransferenciaRepository;
import com.jh.picpay_simplificado.repository.UserRepository;
import com.jh.picpay_simplificado.service.SecurityService;
import com.jh.picpay_simplificado.service.TransferenciaService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class TransferenciaServiceUnitTest {
	@InjectMocks
	private TransferenciaService transferenciaService;
	
	@Mock
	private SecurityService securityService;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private TransferenciaRepository transferenciaRepository;
	
	@Mock
	private AuthorizationClient authorizationClient;
	
	@Mock
	private NotificationClient notificationClient;
	
	private User userComprador;
	
	private User userLojista;
	
	private TransferenciaRequest request;
	
	private Carteira carteiraComprador;
	
	private Carteira carteiraLojista;
	
	@BeforeEach
	public void setUp() {
		Role roleComprador = RoleCreator.comprador();
		Role roleLojista = RoleCreator.lojista();
		
		carteiraComprador = CarteiraCreator.carteiraWith100Balanco();
		carteiraLojista = CarteiraCreator.carteiraWith0Balanco();

		userComprador = UserCreator.userWithId(1L, carteiraComprador, roleComprador);
		userLojista = UserCreator.userWithId(2L, carteiraLojista, roleLojista);
		
		request = new TransferenciaRequest(BigDecimal.valueOf(100), userLojista.getId());
	}
	
	@Test
	public void realizarTransferenciaSucess() {
		when(securityService.getCurrentUser()).thenReturn(userComprador);
		when(userRepository.findById(userLojista.getId())).thenReturn(Optional.of(userLojista));
		when(authorizationClient.autorizarTransferencia()).thenReturn(true);
		doNothing().when(notificationClient).notificar();
		
		
		transferenciaService.realizarTransferencia(request);
		
		verify(transferenciaRepository, atMostOnce()).save(any());
		verify(userRepository, atMostOnce()).save(any());
		assertEquals(BigDecimal.ZERO, carteiraComprador.getBalanco());
		assertEquals(BigDecimal.valueOf(100), carteiraLojista.getBalanco());
	}
	
	@Test
	public void realizarTransferencia_WhenPagadorIsLojista() {
		when(securityService.getCurrentUser()).thenReturn(userLojista);
		when(userRepository.findById(userComprador.getId())).thenReturn(Optional.of(userComprador));
		request = new TransferenciaRequest(BigDecimal.valueOf(100), userComprador.getId());
		
		assertThrows(NotAuthorizedException.class, () -> transferenciaService.realizarTransferencia(request));
		
		verify(transferenciaRepository, never()).save(any());
		verify(userRepository, never()).save(any());
	}
	
	@Test
	public void realizarTransferencia_WhenBalancoInsuficiente() {
		when(securityService.getCurrentUser()).thenReturn(userComprador);
		when(userRepository.findById(userLojista.getId())).thenReturn(Optional.of(userLojista));
		carteiraComprador.setBalanco(BigDecimal.valueOf(99));
		
		assertThrows(ConflictException.class, () -> transferenciaService.realizarTransferencia(request));
		
		verify(transferenciaRepository, never()).save(any());
		verify(userRepository, never()).save(any());
	}
	
	@Test
	public void realizarTransferencia_WhenNaoAutorizado() {
		when(securityService.getCurrentUser()).thenReturn(userComprador);
		when(userRepository.findById(userLojista.getId())).thenReturn(Optional.of(userLojista));
		doThrow(NotAuthorizedException.class).when(authorizationClient).autorizarTransferencia();;
		
		assertThrows(NotAuthorizedException.class, () -> transferenciaService.realizarTransferencia(request));
		
		verify(transferenciaRepository, never()).save(any());
		verify(userRepository, never()).save(any());
	}
	
	@Test
	public void realizarTransferencia_WhenPagadorAndUserIsEquals() {
		when(securityService.getCurrentUser()).thenReturn(userComprador);
		when(userRepository.findById(userComprador.getId())).thenReturn(Optional.of(userComprador));
		
		request = new TransferenciaRequest(BigDecimal.valueOf(100), userComprador.getId());

		
		assertThrows(ConflictException.class, () -> transferenciaService.realizarTransferencia(request));
		
		verify(transferenciaRepository, never()).save(any());
		verify(userRepository, never()).save(any());
	}
}
