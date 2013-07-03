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
package com.codenvy.ide.ext.aws.shared.ec2;

import com.codenvy.ide.json.JsonStringMap;

/**
 * Information about specific Amazon machine image
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ImageInfo {
    /**
     * Get unique ID of the AMI.
     *
     * @return Amazon machine image ID
     */
    String getAmiId();

    /**
     * Get the location of the AMI.
     *
     * @return the location of the AMI
     */
    String getManifest();

    /**
     * Get current state of the AMI.
     *
     * @return state of the AMI
     */
    ImageState getState();

    /**
     * Get EC2 machine image owner ID
     *
     * @return ID of the machine image owner
     */
    String getOwnerId();

    /**
     * Get the AWS account alias (e.g., "amazon", "redhat", "self", etc.) or AWS account ID that owns the AMI.
     *
     * @return account alias or account ID that owns the AMI
     */
    String getOwnerAlias();

    /**
     * Get tags for the EC2 machine image
     *
     * @return list of tags contains tag key and value
     */
    JsonStringMap<String> getTags();
}
