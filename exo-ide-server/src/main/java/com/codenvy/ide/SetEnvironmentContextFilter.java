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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SetEnvironmentContextFilter implements Filter {
    private Map<String, Object> env;

    /** Set current {@link EnvironmentContext} */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                                                                                                     ServletException {
        try {
            EnvironmentContext environment = EnvironmentContext.getCurrent();
            for (Map.Entry<String, Object> entry : env.entrySet()) {
                environment.setVariable(entry.getKey(), entry.getValue());
            }

            chain.doFilter(request, response);
        } finally {
            EnvironmentContext.reset();
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Map<String, Object> myEnv = new HashMap<String, Object>();
        myEnv.put(EnvironmentContext.WORKSPACE_NAME, "codenvy");
        myEnv.put(EnvironmentContext.WORKSPACE_ID, "dev-monit");
        myEnv.put(EnvironmentContext.GIT_SERVER, "git");
        myEnv.put(EnvironmentContext.TMP_DIR, new File("../temp"));
        myEnv.put(EnvironmentContext.VFS_ROOT_DIR, new File("../temp/fs-root"));
        myEnv.put(EnvironmentContext.VFS_INDEX_DIR, new File("../temp/fs-index-root"));
        this.env = Collections.unmodifiableMap(myEnv);
    }
}
