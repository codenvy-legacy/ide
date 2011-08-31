/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.extension.groovy.client.classpath.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.extension.groovy.client.classpath.EnumSourceType;
import org.exoplatform.ide.extension.groovy.client.classpath.GroovyClassPathEntry;
import org.exoplatform.ide.extension.groovy.client.classpath.Workspace;
import org.exoplatform.ide.extension.groovy.client.classpath.ui.event.AddSourceToBuildPathEvent;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for choosing source for class path.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jan 10, 2011 $
 *
 */
public class ChooseSourcePathPresenter
{
   interface Display extends IsView
   {
      /**
       * Get confirm button.
       * 
       * @return {@link HasClickHandlers} confirm button
       */
      HasClickHandlers getOkButton();

      /**
       * Get cancel button.
       * 
       * @return {@link HasClickHandlers} cancel button
       */
      HasClickHandlers getCancelButton();

      /**
       * Get tree with items.
       * 
       * @return {@link TreeGridItem} tree
       */
      TreeGridItem<Item> getItemsTree();

      /**
       * Get the list of selected items in the tree.
       * 
       * @return {@link List}
       */
      List<Item> getSelectedItems();

      /**
       * Change the enable state of the confirm button.
       * 
       * @param isEnabled is enabled or not
       */
      void enableOkButtonState(boolean isEnabled);
   }

   /**
    * The display.
    */
   private Display display;

   /**
    * Handler manager.
    */
   private HandlerManager eventBus;

   
   private FolderModel root;

   /**
    * @param eventBus handler manager
    * @param restContext REST context
    */
   public ChooseSourcePathPresenter(HandlerManager eventBus, FolderModel root)
   {
      this.eventBus = eventBus;
      this.root = root;

      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
      }

      IDE.getInstance().openView(display.asView());

      display.enableOkButtonState(false);
      display.getItemsTree().setValue(root);
//      getWorkspaces();
   }

   /**
    * Bind pointed display with presenter.
    */
   public void bindDisplay()
   {
      display.getItemsTree().addOpenHandler(new OpenHandler<Item>()
      {

         public void onOpen(OpenEvent<Item> event)
         {
            if (event.getTarget() instanceof FolderModel)
            {
               getFolderContent((FolderModel)event.getTarget());
            }
         }
      });

      display.getItemsTree().addSelectionHandler(new SelectionHandler<Item>()
      {
         public void onSelection(SelectionEvent<Item> event)
         {
            onSelectionChanged();
         }
      });

      display.getOkButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            addSourceToBuildPath();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            closeView();
         }
      });
   }

   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }

   /**
    * Perform actions on selection in the tree with items changed.
    */
   private void onSelectionChanged()
   {
      List<Item> selectedItems = display.getSelectedItems();
      if (selectedItems.size() < 0)
      {
         display.enableOkButtonState(false);
         return;
      }
      //Check workspace is among selected:
      for (Item item : selectedItems)
      {
         if (item instanceof Workspace)
         {
            display.enableOkButtonState(false);
            return;
         }
      }
      display.enableOkButtonState(true);
   }

   /**
    * Get content of the pointed folder.
    * 
    * @param folder
    */
   private void getFolderContent(final FolderModel folder)
   {
      try
      {
         VirtualFileSystem.getInstance().getChildren(
            folder,
            new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<List<Item>>(
               new ChildrenUnmarshaller(new ArrayList<Item>()))
            {

               @Override
               protected void onSuccess(List<Item> result)
               {
                  folder.getChildren().setItems(result);
                  for (Item i : result)
                  {
                     if (i.getItemType() == ItemType.FILE)
                     {
                        ((FileModel)i).setParent(folder);
                     }
                     else
                     {
                        ((FolderModel)i).setParent(folder);
                     }
                  }
                  display.getItemsTree().setValue(folder);
               }

               @Override
               protected void onFailure(Throwable exception)
               {

                  eventBus.fireEvent(new ExceptionThrownEvent(exception,
                     "Service is not deployed.<br>Parent folder not found."));
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
         eventBus.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Get the list of available workspaces. 
    */
   private void getWorkspaces()
   {
//      DiscoveryService.getInstance().getEntryPoints(new DiscoveryCallback()
//      {
//         @Override
//         protected void onSuccess(List<EntryPoint> result)
//         {
//            FolderModel root = new FolderModel();
//            root.getChildren().setItems(new ArrayList<Item>());
//            for (EntryPoint entryPoint : result)
//            {
//               Workspace workspace = new Workspace(entryPoint.getHref());
////               workspace.setIcon(Images.ClassPath.WORKSPACE);
//               root.getChildren().getItems().add(workspace);
//            }
//            display.getItemsTree().setValue(root);
//         }
//      });
   }

   /**
    * Add chosen source to class path.
    */
   private void addSourceToBuildPath()
   {
      List<GroovyClassPathEntry> classPathEntries = new ArrayList<GroovyClassPathEntry>();
      for (Item item : display.getSelectedItems())
      {
         String path = item.getPath();//GroovyClassPathUtil.formPathFromHref(item.getHref(), restContext);
         String kind = (item instanceof FileModel) ? EnumSourceType.FILE.getValue() : EnumSourceType.DIR.getValue();
         GroovyClassPathEntry groovyClassPathEntry = GroovyClassPathEntry.build(kind, path);
         classPathEntries.add(groovyClassPathEntry);
      }

      eventBus.fireEvent(new AddSourceToBuildPathEvent(classPathEntries));
      closeView();
   }

}
