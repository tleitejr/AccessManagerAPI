package com.supera.accessmanager.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import jakarta.servlet.FilterChain;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(userDetailsService, jwtService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void quandoHeaderForNulo_deveContinuarACadeiaSemInteragirComMocks() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void quandoHeaderNaoComecarComBearer_deveContinuarSemInteragirComMocks() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Token sometoken");

        filter.doFilterInternal(request, response, filterChain);

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void quandoTokenForValido_deveCarregarUserDetails_ePreencherSecurityContext() throws Exception {
        String token = "valid.jwt.token";
        String bearer = "Bearer " + token;
        String email = "user@example.com";

        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        request.addHeader("Authorization", bearer);

        UserDetails user = new User(email, "pwd", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(jwtService.extrairEmail(eq(token))).thenReturn(email);
        when(jwtService.tokenValido(eq(token))).thenReturn(true);
        when(userDetailsService.loadUserByUsername(eq(email))).thenReturn(user);

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtService, times(1)).extrairEmail(eq(token));
        verify(jwtService, times(1)).tokenValido(eq(token));
        verify(userDetailsService, times(1)).loadUserByUsername(eq(email));
        verify(filterChain, times(1)).doFilter(request, response);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertInstanceOf(UsernamePasswordAuthenticationToken.class, auth);
        assertEquals(user, auth.getPrincipal());
    }

    @Test
    void quandoTokenForInvalido_naoDeveCarregarUserDetails() throws Exception {
        String token = "invalid.jwt";
        String bearer = "Bearer " + token;
        String email = "user2@example.com";

        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        request.addHeader("Authorization", bearer);

        when(jwtService.extrairEmail(eq(token))).thenReturn(email);
        when(jwtService.tokenValido(eq(token))).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtService, times(1)).extrairEmail(eq(token));
        verify(jwtService, times(1)).tokenValido(eq(token));
        verify(userDetailsService, times(1)).loadUserByUsername(eq(email));
        verifyNoMoreInteractions(userDetailsService);
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }


    @Test
    void quandoExtrairEmailLancarExcecao_deveTratarESeguirSemPropagar() throws Exception {
        String token = "bad.jwt";
        String bearer = "Bearer " + token;

        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        request.addHeader("Authorization", bearer);

        when(jwtService.extrairEmail(eq(token))).thenThrow(new JwtException("invalid"));

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtService, times(1)).extrairEmail(eq(token));
        verifyNoInteractions(userDetailsService);
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void quandoJaExistirAuthentication_noContextoNaoDeveCarregarOutroUsuario() throws Exception {
        String token = "some.jwt";
        String bearer = "Bearer " + token;
        String email = "already@example.com";

        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        request.addHeader("Authorization", bearer);

        var existingAuth = new UsernamePasswordAuthenticationToken("existing", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        when(jwtService.extrairEmail(eq(token))).thenReturn(email);

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtService, times(1)).extrairEmail(eq(token));
        verifyNoInteractions(userDetailsService);
        verify(filterChain, times(1)).doFilter(request, response);

        assertSame(existingAuth, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void quandoEmailForNulo_naoDeveAutenticar() throws Exception {
        String token = "null.jwt";
        String bearer = "Bearer " + token;

        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        request.addHeader("Authorization", bearer);

        when(jwtService.extrairEmail(eq(token))).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtService, times(1)).extrairEmail(eq(token));
        verifyNoInteractions(userDetailsService);
        verify(filterChain, times(1)).doFilter(request, response);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
