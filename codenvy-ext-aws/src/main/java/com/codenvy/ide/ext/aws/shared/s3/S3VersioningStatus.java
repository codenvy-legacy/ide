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
package com.codenvy.ide.ext.aws.shared.s3;

/**
 * S3 bucket versioning configuration states.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public enum S3VersioningStatus {
    /**
     * By default, for new S3 Buckets versioning value is off.
     * Once versioning is enabled, it can never be reverted to status "Off", it's can be changed to "Suspend".
     */
    OFF("Off"),

    /** Indicating that versioning is disabled for S3 bucked that has enabled value before. */
    SUSPENDED("Suspended"),

    /** Indicating that versioning is enabled for S3 bucket. */
    ENABLED("Enabled");

    private final String value;

    private S3VersioningStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static S3VersioningStatus fromValue(String value) {
        for (S3VersioningStatus v : S3VersioningStatus.values()) {
            if (v.value.equals(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Invalid value '" + value + "' ");
    }
}
