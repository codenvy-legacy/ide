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
package org.exoplatform.ide.extension.java.jdi.client.fqn;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 5:00:18 PM Mar 28, 2012 evgen $
 */
public class FqnResolverFactory {

    private Map<String, FqnResolver> resolvers = new HashMap<String, FqnResolver>();

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
