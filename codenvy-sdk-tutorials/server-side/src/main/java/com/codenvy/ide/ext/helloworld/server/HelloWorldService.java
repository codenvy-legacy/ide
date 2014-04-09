package com.codenvy.ide.ext.helloworld.server;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Register a RESTful server side component
 */

@Path("hello")
public class HelloWorldService {
    
    private MyDependency d;
    
    @Inject
    public HelloWorldService(MyDependency d) {
        this.d = d;
    }
    
    @GET
    @Path("{name}")
    public String sayHello(@PathParam("name") String name ) {
       return d.sayHello(name);
    }

}
