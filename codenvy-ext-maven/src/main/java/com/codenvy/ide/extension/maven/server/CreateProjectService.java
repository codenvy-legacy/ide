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
package com.codenvy.ide.extension.maven.server;

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.Property;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Server service for creating projects.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Path("ide/maven/create")
public class CreateProjectService {
    @Inject
    VirtualFileSystemRegistry registry;
    @Inject
    EventListenerList         eventListenerList;

    @Path("project/java")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Project createJavaProject(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name,
                                     @QueryParam("source") String source, List<Property> properties)
            throws VirtualFileSystemException {

        VirtualFileSystem vfs = registry.getProvider(vfsId).newInstance(null, eventListenerList);
        Project project =
                vfs.createProject(vfs.getInfo().getRoot().getId(), name, "deprecated.project.type", properties);
        String projectId = project.getId();

        String readMeContent = "This file was auto created when you created this project.";
        InputStream readMeIS = new ByteArrayInputStream(readMeContent.getBytes());
        vfs.createFile(projectId, "Readme.txt", MediaType.TEXT_PLAIN_TYPE, readMeIS);

        Folder sourceFolder = vfs.createFolder(projectId, source);

        String javaFileContent = "\n" +
                                 "public class HelloWorld{\n" +
                                 "   public static void main(String args[]){\n" +
                                 "      System.out.println(\"Hello World!\");\n" +
                                 "   }\n" +
                                 "}";
        InputStream javaFileIS = new ByteArrayInputStream(javaFileContent.getBytes());
        vfs.createFile(sourceFolder.getId(), "HelloWorld.java", MediaType.TEXT_PLAIN_TYPE, javaFileIS);

        return project;
    }

    @Path("project/war")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Project createWarProject(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name, List<Property> properties,
                                    @Context UriInfo uriInfo) throws VirtualFileSystemException {

        VirtualFileSystem vfs = registry.getProvider(vfsId).newInstance(null, eventListenerList);
        Project project =
                vfs.createProject(vfs.getInfo().getRoot().getId(), name, "deprecated.project.type", properties);
        String projectId = project.getId();

        String projectContent = "[{\"name\":\"vfs:mimeType\",\"value\":[\"text/vnd.ideproject+directory\"]}," +
                                "{\"name\":\"vfs:projectType\",\"value\":[\"Servlet/JSP\"]},{\"name\":\"exoide:projectDescription\"," +
                                "\"value\":[\"Java Web project.\"]}, {\"name\":\"exoide:target\",\"value\":[\"CloudBees\", " +
                                "\"CloudFoundry\", \"AWS\", \"AppFog\"]}]";
        InputStream projectIS = new ByteArrayInputStream(projectContent.getBytes());
        vfs.createFile(projectId, ".project", MediaType.TEXT_PLAIN_TYPE, projectIS);


        String host = uriInfo.getAbsolutePath().getHost();
        String groupId = null;
        if (host.contains(".")) {
            String[] split = host.split("\\.");
            StringBuffer result = new StringBuffer();
            int j = split.length - 1;
            while (j > 0) {
                result.append(split[j--]).append(".");
            }
            result.append(split[0]);
            groupId = result.toString();
        } else {
            groupId = host;
        }

        String pomContent = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3" +
                            ".org/2001/XMLSchema-instance\"\n" +
                            "  xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                            "  <modelVersion>4.0.0</modelVersion>\n" +
                            "  <groupId>" + groupId + "</groupId>\n" +
                            "  <artifactId>" + name + "</artifactId>\n" +
                            "  <packaging>war</packaging>\n" +
                            "  <version>1.0-SNAPSHOT</version>\n" +
                            "  <name>java-web-sample</name>\n" +
                            "  <build>\n" +
                            "    <finalName>java-web-sample</finalName>\n" +
                            "  </build>\n" +
                            "</project>";
        InputStream pomIS = new ByteArrayInputStream(pomContent.getBytes());
        vfs.createFile(projectId, "pom.xml", MediaType.TEXT_XML_TYPE, pomIS);

        Folder src = vfs.createFolder(projectId, "src");
        Folder main = vfs.createFolder(src.getId(), "main");
        String mainId = main.getId();

        vfs.createFolder(mainId, "java");
        vfs.createFolder(mainId, "resources");
        Folder webapp = vfs.createFolder(mainId, "webapp");

        String webappId = webapp.getId();
        String indexContent = "<html>\n" +
                              "<head><title>Hello, User</title></head>\n" +
                              "<body bgcolor=\"#ffffff\">\n" +
                              "\n" +
                              "\n" +
                              "<table border=\"0\" width=\"700\">\n" +
                              "<tr>\n" +
                              "<td width=\"150\"> &nbsp; </td>\n" +
                              "<td width=\"550\">\n" +
                              "<h1>My name is eXo. What's yours?</h1>\n" +
                              "</td>\n" +
                              "</tr>\n" +
                              "<tr>\n" +
                              "<td width=\"150\" &nbsp; </td>\n" +
                              "<td width=\"550\">\n" +
                              "<form method=\"get\">\n" +
                              "<input type=\"text\" name=\"username\" size=\"25\">\n" +
                              "<br>\n" +
                              "<input type=\"submit\" value=\"Submit\">\n" +
                              "<input type=\"reset\" value=\"Reset\">\n" +
                              "</td>\n" +
                              "</tr>\n" +
                              "</form>\n" +
                              "</table>\n" +
                              "\n" +
                              "<%\n" +
                              "  if ( request.getParameter(\"username\") != null ) {\n" +
                              "%>\n" +
                              "\n" +
                              "<%@include file=\"sayhello.jsp\" %>\n" +
                              "\n" +
                              "<%\n" +
                              "    }\n" +
                              "%>\n" +
                              "\n" +
                              "</body>\n" +
                              "</html>";
        InputStream indexIS = new ByteArrayInputStream(indexContent.getBytes());
        vfs.createFile(webappId, "index.jsp", MediaType.TEXT_PLAIN_TYPE, indexIS);

        String sayHelloContent = "<table border=\"0\" width=\"700\">\n" +
                                 "<tr>\n" +
                                 "<td width=\"150\"> &nbsp; </td>\n" +
                                 "\n" +
                                 "<td width=\"550\">\n" +
                                 "\n" +
                                 "<h1>Hello, <%= request.getParameter(\"username\") %>\n" +
                                 "</h1>\n" +
                                 "\n" +
                                 "</td>\n" +
                                 "</tr>\n" +
                                 "</table>";
        InputStream sayHelloIS = new ByteArrayInputStream(sayHelloContent.getBytes());
        vfs.createFile(webappId, "sayhello.jsp", MediaType.TEXT_PLAIN_TYPE, sayHelloIS);

        Folder webInf = vfs.createFolder(webappId, "WEB-INF");
        String webContent = "<!DOCTYPE web-app PUBLIC\n" +
                            " \"-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN\"\n" +
                            " \"http://java.sun.com/dtd/web-app_2_3.dtd\" >\n" +
                            "\n" +
                            "<web-app>\n" +
                            "  <display-name>Web Application Created With eXo IDE</display-name>\n" +
                            "</web-app>";
        InputStream webIS = new ByteArrayInputStream(webContent.getBytes());
        vfs.createFile(webInf.getId(), "web.xml", MediaType.TEXT_XML_TYPE, webIS);

        return project;
    }
}