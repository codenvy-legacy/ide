/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.module.navigation.control;

import org.exoplatform.gwtframework.ui.client.component.command.SimpleControl;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ide.client.module.navigation.event.OpenFileWithEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.vfs.File;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class OpenFileWithCommand extends SimpleControl implements ItemsSelectedHandler, EntryPointChangedHandler
{
   private static final String ID = "File/Open File With...";

   private boolean browserPanelSelected = true;

   public OpenFileWithCommand(HandlerManager eventBus)
   {
      super(ID);
      setTitle("Open With...");
      setPrompt("Open File With...");
      setImages(IDEImageBundle.INSTANCE.openWith(), IDEImageBundle.INSTANCE.openWithDisabled());
      setEvent(new OpenFileWithEvent());

      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(EntryPointChangedEvent.TYPE, this);
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (!browserPanelSelected)
      {
         setEnabled(false);
         return;
      }
      if (event.getSelectedItems().size() != 1 || !(event.getSelectedItems().get(0) instanceof File))
      {
         setEnabled(false);
         return;
      }
      setEnabled(true);
   }

   public void onEntryPointChanged(EntryPointChangedEvent event)
   {
      if (event.getEntryPoint() != null) {
         setVisible(true);
      } else {
         setVisible(false);
      }
   }

}
