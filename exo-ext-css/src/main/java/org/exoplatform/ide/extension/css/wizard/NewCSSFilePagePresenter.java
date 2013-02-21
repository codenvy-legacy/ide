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
package org.exoplatform.ide.extension.css.wizard;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import org.exoplatform.ide.Resources;
import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.api.selection.SelectionAgent;
import org.exoplatform.ide.resources.model.File;
import org.exoplatform.ide.rest.MimeType;
import org.exoplatform.ide.util.loging.Log;
import org.exoplatform.ide.wizard.newfile.AbstractNewFilePagePresenter;
import org.exoplatform.ide.wizard.newfile.NewGenericFilePageViewImpl;
import org.exoplatform.ide.wizard.newfile.NewGenericFileView;

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
    * @param resources
    * @param resourceProvider
    */
   @Inject
   public NewCSSFilePagePresenter(Resources resources, ResourceProvider resourceProvider, SelectionAgent selectionAgent)
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
   protected NewCSSFilePagePresenter(ImageResource image, NewGenericFileView view, ResourceProvider resourceProvider, SelectionAgent selectionAgent)
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