/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.server;

import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a> */
public abstract class VirtualFileSystemUserContext {

    protected VirtualFileSystemUserContext() {
    }

    public abstract VirtualFileSystemUser getVirtualFileSystemUser();

    public static VirtualFileSystemUserContext newInstance() {
        return new DefaultVirtualFileSystemUserContext();
    }

    private static class DefaultVirtualFileSystemUserContext extends VirtualFileSystemUserContext {
        public VirtualFileSystemUser getVirtualFileSystemUser() {
            final ConversationState cs = ConversationState.getCurrent();

            if (cs == null) {
                return new VirtualFileSystemUser(VirtualFileSystemInfo.ANONYMOUS_PRINCIPAL, Collections.<String>emptySet());
            }
            final Identity identity = cs.getIdentity();
            final Set<String > groups = new HashSet<String>(2);
            if (identity.getRoles().contains("developer")) {
                groups.add("workspace/developer");
            }
            if (identity.getRoles().contains("admin")) {
                groups.add("workspace/admin");
            }
            return new VirtualFileSystemUser(identity.getUserId(), groups);
        }
    }
}
