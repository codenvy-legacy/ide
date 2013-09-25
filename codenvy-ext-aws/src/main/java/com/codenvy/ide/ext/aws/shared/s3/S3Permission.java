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
 * Type of permission to use with ACL.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public enum S3Permission {
    /**
     * Provides READ, WRITE, READ_ACP, and WRITE_ACP permissions.
     * <p>
     * It does not convey additional rights and is provided only for
     * convenience.
     * </p>
     */
    FullControl("FULL_CONTROL"),

    /**
     * Grants permission to list the bucket when applied to a bucket.
     * Grants permission to read object data
     * and/or metadata when applied to an object.
     */
    Read("READ"),

    /**
     * Grants permission to create, overwrite, and
     * delete any objects in the bucket.
     * <p>
     * This permission is not supported for objects.
     * </p>
     */
    Write("WRITE"),

    /**
     * Grants permission to read the ACL for the applicable bucket or object.
     * <p>
     * The owner of a bucket or object always implicitly has this permission.
     * </p>
     */
    ReadAcp("READ_ACP"),

    /**
     * Gives permission to overwrite the ACP for the applicable bucket or
     * object.
     * <p>
     * The owner of a bucket or object always has this permission implicitly.
     * </p>
     * <p>
     * Granting this permission is equivalent to granting <code>FULL_CONTROL</code>because
     * the grant recipient can make any changes to the ACP.
     * </p>
     */
    WriteAcp("WRITE_ACP");

    private final String value;

    private S3Permission(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    /**
     * Returns the {@link S3Permission} enumeration value representing the specified Amazon
     * S3 Region ID string. If specified string doesn't map to a known Amazon S3
     * Region, returns <code>null</code>.
     *
     * @param value
     *         A string representation of an Amazon S3 permission, eg.
     *         <code>FULL_CONTROL</code>
     * @return The {@link S3Permission} object represented by the given permission string,
     *         Returns <code>null</code> if the string isn't a valid representation
     *         of an Amazon S3 permission.
     */
    public static S3Permission fromValue(String value) {
        for (S3Permission v : S3Permission.values()) {
            if (v.value.equals(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Invalid value '" + value + "' ");
    }
}
