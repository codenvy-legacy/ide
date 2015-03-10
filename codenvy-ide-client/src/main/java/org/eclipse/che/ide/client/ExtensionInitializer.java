/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.client;

import org.eclipse.che.ide.api.preferences.PreferencesManager;
import org.eclipse.che.ide.collections.Jso;
import org.eclipse.che.ide.collections.StringMap;
import org.eclipse.che.ide.collections.StringMap.IterationCallback;
import org.eclipse.che.ide.extension.ExtensionDescription;
import org.eclipse.che.ide.extension.ExtensionRegistry;
import org.eclipse.che.ide.util.loging.Log;
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
                try {
                    if (enabled) {
                        // this will instantiate extension so it's get enabled
                        // Order of startup is managed by GIN dependency injection framework
                        extensionProvider.get();
                    }
                    // extension has been enabled
                    extensionRegistry.getExtensionDescriptions().get(extensionFqn).setEnabled(enabled);
                } catch (Throwable e) {
                    Log.error(ExtensionInitializer.class, "Can't initialize extension: " + extensionFqn, e);
                }
            }
        });
    }

    /** {@inheritDoc} */
    public StringMap<ExtensionDescription> getExtensionDescriptions() {
        return extensionRegistry.getExtensionDescriptions();
    }

}
