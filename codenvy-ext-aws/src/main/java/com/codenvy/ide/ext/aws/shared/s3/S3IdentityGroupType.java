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
