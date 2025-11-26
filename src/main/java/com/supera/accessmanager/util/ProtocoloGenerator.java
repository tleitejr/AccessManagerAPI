package com.supera.accessmanager.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ProtocoloGenerator {

    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static String gerarProtocolo() {
        String date = LocalDate.now().format(DF);
        int n = COUNTER.incrementAndGet() % 10000;
        return String.format("SOL-%s-%04d", date, n);
    }
}
