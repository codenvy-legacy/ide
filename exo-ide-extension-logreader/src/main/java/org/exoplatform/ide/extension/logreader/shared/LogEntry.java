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
package org.exoplatform.ide.extension.logreader.shared;

/**
 * Interface represent log one logical Log file.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: Job.java Mar 15, 2012 3:14:27 PM azatsarynnyy $
 */
public interface LogEntry {

    /**
     * Get identifier for the log file.
     *
     * @return the token
     */
    public String getLrtoken();

    /**
     * Set identifier for the log file.
     *
     * @param lrtoken
     *         the token to set
     */
    public void setLrtoken(String lrtoken);

    /**
     * Get content of the log file.
     *
     * @return the content
     */
    public String getContent();

    /**
     * Set content of the log file.
     *
     * @param content
     *         the content to set
     */
    public void setContent(String content);

    /**
     * Is log has next log file.
     *
     * @return true if log has next log file
     */
    public boolean isHasNext();

    /** @param hasNext */
    public void setHasNext(boolean hasNext);

    /**
     * Is log has previous log file.
     *
     * @return true if log has previous log file
     */
    public boolean isHasPrevious();

    /** @param hasPrevious */
    public void setHasPrevious(boolean hasPrevious);

}