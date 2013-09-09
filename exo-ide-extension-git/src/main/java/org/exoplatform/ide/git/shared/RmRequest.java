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
package org.exoplatform.ide.git.shared;

/**
 * Request to remove files.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RmRequest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
public class RmRequest extends GitRequest {
    /** List of files to remove. */
    private String[] files;

    /** Only from index */
    private Boolean  cached;


    /**
     * @param files files to remove
     */
    public RmRequest(String[] files) {
        this.files = files;
    }

    /**
     * "Empty" remove request. Corresponding setters used to setup required parameters.
     */
    public RmRequest() {
    }

    /** @return files to remove */
    public String[] getFiles() {
        return files;
    }

    /**
     * @param files files to remove
     */
    public void setFiles(String[] files) {
        this.files = files;
    }

    /** @return is RmRequest represents remove from index only */
    public Boolean getCached() {
        return cached;
    }

    /**
     * @param Boolean cached represents remove from index only
     */
    public void setCached(Boolean cached) {
        this.cached = cached;
    }
}
