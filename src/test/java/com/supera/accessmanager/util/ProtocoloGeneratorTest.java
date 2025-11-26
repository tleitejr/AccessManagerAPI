package com.supera.accessmanager.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class ProtocoloGeneratorTest {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Test
    void deveGerarProtocoloComFormatoCorreto() {
        String protocolo = ProtocoloGenerator.gerarProtocolo();

        String hoje = LocalDate.now().format(DF);
        assertTrue(protocolo.matches("SOL-" + hoje + "-\\d{4}"));
    }

    @Test
    void deveIncrementarSequencialmente() {
        String p1 = ProtocoloGenerator.gerarProtocolo();
        String p2 = ProtocoloGenerator.gerarProtocolo();

        String seq1 = p1.substring(p1.lastIndexOf("-") + 1);
        String seq2 = p2.substring(p2.lastIndexOf("-") + 1);

        int n1 = Integer.parseInt(seq1);
        int n2 = Integer.parseInt(seq2);

        assertEquals(n1 + 1, n2);
    }

    @Test
    void deveResetarQuandoAtinge9999() {
        String anterior = ProtocoloGenerator.gerarProtocolo();
        int seqAnterior = extrairSeq(anterior);

        for (int i = 0; i < 20000; i++) {
            String atual = ProtocoloGenerator.gerarProtocolo();
            int seqAtual = extrairSeq(atual);

            if (seqAtual == 0) {
                assertEquals(9999, seqAnterior);
                return;
            }

            seqAnterior = seqAtual;
        }

        fail("O reset (volta para 0000) não ocorreu dentro das iterações.");
    }

    private int extrairSeq(String protocolo) {
        String n = protocolo.substring(protocolo.lastIndexOf("-") + 1);
        return Integer.parseInt(n);
    }
}
