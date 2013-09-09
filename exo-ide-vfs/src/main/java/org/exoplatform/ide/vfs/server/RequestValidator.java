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

/**
 * Check is it allowed to use {@link VirtualFileSystem} for client that makes specified HttpServletRequest.
 * Implementation of this interface may check any parameter of HTTP request and throw {@link
 * VirtualFileSystemRuntimeException} if tested parameter has unexpected value.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface RequestValidator {
    void validate(javax.servlet.http.HttpServletRequest request) throws VirtualFileSystemRuntimeException;
}
