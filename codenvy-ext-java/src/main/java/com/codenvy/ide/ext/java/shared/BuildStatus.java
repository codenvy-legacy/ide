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

/**
 * Status of build.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface BuildStatus {
    public enum Status {
        IN_PROGRESS("In progress"), //
        SUCCESSFUL("Successful"), //
        FAILED("Failed"), //
        CANCELLED("Cancelled");

        private final String value;

        private Status(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Returns the current build job status.
     *
     * @return current build job status
     */
    Status getStatus();

    int getExitCode();

    /**
     * Returns the specific details about the error.
     *
     * @return an error message
     */
    String getError();

    /**
     * Returns the URL to download artifact.
     *
     * @return URL to download artifact
     */
    String getDownloadUrl();

    /**
     * Sets the current build job status.
     *
     * @param status
     *         current build job status
     */
    void setStatus(Status status);

    void setExitCode(int exitCode);

    /**
     * Sets the message that describes the specific details about the error.
     *
     * @param error
     *         an error message
     */
    void setError(String error);

    /**
     * Changes the URL to download artifact
     *
     * @param downloadUrl
     *         URL to download artifact
     */
    void setDownloadUrl(String downloadUrl);

    String getTime();

    void setTime(String time);
}