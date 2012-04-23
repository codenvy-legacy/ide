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
package org.eclipse.jdt.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;

import org.eclipse.jdt.client.core.JavaConventions;
import org.eclipse.jdt.client.core.JavaCore;
import org.eclipse.jdt.client.event.CreatePackageEvent;
import org.eclipse.jdt.client.event.CreatePackageHandler;
import org.eclipse.jdt.client.runtime.IStatus;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FolderUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class CreatePackagePresenter implements ViewClosedHandler, ItemsSelectedHandler, CreatePackageHandler
{
   interface Display extends IsView
   {
      String ID = "ideCreatePackageView";

      HasValue<String> getPackageNameField();

      HasClickHandlers getOkButton();

      HasClickHandlers getCancelButton();

      HasText getErrorLabel();

      HasText getWarningLabel();

      void setOkButtonEnabled(boolean enabled);
   }

   private HandlerManager eventBus;

   private VirtualFileSystem vfs;

   private IDE ide;

   private Display display;

   private FolderModel parentFolder;

   /**
    * @param eventBus
    * @param vfs
    * @param ide
    */
   public CreatePackagePresenter(HandlerManager eventBus, VirtualFileSystem vfs, IDE ide)
   {
      super();
      this.eventBus = eventBus;
      this.vfs = vfs;
      this.ide = ide;
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(CreatePackageEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (!event.getSelectedItems().isEmpty())
      {
         Item item = event.getSelectedItems().get(0);
         if (item instanceof FolderModel)
            parentFolder = (FolderModel)item;
         else
            parentFolder = ((FileModel)item).getParent();
      }
      else
         parentFolder = null;
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView().getId().equals(Display.ID))
      {
         display = null;
      }
   }

   /**
    * @see org.eclipse.jdt.client.event.CreatePackageHandler#onCreatePackage(org.eclipse.jdt.client.event.CreatePackageEvent)
    */
   @Override
   public void onCreatePackage(CreatePackageEvent event)
   {
      if (parentFolder == null)
         return;

      if (display == null)
      {
         display = GWT.create(Display.class);
      }
      ide.openView(display.asView());
      bind();

   }

   /**
    * 
    */
   private void bind()
   {
      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            ide.closeView(Display.ID);
         }
      });

      display.getOkButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doCreate();
         }
      });

      display.getPackageNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            validate(event.getValue());
         }
      });

      display.setOkButtonEnabled(false);
   }

   /**
    * @param value
    */
   private void validate(String value)
   {
      IStatus status =
         JavaConventions.validatePackageName(value, JavaCore.getOption(JavaCore.COMPILER_SOURCE),
            JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE));
      switch (status.getSeverity())
      {
         case IStatus.WARNING :
            display.getWarningLabel().setText(status.getMessage());
            display.getErrorLabel().setText("");
            display.setOkButtonEnabled(true);
            break;
         case IStatus.OK :
            display.getErrorLabel().setText("");
            display.getWarningLabel().setText("");
            display.setOkButtonEnabled(true);
            break;

         default :
            display.setOkButtonEnabled(false);
            display.getWarningLabel().setText("");
            display.getErrorLabel().setText(status.getMessage());
            break;
      }
   }

   /**
    * 
    */
   protected void doCreate()
   {
      String pack = display.getPackageNameField().getValue().replaceAll("\\.", "/");
      FolderModel newFolder = new FolderModel(pack, parentFolder);
      try
      {
         vfs.createFolder(parentFolder, new AsyncRequestCallback<FolderModel>(new FolderUnmarshaller(newFolder))
         {

            @Override
            protected void onSuccess(FolderModel result)
            {

               ide.closeView(Display.ID);
               eventBus.fireEvent(new RefreshBrowserEvent(parentFolder));
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               eventBus.fireEvent(new ExceptionThrownEvent(exception));
            }
         });
      }
      catch (RequestException e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(e));
      }
   }
}
