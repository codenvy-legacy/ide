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
package com.codenvy.ide.client;

import com.codenvy.ide.extension.ExtensionDescription;
import com.codenvy.ide.extension.ExtensionRegistry;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.json.JsonStringMap.IterationCallback;
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

    private final ExtensionManager extensionManager;

    /**
     *
     */
    @Inject
    public ExtensionInitializer(final ExtensionRegistry extensionRegistry, final ExtensionManager extensionManager) {
        this.extensionRegistry = extensionRegistry;
        this.extensionManager = extensionManager;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("rawtypes")
    public void startExtensions() {
        extensionManager.getExtensions().iterate(new IterationCallback<Provider>() {
            @Override
            public void onIteration(String extensionFqn, Provider extensionProvider) {
                // this will instantiate extension so it's get enabled
                // Order of startup is managed by GIN dependency injection framework
                extensionProvider.get();
                // extension has been enabled
                extensionRegistry.getExtensionDescriptions().get(extensionFqn).setEnabled(true);
            }
        });
    }

    /** {@inheritDoc} */
    public JsonStringMap<ExtensionDescription> getExtensionDescriptions() {
        return extensionRegistry.getExtensionDescriptions();
    }

}
