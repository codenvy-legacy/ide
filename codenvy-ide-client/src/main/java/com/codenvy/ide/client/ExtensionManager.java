package com.codenvy.ide.client;

import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/** THIS CLASS WILL BE OVERRIDEN BY MAVEN BUILD. DON'T EDIT CLASS, IT WILL HAVE NO EFFECT. */
@Singleton
@SuppressWarnings("rawtypes")
public class ExtensionManager {

    /** Contains the map will all the Extnesion Providers <FullClassFQN, Provider>. */
    protected final JsonStringMap<Provider> extensions = JsonCollections.createStringMap();

    /** Constructor that accepts all the Extension found in IDE package */
    @Inject
    public ExtensionManager(
                           ) {
    }

    /** Returns  the map will all the Extnesion Providers <FullClassFQN, Provider>. */
    public JsonStringMap<Provider> getExtensions() {
        return extensions;
    }
}
