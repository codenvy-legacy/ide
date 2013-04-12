/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.shared;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface File extends Item {
    /** @return version id */
    String getVersionId();

    /**
     * @param versionId
     *         the version id
     */
    void setVersionId(String versionId);

    /** @return content length */
    long getLength();

    /**
     * @param length
     *         the content length
     */
    void setLength(long length);

    /** @return date of last modification */
    long getLastModificationDate();

    /**
     * @param lastModificationDate
     *         the date of last modification
     */
    void setLastModificationDate(long lastModificationDate);

    /** @return <code>true</code> if object locked and <code>false</code> otherwise */
    boolean isLocked();

    /**
     * @param locked
     *         locking flag. Must be <code>true</code> if object locked and <code>false</code> otherwise
     */
    void setLocked(boolean locked);
}
