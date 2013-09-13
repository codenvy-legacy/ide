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
package org.exoplatform.ide.shell.client.maven;

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
        FAILED("Failed"); //

        private final String value;

        private Status(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    Status getStatus();

    /**
     * Returns exit code.
     *
     * @return {@link Integer} exit code
     */
    int getExitCode();

    /**
     * Returns build error.
     *
     * @return {@link String} error
     */
    String getError();

    /**
     * Returns location of the built war.
     *
     * @return {@link String} built war location
     */
    String getDownloadUrl();

    /**
     * Set build status.
     *
     * @param status
     */
    void setStatus(Status status);

    /**
     * Set exit code.
     *
     * @param exitCode
     */
    void setExitCode(int exitCode);

    /**
     * Set error.
     *
     * @param error
     */
    void setError(String error);

    /**
     * Set built war location.
     *
     * @param downloadUrl
     */
    void setDownloadUrl(String downloadUrl);
}
