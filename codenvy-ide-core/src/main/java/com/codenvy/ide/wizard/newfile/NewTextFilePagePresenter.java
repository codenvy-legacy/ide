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
package com.codenvy.ide.wizard.newfile;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.rest.MimeType;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;


/**
 * Provides creating new empty text file.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewTextFilePagePresenter extends AbstractNewFilePagePresenter
{

   /**
    * Create presenter.
    * 
    * @param resources
    * @param resourceProvider
    */
   @Inject
   public NewTextFilePagePresenter(Resources resources, ResourceProvider resourceProvider, SelectionAgent selectionAgent)
   {
      this(resources.newResourceIcon(), new NewGenericFilePageViewImpl(), resourceProvider, selectionAgent);
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
   protected NewTextFilePagePresenter(ImageResource image, NewGenericFilePageView view, ResourceProvider resourceProvider,
      SelectionAgent selectionAgent)
   {
      super("Create a new empty text file", image, view, "txt", resourceProvider, selectionAgent);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void doFinish()
   {
      String fileName = view.getFileName() + (hasExtension() ? "" : '.' + getFileExtension());

      project.createFile(project, fileName, "", MimeType.TEXT_PLAIN, new AsyncCallback<File>()
      {
         @Override
         public void onSuccess(File result)
         {
         }

         @Override
         public void onFailure(Throwable caught)
         {
            // TODO : Handle error to be able to display message to the User
            Log.error(NewTextFilePagePresenter.class, caught);
         }
      });
   }
}