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
package com.codenvy.ide.wizard.warproject;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.ProjectDescription;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.wizard.newgenericproject.NewGenericProjectPagePresenter;
import com.codenvy.ide.wizard.newgenericproject.NewGenericProjectPageView;
import com.codenvy.ide.wizard.newgenericproject.NewGenericProjectWizardResource;

import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.rest.MimeType;
import com.codenvy.ide.util.loging.Log;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;


/**
 * Provides creating new war project.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class NewWarProjectPagePresenter extends NewGenericProjectPagePresenter
{

   @Inject
   public NewWarProjectPagePresenter(NewGenericProjectWizardResource resources, ResourceProvider resourceProvider)
   {
      super(resources, resourceProvider);
   }

   protected NewWarProjectPagePresenter(ImageResource image, NewGenericProjectPageView view,
      ResourceProvider resourceProvider)
   {
      super(image, view, resourceProvider);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getCaption()
   {
      // TODO Auto-generated method stub
      return "New war project wizard";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void doFinish()
   {
      // TODO
      resourceProvider.createProject(view.getProjectName(), JsonCollections.<Property> createArray(new Property(
         ProjectDescription.PROPERTY_PRIMARY_NATURE, "Servlet/JSP"), new Property("cloudfoundry-application",
         "aplotnikov2"), new Property("vmc-target", "http://api.cloudfoundry.com")), new AsyncCallback<Project>()
      {
         @Override
         public void onFailure(Throwable caught)
         {
            Log.error(NewWarProjectPagePresenter.class, caught);
         }

         @Override
         public void onSuccess(final Project project)
         {
            project
               .createFile(
                  project,
                  "pom.xml",
                  "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                     + "xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">"
                     + " <modelVersion>4.0.0</modelVersion>\n"
                     + "<groupId>com.codenvy.codenvy</groupId>\n<artifactId>CloudFoundryTest</artifactId>\n"
                     + "<packaging>war</packaging>\n<version>1.0-SNAPSHOT</version>\n"
                     + "<name>java-web-sample</name>\n<build>\n<finalName>java-web-sample</finalName>\n</build>\n</project>",
                  MimeType.TEXT_XML, new AsyncCallback<File>()
                  {
                     @Override
                     public void onSuccess(File result)
                     {
                     }

                     @Override
                     public void onFailure(Throwable caught)
                     {
                        Log.error(NewWarProjectPagePresenter.class, caught);
                     }
                  });

            project.createFolder(project, "src", new AsyncCallback<Folder>()
            {
               @Override
               public void onSuccess(Folder result)
               {
                  project.createFolder(result, "main", new AsyncCallback<Folder>()
                  {
                     @Override
                     public void onSuccess(Folder result)
                     {
                        project.createFolder(result, "java", new AsyncCallback<Folder>()
                        {
                           @Override
                           public void onSuccess(Folder result)
                           {
                           }

                           @Override
                           public void onFailure(Throwable caught)
                           {
                              Log.error(NewWarProjectPagePresenter.class, caught);
                           }
                        });

                        project.createFolder(result, "resources", new AsyncCallback<Folder>()
                        {
                           @Override
                           public void onSuccess(Folder result)
                           {
                           }

                           @Override
                           public void onFailure(Throwable caught)
                           {
                              Log.error(NewWarProjectPagePresenter.class, caught);
                           }
                        });

                        project.createFolder(result, "webapp", new AsyncCallback<Folder>()
                        {
                           @Override
                           public void onSuccess(Folder result)
                           {
                              project.createFile(result, "sayhello.jsp",
                                 "<table border=\"0\" width=\"700\">\n<tr>\n<td width=\"150\"> &nbsp; </td>\n\n<td width=\"550\">\n\n"
                                    + "<h1>Hello, <%= request.getParameter(\"username\") %>\n"
                                    + "</h1>\n\n</td>\n</tr>\n</table>", MimeType.APPLICATION_JSP,
                                 new AsyncCallback<File>()
                                 {
                                    @Override
                                    public void onSuccess(File result)
                                    {
                                    }

                                    @Override
                                    public void onFailure(Throwable caught)
                                    {
                                       Log.error(NewWarProjectPagePresenter.class, caught);
                                    }
                                 });

                              project
                                 .createFile(
                                    result,
                                    "index.jsp",
                                    "<html>\n<head>\n<title>Hello, User</title>\n</head>\n<body bgcolor=\"#ffffff\">"
                                       + "<table border=\"0\" width=\"700\">\n<tr>\n<td width=\"150\"> &nbsp; </td>\n"
                                       + "<td width=\"550\">\n<h1>My name is eXo. What's yours?</h1>\n</td>\n</tr>\n"
                                       + "<tr>\n<td width=\"150\" &nbsp; </td>\n<td width=\"550\">\n"
                                       + "<form method=\"get\"><input type=\"text\" name=\"username\" size=\"25\">\n"
                                       + "<br>\n<input type=\"submit\" value=\"Submit\"><input type=\"reset\" value=\"Reset\">\n"
                                       + "</td>\n</tr>\n</form>\n</table>\n"
                                       + "<%  if ( request.getParameter(\"username\") != null ) { %>\n"
                                       + "<%@include file=\"sayhello.jsp\" %>\n" + "<% } %>\n" + "</body>\n</html>",
                                    MimeType.APPLICATION_JSP, new AsyncCallback<File>()
                                    {
                                       @Override
                                       public void onSuccess(File result)
                                       {
                                       }

                                       @Override
                                       public void onFailure(Throwable caught)
                                       {
                                          Log.error(NewWarProjectPagePresenter.class, caught);
                                       }
                                    });

                              project.createFolder(result, "WEB-INF", new AsyncCallback<Folder>()
                              {
                                 @Override
                                 public void onSuccess(Folder result)
                                 {
                                    project
                                       .createFile(
                                          result,
                                          "web.xml",
                                          "<!DOCTYPE web-app PUBLIC\"-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN\"\n\"http://java.sun.com/dtd/web-app_2_3.dtd\" >\n<web-app>\n"
                                             + "<display-name>Web Application Created With eXo IDE</display-name>\n</web-app>",
                                          MimeType.TEXT_XML, new AsyncCallback<File>()
                                          {
                                             @Override
                                             public void onSuccess(File result)
                                             {
                                             }

                                             @Override
                                             public void onFailure(Throwable caught)
                                             {
                                                Log.error(NewWarProjectPagePresenter.class, caught);
                                             }
                                          });
                                 }

                                 @Override
                                 public void onFailure(Throwable caught)
                                 {
                                    Log.error(NewWarProjectPagePresenter.class, caught);
                                 }
                              });
                           }

                           @Override
                           public void onFailure(Throwable caught)
                           {
                              Log.error(NewWarProjectPagePresenter.class, caught);
                           }
                        });

                     }

                     @Override
                     public void onFailure(Throwable caught)
                     {
                        Log.error(NewWarProjectPagePresenter.class, caught);
                     }
                  });

                  // TODO
                  //                  paas.getPaaSActions().deploy(project, deployResultHandler);
               }

               @Override
               public void onFailure(Throwable caught)
               {
                  Log.error(NewWarProjectPagePresenter.class, caught);
               }
            });
         }
      });
   }
}