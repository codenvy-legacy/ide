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
    public void createJavaProject(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name,
                                  @QueryParam("source") String source, List<Property> properties) throws VirtualFileSystemException {
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
    }

    @Path("project/war")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public void createWarProject(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name, List<Property> properties,
                                 @Context UriInfo uriInfo) throws VirtualFileSystemException {
        VirtualFileSystem vfs = registry.getProvider(vfsId).newInstance(null, eventListenerList);
        Project project =
                vfs.createProject(vfs.getInfo().getRoot().getId(), name, "deprecated.project.type", properties);
        String projectId = project.getId();

        String host = uriInfo.getAbsolutePath().getHost();
        String groupId;
        if (host.contains(".")) {
            String[] split = host.split("\\.");
            StringBuilder result = new StringBuilder();
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
    }

    @Path("project/spring")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public void createSpringProject(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name, List<Property> properties,
                                    @Context UriInfo uriInfo) throws VirtualFileSystemException {
        VirtualFileSystem vfs = registry.getProvider(vfsId).newInstance(null, eventListenerList);
        Project project =
                vfs.createProject(vfs.getInfo().getRoot().getId(), name, "deprecated.project.type", properties);
        String projectId = project.getId();

        String host = uriInfo.getAbsolutePath().getHost();
        String groupId;
        if (host.contains(".")) {
            String[] split = host.split("\\.");
            StringBuilder result = new StringBuilder();
            int j = split.length - 1;
            while (j > 0) {
                result.append(split[j--]).append(".");
            }
            result.append(split[0]);
            groupId = result.toString();
        } else {
            groupId = host;
        }

        String pomContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                            " xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                            "   <modelVersion>4.0.0</modelVersion>\n" +
                            "<groupId>" + groupId + "</groupId>\n" +
                            "<artifactId>" + name + "</artifactId>\n" +
                            "   <packaging>war</packaging>\n" +
                            "   <version>1.0-SNAPSHOT</version>\n" +
                            "   <name>SpringDemo</name>\n" +
                            "   <properties>\n" +
                            "      <maven.compiler.source>1.6</maven.compiler.source>\n" +
                            "      <maven.compiler.target>1.6</maven.compiler.target>\n" +
                            "   </properties>\n" +
                            "   <dependencies>\n" +
                            "      <dependency>\n" +
                            "         <groupId>javax.servlet</groupId>\n" +
                            "         <artifactId>servlet-api</artifactId>\n" +
                            "         <version>2.5</version>\n" +
                            "         <scope>provided</scope>\n" +
                            "      </dependency>\n" +
                            "      <dependency>\n" +
                            "         <groupId>org.springframework</groupId>\n" +
                            "         <artifactId>spring-webmvc</artifactId>\n" +
                            "         <version>3.0.5.RELEASE</version>\n" +
                            "      </dependency>\n" +
                            "      <dependency>\n" +
                            "         <groupId>junit</groupId>\n" +
                            "         <artifactId>junit</artifactId>\n" +
                            "         <version>3.8.1</version>\n" +
                            "         <scope>test</scope>\n" +
                            "      </dependency>\n" +
                            "   </dependencies>\n" +
                            "   <build>\n" +
                            "      <finalName>greeting</finalName>\n" +
                            "   </build>\n" +
                            "</project>";
        InputStream pomIS = new ByteArrayInputStream(pomContent.getBytes());
        vfs.createFile(projectId, "pom.xml", MediaType.TEXT_XML_TYPE, pomIS);

        Folder src = vfs.createFolder(projectId, "src");
        Folder main = vfs.createFolder(src.getId(), "main");
        String mainId = main.getId();

        Folder java = vfs.createFolder(mainId, "java");
        Folder webapp = vfs.createFolder(mainId, "webapp");

        String webappId = webapp.getId();
        String indexContent = "<%\n" +
                              "   response.sendRedirect(\"spring/hello\");\n" +
                              "%>";
        InputStream indexIS = new ByteArrayInputStream(indexContent.getBytes());
        vfs.createFile(webappId, "index.jsp", MediaType.TEXT_PLAIN_TYPE, indexIS);

        Folder webInf = vfs.createFolder(webappId, "WEB-INF");
        String webInfId = webInf.getId();

        String webContent = "<!DOCTYPE web-app PUBLIC\n" +
                            " \"-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN\"\n" +
                            " \"http://java.sun.com/dtd/web-app_2_3.dtd\" >\n" +
                            "\n" +
                            "<web-app>\n" +
                            "   <display-name>Spring Web Application</display-name>\n" +
                            "   <servlet>\n" +
                            "      <servlet-name>spring</servlet-name>\n" +
                            "      <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>\n" +
                            "      <load-on-startup>1</load-on-startup>\n" +
                            "   </servlet>\n" +
                            "   <servlet-mapping>\n" +
                            "      <servlet-name>spring</servlet-name>\n" +
                            "      <url-pattern>/spring/*</url-pattern>\n" +
                            "   </servlet-mapping>\n" +
                            "</web-app>";
        InputStream webContentIS = new ByteArrayInputStream(webContent.getBytes());
        vfs.createFile(webInfId, "web.xml", MediaType.TEXT_XML_TYPE, webContentIS);

        String springServletContent = "<?xml version='1.0' encoding='utf-8'?>\n" +
                                      "<beans xmlns=\"http://www.springframework.org/schema/beans\" xmlns:xsi=\"http://www.w3" +
                                      ".org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.springframework.org/schema/beans " +
                                      "http://www.springframework.org/schema/beans/spring-beans-3.0.xsd\">\n" +
                                      "   <bean name=\"/hello\" class=\"helloworld.GreetingController\"></bean>\n" +
                                      "   <bean id=\"viewResolver\" class=\"org.springframework.web.servlet.view" +
                                      ".InternalResourceViewResolver\">\n" +
                                      "      <property name=\"prefix\" value=\"/WEB-INF/jsp/\" />\n" +
                                      "      <property name=\"suffix\" value=\".jsp\" />\n" +
                                      "   </bean>\n" +
                                      "</beans>";
        InputStream springServletContentIS = new ByteArrayInputStream(springServletContent.getBytes());
        vfs.createFile(webInfId, "spring-servlet.xml", MediaType.TEXT_XML_TYPE, springServletContentIS);

        Folder jsp = vfs.createFolder(webInfId, "jsp");

        String helloViewContent = "<html>\n" +
                                  "  <body bgcolor=\"white\">\n" +
                                  "    <div style=\"font-size: 150%; color: #850F0F\">\n" +
                                  "      <span>Enter your name: </span><br />\n" +
                                  "      <form method=\"post\" action=\"hello\">\n" +
                                  "        <input type=text size=\"15\" name=\"user\" >\n" +
                                  "        <input type=submit name=\"submit\" value=\"Ok\">\n" +
                                  "      </form>\n" +
                                  "    </div>\n" +
                                  "    <div>\n" +
                                  "      <%\n" +
                                  "          {\n" +
                                  "            java.lang.String answer = (java.lang.String)request.getAttribute(\"greeting\");   \n" +
                                  "      %>\n" +
                                  "      <span><%=answer%></span>\n" +
                                  "      <%\n" +
                                  "          }\n" +
                                  "      %>\n" +
                                  "    </div>\n" +
                                  "  </body>\n" +
                                  "</html>";
        InputStream helloViewContentIS = new ByteArrayInputStream(helloViewContent.getBytes());
        vfs.createFile(jsp.getId(), "hello_view.jsp", MediaType.TEXT_PLAIN_TYPE, helloViewContentIS);

        Folder helloworld = vfs.createFolder(java.getId(), "helloworld");

        String controllerContent = "package helloworld;\n" +
                                   "\n" +
                                   "import org.springframework.web.servlet.ModelAndView;\n" +
                                   "import org.springframework.web.servlet.mvc.Controller;\n" +
                                   "\n" +
                                   "import java.util.HashMap;\n" +
                                   "import java.util.Map;\n" +
                                   "\n" +
                                   "import javax.servlet.http.HttpServletRequest;\n" +
                                   "import javax.servlet.http.HttpServletResponse;\n" +
                                   "\n" +
                                   "public class GreetingController implements Controller\n" +
                                   "{\n" +
                                   "\n" +
                                   "   @Override\n" +
                                   "   public ModelAndView handleRequest(HttpServletRequest request, " +
                                   "HttpServletResponse response) throws Exception\n" +
                                   "   {\n" +
                                   "      String userName = request.getParameter(\"user\");\n" +
                                   "      String result = \"\";\n" +
                                   "      if (userName != null)\n" +
                                   "      {\n" +
                                   "        result = \"Hello, \" + userName + \"!\";\n" +
                                   "      }\n" +
                                   "\n" +
                                   "      ModelAndView view = new ModelAndView(\"hello_view\");\n" +
                                   "      view.addObject(\"greeting\", result);\n" +
                                   "      return view;\n" +
                                   "   }\n" +
                                   "}";
        InputStream controllerContentIS = new ByteArrayInputStream(controllerContent.getBytes());
        vfs.createFile(helloworld.getId(), "GreetingController.java", MediaType.TEXT_PLAIN_TYPE, controllerContentIS);
    }

    @Path("project/empty")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public void createEmptyProject(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name, List<Property> properties)
            throws VirtualFileSystemException {
        VirtualFileSystem vfs = registry.getProvider(vfsId).newInstance(null, eventListenerList);
        Project project =
                vfs.createProject(vfs.getInfo().getRoot().getId(), name, "deprecated.project.type", properties);
        String projectId = project.getId();

        String readMeContent = "This file was auto created when you created this project.";
        InputStream readMeIS = new ByteArrayInputStream(readMeContent.getBytes());
        vfs.createFile(projectId, "Readme.txt", MediaType.TEXT_PLAIN_TYPE, readMeIS);
    }
}