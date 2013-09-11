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
 * Region where stores content on S3
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public enum S3Region {
    US_Standard(null),
    US_West("us-west-1"),
    US_West_2("us-west-2"),
    EU_Ireland("EU"),
    AP_Singapore("ap-southeast-1"),
    AP_Tokyo("ap-northeast-1"),
    SA_SaoPaulo("sa-east-1");

    private final String value;

    private S3Region(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static S3Region fromValue(String value) {
        for (S3Region v : S3Region.values()) {
            if (v.value == null) {
                if (value == null) {
                    return v;
                }
            } else if (v.value.equals(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Invalid value '" + value + "' ");
    }
}
