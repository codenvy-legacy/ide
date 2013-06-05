/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.java.jdi.client.fqn;

import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 5:00:18 PM Mar 28, 2012 evgen $
 */
@Singleton
public class FqnResolverFactory {
    private JsonStringMap<FqnResolver> resolvers;

    @Inject
    protected FqnResolverFactory() {
        this.resolvers = JsonCollections.createStringMap();
    }

    public void addResolver(String mimeType, FqnResolver resolver) {
        resolvers.put(mimeType, resolver);
    }

    public FqnResolver getResolver(String mimeType) {
        return resolvers.get(mimeType);
    }

    public boolean isResolverExist(String mimeType) {
        return resolvers.containsKey(mimeType);
    }
}