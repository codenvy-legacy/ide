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
package com.codenvy.ide.ext.java.shared;

/** @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a> */
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

    /** {@inheritDoc} */
    @Override
    public Status getStatus() {
        return status;
    }

    /** {@inheritDoc} */
    @Override
    public int getExitCode() {
        return exitCode;
    }

    /** {@inheritDoc} */
    @Override
    public String getError() {
        return error;
    }

    /** {@inheritDoc} */
    @Override
    public String getDownloadUrl() {
        return downloadUrl;
    }

    /** {@inheritDoc} */
    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    /** {@inheritDoc} */
    @Override
    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    /** {@inheritDoc} */
    @Override
    public void setError(String error) {
        this.error = error;
    }

    /** {@inheritDoc} */
    @Override
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    /** {@inheritDoc} */
    @Override
    public String getTime() {
        return time;
    }

    /** {@inheritDoc} */
    @Override
    public void setTime(String time) {
        this.time = time;
    }
}