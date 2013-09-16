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
package org.exoplatform.ide.extension.aws.server.s3;

import org.exoplatform.ide.extension.aws.shared.s3.S3AccessControl;
import org.exoplatform.ide.extension.aws.shared.s3.S3IdentityType;
import org.exoplatform.ide.extension.aws.shared.s3.S3Permission;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class S3AccessControlImpl implements S3AccessControl {
    private S3IdentityType identityType;
    private S3Permission   permission;
    private String         identifier;

    public S3AccessControlImpl() {
    }

    public S3AccessControlImpl(S3IdentityType identityType, S3Permission permission, String identifier) {
        this.identityType = identityType;
        this.permission = permission;
        this.identifier = identifier;
    }

    @Override
    public S3IdentityType getIdentityType() {
        return identityType;
    }

    @Override
    public void setIdentityType(S3IdentityType identityType) {
        this.identityType = identityType;
    }

    @Override
    public S3Permission getPermission() {
        return permission;
    }

    @Override
    public void setPermission(S3Permission permission) {
        this.permission = permission;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "S3AccessControlImpl{" +
               "identityType=" + identityType +
               ", permission=" + permission +
               ", identifier='" + identifier + '\'' +
               '}';
    }
}
