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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * @author <a href="mailto:vparfonov@codenvy.com">Vitaly Parfonov</a>
 * @version $Id: ResourceServlet.java May 14, 2013 vetal $
 */
@SuppressWarnings("serial")
public class ResourceServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceServlet.class);


    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest)req;
        String requestURI = request.getRequestURI();
        if (!requestURI.contains("w/ide"))
        {
            int i = requestURI.indexOf('/', 3);
            String ws;
            String newuri;
            if (i == -1) {
                ws = requestURI.substring(3);
                newuri = requestURI.replace(ws, "ide/Application.html");
            }
            else {
                ws = requestURI.substring(3, i + 1);
                newuri = requestURI.replace(ws, "");
            }
            System.out.println("ResourceServlet : " + newuri);
            req.getRequestDispatcher(newuri).forward(req, res);
        }
    }
}
