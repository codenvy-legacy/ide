/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.client.module.groovy.classpath.ui;

import com.google.gwt.http.client.URL;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ide.client.framework.discovery.DiscoveryService;
import org.exoplatform.ide.client.framework.discovery.EntryPoint;
import org.exoplatform.ide.client.framework.discovery.event.EntryPointsReceivedEvent;
import org.exoplatform.ide.client.framework.discovery.event.EntryPointsReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.event.ChildrenReceivedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ChildrenReceivedHandler;
import org.exoplatform.ide.client.module.groovy.Images;
import org.exoplatform.ide.client.module.groovy.classpath.EnumSourceType;
import org.exoplatform.ide.client.module.groovy.classpath.GroovyClassPathEntry;
import org.exoplatform.ide.client.module.groovy.classpath.Workspace;
import org.exoplatform.ide.client.module.groovy.classpath.ui.event.AddSourceToBuildPathEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for choosing source for class path.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jan 10, 2011 $
 *
 */
public class ChooseSourcePathPresenter implements EntryPointsReceivedHandler, ChildrenReceivedHandler
{
   public interface Display
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
       * Close the view.
       */
      void closeView();

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

   /**
    * Handler.
    */
   private Handlers handlers;

   /**
    * The REST context.
    */
   private String restContext;

   private final String WEBDAV_CONTEXT = "/jcr/";

   private final String JCR_PROTOCOL = "jcr://";

   /**
    * @param eventBus handler manager
    * @param restContext REST context
    */
   public ChooseSourcePathPresenter(HandlerManager eventBus, String restContext)
   {
      this.eventBus = eventBus;
      this.restContext = restContext;

      handlers = new Handlers(eventBus);

      Display display = new ChooseSourcePathForm(eventBus);
      bindDisplay(display);
      display.enableOkButtonState(false);
      getWorkspaces();
   }

   /**
    * Bind pointed display with presenter.
    * 
    * @param d display
    */
   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getItemsTree().addOpenHandler(new OpenHandler<Item>()
      {

         public void onOpen(OpenEvent<Item> event)
         {
            if (event.getTarget() instanceof Folder)
            {
               getFolderContent((Folder)event.getTarget());
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
            display.closeView();
         }
      });
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
   private void getFolderContent(Folder folder)
   {
      handlers.addHandler(ChildrenReceivedEvent.TYPE, this);
      VirtualFileSystem.getInstance().getChildren(folder);
   }

   /**
    * Get the list of available workspaces. 
    */
   private void getWorkspaces()
   {
      handlers.addHandler(EntryPointsReceivedEvent.TYPE, this);
      DiscoveryService.getInstance().getEntryPoints();
   }

   /**
    * Add chosen source to class path.
    */
   private void addSourceToBuildPath()
   {
      List<GroovyClassPathEntry> classPathEntries = new ArrayList<GroovyClassPathEntry>();
      for (Item item : display.getSelectedItems())
      {
         String path = formPathFromHref(item.getHref());
         String kind = (item instanceof File) ? EnumSourceType.FILE.getValue() : EnumSourceType.DIR.getValue();
         GroovyClassPathEntry groovyClassPathEntry = GroovyClassPathEntry.build(kind, path);
         classPathEntries.add(groovyClassPathEntry);
      }

      eventBus.fireEvent(new AddSourceToBuildPathEvent(classPathEntries));
      display.closeView();
   }

   /**
    * Get jcr location of the source from WEBDAV href.
    * 
    * @param href WEBDAV href of the source
    * @return String jcr location
    */
   private String formPathFromHref(String href)
   {
      String context = restContext + WEBDAV_CONTEXT;
      String path = href.substring(href.indexOf(context) + context.length());
      String[] parts = path.split("/");

      //Add sybol "#" after workspace name (the second part of the path):
      if (parts.length > 2)
      {
         path = path.replaceFirst(parts[0] + "/" + parts[1], parts[0] + "/" + parts[1] + "#");
      }
      path = JCR_PROTOCOL + path;
      return URL.encode(path);
   }

   /**
    * @see org.exoplatform.ide.client.model.discovery.event.EntryPointsReceivedHandler#onEntryPointsReceived(org.exoplatform.ide.client.model.discovery.event.EntryPointsReceivedEvent)
    */
   public void onEntryPointsReceived(EntryPointsReceivedEvent event)
   {
      handlers.removeHandler(EntryPointsReceivedEvent.TYPE);
      Folder root = new Folder(null);
      root.setChildren(new ArrayList<Item>());
      for (EntryPoint entryPoint : event.getEntryPointList())
      {
         Workspace workspace = new Workspace(entryPoint.getHref());
         workspace.setIcon(Images.ClassPath.WORKSPACE);
         root.getChildren().add(workspace);
      }
      display.getItemsTree().setValue(root);
   }

   /**
    * @see org.exoplatform.ide.client.framework.vfs.event.ChildrenReceivedHandler#onChildrenReceived(org.exoplatform.ide.client.framework.vfs.event.ChildrenReceivedEvent)
    */
   public void onChildrenReceived(ChildrenReceivedEvent event)
   {
      handlers.removeHandler(ChildrenReceivedEvent.TYPE);
      display.getItemsTree().setValue(event.getFolder());
   }
}
