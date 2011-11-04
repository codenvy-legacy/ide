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
package org.exoplatform.ide.client.operation.createfolder;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.client.navigator.NavigatorPresenter;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"administrators", "developers"})
public class CreateFolderControl extends SimpleControl implements IDEControl, ItemsSelectedHandler,
   ViewVisibilityChangedHandler, ViewActivatedHandler, ViewOpenedHandler, ViewClosedHandler
{

   private boolean folderItemSelected = true;

   private boolean browserPanelSelected = true;

   public final static String ID = "File/New/Create Folder...";
   
   private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.createFolderTitleControl();
   
   private static final String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.createFolderPromptControl();

   public CreateFolderControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(IDEImageBundle.INSTANCE.newFolder(), IDEImageBundle.INSTANCE.newFolderDisabled());
      setEvent(new CreateFolderEvent());
      setVisible(true);
      setGroup(0);
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(ViewVisibilityChangedEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(ViewActivatedEvent.TYPE, this);
      eventBus.addHandler(ViewOpenedEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }

   private void updateEnabling()
   {
      if (!browserPanelSelected)
      {
         setEnabled(false);
         return;
      }
      if (folderItemSelected)
      {
         setEnabled(true);
      }
      else
      {
         setEnabled(false);
      }
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
//      System.out.println("selected items > " + event.getSelectedItems().size() );
      
      if (event.getSelectedItems().size() != 1)
      {
         folderItemSelected = false;
         updateEnabling();
         return;
      }

      folderItemSelected = true;
      updateEnabling();
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent)
    */
   @Override
   public void onViewVisibilityChanged(ViewVisibilityChangedEvent event)
   {
//      System.out.println("view visibility changed > " + event.getView().getId());
      
      if (event.getView() instanceof NavigatorPresenter.Display)
      {
         browserPanelSelected = event.getView().isViewVisible();
         updateEnabling();
      }
   }

   @Override
   public void onViewActivated(ViewActivatedEvent event)
   {
//      System.out.println("view activated > " + event.getView().getId());
      
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
//      System.out.println("view closed > " + event.getView().getId());
   }

   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
//      System.out.println("view opened > " + event.getView().getId());
   }

}
