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
 * @version $Id: ResourcesFilter.java May 8, 2013 vetal $
 */
public class WsServlet  extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(WsServlet.class);
    
    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        super.service(req, res);
        HttpServletRequest request = (HttpServletRequest)req;
        String requestURI = request.getRequestURI();
        int w = requestURI.lastIndexOf("/w/");
        String substring = requestURI.substring(w);
        String[] split = substring.split("/");
        
        
    }

}
