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
package com.codenvy.ide.extension.html.server;

import org.exoplatform.container.ExoContainerContext;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

/**
 * Handles requests to access to HTML projects.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: HtmlAppRunnerServlet.java Jun 26, 2013 3:59:07 PM azatsarynnyy $
 */
@SuppressWarnings("serial")
public class HtmlAppRunnerServlet extends HttpServlet {

    private static final int  BUFFER_SIZE = 100 * 1024; // 100 kB

    private ApplicationRunner appRunner;

    /** @see javax.servlet.GenericServlet#init() */
    @Override
    public void init() throws ServletException {
        super.init();
        appRunner = ((ApplicationRunner)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(ApplicationRunner.class));
    }

    /** @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse) */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String requestPath = request.getPathInfo();
        if (requestPath == null) {
            response.setStatus(SC_BAD_REQUEST);
            return;
        }

        try {
            String appId = null;
            String projectPath = null;
            String filePath = null;
            final int secondSlashPosition = requestPath.indexOf('/', 1);
            if (secondSlashPosition == -1) {
                response.setStatus(SC_BAD_REQUEST);
                return;
            }
            appId = requestPath.substring(1, secondSlashPosition);
            filePath = requestPath.substring(secondSlashPosition + 1, requestPath.length());
            if (filePath.isEmpty()) {
                return;
            }
            RunnedApplication app = appRunner.getApplicationByName(appId);
            projectPath = app.projectPath;

            ServletOutputStream outputStream = response.getOutputStream();
            final byte[] fileContent = getFileContentByPath(projectPath, "/" + filePath);
            outputStream.write(fileContent);
            outputStream.close();
        } catch (ApplicationRunnerException e) {
            response.sendError(SC_NOT_FOUND, e.getMessage());
        }
    }

    private byte[] getFileContentByPath(String projectPath, String filePath) throws IOException {
        final File ioFile = new File(projectPath + filePath);
        FileInputStream fileInputStream = new FileInputStream(ioFile);

        final byte[] buffer = new byte[BUFFER_SIZE];
        int lengthToRead = buffer.length;
        int offset = 0;
        int readCount;
        while ((readCount = fileInputStream.read(buffer, offset, lengthToRead)) > 0) {
            offset += readCount;
            lengthToRead -= readCount;
        }
        fileInputStream.close();
        return buffer;
    }
}
