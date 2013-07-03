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

import org.exoplatform.commons.utils.MimeTypeResolver;
import org.exoplatform.container.ExoContainerContext;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    private ApplicationRunner appRunner;

    private MimeTypeResolver  mimeTypeResolver;

    /** @see javax.servlet.GenericServlet#init() */
    @Override
    public void init() throws ServletException {
        super.init();
        appRunner = ((ApplicationRunner)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(ApplicationRunner.class));
        mimeTypeResolver = new MimeTypeResolver();
    }

    /** @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse) */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestPath = request.getPathInfo();
        if (requestPath == null) {
            response.setStatus(SC_BAD_REQUEST);
            return;
        }

        String appName;
        String filePath;
        final int secondSlashPosition = requestPath.indexOf('/', 1);
        if (secondSlashPosition == -1) {
            appName = requestPath.substring(1);
            filePath = "";
        } else {
            appName = requestPath.substring(1, secondSlashPosition);
            filePath = requestPath.substring(secondSlashPosition + 1, requestPath.length());
        }

        String projectPath;
        try {
            projectPath = appRunner.getApplicationByName(appName).projectPath;
        } catch (ApplicationRunnerException e) {
            response.sendError(SC_NOT_FOUND, e.getMessage());
            return;
        }

        if (filePath.isEmpty()) {
            filePath = getIndexFileNameIfExist(projectPath);
            if (filePath == null) {
                response.setStatus(SC_BAD_REQUEST);
                return;
            }
        }

        byte[] fileContent;
        try {
            final String requestedFilePath = projectPath + "/" + filePath;

            final File childFile = new File(requestedFilePath);
            if (!(childFile.toPath().normalize().startsWith(projectPath))) {
                throw new InvalidPathException(String.format("Invalid relative path %s", filePath), projectPath);
            }

            fileContent = getFileContentByPath(requestedFilePath);
        } catch (IOException e) {
            response.sendError(SC_NOT_FOUND, "File '" + filePath + "' not found. ");
            return;
        }

        response.setContentType(mimeTypeResolver.getMimeType(filePath.substring(filePath.lastIndexOf("/") + 1)));
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(fileContent);
        outputStream.flush();
    }

    private String getIndexFileNameIfExist(String projectPath) {
        final Path indexHtmFilePath = Paths.get(projectPath, "/index.htm");
        final Path indexHtmlFilePath = Paths.get(projectPath, "/index.html");
        if (Files.exists(indexHtmFilePath)) {
            return indexHtmFilePath.getFileName().toString();
        } else if (Files.exists(indexHtmlFilePath)) {
            return indexHtmlFilePath.getFileName().toString();
        }
        return null;
    }

    private byte[] getFileContentByPath(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }
}
