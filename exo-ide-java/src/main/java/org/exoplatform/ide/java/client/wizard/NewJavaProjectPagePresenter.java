/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.java.client.wizard;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.java.client.JavaClientBundle;
import org.exoplatform.ide.java.client.projectmodel.CompilationUnit;
import org.exoplatform.ide.java.client.projectmodel.JavaProject;
import org.exoplatform.ide.java.client.projectmodel.JavaProjectDesctiprion;
import org.exoplatform.ide.java.client.wizard.NewJavaProjectPageView.ActionDelegate;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.resources.model.File;
import org.exoplatform.ide.resources.model.Folder;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.resources.model.ProjectDescription;
import org.exoplatform.ide.resources.model.Property;
import org.exoplatform.ide.resources.model.ResourceNameValidator;
import org.exoplatform.ide.rest.MimeType;
import org.exoplatform.ide.util.loging.Log;
import org.exoplatform.ide.wizard.AbstractWizardPagePresenter;
import org.exoplatform.ide.wizard.WizardPagePresenter;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 
 */
public class NewJavaProjectPagePresenter extends AbstractWizardPagePresenter implements ActionDelegate
{
   private NewJavaProjectPageView view;

   private ResourceProvider resourceProvider;

   private boolean hasProjectList;

   private JsonArray<String> projectList;

   private boolean hasProjectIncorrectSymbol;

   private boolean hasSameProject;

   private boolean hasResourceFolderIncorrectSymbol;

   @Inject
   public NewJavaProjectPagePresenter(JavaClientBundle resources, NewJavaProjectPageView view, ResourceProvider resourceProvider)
   {
      super("Java Project", resources.javaProject());
      this.view = view;
      this.resourceProvider = resourceProvider;
      view.setDelegate(this);
      resourceProvider.listProjects(new AsyncCallback<JsonArray<String>>()
      {
         public void onSuccess(JsonArray<String> result)
         {
            projectList = result;
            hasProjectList = true;
         }

         public void onFailure(Throwable caught)
         {
            Log.error(NewJavaProjectPagePresenter.class, caught);
         }
      });
   }

   @Override
   public WizardPagePresenter flipToNext()
   {
      return null;
   }

   @Override
   public boolean canFinish()
   {
      return isCompleted();
   }

   @Override
   public boolean hasNext()
   {
      return false;
   }

   @Override
   public boolean isCompleted()
   {
      return !view.getProjectName().isEmpty() && !hasProjectIncorrectSymbol && hasProjectList && !hasSameProject
         && !hasResourceFolderIncorrectSymbol && !view.getResourceFolder().isEmpty();
   }

   @Override
   public String getNotice()
   {
      if (view.getProjectName().isEmpty())
      {
         return "Please, enter a project name.";
      }
      else
      {
         if (hasProjectList)
         {
            if (hasSameProject)
            {
               return "Project with this name already exists.";
            }
            else
            {
               if (view.getResourceFolder().isEmpty())
               {
                  return "Please, enter a source folder name.";
               }
               else if (hasProjectIncorrectSymbol)
               {
                  return "Incorrect project name.";
               }
               else if (hasResourceFolderIncorrectSymbol)
               {
                  return "Incorrect source folder name.";
               }
               else
               {
                  return null;
               }
            }
         }
         else
         {
            return "Please wait, checking project list";
         }
      }
   }

   @Override
   public void go(AcceptsOneWidget container)
   {
      container.setWidget(view);
   }

   @Override
   public void checkProjectInput()
   {

      hasProjectIncorrectSymbol = false;
      String projectName = view.getProjectName();
      String resourceFolder = view.getResourceFolder();
      hasProjectIncorrectSymbol = !ResourceNameValidator.isProjectNameValid(projectName);
      hasResourceFolderIncorrectSymbol = !ResourceNameValidator.isFolderNameValid(resourceFolder);

      hasSameProject = false;
      for (int i = 0; i < projectList.size() && !hasSameProject; i++)
      {
         String name = projectList.get(i);
         hasSameProject = projectName.compareTo(name) == 0;
      }

      delegate.updateControls();
   }

   @Override
   public void doFinish()
   {
      resourceProvider.createProject(view.getProjectName(), JsonCollections
         .<Property>createArray(new Property(ProjectDescription.PROPERTY_PRIMARY_NATURE, JavaProject.PRIMARY_NATURE),//
            new Property(JavaProjectDesctiprion.PROPERTY_SOURCE_FOLDERS, JsonCollections.createArray(view.getResourceFolder()))), new AsyncCallback<Project>()
      {
         @Override
         public void onFailure(Throwable caught)
         {
            Log.error(NewJavaProjectPagePresenter.class, caught);
         }

         @Override
         public void onSuccess(Project result)
         {
            createSourceFolder(result);
            createReadMeFile(result);
         }
      }

      );
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

   private void createSourceFolder(final Project project)
   {
      project.createFolder(project, view.getResourceFolder(), new AsyncCallback<Folder>()
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
      project.createCompilationUnit(result, "HelloWorld.java", "\npublic class HelloWorld{\n   public static void main(String args[]){\n      System.out.println(\"Hello World!\");\n   }\n}", new AsyncCallback<CompilationUnit>()
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
