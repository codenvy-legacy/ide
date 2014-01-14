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
package com.codenvy.ide.ext.java.jdi.client.fqn;

import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 5:00:18 PM Mar 28, 2012 evgen $
 */
@Singleton
public class FqnResolverFactory {
    private StringMap<FqnResolver> resolvers;

    /** Create factory. */
    @Inject
    protected FqnResolverFactory() {
        this.resolvers = Collections.createStringMap();
    }

    public void addResolver(@NotNull String mimeType, @NotNull FqnResolver resolver) {
        resolvers.put(mimeType, resolver);
    }

    @Nullable
    public FqnResolver getResolver(@NotNull String mimeType) {
        return resolvers.get(mimeType);
    }

    public boolean isResolverExist(@NotNull String mimeType) {
        return resolvers.containsKey(mimeType);
    }
}