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