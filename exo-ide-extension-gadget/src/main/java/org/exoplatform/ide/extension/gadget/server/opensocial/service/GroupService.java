/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.extension.gadget.server.opensocial.service;

import org.exoplatform.ide.extension.gadget.server.opensocial.model.Group;

import java.util.List;

/**
 * Service, used to manipulate with OpenSocial Groups data.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 19, 2010 $
 * 
 */
public interface GroupService
{
   /**
    * Retrieving information about one or multiple groups in a single request.
    * 
    * @param userId user ID of the person initiating the group request
    * @param groupId group Id of the single group to be returned by the request (optional)
    * @return {@link List<{@link Group}>}
    */
   List<Group> getGroups(String userId, String groupId);

   /**
    * @param userId user ID of the person initiating the group creation request
    * @param group group object specifying group to be created
    * @return {@link Group} created group
    */
   Group createGroup(String userId, Group group);

   /**
    * Request to update a group.
    * 
    * @param userId user ID of the person initiating the update request
    * @param group group object containing the updated fields
    * @return {@link Group} updated group
    */
   Group updateGroup(String userId, Group group);

   /**
    * Request to remove group.
    * 
    * @param userId user ID of the person initiating the group deletion request
    * @param groupId group ID specifying group to be deleted
    */
   void deleteGroup(String userId, String groupId);
}
