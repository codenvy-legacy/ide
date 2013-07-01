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
