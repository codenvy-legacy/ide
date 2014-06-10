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
package com.codenvy.ide.client;

import com.codenvy.ide.api.preferences.PreferencesManager;
import com.codenvy.ide.collections.Jso;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.collections.StringMap.IterationCallback;
import com.codenvy.ide.extension.ExtensionDescription;
import com.codenvy.ide.extension.ExtensionRegistry;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * {@link ExtensionInitializer} responsible for bringing up Extensions. It uses ExtensionRegistry to acquire
 * Extension description and dependencies.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
public class ExtensionInitializer {
    protected final ExtensionRegistry extensionRegistry;

    private final ExtensionManager   extensionManager;
    private       PreferencesManager preferencesManager;

    /**
     *
     */
    @Inject
    public ExtensionInitializer(final ExtensionRegistry extensionRegistry, final ExtensionManager extensionManager,
                                PreferencesManager preferencesManager) {
        this.extensionRegistry = extensionRegistry;
        this.extensionManager = extensionManager;
        this.preferencesManager = preferencesManager;
    }

    /** {@inheritDoc} */
    public void startExtensions() {
        String value = preferencesManager.getValue("ExtensionsPreferences");
        final Jso jso = Jso.deserialize(value == null ? "{}" : value);
        extensionManager.getExtensions().iterate(new IterationCallback<Provider>() {
            @Override
            public void onIteration(String extensionFqn, Provider extensionProvider) {
                boolean enabled = !jso.hasOwnProperty(extensionFqn) || jso.getBooleanField(extensionFqn);
                if (enabled) {
                    // this will instantiate extension so it's get enabled
                    // Order of startup is managed by GIN dependency injection framework
                    extensionProvider.get();
                }
                // extension has been enabled
                extensionRegistry.getExtensionDescriptions().get(extensionFqn).setEnabled(enabled);
            }
        });
    }

    /** {@inheritDoc} */
    public StringMap<ExtensionDescription> getExtensionDescriptions() {
        return extensionRegistry.getExtensionDescriptions();
    }

}
