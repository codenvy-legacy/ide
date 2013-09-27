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

import org.exoplatform.ide.vfs.server.exceptions.GitUrlResolveException;

import javax.ws.rs.core.UriInfo;

/**
 * Need for resolving Git URL for public access. It can be need for using continues integration system that use Git for
 * getting source for build project or some other services that manipulate with source via Git. Implementation depend
 * on VirtualFileSystem implementation.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public interface GitUrlResolver {
    String resolve(UriInfo uriInfo, VirtualFileSystem vfs, String itemId) throws GitUrlResolveException;
}
