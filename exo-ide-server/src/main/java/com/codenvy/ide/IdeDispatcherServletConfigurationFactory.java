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
package com.codenvy.ide;

import com.codenvy.commons.servlet.Action;
import com.codenvy.commons.servlet.Condition;
import com.codenvy.commons.servlet.DispatcherServletConfiguration;
import com.codenvy.commons.servlet.DispatcherServletConfigurationFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
                                                         && request.getHeader("Referer") != null && !request.getHeader("Referer").isEmpty()
                                                         && request.getHeader("Referer").contains("/ide/")
                                                         && !request.getHeader("Referer").contains("_app"))
                                                     {
                                                         String orginalUrl = request.getHeader("Referer");
                                                         
                                                         int i = orginalUrl.indexOf("?");
                                                         if (i > 0)
                                                             orginalUrl = orginalUrl.substring(0, i);
                                                         
                                                         if (orginalUrl.endsWith("/"))
                                                             orginalUrl = orginalUrl.substring(0, orginalUrl.length()-1);
                                                         
                                                         String tmp = orginalUrl.substring(request.getRequestURL().length());
                                                         int n = tmp.indexOf('/', 1);
                                                         if (n > 0)
                                                         {
                                                             project = tmp.substring(0, n);
                                                             filePath = tmp.substring(n);
                                                         } else
                                                             project = tmp;

                                                         request.setAttribute("project", project);
                                                         request.setAttribute("path", filePath);
                                                         request.getSession().removeAttribute("openProjectOperation");
                                                         final String myPath = "/_app/main";
                                                         request.getRequestDispatcher(myPath).forward(request, response);
                                                         return;
                                                     }
                                                     else {
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
                                                     int trim = 0;
                                                     if (project != null)
                                                         trim = project.length();
                                                     if (filePath != null)
                                                         trim += filePath.length();

                                                     if (trim > 0)
                                                     {
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
