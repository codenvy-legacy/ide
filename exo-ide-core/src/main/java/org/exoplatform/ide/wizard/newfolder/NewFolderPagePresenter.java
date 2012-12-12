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
package org.exoplatform.ide.wizard.newfolder;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import org.exoplatform.ide.Resources;
import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.resources.model.Folder;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.resources.model.Resource;
import org.exoplatform.ide.resources.model.ResourceNameValidator;
import org.exoplatform.ide.wizard.AbstractWizardPagePresenter;
import org.exoplatform.ide.wizard.WizardPagePresenter;
import org.exoplatform.ide.wizard.newfolder.NewFolderPageView.ActionDelegate;

/**
 * Provides creating new folder.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewFolderPagePresenter extends AbstractWizardPagePresenter implements ActionDelegate
{
   private NewFolderPageView view;
   
   private boolean isFolderNameValid;

   private boolean hasSameFolder;

   protected Project project;

   /**
    * Create presenter.
    * 
    * @param resources
    * @param resourceProvider
    */
   @Inject
   public NewFolderPagePresenter(Resources resources, ResourceProvider resourceProvider)
   {
      // TODO change icon?
      this("Create a new folder resource", resources.folder(), new NewFolderPageViewImpl(), resourceProvider);
   }

   /**
    * Create presenter.
    * 
    * For tests.
    * 
    * @param caption
    * @param image
    * @param view
    * @param resourceProvider
    */
   protected NewFolderPagePresenter(String caption, ImageResource image, NewFolderPageView view,
      ResourceProvider resourceProvider)
   {
      super(caption, image);
      this.view = view;
      view.setDelegate(this);
      this.project = resourceProvider.getActiveProject();
   }

   /**
    * {@inheritDoc}
    */
   public boolean canFinish()
   {
      return true;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCompleted()
   {
      return !view.getFolderName().isEmpty() && isFolderNameValid && !hasSameFolder;
   }

   /**
    * {@inheritDoc}
    */
   public String getNotice()
   {
      if (view.getFolderName().isEmpty())
      {
         return "The folder name can't be empty.";
      }
      else if (!isFolderNameValid)
      {
         return "The folder name has incorrect symbol.";
      }
      else if (hasSameFolder)
      {
         return "The folder with same name already exists.";
      }

      return null;
   }

   /**
    * {@inheritDoc}
    */
   public void doFinish()
   {
      project.createFolder(project, view.getFolderName(), new AsyncCallback<Folder>()
      {
         public void onSuccess(Folder result)
         {
         }

         public void onFailure(Throwable caught)
         {
         }
      });
   }

   /**
    * {@inheritDoc}
    */
   public void checkEnteredInformation()
   {
      String newFolderName = view.getFolderName();
      isFolderNameValid = ResourceNameValidator.isFolderNameValid(newFolderName);

      // Does the folder with same name exist?
      hasSameFolder = false;
      JsonArray<Resource> children = project.getChildren();
      for (int i = 0; i < children.size() && !hasSameFolder; i++)
      {
         Resource child = children.get(i);
         if (child.isFolder())
         {
            hasSameFolder = child.getName().compareTo(newFolderName) == 0;
         }
      }

      delegate.updateControls();
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
   public WizardPagePresenter flipToNext()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public boolean hasNext()
   {
      return false;
   }
}