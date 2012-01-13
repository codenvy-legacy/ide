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

import org.exoplatform.ide.extension.gadget.server.opensocial.model.EscapeType;
import org.exoplatform.ide.extension.gadget.server.opensocial.model.Person;

import java.util.List;

/**
 * Service is used for operations with OpenSocial Person data (retrieving, saving, deleting).
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 19, 2010 $
 * 
 */
public interface PeopleService
{
   /**
    * A request for a Person object. User id defaults to "@me", which MUST return the currently logged in user.
    * 
    * @param userId User ID of person to retrieve.
    * @param fields An array of Person field names.
    * @param escapeType the type of escaping to use on any AppData values included in the response.
    * @return {@link Person} requested person
    */
   Person getPerson(String userId, List<String> fields, EscapeType escapeType);

   /**
    * @param userId of person to retrieve
    * @param groupId Group ID of the group of users related to User ID
    * @param fields an array of Person field names
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
    * @param userId user id
    * @return {@link List<{@link Person} >} list of deleted friends
    */
   List<Person> getDeletedFriends(String userId);

   /**
    * A request to create a relationship MUST support the Standard-Request-Parameters and the following additional parameters:
    * 
    * @param userId user id
    * @param groupId the Group ID specifying the type of relationship
    * @param person the target of the relationship
    */
   void createRelationship(String userId, String groupId, Person person);

   /**
    * Request to update a person.
    * 
    * @param userId user ID of the person initiating the update request
    * @param groupId group ID specifying the type of relationship
    * @param person person object containing the updated fields
    * @return {@link Person}
    */
   Person updatePerson(String userId, String groupId, Person person);

   /**
    * Request to remove a Person.
    * 
    * @param userId user id
    */
   void deletePerson(String userId);
}
