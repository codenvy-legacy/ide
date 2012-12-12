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
package org.exoplatform.ide.wizard.newfile;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.resources.model.Resource;
import org.exoplatform.ide.resources.model.ResourceNameValidator;
import org.exoplatform.ide.util.StringUtils;
import org.exoplatform.ide.wizard.AbstractWizardPagePresenter;
import org.exoplatform.ide.wizard.WizardPagePresenter;
import org.exoplatform.ide.wizard.newfile.NewGenericFileView.ActionDelegate;

/**
 * NewGenericFilePagePresenter is an abstract implementation of NewFilePresenter.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public abstract class AbstractNewFilePagePresenter extends AbstractWizardPagePresenter implements ActionDelegate
{
   private boolean isExtensionValid;

   private boolean hasExtension;

   private boolean isFileNameValid;

   private boolean hasSameFile;

   protected NewGenericFileView view;

   private String fileExtension;

   protected Project project;

   /**
    * Create presenter.
    * 
    * @param caption
    * @param image
    * @param view
    * @param fileExtension
    * @param resourceProvider
    */
   public AbstractNewFilePagePresenter(String caption, ImageResource image, NewGenericFileView view,
      String fileExtension, ResourceProvider resourceProvider)
   {
      super(caption, image);

      this.view = view;
      view.setDelegate(this);
      this.fileExtension = fileExtension;
      this.project = resourceProvider.getActiveProject();
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCompleted()
   {
      return !view.getFileName().isEmpty() && isFileNameValid && isExtensionValid && !hasSameFile;
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
   public boolean canFinish()
   {
      return true;
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
   public String getNotice()
   {
      if (view.getFileName().isEmpty())
      {
         return "The file name can't be empty.";
      }
      else if (!isFileNameValid)
      {
         return "The file name has incorrect symbol.";
      }
      else if (!isExtensionValid)
      {
         return "The file name must end in one of the following extensions " + '[' + getFileExtension() + "].";
      }
      else if (hasSameFile)
      {
         return "The file with same name already exists.";
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
   public void checkEnteredInformation()
   {
      isExtensionValid = true;
      hasExtension = false;

      String fileName = view.getFileName();
      JsonArray<String> parts = StringUtils.split(fileName, ".");
      int size = parts.size();
      // check have file name extension
      if (size > 1)
      {
         hasExtension = true;
         // compare last part of file name with extension for this type of file 
         isExtensionValid = parts.get(size - 1).compareTo(getFileExtension()) == 0;
      }

      isFileNameValid = ResourceNameValidator.isFileNameValid(parts.get(0));

      // add extension to file name if it needs
      String name = hasExtension ? fileName : fileName + '.' + getFileExtension();

      // Does the file with same name exist? 
      hasSameFile = false;
      JsonArray<Resource> children = project.getChildren();
      for (int i = 0; i < children.size() && !hasSameFile; i++)
      {
         Resource child = children.get(i);
         if (child.isFile())
         {
            hasSameFile = child.getName().compareTo(name) == 0;
         }
      }

      delegate.updateControls();
   }

   /**
    * Returns whether entered file name has extension. 
    * 
    * @return <code>true</code> if the name has extension, and 
    * <code>false</code> otherwise
    */
   protected boolean hasExtension()
   {
      return hasExtension;
   }

   /**
    * Returns file extension.
    * 
    * @return
    */
   protected String getFileExtension()
   {
      return fileExtension;
   }
}