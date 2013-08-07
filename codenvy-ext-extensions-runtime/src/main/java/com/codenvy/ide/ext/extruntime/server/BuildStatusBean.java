/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.extruntime.server;

import com.codenvy.ide.extension.maven.shared.BuildStatus;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class BuildStatusBean implements BuildStatus {

    private Status status;

    private int    exitCode;

    private String error;

    private String downloadUrl;

    private String time;

    /** Default constructor */
    public BuildStatusBean() {
    }

    /**
     * @param status
     * @param exitCode
     * @param error
     * @param downloadUrl
     */
    public BuildStatusBean(Status status, int exitCode, String error, String downloadUrl, String time) {
        super();
        this.status = status;
        this.exitCode = exitCode;
        this.error = error;
        this.downloadUrl = downloadUrl;
        this.time = time;
    }

    /** @see org.exoplatform.ide.extension.maven.shared.BuildStatus#getStatus() */
    @Override
    public Status getStatus() {
        return status;
    }

    /** @see org.exoplatform.ide.extension.maven.shared.BuildStatus#getExitCode() */
    @Override
    public int getExitCode() {
        return exitCode;
    }

    /** @see org.exoplatform.ide.extension.maven.shared.BuildStatus#getError() */
    @Override
    public String getError() {
        return error;
    }

    /** @see org.exoplatform.ide.extension.maven.shared.BuildStatus#getDownloadUrl() */
    @Override
    public String getDownloadUrl() {
        return downloadUrl;
    }

    /** @see org.exoplatform.ide.extension.maven.shared.BuildStatus#getTime() */
    @Override
    public String getTime() {
        return time;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
