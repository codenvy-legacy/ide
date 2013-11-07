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

import java.util.Iterator;
import java.util.ServiceLoader;

/** @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a> */
public abstract class VirtualFileSystemUserContext {

    private static VirtualFileSystemUserContextProvider userContextProvider;

    static {
        ServiceLoader<VirtualFileSystemUserContextProvider> sl = ServiceLoader.load(VirtualFileSystemUserContextProvider.class);
        Iterator<VirtualFileSystemUserContextProvider> iterator = sl.iterator();
        if (iterator.hasNext()) {
            userContextProvider = iterator.next();
        } else {
            userContextProvider = new ConversationStateUserContextProvider();
        }
    }

    protected VirtualFileSystemUserContext() {
    }

    public abstract VirtualFileSystemUser getVirtualFileSystemUser();

    public static VirtualFileSystemUserContext newInstance() {
        return userContextProvider.newUserContext();
    }
}
