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
