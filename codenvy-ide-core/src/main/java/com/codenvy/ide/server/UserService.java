/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.server;

import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.organization.client.UserManager;
import com.codenvy.organization.exception.OrganizationServiceException;
import com.codenvy.organization.model.Profile;
import com.codenvy.organization.model.User;

import org.exoplatform.services.security.ConversationState;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Server service for manage information of user.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Path("ide/user")
public class UserService {

    @Inject
    UserManager userManager;

    /**
     * Returns current user with additional information.
     *
     * @return current user
     */
    @Path("get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUser() {
        try {
            String userId = ConversationState.getCurrent().getIdentity().getUserId();
            User currentUser = userManager.getUserByAlias(userId);

            DtoServerImpls.UserImpl user = DtoServerImpls.UserImpl.make();
            user.setUserId(currentUser.getId());
            user.setProfileAttributes(currentUser.getProfile().getAttributes());

            return user.toJson();

        } catch (OrganizationServiceException e) {
            throw new IllegalStateException("Can't get user info. Please contact support.", e);
        }
    }

    /**
     * Updates user's attribute from information what contains into jsonUpdateAttribute. If the attribute isn't exist then this attribute
     * will add.
     *
     * @param jsonUpdateAttribute
     */
    @Path("update/attribute")
    @POST
    public void updateUserAttribute(String jsonUpdateAttribute) {
        try {
            DtoServerImpls.UpdateUserAttributeImpl updateUserAttribute =
                    DtoServerImpls.UpdateUserAttributeImpl.fromJsonString(jsonUpdateAttribute);

            User user = userManager.getUserById(updateUserAttribute.getUserId());
            user.getProfile().setAttribute(updateUserAttribute.getAttributeName(), updateUserAttribute.getAttributeValue());
        } catch (OrganizationServiceException e) {
            throw new IllegalStateException("Problem with update user's attribute. Please contact support.", e);
        }
    }

    /**
     * Removes user's attribute. Which attribute needs to remove describe into jsonRemoveAttribute. If the attribute isn't exist then no
     * attribute will be removed.
     *
     * @param jsonRemoveAttribute
     */
    @Path("remove/attribute")
    @POST
    public void removeUserAttribute(String jsonRemoveAttribute) {
        try {
            DtoServerImpls.RemoveUserAttributeImpl removeUserAttribute =
                    DtoServerImpls.RemoveUserAttributeImpl.fromJsonString(jsonRemoveAttribute);

            User user = userManager.getUserById(removeUserAttribute.getUserId());
            user.getProfile().removeAttribute(removeUserAttribute.getAttributeName());
        } catch (OrganizationServiceException e) {
            throw new IllegalStateException("Problem with remove user's attribute. Please contact support.", e);
        }
    }

    /**
     * Updates user's attributes from information what contains into updateUserAttributes. If some attributes aren't exist then these
     * attributes will be added. This method can add/update many attribute per one operation.
     *
     * @param jsonUpdateAttributes
     */
    @Path("update/attributes")
    @POST
    public void updateUserAttributes(String jsonUpdateAttributes) {
        try {
            DtoServerImpls.UpdateUserAttributesImpl updateUserAttributes =
                    DtoServerImpls.UpdateUserAttributesImpl.fromJsonString(jsonUpdateAttributes);

            User user = userManager.getUserById(updateUserAttributes.getUserId());
            final Profile profile = user.getProfile();

            updateUserAttributes.getAttributes().iterate(new JsonStringMap.IterationCallback<String>() {
                @Override
                public void onIteration(String key, String value) {
                    profile.setAttribute(key, value);
                }
            });
        } catch (OrganizationServiceException e) {
            throw new IllegalStateException("Problem with update user's attribute. Please contact support.", e);
        }
    }
}