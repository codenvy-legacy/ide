package com.codenvy.ide.ext.helloworld.server;

import javax.inject.Singleton;

/**
 * Add some logic to a server side component, forcing add to return 'Hello'
 */


@Singleton
public class MyDependency {
    public String sayHello(String name) {
        return "Hello, " + name;
    }
}
