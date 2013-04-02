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
