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
package com.codenvy.ide.ext.aws.shared.s3;

/**
 * Specifies constants defining a group of Amazon S3 users
 * who can be granted permissions to Amazon S3 buckets and objects.
 * This enumeration contains all the valid Amazon S3 group grantees.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public enum S3IdentityGroupType {
    /**
     * Grants anonymous access to any Amazon S3 object or bucket. Any user will
     * be able to access the object by omitting the AWS Key ID and Signature
     * from a request.
     * <p>
     * Amazon highly recommends that users do not grant the
     * <code>AllUsers</code> group write
     * access to their buckets. If granted, users will have no control over the objects
     * others can store and their associated charges.
     * </p>
     */
    AllUsers("AllUsers", "http://acs.amazonaws.com/groups/global/AllUsers"),

    /**
     * Grants access to buckets or objects to anyone with an Amazon AWS account.
     * Although this is inherently insecure as any AWS user who is aware of the
     * bucket or object will be able to access it, users may find this authentication
     * method useful.
     */
    AuthenticatedUsers("AuthenticatedUsers", "http://acs.amazonaws.com/groups/global/AuthenticatedUsers"),

    /**
     * Grants access to Amazon S3 log delivery so that an S3 bucket can receive
     * server access logs. Turning on server access logging for an Amazon S3
     * bucket requires that the bucket receiving the logs is granted permission
     * for the log delivery group to deliver logs.
     */
    LogDelivery("LogDelivery", "http://acs.amazonaws.com/groups/s3/LogDelivery");

    private final String value;
    private final String uri;

    private S3IdentityGroupType(String value, String uri) {
        this.value = value;
        this.uri = uri;
    }

    /**
     * Gets the group grantee's URI.
     *
     * @return The group grantee's URI.
     */
    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return value;
    }

    /**
     * Gets the {@link S3IdentityGroupType} enumeration value
     * with the specified Amazon S3 group URI (eg.
     * http://acs.amazonaws.com/groups/global/AllUsers).
     * Returns <code>null</code> if an invalid
     * Amazon S3 group URI is specified.
     *
     * @param value
     *         A string representation of an Amazon S3 group URI (eg.
     *         http://acs.amazonaws.com/groups/global/AllUsers)
     * @return The {@link S3IdentityGroupType} object represented by the given Amazon S3 group
     *         URI string. Returns <code>null</code>
     *         if the string isn't a valid Amazon S3 group
     *         URI.
     */
    public static S3IdentityGroupType fromValue(String value) {
        for (S3IdentityGroupType v : S3IdentityGroupType.values()) {
            if (v.value.equals(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Invalid value '" + value + "' ");
    }
}
