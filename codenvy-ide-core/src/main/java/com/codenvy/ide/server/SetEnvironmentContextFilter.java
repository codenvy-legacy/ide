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

import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.organization.client.WorkspaceManager;
import com.codenvy.organization.exception.OrganizationServiceException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * @author <a href="mailto:vparfonov@codenvy.com">Vitaly Parfonov</a>
 * @version $Id:
 */
public class SetEnvironmentContextFilter implements Filter {
    private Map<String, Object> env;
    private WorkspaceManager    workspaceManager;

    /** Set current {@link EnvironmentContext} */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                                                                                                     ServletException {
        try {
            String url = ((HttpServletRequest)request).getRequestURI();
            if (url.contains("/rest/") || url.contains("/websocket/")) {
                String[] split = url.split("/");
                String ws = split[3];
                if (ws.equals("ide")) {
                    chain.doFilter(request, response);
                    return;
                }
                if (workspaceManager != null) {
                    try {
                        workspaceManager.getWorkspaceByName(ws);//check ws on existing
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
