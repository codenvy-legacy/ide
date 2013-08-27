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
package org.exoplatform.ide.git.shared;

import java.util.Map;

/**
 * Abstract request to {@link org.exoplatform.ide.git.server.GitConnection}.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GitRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public abstract class GitRequest {
    /**
     * Opaque set of request attributes. If some attribute not need value then empty string or <code>null</code> may be used.
     */
    private Map<String, String> attributes;

    /** @return set of request attributes */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes set of request attributes
     */
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}
