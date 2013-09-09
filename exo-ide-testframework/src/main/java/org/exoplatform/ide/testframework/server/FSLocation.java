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
package org.exoplatform.ide.testframework.server;

import javax.ws.rs.core.UriInfo;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class FSLocation {
    private final String url;

    /**
     * @param url
     *         full URL of resource in IDE notation
     */
    public FSLocation(String url) {
        this.url = url;
    }

    public String getURL() {
        return url;
    }

    @Deprecated
    public String getLocalPath(UriInfo uriInfo) {
        return getLocalPath();
    }

    public String getLocalPath() {

        String localPath = getRootPath();
        if (localPath == null)
            throw new IllegalStateException("Root path may not be null. ");
        if (!localPath.endsWith("/"))
            localPath += "/"; // unix like path only!
        localPath += "repository/dev-monit" + url;
        return localPath;
    }

    protected String getRootPath() {
        return System.getProperty("org.exoplatform.ide.server.fs-root-path");
    }
}