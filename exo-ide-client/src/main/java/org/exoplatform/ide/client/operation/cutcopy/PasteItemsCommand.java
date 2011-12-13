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
package org.exoplatform.ide.client.operation.cutcopy;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.NavigatorDisplay;
import org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
@RolesAllowed({"administrators", "developers"})
public class PasteItemsCommand extends SimpleControl implements IDEControl, ItemsToPasteSelectedHandler,
   PasteItemsCompleteHandler, ItemsSelectedHandler, VfsChangedHandler, ViewActivatedHandler
{
   public static final String ID = "Edit/Paste Item(s)";

   private final static String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.pasteItemsTitleControl();

   private final static String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.pasteItemsPromptControl();

   private boolean itemsToPasteSelected = false;

   /**
    * 
    */
   public PasteItemsCommand()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(IDEImageBundle.INSTANCE.paste(), IDEImageBundle.INSTANCE.pasteDisabled());
      setEvent(new PasteItemsEvent());
   }

   /**
    * @see org.exoplatform.ide.client.navigation.control.MultipleSelectionItemsCommand#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(ItemsToPasteSelectedEvent.TYPE, this);
      IDE.addHandler(PasteItemsCompleteEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(ViewActivatedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.navigation.event.ItemsToPasteSelectedHandler#onItemsToPasteSelected(org.exoplatform.ide.client.navigation.event.ItemsToPasteSelectedEvent)
    */
   @Override
   public void onItemsToPasteSelected(ItemsToPasteSelectedEvent event)
   {
      itemsToPasteSelected = true;
      setEnabled(true);
   }

   /**
    * @see org.exoplatform.ide.client.navigation.event.PasteItemsCompleteHandler#onPasteItemsComlete(org.exoplatform.ide.client.navigation.event.PasteItemsCompleteEvent)
    */
   @Override
   public void onPasteItemsComlete(PasteItemsCompleteEvent event)
   {
      itemsToPasteSelected = false;
      setEnabled(false);
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() != 1)
      {
         setEnabled(false);
         return;
      }

      if (itemsToPasteSelected)
      {
         setEnabled(true);
      }
      else
      {
         setEnabled(false);
      }
   }

   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      if (event.getVfsInfo() != null)
      {
         setVisible(true);
      }
      else
      {
         setVisible(false);
      }
   }

   @Override
   public void onViewActivated(ViewActivatedEvent event)
   {
      if (!(event.getView() instanceof NavigatorDisplay || event.getView() instanceof ProjectExplorerDisplay))
      {
         setEnabled(false);
      }
   }

}
