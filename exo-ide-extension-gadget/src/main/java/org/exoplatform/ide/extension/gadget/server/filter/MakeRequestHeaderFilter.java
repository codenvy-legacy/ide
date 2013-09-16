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
package org.exoplatform.ide.extension.gadget.server.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * This filter adds to the gadget's makeRequest request user's session info, passing the value of "Cookie" header to "headers"
 * query parameter.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jan 24, 2011 4:29:51 PM anya $
 */
public class MakeRequestHeaderFilter implements Filter {
    private final String HEADERS_PARAMETER = "headers";

    /** @see javax.servlet.Filter#init(javax.servlet.FilterConfig) */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /** @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain) */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                                                                                                     ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        // Get user session info from "Cookie" header:
        String cookie = httpRequest.getHeader(HttpHeaders.COOKIE);
        if (cookie == null) {
            chain.doFilter(httpRequest, response);
            return;
        }
        // Set the value of "Cookie" header to "headers" request query paramater:
        String headers = httpRequest.getParameter(HEADERS_PARAMETER);
        String cookieHeader = HttpHeaders.COOKIE + "=" + URLEncoder.encode(cookie, "UTF-8");
        cookieHeader = (headers == null || headers.length() <= 0) ? cookieHeader : "&" + cookieHeader;
        headers += cookieHeader;

        RequestWrapper requestWrapper = new RequestWrapper(httpRequest, headers);
        chain.doFilter(requestWrapper, response);
    }

    /** @see javax.servlet.Filter#destroy() */
    @Override
    public void destroy() {
    }

    /**
     * The wrapper for the {@link HttpServletRequest} to override the getParameter(String name) method to be able to change the
     * value of the "headers" parameter.
     *
     * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
     * @version $Id: Jan 25, 2011 12:50:24 PM anya $
     */
    private class RequestWrapper extends HttpServletRequestWrapper {

        /** The value of "headers" parameter. */
        private String headersParameter;

        /**
         * @param request
         *         http servlet request to wrap
         * @param headersParameter
         *         the value of "headers" parameter
         */
        public RequestWrapper(HttpServletRequest request, String headersParameter) {
            super(request);
            this.headersParameter = headersParameter;
        }

        /** @see javax.servlet.ServletRequestWrapper#getParameter(java.lang.String) */
        @Override
        public String getParameter(String name) {
            if (name.equals(HEADERS_PARAMETER) && headersParameter != null) {
                return headersParameter;
            }
            return super.getParameter(name);
        }
    }
}
