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
package org.exoplatform.ide.jrebel.server;

import com.codenvy.organization.client.UserManager;
import com.codenvy.organization.exception.OrganizationServiceException;
import com.codenvy.organization.model.Profile;
import com.codenvy.organization.model.User;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="vzhukovskii@exoplatform.com">Vladyslav Zhukovskii</a>
 * @version $Id: JRebelProfilerService.java 34027 19.12.12 17:02Z vzhukovskii $
 */
@Path("{ws-name}/jrebel")
public class JRebelConsumerService {
    @Inject
    UserManager userManager;

    private static final Log LOG = ExoLogger.getLogger("JRebel");

    @Path("profile/send")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void sendProfileInfo(Map<String, String> values) throws JRebelConsumerException {
        String userId = ConversationState.getCurrent().getIdentity().getUserId();
        try {
            User user = userManager.getUserByAlias(userId);
            user.getProfile().setAttributes(values);
            userManager.updateUser(user);

            LOG.info("EVENT#jrebel-user-profile-info# USER-ID#" + userId + "# FIRSTNAME#" + values.get("firstName") + "# LASTNAME#" + values.get("lastName")
                     + "# PHONE#" + values.get("phone") + "#");
        } catch (OrganizationServiceException e) {
            throw new JRebelConsumerException("Unable to register profile info. Please contact support.", e);
        }
    }

    @Path("profile/get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> getProfileInfo() throws JRebelConsumerException {
        String userId = ConversationState.getCurrent().getIdentity().getUserId();
        try {
            Profile profile = userManager.getUserByAlias(userId).getProfile();
            Map<String, String> values = new HashMap<String, String>();
            if (profile.getAttribute("firstName") != null)
                values.put("firstName", profile.getAttribute("firstName"));
            if (profile.getAttribute("lastName") != null)
                values.put("lastName", profile.getAttribute("lastName"));
            if (profile.getAttribute("phone") != null)
                values.put("phone", profile.getAttribute("phone"));
            return values;
        } catch (OrganizationServiceException e) {
            throw new JRebelConsumerException("Unable to get profile info. Please contact support.", e);
        }
    }
}