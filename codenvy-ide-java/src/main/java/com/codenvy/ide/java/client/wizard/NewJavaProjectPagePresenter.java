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
package com.codenvy.ide.java.client.wizard;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.java.client.JavaClientBundle;
import com.codenvy.ide.java.client.wizard.NewJavaProjectPageView.ActionDelegate;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.paas.AbstractPaasWizardPagePresenter;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.ResourceNameValidator;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.wizard.WizardPagePresenter;
import com.codenvy.ide.wizard.newproject.AbstractNewProjectWizardPage;
import com.codenvy.ide.wizard.newproject.CreateProjectHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

/**
 * Presenter of wizard for creating java project
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 
 */
public class NewJavaProjectPagePresenter extends AbstractNewProjectWizardPage implements ActionDelegate
{
   private NewJavaProjectPageView view;

   private boolean hasProjectList;

   private JsonArray<String> projectList;

   private boolean hasProjectIncorrectSymbol;

   private boolean hasSameProject;

   private boolean hasResourceFolderIncorrectSymbol;

   /**
    * Create new java project wizard page presenter.
    * 
    * @param resources
    * @param view
    * @param resourceProvider
    */
   @Inject
   public NewJavaProjectPagePresenter(JavaClientBundle resources, NewJavaProjectPageView view,
      ResourceProvider resourceProvider)
   {
      super("Java Project", resources.javaProject());
      this.view = view;
      this.view.setDelegate(this);
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

   /**{@inheritDoc}*/
   @Override
   public WizardPagePresenter flipToNext()
   {
      AbstractPaasWizardPagePresenter paasWizardPage = getPaaSWizardPage();
      paasWizardPage.setCreateProjectHandler(getCreateProjectHandler());
      paasWizardPage.setPrevious(this);
      paasWizardPage.setUpdateDelegate(delegate);

      return paasWizardPage;
   }

   /**{@inheritDoc}*/
   @Override
   public boolean canFinish()
   {
      return isCompleted() && !hasNext();
   }

   /**{@inheritDoc}*/
   @Override
   public boolean hasNext()
   {
      return getPaaSWizardPage() != null;
   }

   /**{@inheritDoc}*/
   @Override
   public boolean isCompleted()
   {
      return !view.getProjectName().isEmpty() && !hasProjectIncorrectSymbol && hasProjectList && !hasSameProject
         && !hasResourceFolderIncorrectSymbol && !view.getSourceFolder().isEmpty();
   }

   /**{@inheritDoc}*/
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
               if (view.getSourceFolder().isEmpty())
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

   /**{@inheritDoc}*/
   @Override
   public void go(AcceptsOneWidget container)
   {
      container.setWidget(view);
   }

   /**{@inheritDoc}*/
   @Override
   public void checkProjectInput()
   {

      hasProjectIncorrectSymbol = false;
      String projectName = view.getProjectName();
      String resourceFolder = view.getSourceFolder();
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

   /**{@inheritDoc}*/
   @Override
   public void doFinish()
   {
      CreateProjectHandler createProjectHandler = getCreateProjectHandler();
      createProjectHandler.setProjectName(view.getProjectName());
      createProjectHandler.addParam(CreateJavaProjectPresenter.SOURCE_FOLDER, view.getSourceFolder());
      createProjectHandler.create(new AsyncCallback<Project>()
      {
         @Override
         public void onSuccess(Project result)
         {
            // do nothing
         }

         @Override
         public void onFailure(Throwable caught)
         {
            // do nothing
         }
      });
   }
}