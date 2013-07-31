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
@Path("{ws-name}/user")
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
        String userId = ConversationState.getCurrent().getIdentity().getUserId();
        DtoServerImpls.UserImpl user = DtoServerImpls.UserImpl.make();
        user.setUserId(userId); //userId - "user alias" e.g. email

        try {
            User currentUser = userManager.getUserByAlias(userId);

            user.setProfileAttributes(currentUser.getProfile().getAttributes());
        } catch (OrganizationServiceException e) {
            // do nothing
        }

        return user.toJson();
    }

    /**
     * Updates user's attributes from information what contains into updateUserAttributes. If some attributes aren't exist then these
     * attributes will be added. This method can add/update many attribute per one operation.
     *
     * @param jsonUpdateAttributes
     */
    @Path("update")
    @POST
    public void updateUserAttributes(String jsonUpdateAttributes) {
        try {
            DtoServerImpls.UpdateUserAttributesImpl updateUserAttributes =
                    DtoServerImpls.UpdateUserAttributesImpl.fromJsonString(jsonUpdateAttributes);

            String userId = ConversationState.getCurrent().getIdentity().getUserId();
            User user = userManager.getUserByAlias(userId);
            final Profile profile = user.getProfile();

            updateUserAttributes.getAttributes().iterate(new JsonStringMap.IterationCallback<String>() {
                @Override
                public void onIteration(String key, String value) {
                    if (value == null) {
                        profile.removeAttribute(key);
                    } else {
                        profile.setAttribute(key, value);
                    }
                }
            });

            userManager.updateUser(user);
        } catch (OrganizationServiceException e) {
            throw new IllegalStateException("Problem with update user's attribute. Please contact support.", e);
        }
    }
}