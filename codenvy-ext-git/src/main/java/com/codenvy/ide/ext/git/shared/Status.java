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

import java.util.Set;

/**
 * @author <a href="mailto:dvishinskiy@codenvy.com">Dmitriy Vyshinskiy</a>
 * @version $Id: Status.java 68135 2011-04-08 14:23:36Z andrew00x $
 */
public interface Status {
    boolean isClean();

    void setClean(Boolean clean);

    boolean getShortFormat();

    void setShortFormat(Boolean shortFormat);

    String getBranchName();

    void setBranchName(String branchName);

    Set<String> getAdded();

    void setAdded(Set<String> added);

    Set<String> getChanged();

    void setChanged(Set<String> changed);

    Set<String> getRemoved();

    void setRemoved(Set<String> removed);

    Set<String> getMissing();

    void setMissing(Set<String> missing);

    Set<String> getModified();

    void setModified(Set<String> modified);

    Set<String> getUntracked();

    void setUntracked(Set<String> untracked);

    Set<String> getUntrackedFolders();

    void setUntrackedFolders(Set<String> untrackedFolders);

    Set<String> getConflicting();

    void setConflicting(Set<String> conflicting);
}