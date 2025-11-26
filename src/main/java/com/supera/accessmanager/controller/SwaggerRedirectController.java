package com.supera.accessmanager.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class SwaggerRedirectController {

    @GetMapping("/swagger-ui")
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui/swagger-ui.html");
    }
}

