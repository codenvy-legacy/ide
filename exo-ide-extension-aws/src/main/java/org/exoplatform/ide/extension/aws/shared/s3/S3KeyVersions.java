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
package org.exoplatform.ide.extension.aws.shared.s3;

import java.util.List;

/**
 * Information about S3 key versions. Used with rest-method (deleteVersion()) to delete S3 keys with specific version ID.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface S3KeyVersions {
    /**
     * Get S3 key name
     *
     * @return
     */
    String getS3Key();

    /**
     * Set S3 key name
     *
     * @param s3Key
     */
    void setS3Key(String s3Key);

    /**
     * Get list of versions for S3 key
     *
     * @return
     */
    List<String> getVersions();

    /**
     * Set list of versions for S3 key
     *
     * @param versions
     */
    void setVersions(List<String> versions);
}
