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
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public enum S3IdentityGroupType {
    AllUsers("AllUsers", "http://acs.amazonaws.com/groups/global/AllUsers"),
    AuthenticatedUsers("AuthenticatedUsers", "http://acs.amazonaws.com/groups/global/AuthenticatedUsers"),
    LogDelivery("LogDelivery", "http://acs.amazonaws.com/groups/s3/LogDelivery");

    private final String value;
    private final String uri;

    private S3IdentityGroupType(String value, String uri) {
        this.value = value;
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return value;
    }

    public static S3IdentityGroupType fromValue(String value) {
        for (S3IdentityGroupType v : S3IdentityGroupType.values()) {
            if (v.value.equals(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Invalid value '" + value + "' ");
    }
}
