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
package org.exoplatform.ide.wizard.newgenericproject;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.resources.model.File;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.resources.model.Property;
import org.exoplatform.ide.rest.MimeType;
import org.exoplatform.ide.util.StringUtils;
import org.exoplatform.ide.util.loging.Log;
import org.exoplatform.ide.wizard.AbstractWizardPagePresenter;
import org.exoplatform.ide.wizard.WizardPagePresenter;
import org.exoplatform.ide.wizard.newgenericproject.NewGenericProjectPageView.ActionDelegate;

/**
 * Provides creating new generic project.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewGenericProjectPagePresenter extends AbstractWizardPagePresenter implements ActionDelegate
{
   private NewGenericProjectPageView view;

   private WizardPagePresenter next;

   private ResourceProvider resourceProvider;

   private boolean hasIncorrectSymbol;

   private boolean hasProjectList;

   private boolean hasSameProject;

   private JsonArray<String> projectList;

   /**
    * Create presenter
    * 
    * @param resources 
    * @param resourceProvider
    */
   @Inject
   public NewGenericProjectPagePresenter(NewGenericProjectWizardResource resources,
      ResourceProvider resourceProvider)
   {
      this(resources.genericProjectIcon(), new NewGenericProjectPageViewImpl(), resourceProvider);
   }

   /**
    * Create presenter
    * 
    * For Unit Tests
    * 
    * @param image
    * @param view
    * @param resourceProvider
    */
   protected NewGenericProjectPagePresenter(ImageResource image, NewGenericProjectPageView view,
      ResourceProvider resourceProvider)
   {
      super("New generic project wizard", image);
      this.view = view;
      view.setDelegate(this);
      this.resourceProvider = resourceProvider;
      
      this.resourceProvider.listProjects(new AsyncCallback<JsonArray<String>>()
      {
         public void onSuccess(JsonArray<String> result)
         {
            projectList = result;
            hasProjectList = true;
         }
         
         public void onFailure(Throwable caught)
         {
            Log.error(NewGenericProjectPagePresenter.class, caught);
         }
      });
   }

   /**
    * {@inheritDoc}
    */
   public WizardPagePresenter flipToNext()
   {
      return next;
   }

   /**
    * {@inheritDoc}
    */
   public boolean canFinish()
   {
      return isCompleted();
   }

   /**
    * {@inheritDoc}
    */
   public boolean hasNext()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCompleted()
   {
      return !view.getProjectName().isEmpty() && !hasIncorrectSymbol && hasProjectList && !hasSameProject;
   }

   /**
    * {@inheritDoc}
    */
   public String getNotice()
   {
      if (view.getProjectName().isEmpty())
      {
         return "Please, enter a project name.";
      }
      else if (!hasProjectList)
      {
         return "Please wait, checking project list";
      }
      else if (hasSameProject)
      {
         return "Project with this name already exists.";
      }
      else if (hasIncorrectSymbol)
      {
         return "Incorrect project name.";
      }

      return null;
   }

   /**
    * {@inheritDoc}
    */
   public void go(AcceptsOneWidget container)
   {
      container.setWidget(view);
   }

   /**
    * {@inheritDoc}
    */
   public void checkProjectName()
   {
      hasIncorrectSymbol = false;
      String projectName = view.getProjectName();
      for (int i = 0; i < projectName.length() && hasIncorrectSymbol == false; i++)
      {
         Character ch = projectName.charAt(i);
         hasIncorrectSymbol = !(StringUtils.isWhitespace(ch) || StringUtils.isAlphaNumOrUnderscore(ch));
      }

      hasSameProject = false;
      for (int i = 0; i < projectList.size() && hasSameProject == false; i++)
      {
         String name = projectList.get(i);
         hasSameProject = projectName.compareTo(name) == 0;
      }

      delegate.updateControls();
   }

   /**
    * {@inheritDoc}
    */
   public void doFinish()
   {
      resourceProvider.createProject(view.getProjectName(), JsonCollections.<Property> createArray(),
         new AsyncCallback<Project>()
         {
            public void onSuccess(Project project)
            {
               project.createFile(project, "Readme.txt", "This file was auto created when you created this project.",
                  MimeType.TEXT_PLAIN, new AsyncCallback<File>()
                  {
                     public void onFailure(Throwable caught)
                     {
                        Log.error(NewGenericProjectPagePresenter.class, caught);
                     }

                     public void onSuccess(File result)
                     {
                     }
                  });
            }

            public void onFailure(Throwable caught)
            {
               Log.error(NewGenericProjectPagePresenter.class, caught);
            }
         });
   }
}