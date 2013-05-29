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
package com.codenvy.servlet;


import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("serial")
public class DispatcherServlet extends HttpServlet {
    private static String ideStaticResourcesPrefix = "/ide";
    private static String shellStaticResourcesPrefix = "/shell";
    private static String ssoPrefix = "/sso";
    private static String mainPage = ideStaticResourcesPrefix + "/main";

    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest)req;
        String requestPath = request.getPathInfo();

        String ws;
        String project = null;
        String path = null;

        final int length = requestPath.length();
        String s;
        int p = 1;
        int n = requestPath.indexOf('/', p);
        if (n < 0) {
            n = length;
        }
        s = requestPath.substring(p, n);
        if (s.isEmpty()) {
            throw new IllegalStateException(); // TODO
        }
        ws = s; // workspace
        if (n < length) {
            p = n + 1;
            n = requestPath.indexOf('/', p);
            if (n < 0) {
                n = length;
            }
            s = requestPath.substring(p, n);
            if (s.equals("p") && n < length) {
                p = n + 1;
                n = requestPath.indexOf('/', p);
                if (n < 0) {
                    n = length;
                }
                s = requestPath.substring(p, n);
                if (!s.isEmpty()) {
                    project = s; // project
                }
                if (n < length) {
                    p = n + 1;
                    n = length;
                    s = requestPath.substring(p, n);
                    if (!s.isEmpty()) {
                        path = s;
                    }
                }
            }
        }

        System.err.printf("request: %s%n", requestPath);
        System.err.printf("ws     : %s%n", ws);
        System.err.printf("project: %s%n", project);
        System.err.printf("path   : %s%n", path);

        request.setAttribute("ws", ws);
        request.setAttribute("project", project);
        request.setAttribute("path", path);

        if (requestPath.startsWith('/' + ws + ideStaticResourcesPrefix) || requestPath.startsWith('/' + ws + shellStaticResourcesPrefix) || requestPath.startsWith('/' + ws + ssoPrefix)) { // /xxx/ide/....
            // remove workspace name
            request.getRequestDispatcher(requestPath.substring(ws.length() + 1)).forward(request, res);
        } else {
            request.getRequestDispatcher(mainPage).forward(request, res);
        }
    }

    public static String genStaticResourceUrl(HttpServletRequest request, String name) {
        String contextPath = request.getContextPath();
        return contextPath + '/' + request.getAttribute("ws") + ideStaticResourcesPrefix + '/' + name;
    }
}
