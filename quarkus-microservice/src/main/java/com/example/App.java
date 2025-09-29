package com.example;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;

@Path("/api")
public class GreetingResource {

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    @Timed(value = "hello.time", description = "Time spent on hello endpoint")
    @Counted(value = "hello.count", description = "Times hello endpoint is called")
    public String hello() {
        return "Hello from Quarkus!";
    }
}
