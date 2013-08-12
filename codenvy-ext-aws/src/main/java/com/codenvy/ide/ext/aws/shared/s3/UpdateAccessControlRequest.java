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

import com.codenvy.ide.json.JsonArray;

/**
 * Describe information about list of permissions to add and list of permissions to delete from current Access Control
 * List.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface UpdateAccessControlRequest {
    /**
     * Get list of permissions to add to current ACL.
     *
     * @return list of permissions to add to Acl
     */
    JsonArray<S3AccessControl> getS3AccessControlsToAdd();

    /**
     * Get list of permissions to delete from current ACL.
     *
     * @return list of permissions to delete from Acl
     */
    JsonArray<S3AccessControl> getS3AccessControlsToDelete();
}
