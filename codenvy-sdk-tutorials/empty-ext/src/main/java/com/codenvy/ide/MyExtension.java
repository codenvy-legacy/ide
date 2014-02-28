package com.codenvy.ide;

import com.codenvy.ide.api.extension.Extension;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** The skeleton of empty Codenvy extension. */
@Singleton
@Extension(title = "Empty extension", version = "1.0.0")
public class MyExtension {

    @Inject
    public MyExtension() {
    }
}