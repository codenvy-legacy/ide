/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.core;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.resources.ResourceProviderComponent;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.Callback;
import com.google.inject.Inject;

/** @author Nikolay Zamosenchuk */
public class ComponentRegistry {
    private Array<Component>             pendingComponents;
    private StandardComponentInitializer componentInitializer;

    /** Instantiates Component Registry. All components should be listed in this constructor. */
    @Inject
    public ComponentRegistry(ResourceProviderComponent resourceManager, StandardComponentInitializer componentInitializer) {
        this.componentInitializer = componentInitializer;
        pendingComponents = Collections.createArray();
        pendingComponents.add(resourceManager);
    }

    /**
     * Starts all the components listed in registry.
     *
     * @param callback
     */
    public void start(final Callback<Void, ComponentException> callback) {
        Callback<Component, ComponentException> internalCallback = new Callback<Component, ComponentException>() {
            @Override
            public void onSuccess(Component result) {
                pendingComponents.remove(result);
                // all components started
                if (pendingComponents.size() == 0) {
                    Log.info(ComponentRegistry.class, "All services initialized. Starting.");
                    //initialize standard components
                    try {
                        componentInitializer.initialize();
                    } catch (Throwable e) {
                        Log.error(ComponentRegistry.class, e);
                    }
                    callback.onSuccess(null);
                }
            }

            @Override
            public void onFailure(ComponentException reason) {
                callback.onFailure(new ComponentException("Failed to start component", reason.getComponent()));
                Log.info(ComponentRegistry.class, "FAILED to start service:" + reason.getComponent(), reason);
            }
        };

        for (Component component : pendingComponents.asIterable()) {
            component.start(internalCallback);
        }
    }

}
