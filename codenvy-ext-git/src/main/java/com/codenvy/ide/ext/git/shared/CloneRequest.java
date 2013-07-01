/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package com.codenvy.ide.ext.git.shared;

import com.codenvy.ide.json.JsonArray;

/**
 * Clone repository to {@link #workingDir}.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CloneRequest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
public interface CloneRequest extends GitRequest {
    /** @return URI of repository to be cloned */
    String getRemoteUri();

    /** @return list of remote branches to fetch in cloned repository */
    JsonArray<String> getBranchesToFetch();

    /** @return work directory for cloning */
    String getWorkingDir();

    /** @return remote name. If <code>null</code> then 'origin' will be used */
    String getRemoteName();

    /**
     * @return time (in seconds) to wait without data transfer occurring before aborting fetching data from remote repository. If 0 then
     *         default timeout may be used. This is implementation specific
     */
    int getTimeout();
}