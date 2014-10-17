/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.core;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.Scheduler;
import com.google.inject.Inject;

/**
 * @author Nikolay Zamosenchuk
 * @author Artem Zatsarynnyy
 */
public class ComponentRegistry {
    private Array<Component>             pendingComponents;
    private StandardComponentInitializer componentInitializer;

    /** Instantiates Component Registry. All components should be listed in this constructor. */
    @Inject
    public ComponentRegistry(ProjectStateHandler projectStateHandler, StandardComponentInitializer componentInitializer) {
        this.componentInitializer = componentInitializer;
        pendingComponents = Collections.createArray();
        pendingComponents.add(projectStateHandler);
    }

    /**
     * Starts all the components listed in registry.
     *
     * @param callback
     */
    public void start(final Callback<Void, ComponentException> callback) {
        Callback<Component, ComponentException> internalCallback = new Callback<Component, ComponentException>() {
            @Override
            public void onSuccess(final Component result) {
                pendingComponents.remove(result);

                // all components started
                if (pendingComponents.size() == 0) {
                    initializeStandardComponents(callback);
                }
            }

            @Override
            public void onFailure(final ComponentException reason) {
                Log.info(ComponentRegistry.class, "Unable to start component " + reason.getComponent(), reason);
                callback.onFailure(new ComponentException("Unable to start component", reason.getComponent()));
            }
        };

        if (!pendingComponents.isEmpty()) {
            for (Component component : pendingComponents.asIterable()) {
                component.start(internalCallback);
            }
        } else {
            initializeStandardComponents(callback);
        }
    }

    private void initializeStandardComponents(final Callback<Void, ComponentException> callback) {
        Log.info(ComponentRegistry.class, "All services have been successfully initialized.");

        // initialize standard components
        try {
            componentInitializer.initialize();
        } catch (Exception e) {
            Log.error(ComponentRegistry.class, e);
        }

        // Finalization of starting components
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                callback.onSuccess(null);
            }
        });
    }
}
