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
package com.codenvy.ide.ext.git.shared;

import com.codenvy.ide.json.JsonArray;

/**
 * @author <a href="mailto:dvishinskiy@codenvy.com">Dmitriy Vyshinskiy</a>
 * @version $Id: Status.java 68135 2011-04-08 14:23:36Z andrew00x $
 */
public interface Status {
    boolean clean();

    boolean getShortFormat();

    String getBranchName();

    JsonArray<String> getAdded();

    JsonArray<String> getChanged();

    JsonArray<String> getRemoved();

    JsonArray<String> getMissing();

    JsonArray<String> getModified();

    JsonArray<String> getUntracked();

    JsonArray<String> getUntrackedFolders();

    JsonArray<String> getConflicting();
}