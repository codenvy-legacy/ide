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

import org.exoplatform.ide.extension.gadget.server.opensocial.model.EscapeType;
import org.exoplatform.ide.extension.gadget.server.opensocial.model.Person;

import java.util.List;

/**
 * Service is used for operations with OpenSocial Person data (retrieving, saving, deleting).
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 19, 2010 $
 */
public interface PeopleService {
    /**
     * A request for a Person object. User id defaults to "@me", which MUST return the currently logged in user.
     *
     * @param userId
     *         User ID of person to retrieve.
     * @param fields
     *         An array of Person field names.
     * @param escapeType
     *         the type of escaping to use on any AppData values included in the response.
     * @return {@link Person} requested person
     */
    Person getPerson(String userId, List<String> fields, EscapeType escapeType);

    /**
     * @param userId
     *         of person to retrieve
     * @param groupId
     *         Group ID of the group of users related to User ID
     * @param fields
     *         an array of Person field names
     * @param networkDistance
     * @return {@link List<{@link Person} >} list of people
     */
    List<Person> getPeopleList(String userId, String groupId, List<String> fields, Float networkDistance);

    /**
     * Retrieve a list of supported Person fields.
     *
     * @return {@link List<{@link String} >} list of supported fields
     */
    List<String> getPersonFields();

    /**
     * Get list of people who are no longer friends with a given user.
     *
     * @param userId
     *         user id
     * @return {@link List<{@link Person} >} list of deleted friends
     */
    List<Person> getDeletedFriends(String userId);

    /**
     * A request to create a relationship MUST support the Standard-Request-Parameters and the following additional parameters:
     *
     * @param userId
     *         user id
     * @param groupId
     *         the Group ID specifying the type of relationship
     * @param person
     *         the target of the relationship
     */
    void createRelationship(String userId, String groupId, Person person);

    /**
     * Request to update a person.
     *
     * @param userId
     *         user ID of the person initiating the update request
     * @param groupId
     *         group ID specifying the type of relationship
     * @param person
     *         person object containing the updated fields
     * @return {@link Person}
     */
    Person updatePerson(String userId, String groupId, Person person);

    /**
     * Request to remove a Person.
     *
     * @param userId
     *         user id
     */
    void deletePerson(String userId);
}
