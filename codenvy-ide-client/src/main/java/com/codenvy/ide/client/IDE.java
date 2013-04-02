package com.codenvy.ide.client;

import com.codenvy.ide.client.inject.IDEInjector;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;


/** The EntryPoint of the IDE application */
public class IDE implements EntryPoint {
    /** This is the entry point method. */
    @Override
    public void onModuleLoad() {
        IDEInjector injector = GWT.create(IDEInjector.class);
        // Force instance to be created
        @SuppressWarnings("unused")
        BootstrapController bootstrap = injector.getBootstrapController();
    }
}
