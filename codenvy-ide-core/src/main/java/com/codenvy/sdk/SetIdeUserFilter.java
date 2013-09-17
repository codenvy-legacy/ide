package com.codenvy.sdk;

import org.exoplatform.container.*;
import org.exoplatform.container.web.AbstractFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.*;
import org.exoplatform.services.security.web.HttpSessionStateKey;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.web.AbstractFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.ConversationRegistry;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityConstants;
import org.exoplatform.services.security.IdentityRegistry;
import org.exoplatform.services.security.StateKey;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: vetal
 * Date: 9/16/13
 * Time: 11:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class SetIdeUserFilter implements Filter {

    /**
     * Logger.
     */
    private static final Log LOG = ExoLogger.getLogger("exo.core.component.security.core.SetCurrentIdentityFilter");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Set current {@link org.exoplatform.services.security.ConversationState}, if it is not registered yet then
     * create new one and register in {@link org.exoplatform.services.security.ConversationRegistry}. {@inheritDoc}
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        ExoContainer container = getContainer();

        try {
            ExoContainerContext.setCurrentContainer(container);
            ConversationState state = getCurrentState(container, httpRequest);
            ConversationState.setCurrent(state);
            chain.doFilter(request, response);
        } finally {
            try {
                ConversationState.setCurrent(null);
            } catch (Exception e) {
                LOG.warn("An error occured while cleaning the ThreadLocal", e);
            }
            try {
                ExoContainerContext.setCurrentContainer(null);
            } catch (Exception e) {
                LOG.warn("An error occured while cleaning the ThreadLocal", e);
            }
        }
    }

    /**
     * Gives the current state
     */
    private ConversationState getCurrentState(ExoContainer container, HttpServletRequest httpRequest) {
        ConversationState state = null;
        String userId = httpRequest.getRemoteUser();

        if (userId == null)
            userId = "ide";

        HttpSession httpSession = httpRequest.getSession();
        StateKey stateKey = new HttpSessionStateKey(httpSession);

        final Set<String> groups = new HashSet<String>(2);
        groups.add("developer");
        groups.add("admin");
        Identity identity = new Identity(userId, Collections.<MembershipEntry>emptyList(),groups);
        state = new ConversationState(identity);
        state.setAttribute(ConversationState.SUBJECT, identity.getSubject());

        return state;
    }

    /**
     * {@inheritDoc}
     */
    public void destroy() {
        // nothing to do.
    }


    protected final ExoContainer getContainer() {
        ExoContainer container = ExoContainerContext.getCurrentContainer();
        return container;
    }

}
