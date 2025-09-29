package com.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Plain Java microservice with Jetty
 *
 */
public class App 
{
    public static final PrometheusMeterRegistry prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    public static void main( String[] args ) throws Exception
    {
        // Create server
        Server server = new Server(8082);

        // Create context
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        server.setHandler(context);

        // Add health servlet
        context.addServlet(new ServletHolder(HealthServlet.class), "/health");

        // Add metrics servlet
        context.addServlet(new ServletHolder(MetricsServlet.class), "/metrics");

        // Start server
        server.start();
        System.out.println("Server started on port 8082");
        server.join();
    }
}

class HealthServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        App.prometheusRegistry.counter("health.requests").increment();
        resp.setContentType("application/json");
        resp.getWriter().write("{\"status\":\"Healthy\"}");
    }
}

class MetricsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().write(App.prometheusRegistry.scrape());
    }
}
