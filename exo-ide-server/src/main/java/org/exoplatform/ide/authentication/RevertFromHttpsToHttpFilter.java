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

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter provide possibility return back to HTTP from HTTPS and visa versa for security resource. Problem with cookies: after
 * login on HHTPS resource we get cookie that set to encrypted path only, then we try switch back to HTTP or cookie became invalid
 * and we get other cookie for not encrypted path, in this case we MUST login twice. For resolve this problem we rewrite cookie
 * "JSESSIONID" in this filter we make cookie that we get in HTTPS connection valide for all connections.
 * sessionCookie.setSecure(false);
 * <p/>
 * In initparams need set ports for HHTP & HTTPS and servlet path for SSL (encrypted connections) by default it "/rest/ssl"
 * <p/>
 * <p/>
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class RevertFromHttpsToHttpFilter implements Filter {

    /**
     *
     */
    private String sslServletPath = "/rest/ssl";

    /**
     *
     */
    private String httpPort = null;

    /**
     *
     */
    private String httpsPort = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        httpsPort = filterConfig.getInitParameter("HTTPS_PORT");
        httpPort = filterConfig.getInitParameter("HTTP_PORT");
        sslServletPath = filterConfig.getInitParameter("SSL_SERVLET_PATH");
    }

    /** @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain) */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                                                                                                     ServletException {

        final HttpServletRequest httpRequest = (HttpServletRequest)request;

        final HttpServletResponse httpResponse = (HttpServletResponse)response;
        final HttpSession session = httpRequest.getSession(false);

        Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals("JSESSIONID") && request.isSecure() && session != null) {
                    final Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());
                    sessionCookie.setMaxAge(cookies[i].getMaxAge());
                    sessionCookie.setSecure(false);
                    sessionCookie.setPath(cookies[i].getPath());
                    httpResponse.addCookie(sessionCookie);

                }
            }
        }
        String servletPath = httpRequest.getServletPath();
        boolean secure = httpRequest.isSecure();

        if (secure && !servletPath.equalsIgnoreCase(sslServletPath) && httpRequest.getUserPrincipal() != null) {
            String location = null;
            if (httpPort == null)
                location = "http://" + request.getServerName() + httpRequest.getRequestURI();
            else
                location = "http://" + request.getServerName() + ":" + httpPort + httpRequest.getRequestURI();
            httpResponse.sendRedirect(location);
            return;
        }

        if (!secure && servletPath.equalsIgnoreCase(sslServletPath) && httpRequest.getUserPrincipal() != null) {
            String location = null;
            if (httpsPort == null)
                location = "https://" + request.getServerName() + httpRequest.getRequestURI();
            else
                location = "https://" + request.getServerName() + ":" + httpPort + httpRequest.getRequestURI();
            httpResponse.sendRedirect(location);
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
