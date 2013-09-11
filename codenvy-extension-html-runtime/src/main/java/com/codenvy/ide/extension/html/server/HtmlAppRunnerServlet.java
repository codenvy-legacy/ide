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

    private ApplicationRunner     appRunner;
    private MimeTypeResolver      mimeTypeResolver;
    private static final String[] HTML_INDEX_FILES = new String[]{"index.htm", "index.html"};

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
            filePath = getHtmlIndexFileNameIfExist(projectPath);
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

    private String getHtmlIndexFileNameIfExist(String projectPath) {
        for (String indexFile : HTML_INDEX_FILES) {
            Path indexFilePath = Paths.get(projectPath, indexFile);
            if (Files.exists(indexFilePath)) {
                return indexFilePath.getFileName().toString();
            }
        }
        return null;
    }

    private byte[] getFileContentByPath(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }
}
