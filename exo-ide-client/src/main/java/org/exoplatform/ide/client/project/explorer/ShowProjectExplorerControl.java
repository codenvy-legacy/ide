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

package org.exoplatform.ide.client.project.explorer;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import com.google.gwt.event.shared.HandlerManager;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@RolesAllowed({"administrators", "developers"})
public class ShowProjectExplorerControl extends SimpleControl implements IDEControl, ItemsSelectedHandler, ViewOpenedHandler, ViewClosedHandler
{
   
   public static final String ID = "Window/Show View/Project Explorer";

   private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.projectExplorerControlTitle();

   private static final String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.projectExplorerControlPrompt();   
   
   public ShowProjectExplorerControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(IDEImageBundle.INSTANCE.projectExplorer(), IDEImageBundle.INSTANCE.projectExplorerDisabled());
      setEvent(new ShowProjectExplorerEvent());      
   }
   
   @Override
   public void initialize(HandlerManager eventBus)
   {
      IDE.EVENT_BUS.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(ViewOpenedEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(ViewClosedEvent.TYPE, this);
      
      setEnabled(true);
      setVisible(true);
   }    

   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() != 1) {
         setEnabled(false);
         return;
      }
      
      Item selectedItem = event.getSelectedItems().get(0);
      if (!(selectedItem instanceof ProjectModel)) {
         setEnabled(false);
         return;
      }
      
      setEnabled(true);
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof TinyProjectExplorerPresenter.Display
               || event.getView() instanceof ProjectExplorerPresenter.Display) {
         setSelected(false);
      }
   }

   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
      if (event.getView() instanceof ProjectExplorerPresenter.Display
               || event.getView() instanceof TinyProjectExplorerPresenter.Display) {
         setSelected(true);
      }
      
   }

}
