package com.codenvy.ide.ext.helloworld.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Adding an icon to be placed on a toolbar.
 * A native GWT interface is used
 * Icon file is located in resources.../client folder
 */

public interface Resource extends ClientBundle {
    @Source("default.jpg")
    ImageResource hello();
}
