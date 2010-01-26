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
package org.exoplatform.ideall.client.common.command.edit;

import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.application.component.SimpleCommand;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.event.edit.ShowLineNumbersEvent;
import org.exoplatform.ideall.client.model.File;
import org.exoplatform.ideall.client.model.settings.event.ApplicationContextSavedEvent;
import org.exoplatform.ideall.client.model.settings.event.ApplicationContextSavedHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ShowLineNumbersCommand extends SimpleCommand implements EditorActiveFileChangedHandler,
   ApplicationContextSavedHandler
{

   private static final String ID = "Edit/Show Line Numbers";

   private static final String TITLE = "Show Line Numbers";

   private File activeFile;

   public ShowLineNumbersCommand()
   {
      super(ID, TITLE, Images.Edit.SHOW_LINE_NUMBERS, new ShowLineNumbersEvent());
   }

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(EditorActiveFileChangedEvent.TYPE, this);
      addHandler(ApplicationContextSavedEvent.TYPE, this);
   }

   @Override
   protected void onInitializeApplication()
   {
      updateState();
   }

   private void updateState()
   {
      if (context.isShowLineNumbers())
      {
         // hide
         setVisible(false);
         return;
      }

      // verify and show
      setVisible(true);
      if (activeFile == null)
      {
         setEnabled(false);
      }
      else
      {
         setEnabled(true);
      }
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
      updateState();
   }

   public void onApplicationContextSaved(ApplicationContextSavedEvent event)
   {
      updateState();
   }

}
