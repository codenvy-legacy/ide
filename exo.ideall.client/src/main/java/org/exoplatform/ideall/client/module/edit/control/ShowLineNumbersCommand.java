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
package org.exoplatform.ideall.client.module.edit.control;

import org.exoplatform.ideall.client.IDEImageBundle;
import org.exoplatform.ideall.client.cookie.event.BrowserCookiesUpdatedEvent;
import org.exoplatform.ideall.client.cookie.event.BrowserCookiesUpdatedHandler;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.framework.control.IDEControl;
import org.exoplatform.ideall.client.module.edit.event.ShowLineNumbersEvent;
import org.exoplatform.ideall.vfs.api.File;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ShowLineNumbersCommand extends IDEControl implements EditorActiveFileChangedHandler,
   BrowserCookiesUpdatedHandler
{

   private static final String ID = "Edit/Show \\ Hide Line Numbers";

   private static final String TITLE_SHOW = "Show Line Numbers";

   private static final String TITLE_HIDE = "Hide Line Numbers";

   private File activeFile;

   public ShowLineNumbersCommand()
   {
      super(ID);
      setTitle(TITLE_HIDE);
      setPrompt(TITLE_HIDE);
      setImages(IDEImageBundle.INSTANCE.hideLineNumbers(), IDEImageBundle.INSTANCE.hideLineNumbersDisabled());
   }

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(EditorActiveFileChangedEvent.TYPE, this);
      addHandler(BrowserCookiesUpdatedEvent.TYPE, this);
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
         setTitle(TITLE_HIDE);
         setPrompt(TITLE_HIDE);
         setImages(IDEImageBundle.INSTANCE.hideLineNumbers(), IDEImageBundle.INSTANCE.hideLineNumbersDisabled());
         setEvent(new ShowLineNumbersEvent(false));
      }
      else
      {
         //show
         setTitle(TITLE_SHOW);
         setPrompt(TITLE_SHOW);
         setImages(IDEImageBundle.INSTANCE.showLineNumbers(), IDEImageBundle.INSTANCE.showLineNumbersDisabled());
         setEvent(new ShowLineNumbersEvent(true));
      }

      // verify and show
      if (activeFile == null)
      {
         setVisible(false);
         setEnabled(false);
      }
      else
      {
         setVisible(true);
         setEnabled(true);
      }
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
      updateState();
   }

   public void onBrowserCookiesUpdated(BrowserCookiesUpdatedEvent event)
   {
      updateState();
   }

}
