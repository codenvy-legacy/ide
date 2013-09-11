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