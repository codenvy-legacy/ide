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