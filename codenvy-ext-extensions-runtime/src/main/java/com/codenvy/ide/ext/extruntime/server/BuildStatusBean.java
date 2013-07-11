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
package com.codenvy.ide.ext.extruntime.server;

import org.exoplatform.ide.extension.maven.shared.BuildStatus;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class BuildStatusBean implements BuildStatus {

    private Status status;

    private int exitCode;

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

    /** @see org.exoplatform.ide.extension.maven.shared.BuildStatus#setStatus(org.exoplatform.ide.extension.maven.shared.BuildStatus
     * .Status) */
    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    /** @see org.exoplatform.ide.extension.maven.shared.BuildStatus#setExitCode(int) */
    @Override
    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    /** @see org.exoplatform.ide.extension.maven.shared.BuildStatus#setError(java.lang.String) */
    @Override
    public void setError(String error) {
        this.error = error;
    }

    /** @see org.exoplatform.ide.extension.maven.shared.BuildStatus#setDownloadUrl(java.lang.String) */
    @Override
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    /** @see org.exoplatform.ide.extension.maven.shared.BuildStatus#getTime() */
    @Override
    public String getTime() {
        return time;
    }

    /** @see org.exoplatform.ide.extension.maven.shared.BuildStatus#setTime(java.lang.String) */
    @Override
    public void setTime(String time) {
        this.time = time;
    }

}
