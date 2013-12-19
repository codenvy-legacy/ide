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

import com.codenvy.commons.servlet.Action;
import com.codenvy.commons.servlet.Condition;
import com.codenvy.commons.servlet.DispatcherServletConfiguration;
import com.codenvy.commons.servlet.DispatcherServletConfigurationFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

/** @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a> */
public class IdeDispatcherServletConfigurationFactory extends DispatcherServletConfigurationFactory {
    @Override
    public DispatcherServletConfiguration newDispatcherServletConfiguration() {
        return DispatcherServletConfiguration.create()
                                             .when(new Condition() {
                                                 @Override
                                                 public boolean matches(HttpServletRequest request, HttpServletResponse response) {
                                                     return "/favicon.ico".equals(request.getPathInfo());
                                                 }
                                             })
                                             .execute(new Action() {
                                                 @Override
                                                 public void perform(HttpServletRequest request, HttpServletResponse response)
                                                         throws ServletException,
                                                                IOException {
                                                     request.getRequestDispatcher("/favicon.ico").forward(request, response);
                                                 }
                                             })
                                             .priority(100)
                                             .done()
                                             .when(new Condition() {
                                                 @Override
                                                 public boolean matches(HttpServletRequest request, HttpServletResponse response) {
                                                     return request.getAttribute("ws") == null;
                                                 }
                                             })
                                             .execute(new Action() {
                                                 @Override
                                                 public void perform(HttpServletRequest request, HttpServletResponse response) {
                                                     throw new IllegalArgumentException(); // workspace is not set.
                                                 }
                                             })
                                             .priority(200)
                                             .done()
                                             .when(new Condition() {
                                                 @Override
                                                 public boolean matches(HttpServletRequest request, HttpServletResponse response) {
                                                     final String workspace = (String)request.getAttribute("ws");
                                                     final String requestPath = request.getPathInfo();
                                                     return requestPath.startsWith("/" + workspace + "/_app");

                                                 }
                                             })
                                             .execute(new Action() {
                                                 @Override
                                                 public void perform(HttpServletRequest request, HttpServletResponse response)
                                                         throws ServletException,
                                                                IOException {
                                                     final String workspace = (String)request.getAttribute("ws");
                                                     final String requestPath = request.getPathInfo();
                                                     final String myPath = requestPath.substring(workspace.length() + 1);
                                                     // System.out.printf("\t\t\t(1) %s => %s%n", requestPath, myPath);
                                                     request.getRequestDispatcher(myPath).forward(request, response);
                                                 }
                                             })
                                             .priority(300)
                                             .done()
                                             .when(new Condition() {
                                                 @Override
                                                 public boolean matches(HttpServletRequest request, HttpServletResponse response) {
                                                     final String workspace = (String)request.getAttribute("ws");
                                                     final String requestPath = request.getPathInfo();
                                                     return requestPath.startsWith("/" + workspace + "/_git");

                                                 }
                                             })
                                             .execute(new Action() {
                                                 @Override
                                                 public void perform(HttpServletRequest request, HttpServletResponse response)
                                                         throws ServletException,
                                                                IOException {
                                                     final String workspace = (String)request.getAttribute("ws");
                                                     final String requestPath = request.getPathInfo();
                                                     final String myPath = requestPath.substring(workspace.length() + 1);
                                                     request.getRequestDispatcher(myPath).forward(request, response);
                                                 }
                                             })
                                             .priority(300)
                                             .done()
                                             .when(new Condition() {
                                                 @Override
                                                 public boolean matches(HttpServletRequest request, HttpServletResponse response) {
                                                     final String workspace = (String)request.getAttribute("ws");
                                                     final String requestPath = request.getPathInfo();
                                                     return requestPath.startsWith("/" + workspace + "/_htmlapprunner");

                                                 }
                                             })
                                             .execute(new Action() {
                                                 @Override
                                                 public void perform(HttpServletRequest request, HttpServletResponse response)
                                                         throws ServletException,
                                                                IOException {
                                                     final String workspace = (String)request.getAttribute("ws");
                                                     final String requestPath = request.getPathInfo();
                                                     final String myPath = requestPath.substring(workspace.length() + 1);
                                                     request.getRequestDispatcher(myPath).forward(request, response);
                                                 }
                                             })
                                             .priority(300)
                                             .done()
                                             // Allow to specify GWT code server URL for app launched with SDK runner.
                                             // h - GWT code server host, p - GWT code server port
                                             .when(new Condition() {
                                                 @Override
                                                 public boolean matches(HttpServletRequest request, HttpServletResponse response) {
                                                     final String host = request.getParameter("h");
                                                     final String port = request.getParameter("p");
                                                     return host != null && port != null;
                                                     
                                                 }
                                             })
                                             .execute(new Action() {
                                                 @Override
                                                 public void perform(HttpServletRequest request, HttpServletResponse response)
                                                     throws ServletException,
                                                     IOException {
                                                     request.getRequestDispatcher("/_app/main").forward(request, response);
                                                 }
                                             })
                                             .priority(350)
                                             .done()
                                             .when(Condition.MATCH)
                                             .execute(new Action() {
                                                 @Override
                                                 public void perform(HttpServletRequest request, HttpServletResponse response)
                                                         throws ServletException,
                                                                IOException {
                                                     final String workspace = (String)request.getAttribute("ws");
                                                     final String requestPath = request.getPathInfo();


//TODO need improve this code
                                                     String project = null;
                                                     String filePath = null;
                                                     //checking for attribute openProjectOperation need to prevent parsing referer again
                                                     //after browser refreshing or pressing F5. when servlet change user location,
                                                     //addition attribute is setted in session to indicate that we need to parse
                                                     //referer to get project name and file path.
                                                     if (request.getSession().getAttribute("openProjectOperation") != null
                                                         && request.getQueryString() == null
                                                         && request.getHeader("Referer") != null && !request.getHeader("Referer").isEmpty()
                                                         && request.getHeader("Referer").contains("/ide/")
                                                         && !request.getHeader("Referer").contains("_app")) {

                                                         String originalUrl = request.getHeader("Referer");




                                                         URL url = new URL(originalUrl);
                                                         String myPath1 = url.getPath().substring(("/ide/" + workspace).length());

                                                         if (myPath1.startsWith("/"))
                                                             myPath1 = myPath1.substring(1);

                                                         if (myPath1.contains("/")) {
                                                             project = myPath1.substring(0, myPath1.indexOf("/"));
                                                             filePath = myPath1.substring(myPath1.indexOf("/"));
                                                         }
                                                         else
                                                             project = myPath1;

                                                         request.setAttribute("project", project);
                                                         request.setAttribute("path", filePath);
                                                         if (url.getQuery() != null && !url.getQuery().isEmpty())
                                                            request.setAttribute("startUpParams", url.getQuery().contains("gwt.codesvr") ? null : url.getQuery());
                                                         request.getSession().removeAttribute("openProjectOperation");
                                                         final String myPath = "/_app/main";
                                                         request.getRequestDispatcher(myPath).forward(request, response);
                                                         return;
                                                     } else {
                                                         final int length = requestPath.length();
                                                         int p = workspace.length();
                                                         int n = requestPath.indexOf('/', p);
                                                         if (n < 0) {
                                                             n = length;
                                                         }

                                                         String tmp;
                                                         if (n < length) {
                                                             p = n + 1;
                                                             n = requestPath.indexOf('/', p);
                                                             if (n < 0) {
                                                                 n = length;
                                                             }
                                                             tmp = requestPath.substring(p, n);
                                                             if (!tmp.isEmpty()) {
                                                                 project = tmp;
                                                             }
                                                             if (n < length) {
                                                                 p = n;
                                                                 n = length;
                                                                 tmp = requestPath.substring(p, n);
                                                                 if (!tmp.isEmpty()) {
                                                                     filePath = tmp;
                                                                 }
                                                             }
                                                         }
                                                     }
                                                     int trim = -1;
                                                     if (project != null)
                                                         trim = project.length();
                                                     if (filePath != null)
                                                         trim += filePath.length();
                                                     if (request.getQueryString() != null  && !request.getQueryString().contains("gwt.codesvr"))
                                                         trim = 0;

                                                     if (trim >= 0) {
                                                         String wsUri = request.getRequestURL().toString();
                                                         wsUri = wsUri.substring(0, wsUri.length() - trim);
                                                         response.setContentType("text/html;charset=UTF-8");
                                                         response.setCharacterEncoding("UTF-8");
                                                         request.getSession().setAttribute("openProjectOperation", "1");
                                                         response.getWriter().write("<script>window.location.replace(\"" + wsUri
                                                                                    + "\");</script>");
                                                         return;
                                                     }
                                                     request.setAttribute("project", project);
                                                     request.setAttribute("path", filePath);
                                                     final String myPath = "/_app/main";
                                                     request.getRequestDispatcher(myPath).forward(request, response);
                                                 }
                                             })
                                             .priority(400)
                                             .done();
    }
}
