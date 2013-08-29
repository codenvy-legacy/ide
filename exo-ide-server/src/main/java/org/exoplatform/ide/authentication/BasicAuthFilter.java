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
package org.exoplatform.ide.authentication;

import org.apache.commons.codec.binary.Base64;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.*;

import javax.security.auth.login.LoginException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.security.Principal;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class BasicAuthFilter implements Filter {

    private static final Log LOG = ExoLogger.getLogger(BasicAuthFilter.class.getName());

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                                                                                                     ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String auth = httpRequest.getHeader("Authorization");
        if (auth == null) {
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            httpResponse.setStatus(401);
            httpResponse.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"exo-domain\"");
            return;
        }

        // cut 'basic '
        byte[] token = Base64.decodeBase64(auth.substring(6).getBytes());
        String username;
        String password = null;
        int colon = -1;
        for (int i = 0; i < token.length; i++) {
            if (token[i] == ':') {
                colon = i;
            }
        }
        if (colon < 0) {
            username = new String(token);
        } else {
            username = new String(token, 0, colon);
            password = new String(token, colon + 1, token.length - colon - 1);
        }
        ExoContainer container = ExoContainerContext.getCurrentContainer();
        if (container == null) {
            container = ExoContainerContext.getTopContainer();
        }

        Identity identity;
        try {
            Authenticator authenticator = (Authenticator)container.getComponentInstanceOfType(Authenticator.class);

            if (authenticator == null) {
                throw new RuntimeException("Authenticator not found");
            }

            Credential[] credentials =
                    new Credential[]{new UsernameCredential(username), new PasswordCredential(password)};

            String userId = authenticator.validateUser(credentials);

            identity = authenticator.createIdentity(userId);

            IdentityRegistry ir = (IdentityRegistry)container.getComponentInstanceOfType(IdentityRegistry.class);
            if (ir == null) {
                throw new RuntimeException("Authenticator not found");
            }

            ir.register(identity);
            ConversationState.setCurrent(new ConversationState(identity));

        } catch (LoginException e) {
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            httpResponse.setStatus(401);
            httpResponse.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"exo-domain\"");
            return;
        } catch (Exception e) {
            LOG.error("Error occurs ", e);
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        chain.doFilter(new AuthServletRequest(httpRequest, identity), response);

    }

    private static class AuthServletRequest extends HttpServletRequestWrapper {
        private final Identity identity;

        public AuthServletRequest(HttpServletRequest request, Identity identity) {
            super(request);
            this.identity = identity;
        }

        public String getRemoteUser() {
            return identity.getUserId();
        }

        @Override
        public Principal getUserPrincipal() {

            return new MockPrincipal(identity.getUserId());
        }

        @Override
        public boolean isUserInRole(String role) {
            return identity.getRoles().contains(role);
        }
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    private static class MockPrincipal implements Principal {
        private String username;

        public MockPrincipal(String username) {
            this.username = username;
        }

        public String getName() {
            return username;
        }
    }

}
