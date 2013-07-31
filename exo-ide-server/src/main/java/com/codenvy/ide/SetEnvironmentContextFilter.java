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

import com.codenvy.commons.env.EnvironmentContext;

import javax.servlet.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;

import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.organization.client.WorkspaceManager;
import com.codenvy.organization.exception.OrganizationServiceException;
import com.codenvy.organization.model.Workspace;

public class SetEnvironmentContextFilter implements Filter {
    private Map<String, Object> env;
    private WorkspaceManager    workspaceManager;

    /** Set current {@link EnvironmentContext} */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                                                                                             ServletException {
        try {
            String url = ((HttpServletRequest)request).getRequestURI();
            if (url.contains("/rest/") || url.contains("/websocket/"))
            {
                String[] split = url.split("/");
                String ws = split[3];
                if (ws.equals("ide"))
                {
                    chain.doFilter(request, response);
                    return;
                }
                if (workspaceManager != null)
                {
                    try {
                        Workspace workspace = workspaceManager.getWorkspaceByName(ws);
                        EnvironmentContext environment = EnvironmentContext.getCurrent();
                        for (Map.Entry<String, Object> entry : env.entrySet()) {
                            environment.setVariable(entry.getKey(), entry.getValue());
                        }
                        environment.setVariable(EnvironmentContext.WORKSPACE_NAME, ws);
                        environment.setVariable(EnvironmentContext.WORKSPACE_ID, ws);
                        chain.doFilter(request, response);
                    } catch (OrganizationServiceException e) {
                        HttpServletResponse httpResponse = (HttpServletResponse)response;
                        httpResponse.setStatus(404);
                        httpResponse.setHeader("JAXRS-Body-Provided", "Error-Message");
                        PrintWriter out = response.getWriter();
                        String msg = String.format("Workspace %s not found", ws);
                        httpResponse.setContentLength(msg.getBytes().length);

                        out.write(msg);
                        out.close();
                        return;
                    }
                }
            }

        } finally {
            EnvironmentContext.reset();
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            workspaceManager = new WorkspaceManager();
        } catch (OrganizationServiceException e) {
            e.printStackTrace();
        }
        Map<String, Object> myEnv = new HashMap<String, Object>();
        myEnv.put(EnvironmentContext.GIT_SERVER, "git");
        myEnv.put(EnvironmentContext.TMP_DIR, new File("../temp"));
        myEnv.put(EnvironmentContext.VFS_ROOT_DIR, new File("../temp/fs-root"));
        myEnv.put(EnvironmentContext.VFS_INDEX_DIR, new File("../temp/fs-index-root"));
        this.env = Collections.unmodifiableMap(myEnv);
    }
}
