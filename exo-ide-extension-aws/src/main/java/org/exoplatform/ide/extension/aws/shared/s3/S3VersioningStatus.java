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
package org.exoplatform.ide.extension.aws.shared.s3;

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
