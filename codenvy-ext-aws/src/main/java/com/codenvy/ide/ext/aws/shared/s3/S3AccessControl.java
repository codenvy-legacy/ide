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
 * Represent information about user identify type and permission given for it.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface S3AccessControl {
    /**
     * Get type of identity e.g. uri, email or canonical name.
     *
     * @return identity type
     */
    S3IdentityType getIdentityType();

    /**
     * Get permission given for this user.
     *
     * @return type of permission
     */
    S3Permission getPermission();

    /**
     * Get user identity value. For example it maybe canonical username or user email representing user account, or
     * if maybe Amazon S3 group, representing by url link.
     *
     * @return value of the user identity
     */
    String getIdentifier();
}
