package com.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import com.example.HealthServlet;
import com.example.MetricsServlet;

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

 
 // The HealthServlet and MetricsServlet classes have been moved to their own files.
