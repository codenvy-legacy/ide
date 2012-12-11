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
package com.google.collide.client.openfile;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Item;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 
 */
public class OpenFileCollaborationControl extends SimpleControl implements IDEControl, ItemsSelectedHandler
{

   public OpenFileCollaborationControl()
   {
      super("File/OpenCollaboration");
      setEnabled(true);
      setVisible(true);
      setEvent(new OpenFileCollaborationEvent());
      setTitle("Open File Collaboration");
      setPrompt("Open File in Collaboration mode");
   }

   @Override
   public void initialize()
   {
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (!event.getSelectedItems().isEmpty())
      {
         Item item = event.getSelectedItems().get(0);
         if (item instanceof File)
         {
            setEnabled(true);
            return;
         }
      }
      setEnabled(false);
   }
}
