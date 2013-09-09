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
 * Request to create new git repository.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: InitRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class InitRequest extends GitRequest {
    /** Working directory for new git repository. */
    private String  workingDir;

    /** If <code>true</code> then bare repository created. */
    private boolean bare;

    /**
     * @param workingDir working directory for new git repository
     * @param bare <code>true</code> then bare repository created
     */
    public InitRequest(String workingDir, boolean bare) {
        this.workingDir = workingDir;
        this.bare = bare;
    }

    /**
     * "Empty" init request. Corresponding setters used to setup required parameters.
     */
    public InitRequest() {
    }

    /** @return working directory for new git repository */
    public String getWorkingDir() {
        return workingDir;
    }

    /**
     * @param workingDir working directory for new git repository
     */
    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    /** @return <code>true</code> then bare repository created */
    public boolean isBare() {
        return bare;
    }

    /**
     * @param bare <code>true</code> then bare repository created
     */
    public void setBare(boolean bare) {
        this.bare = bare;
    }
}
