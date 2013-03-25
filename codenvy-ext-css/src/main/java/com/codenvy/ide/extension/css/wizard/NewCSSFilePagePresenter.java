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
package com.codenvy.ide.extension.css.wizard;

import com.codenvy.ide.api.ui.wizard.newfile.AbstractNewFilePagePresenter;
import com.codenvy.ide.api.ui.wizard.newfile.NewGenericFilePageView;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.extension.css.CssExtensionResource;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.rest.MimeType;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;


/**
 * Provides creating new CSS file.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewCSSFilePagePresenter extends AbstractNewFilePagePresenter
{
   /**
    * Create presenter.
    * 
    * @param view generic NewFileView
    * @param resourceProvider
    * @param selectionAgent
    */
   @Inject
   public NewCSSFilePagePresenter(CssExtensionResource resources, NewGenericFilePageView view, ResourceProvider resourceProvider, SelectionAgent selectionAgent)
   {
      this(resources.file(), view, resourceProvider, selectionAgent);
   }

   /**
    * Create presenter.
    * 
    * For tests.
    * 
    * @param image
    * @param view
    * @param resourceProvider
    */
   protected NewCSSFilePagePresenter(ImageResource image, NewGenericFilePageView view, ResourceProvider resourceProvider, SelectionAgent selectionAgent)
   {
      super("Create a new CSS file", image, view, "css", resourceProvider, selectionAgent);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void doFinish()
   {
      String fileName = view.getFileName() + (hasExtension() ? "" : '.' + getFileExtension());

      project.createFile(project, fileName, "@CHARSET \"UTF-8\";", MimeType.TEXT_CSS, new AsyncCallback<File>()
      {
         @Override
         public void onSuccess(File result)
         {
         }

         @Override
         public void onFailure(Throwable caught)
         {
            // TODO : Handle error to be able to display message to the User
            Log.error(NewCSSFilePagePresenter.class, caught);
         }
      });
   }
}