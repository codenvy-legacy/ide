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
package org.exoplatform.ide.vfs.server;

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemRuntimeException;

import javax.servlet.http.HttpServletRequest;

/**
 * Prevent access to VirtualFileSystem REST API from outside the IDE.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class RefererHeaderValidator implements RequestValidator {
    @Override
    public void validate(HttpServletRequest request) throws VirtualFileSystemRuntimeException {
        String requestURL = request.getScheme() + "://" + request.getServerName();
        int port = request.getServerPort();
        if (port != 80 && port != 443) {
            requestURL += (":" + port);
        }
        String referer = request.getHeader("Referer");
        if (referer == null || !referer.startsWith(requestURL)) {
            throw new VirtualFileSystemRuntimeException("Access forbidden from outside of IDE. ");
        }
    }
}
