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
package org.exoplatform.ide.client.module.navigation.handler;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.event.EnableStandartErrorsHandlingEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Version;
import org.exoplatform.ide.client.framework.vfs.VersionsCallback;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.versioning.ViewVersionsForm;
import org.exoplatform.ide.client.versioning.event.ShowVersionListEvent;
import org.exoplatform.ide.client.versioning.event.ShowVersionListHandler;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 12, 2010 $
 *
 */
public class ShowVersionListCommandHandler implements ShowVersionListHandler, EditorActiveFileChangedHandler
{
   private HandlerManager eventBus;

   private Handlers handlers;

   private File activeFile;

   public ShowVersionListCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      handlers.addHandler(ShowVersionListEvent.TYPE, this);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   private void getVersionHistory()
   {
      if (activeFile != null && !(activeFile instanceof Version))
      {
         VirtualFileSystem.getInstance().getVersions(activeFile, new VersionsCallback()
         {
            @Override
            protected void onSuccess(VersionsData result)
            {
               if (result.getVersions() != null && result.getVersions().size() > 0)
               {
                  new ViewVersionsForm(eventBus, result.getItem(), result.getVersions());
               }
               else
               {
                  Dialogs.getInstance().showInfo("Item \"" + result.getItem().getName() + "\" has no versions.");
               }
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               String errorMessage = "Versions were not received.";
               eventBus.fireEvent(new ExceptionThrownEvent(errorMessage));
               eventBus.fireEvent(new EnableStandartErrorsHandlingEvent(false));               
            }
         });
      }
      else
      {
         Dialogs.getInstance().showInfo("Please, open file.");
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
   }

   /**
    * @see org.exoplatform.ide.client.versioning.event.ShowVersionListHandler#onShowVersionList(org.exoplatform.ide.client.versioning.event.ShowVersionListEvent)
    */
   public void onShowVersionList(ShowVersionListEvent event)
   {
      getVersionHistory();
   }
}