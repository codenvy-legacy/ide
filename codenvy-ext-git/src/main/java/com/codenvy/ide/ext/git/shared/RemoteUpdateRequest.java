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
 * Request to update tracked repositories.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RemoteUpdateRequest.java 67651 2011-03-25 16:15:36Z andrew00x $
 */
public interface RemoteUpdateRequest extends GitRequest {
    /** @return remote name */
    String getName();

    /** @return list tracked branches */
    JsonArray<String> getBranches();

    /**
     * @return if <code>true</code> then {@link #branches} instead of replacing the list of currently tracked branches,
     *         added to that list
     */
    boolean addBranches();

    /** @return remote URLs to be added */
    JsonArray<String> getAddUrl();

    /** @return remote URLs to be removed */
    JsonArray<String> getRemoveUrl();

    /** @return remote push URLs to be added */
    JsonArray<String> getAddPushUrl();

    /** @return remote push URLs to be removed */
    JsonArray<String> getRemovePushUrl();
}