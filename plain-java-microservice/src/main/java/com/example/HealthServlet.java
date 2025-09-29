package com.example;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HealthServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        App.prometheusRegistry.counter("health.requests").increment();
        resp.setContentType("application/json");
        resp.getWriter().write("{\"status\":\"Healthy\"}");
    }
}
