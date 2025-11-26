package com.supera.accessmanager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void gerarToken_deveGerarTokenValido_eConterSubject() {
        String email = "user@example.com";

        String token = jwtService.gerarToken(email);

        assertNotNull(token, "Token não deve ser nulo");
        assertTrue(jwtService.tokenValido(token), "Token gerado deve ser válido");
        assertEquals(email, jwtService.extrairEmail(token), "Email extraído deve ser igual ao informado");
    }

    @Test
    void tokenComPrefixoBearer_deveSerAceito() {
        String email = "user2@example.com";
        String raw = jwtService.gerarToken(email);
        String bearer = "Bearer " + raw;

        assertTrue(jwtService.tokenValido(bearer), "Token com prefixo Bearer deve ser válido");
        assertEquals(email, jwtService.extrairEmail(bearer), "Email extraído do token com prefixo deve ser igual");
    }

    @Test
    void extrairClaims_comTokenInvalido_deveLancarJwtException() {
        String invalid = "this.is.not.a.valid.token";

        assertThrows(JwtException.class, () -> jwtService.extrairClaims(invalid),
                "Extrair claims de token inválido deve lançar JwtException");
    }

    @Test
    void expiracao_doToken_deveCorresponderAExpiracaoConfigurada() {
        String email = "exp@test.com";
        String token = jwtService.gerarToken(email);

        Claims claims = jwtService.extrairClaims(token);
        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();

        assertNotNull(issuedAt, "issuedAt não deve ser nulo");
        assertNotNull(expiration, "expiration não deve ser nulo");

        long delta = expiration.getTime() - issuedAt.getTime();
        long expected = jwtService.getExpiracao();

        long toleranceMillis = 2000L;
        assertTrue(Math.abs(delta - expected) <= toleranceMillis,
                () -> "Diferença entre issuedAt e expiration deve ser aproximadamente EXPIRACAO. esperado="
                        + expected + " actual=" + delta);
    }

    @Test
    void tokenValido_comTokenInvalido_deveRetornarFalse() {
        String invalid = "abc.def.ghi";

        boolean valido = jwtService.tokenValido(invalid);

        assertFalse(valido, "tokenValido deve retornar false para tokens inválidos");
    }

    @Test
    void tokenValido_comNull_deveRetornarFalse() {
        assertFalse(jwtService.tokenValido(null),
                "tokenValido deve retornar false quando o token é null");
    }

    @Test
    void limparPrefixo_quandoNaoTemPrefixo_deveRetornarOriginal() {
        String token = "abc.def.ghi";
        String result = jwtService.limparPrefixo(token);

        assertEquals(token, result, "Token sem prefixo deve ser retornado sem alterações");
    }

    @Test
    void limparPrefixo_quandoTemPrefixo_deveRemoverBearer() {
        String raw = "abc.def.ghi";
        String prefixed = "Bearer " + raw;

        String result = jwtService.limparPrefixo(prefixed);

        assertEquals(raw, result, "Prefixo Bearer deve ser removido");
    }
}
