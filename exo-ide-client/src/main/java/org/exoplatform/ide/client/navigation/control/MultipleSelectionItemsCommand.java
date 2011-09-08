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
package org.exoplatform.ide.client.navigation.control;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.client.navigation.WorkspacePresenter;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public abstract class MultipleSelectionItemsCommand extends SimpleControl implements IDEControl,
   VfsChangedHandler, ViewVisibilityChangedHandler
{

   protected boolean browserSelected = true;

   private VirtualFileSystemInfo vfsInfo;

   public MultipleSelectionItemsCommand(String id)
   {
      super(id);
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(ViewVisibilityChangedEvent.TYPE, this);
      eventBus.addHandler(VfsChangedEvent.TYPE, this);
   }

   public boolean isItemsInSameFolder(List<Item> items)
   {
      List<String> hrefs = new ArrayList<String>();
      for (Item i : items)
      {
         if (i.getId().equals(vfsInfo.getRoot().getId()))
         {
            return false;
         }
         String p = i.getPath();
         p = p.substring(0, p.lastIndexOf("/"));
         // folders href ends with "/"
//         if (i instanceof FolderModel)
//         {
//            p = p.substring(0, p.lastIndexOf("/"));
//         }
         hrefs.add(p);

      }

      for (int i = 0; i < hrefs.size(); i++)
      {
         String path = hrefs.get(i);
         for (int j = i + 1; j < hrefs.size(); j++)
         {
            if (!path.equals(hrefs.get(j)))
            {
               return false;
            }
         }
      }

      return true;
   }

   protected abstract void updateEnabling();

   public void onVfsChanged(VfsChangedEvent event)
   {
      this.vfsInfo = event.getVfsInfo();
      if (event.getEntryPoint() != null)
      {
         setVisible(true);
      }
      else
      {
         setVisible(false);
      }

   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent)
    */
   @Override
   public void onViewVisibilityChanged(ViewVisibilityChangedEvent event)
   {
      if (event.getView() instanceof WorkspacePresenter.Display)
      {
         browserSelected = event.getView().isViewVisible();
         updateEnabling();
      }
   }
}
