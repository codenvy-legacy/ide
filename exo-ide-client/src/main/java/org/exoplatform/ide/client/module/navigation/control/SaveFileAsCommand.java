/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ide.client.module.navigation.control;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.event.SaveFileAsEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.vfs.Version;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SaveFileAsCommand extends MultipleSelectionItemsCommand implements EditorActiveFileChangedHandler,
   ItemsSelectedHandler
{

   private static final String ID = "File/Save As...";

   private static final String TITLE = "Save As...";

   private boolean activeFileSelected = false;

   private boolean singleItemSelected = true;

   public SaveFileAsCommand(HandlerManager eventBus)
   {
      super(ID, eventBus);
      setTitle(TITLE);
      setPrompt(TITLE);
      setImages(IDEImageBundle.INSTANCE.saveAs(), IDEImageBundle.INSTANCE.saveAsDisabled());
      setEvent(new SaveFileAsEvent());

      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null)
      {
         activeFileSelected = false;
         updateEnabling();
      }
      else
      {
         activeFileSelected = true;
         updateEnabling();
      }
   }

   @Override
   protected void updateEnabling()
   {
      if (browserSelected && activeFileSelected && singleItemSelected)
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
      if (event.getSelectedItems().size() == 1)
      {
         singleItemSelected = true;
         updateEnabling();
      }
      else
      {
         singleItemSelected = false;
         updateEnabling();
      }
   }

}
