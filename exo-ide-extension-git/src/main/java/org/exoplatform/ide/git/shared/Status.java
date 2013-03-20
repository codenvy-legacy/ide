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
package org.exoplatform.ide.git.shared;

import java.util.Set;

/**
 * @author <a href="mailto:dvishinskiy@codenvy.com">Dmitriy Vyshinskiy</a>
 * @version $Id: Status.java 68135 2011-04-08 14:23:36Z andrew00x $
 */
public interface Status
{

   public boolean isClean();

   public void setClean(Boolean clean);

   public boolean getShortFormat();

   public void setShortFormat(Boolean shortFormat);

   public String getBranchName();

   public void setBranchName(String branchName);

   public Set<String> getAdded();

   public void setAdded(Set<String> added);

   public Set<String> getChanged();

   public void setChanged(Set<String> changed);

   public Set<String> getRemoved();

   public void setRemoved(Set<String> removed);

   public Set<String> getMissing();

   public void setMissing(Set<String> missing);

   public Set<String> getModified();

   public void setModified(Set<String> modified);

   public Set<String> getUntracked();

   public void setUntracked(Set<String> untracked);

   public Set<String> getUntrackedFolders();

   public void setUntrackedFolders(Set<String> untrackedFolders);

   public Set<String> getConflicting();

   public void setConflicting(Set<String> conflicting);

}
