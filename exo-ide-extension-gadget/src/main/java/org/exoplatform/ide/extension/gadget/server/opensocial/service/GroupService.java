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
package org.exoplatform.ide.extension.gadget.server.opensocial.service;

import org.exoplatform.ide.extension.gadget.server.opensocial.model.Group;

import java.util.List;

/**
 * Service, used to manipulate with OpenSocial Groups data.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 19, 2010 $
 */
public interface GroupService {
    /**
     * Retrieving information about one or multiple groups in a single request.
     *
     * @param userId
     *         user ID of the person initiating the group request
     * @param groupId
     *         group Id of the single group to be returned by the request (optional)
     * @return {@link List<{@link Group}>}
     */
    List<Group> getGroups(String userId, String groupId);

    /**
     * @param userId
     *         user ID of the person initiating the group creation request
     * @param group
     *         group object specifying group to be created
     * @return {@link Group} created group
     */
    Group createGroup(String userId, Group group);

    /**
     * Request to update a group.
     *
     * @param userId
     *         user ID of the person initiating the update request
     * @param group
     *         group object containing the updated fields
     * @return {@link Group} updated group
     */
    Group updateGroup(String userId, Group group);

    /**
     * Request to remove group.
     *
     * @param userId
     *         user ID of the person initiating the group deletion request
     * @param groupId
     *         group ID specifying group to be deleted
     */
    void deleteGroup(String userId, String groupId);
}
