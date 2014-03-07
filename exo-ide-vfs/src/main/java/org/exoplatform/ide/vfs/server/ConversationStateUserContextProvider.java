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

import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** @author <a href="mailto:aparfonov@codenvy.com">Andrey Parfonov</a> */
public class ConversationStateUserContextProvider implements VirtualFileSystemUserContextProvider {
    @Override
    public VirtualFileSystemUserContext newUserContext() {
        return new ConversationStateUserContext();
    }

    private static class ConversationStateUserContext extends VirtualFileSystemUserContext {
        public VirtualFileSystemUser getVirtualFileSystemUser() {
            final ConversationState cs = ConversationState.getCurrent();

            if (cs == null) {
                return new VirtualFileSystemUser(VirtualFileSystemInfo.ANONYMOUS_PRINCIPAL, Collections.<String>emptySet());
            }
            final Identity identity = cs.getIdentity();
            final Set<String > groups = new HashSet<>(2);
            if (identity.getRoles().contains("workspace/developer")) {
                groups.add("workspace/developer");
            }
            if (identity.getRoles().contains("workspace/admin")) {
                groups.add("workspace/admin");
            }
            return new VirtualFileSystemUser(identity.getUserId(), groups);
        }
    }
}
