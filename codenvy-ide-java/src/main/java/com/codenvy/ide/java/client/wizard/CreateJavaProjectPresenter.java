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
package com.codenvy.ide.java.client.wizard;

import com.codenvy.ide.api.wizard.newproject.AbstractCreateProjectPresenter;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.java.client.projectmodel.CompilationUnit;
import com.codenvy.ide.java.client.projectmodel.JavaProject;
import com.codenvy.ide.java.client.projectmodel.JavaProjectDesctiprion;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.ProjectDescription;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.MimeType;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Create java project presenter.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class CreateJavaProjectPresenter extends AbstractCreateProjectPresenter
{
   public static final String SOURCE_FOLDER = "SOURCE_FOLDER";

   private ResourceProvider resourceProvider;

   /**
    * Create new java project presenter.
    * 
    * @param resourceProvider
    */
   @Inject
   public CreateJavaProjectPresenter(ResourceProvider resourceProvider)
   {
      this.resourceProvider = resourceProvider;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void create(final AsyncCallback<Project> callback)
   {
      String projectName = getProjectName();
      final String sourceFolder = getParam(SOURCE_FOLDER);

      resourceProvider.createProject(projectName, JsonCollections.<Property> createArray(new Property(
         ProjectDescription.PROPERTY_PRIMARY_NATURE, JavaProject.PRIMARY_NATURE),//
         new Property(JavaProjectDesctiprion.PROPERTY_SOURCE_FOLDERS, JsonCollections.createArray(sourceFolder))),
         new AsyncCallback<Project>()
         {
            @Override
            public void onFailure(Throwable caught)
            {
               Log.error(NewJavaProjectPagePresenter.class, caught);
            }

            @Override
            public void onSuccess(Project result)
            {
               createSourceFolder(result, sourceFolder);
               createReadMeFile(result);
               callback.onSuccess(result);
            }
         });
   }

   private void createReadMeFile(Project project)
   {

      project.createFile(project, "Readme.txt", "This file was auto created when you created this project.",
         MimeType.TEXT_PLAIN, new AsyncCallback<File>()
         {
            public void onFailure(Throwable caught)
            {
            }

            public void onSuccess(File result)
            {
            }
         });
   }

   private void createSourceFolder(final Project project, String sourceFolder)
   {
      project.createFolder(project, sourceFolder, new AsyncCallback<Folder>()
      {
         @Override
         public void onFailure(Throwable caught)
         {
            Log.error(NewJavaProjectPagePresenter.class, caught);
         }

         @Override
         public void onSuccess(Folder result)
         {
            createTestClass((JavaProject)project, result);
         }
      });
   }

   private void createTestClass(JavaProject project, Folder result)
   {
      project
         .createCompilationUnit(
            result,
            "HelloWorld.java",
            "\npublic class HelloWorld{\n   public static void main(String args[]){\n      System.out.println(\"Hello World!\");\n   }\n}",
            new AsyncCallback<CompilationUnit>()
            {
               @Override
               public void onFailure(Throwable caught)
               {
                  Log.error(NewJavaProjectPagePresenter.class, caught);
               }

               @Override
               public void onSuccess(CompilationUnit result)
               {
               }
            });
   }
}