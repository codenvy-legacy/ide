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
package org.exoplatform.ide.extension.aws.shared.ec2;

/**
 * Describes AWS security group.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface SecurityGroupInfo {
    /**
     * Get AWS Access Key ID of the owner of the security group.
     *
     * @return ID of the owner of the security group
     */
    String getOwnerId();

    /**
     * Set AWS Access Key ID of the owner of the security group.
     *
     * @param ownerId
     *         ID of the owner of the security group
     */
    void setOwnerId(String ownerId);

    /**
     * Get name of this security group.
     *
     * @return name of this security group
     */
    String getName();

    /**
     * Set name of this security group.
     *
     * @param name
     *         name of this security group
     */
    void setName(String name);

    /**
     * Get ID of this security group.
     *
     * @return ID of this security group
     */
    String getId();

    /**
     * Set ID of this security group.
     *
     * @param id
     *         ID of this security group
     */
    void setId(String id);

    /**
     * Get description of this security group.
     *
     * @return description of this security group
     */
    String getDescription();

    /**
     * Set description of this security group.
     *
     * @param description
     *         description of this security group
     */
    void setDescription(String description);
}
