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
package org.exoplatform.ide.security.openid;

import com.codenvy.commons.lang.NameGenerator;

import org.exoplatform.ide.security.login.FederatedLoginList;
import org.exoplatform.ide.security.openid.extensions.UIExtension;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("{ws-name}/openid")
public class OpenIDAuthenticationService {
    private static final Log LOG = ExoLogger.getLogger(OpenIDAuthenticationService.class);

    private static final Map<String, String> openIdProviders;

    static {
        openIdProviders = new HashMap<String, String>(1);
        openIdProviders.put("google", "https://www.google.com/accounts/o8/id");
    }

    private final FederatedLoginList loginList;

    public OpenIDAuthenticationService(FederatedLoginList loginList) {
        this.loginList = loginList;
    }

    @Path("authenticate")
    @GET
    public Response authenticate(@Context UriInfo uriInfo, @Context HttpServletRequest servletRequest) throws OpenIDException {
        final String openIDProviderName = uriInfo.getQueryParameters().getFirst("openid_provider");
        final String discoveryUrl = openIdProviders.get(openIDProviderName);
        if (discoveryUrl == null) {
            LOG.error("Unsupported OpenID provider {} ", openIDProviderName);
            throw new WebApplicationException(Response
                                                      .status(400)
                                                      .entity("Unsupported OpenID provider " + openIDProviderName)
                                                      .type(MediaType.TEXT_PLAIN).build());
        }

        HttpSession session = servletRequest.getSession();
        final String redirectAfterLogin = uriInfo.getQueryParameters().getFirst("redirect_after_login");
        if (redirectAfterLogin != null) {
            session.setAttribute("openid.redirect_after_login", redirectAfterLogin);
        }

        ConsumerManager consumerManager = new ConsumerManager();
        DiscoveryInformation discovered = consumerManager.associate(consumerManager.discover(discoveryUrl));
        session.setAttribute("openid.consumer", consumerManager);
        session.setAttribute("openid.discovered", discovered);

        final String returnTo = uriInfo.getBaseUriBuilder().path(getClass(), "verify").build().toString();
        AuthRequest req = consumerManager.authenticate(discovered, returnTo);
        FetchRequest fetch = FetchRequest.createFetchRequest();
        fetch.addAttribute("email", "http://schema.openid.net/contact/email", true);
        req.addExtension(fetch);
        final boolean popup = null != uriInfo.getQueryParameters().getFirst("popup");
        session.setAttribute("openid.popup", popup);
        final boolean favicon = null != uriInfo.getQueryParameters().getFirst("favicon");
        if (popup || favicon) {
            req.addExtension(new UIExtension(popup ? "popup" : null, favicon));
        }
        return Response.temporaryRedirect(URI.create(req.getDestinationUrl(true))).build();
    }

    @Path("verify")
    @GET
    public Response verify(@Context UriInfo uriInfo,
                           @Context HttpServletRequest servletRequest) throws Exception {
        HttpSession session = servletRequest.getSession();
        try {
            DiscoveryInformation discovered = (DiscoveryInformation)session.getAttribute("openid.discovered");
            ConsumerManager consumerManager = (ConsumerManager)session.getAttribute("openid.consumer");
            ParameterList params = new ParameterList(servletRequest.getParameterMap());
            VerificationResult result = consumerManager.verify(uriInfo.getRequestUri().toString(), params, discovered);
            final Identifier identifier = result.getVerifiedId();
            if (identifier == null) {
                final String mode = result.getAuthResponse().getParameterValue("openid.mode");
                LOG.error("Cannot get openID identifier, result {}. ", mode);
                session.setAttribute("openid.mode", mode);
                // Lets user enter user ID and password.
                return Response.temporaryRedirect(URI.create((String)session.getAttribute("openid.redirect_after_login"))).build();
            }

            AuthSuccess authSuccess = (AuthSuccess)result.getAuthResponse();
            FetchResponse fetchResp = (FetchResponse)authSuccess.getExtension(AxMessage.OPENID_NS_AX);
            final String email = (String)fetchResp.getAttributeValues("email").get(0);

            final String redirectAfterLogin = (String)session.getAttribute("openid.redirect_after_login");
            final String tmpPassword = NameGenerator.generate(null, 16);
            // LoginModule may check userId|password from the FederatedLoginList.
            loginList.add(email, tmpPassword);
            return Response.temporaryRedirect(
                    UriBuilder.fromUri(redirectAfterLogin)
                              .queryParam("username", email)
                              .queryParam("password", tmpPassword)
                              .build()
                                             ).build();
        } finally {
            session.removeAttribute("openid.discovered");
            session.removeAttribute("openid.consumer");
            session.removeAttribute("openid.popup");
            session.removeAttribute("openid.redirect_after_login");
        }
    }
}
