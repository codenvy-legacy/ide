/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.extension.demo;

import com.codenvy.ide.api.expressions.Expression;
import com.codenvy.ide.api.expressions.NoProjectOpenedExpression;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.menu.ExtendedCommand;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.ProjectDescription;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.MimeType;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Date;

/**
 * Creates Demo project, used for demo purposes
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@Singleton
public class CreateDemoCommand implements ExtendedCommand
{
   private final ResourceProvider resourceManager;
   private final NoProjectOpenedExpression noProjectOpenedExpression;

   @Inject
   private CreateDemoCommand(ResourceProvider resourceManager, NoProjectOpenedExpression noProjectOpenedExpression)
   {
      this.resourceManager = resourceManager;
      this.noProjectOpenedExpression = noProjectOpenedExpression;
   }

   @Override
   public void execute()
   {
      // DUMMY CREATE DEMO CONTENT
      resourceManager.createProject("Test Project " + (new Date().getTime()), JsonCollections.<Property> createArray(
         //
         new Property(ProjectDescription.PROPERTY_PRIMARY_NATURE, "java"), //JavaProject.PRIMARY_NATURE
         new Property("folders.source", JsonCollections.createArray("src/main/java", "src/main/resources",
            "src/test/java", "src/test/resources"))), new AsyncCallback<Project>()
      {

         @Override
         public void onSuccess(final Project project)
         {
            project.createFolder(project, "src", new AsyncCallback<Folder>()
            {

               @Override
               public void onFailure(Throwable caught)
               {
                  Log.error(getClass(), caught);
               }

               @Override
               public void onSuccess(Folder result)
               {
                  project.createFolder(result, "main", new AsyncCallback<Folder>()
                  {

                     @Override
                     public void onFailure(Throwable caught)
                     {
                        Log.error(getClass(), caught);
                     }

                     @Override
                     public void onSuccess(Folder result)
                     {
                        project.createFolder(result, "java/org/exoplatform/ide", new AsyncCallback<Folder>()
                        {

                           @Override
                           public void onFailure(Throwable caught)
                           {
                              Log.error(getClass(), caught);
                           }

                           @Override
                           public void onSuccess(Folder result)
                           {
                              project.createFile(result, "Test.java",
                                 "package org.exoplatform.ide;\n public class Test\n{\n}", MimeType.APPLICATION_JAVA,
                                 new AsyncCallback<File>()
                                 {

                                    @Override
                                    public void onFailure(Throwable caught)
                                    {
                                       Log.error(getClass(), caught);
                                    }

                                    @Override
                                    public void onSuccess(File result)
                                    {
                                    }
                                 });
                              project.createFolder(result, "void", new AsyncCallback<Folder>()
                              {

                                 @Override
                                 public void onFailure(Throwable caught)
                                 {
                                    Log.error(getClass(), caught);
                                 }

                                 @Override
                                 public void onSuccess(Folder result)
                                 {
                                 }
                              });
                           }
                        });
                        project.createFolder(result, "resources/org/exoplatform/ide", new AsyncCallback<Folder>()
                        {

                           @Override
                           public void onFailure(Throwable caught)
                           {
                              Log.error(getClass(), caught);
                           }

                           @Override
                           public void onSuccess(Folder result)
                           {
                              project.createFile(result, "styles.css", ".test{\n\n}", "text/css",
                                 new AsyncCallback<File>()
                                 {

                                    @Override
                                    public void onSuccess(File result)
                                    {
                                       // ok
                                    }

                                    @Override
                                    public void onFailure(Throwable caught)
                                    {
                                       Log.error(getClass(), caught);
                                    }
                                 });

                           }
                        });

                        project.createFolder(result, "webapp", new AsyncCallback<Folder>()
                        {

                           @Override
                           public void onFailure(Throwable caught)
                           {
                              Log.error(getClass(), caught);
                           }

                           @Override
                           public void onSuccess(Folder result)
                           {
                           }
                        });
                     }
                  });
                  project.createFolder(result, "test", new AsyncCallback<Folder>()
                  {

                     @Override
                     public void onFailure(Throwable caught)
                     {
                        Log.error(getClass(), caught);
                     }

                     @Override
                     public void onSuccess(Folder result)
                     {
                        project.createFolder(result, "java/org/exoplatform/ide", new AsyncCallback<Folder>()
                        {

                           @Override
                           public void onFailure(Throwable caught)
                           {
                              Log.error(getClass(), caught);
                           }

                           @Override
                           public void onSuccess(Folder result)
                           {
                              project.createFile(result, "TestClass.java",
                                 "package org.exoplatform.ide;\n public class TestClass\n{\n}",
                                 MimeType.APPLICATION_JAVA, new AsyncCallback<File>()
                                 {

                                    @Override
                                    public void onFailure(Throwable caught)
                                    {
                                    }

                                    @Override
                                    public void onSuccess(File result)
                                    {
                                    }
                                 });
                           }
                        });

                        project.createFolder(result, "resources/org/exoplatform/ide", new AsyncCallback<Folder>()
                        {

                           @Override
                           public void onFailure(Throwable caught)
                           {
                              Log.error(getClass(), caught);
                           }

                           @Override
                           public void onSuccess(Folder result)
                           {
                              project.createFile(result, "TestFileOnFs.txt",
                                 "This is file content of the file from VFS", "text/text-pain",
                                 new AsyncCallback<File>()
                                 {

                                    @Override
                                    public void onSuccess(File result)
                                    {
                                       // ok
                                    }

                                    @Override
                                    public void onFailure(Throwable caught)
                                    {
                                       GWT.log("Error creating demo folder" + caught);
                                    }
                                 });

                           }
                        });
                     }
                  });

               }
            });

         }

         @Override
         public void onFailure(Throwable caught)
         {
            GWT.log("Error creating demo content" + caught);
         }
      });
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public ImageResource getIcon()
   {
      return null;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public Expression inContext()
   {
      return null;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public Expression canExecute()
   {
      return noProjectOpenedExpression;
   }

    /**
    * {@inheritDoc}
    */
   @Override
   public String getToolTip()
   {
      return null;
   }
}