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
package org.exoplatform.ide.git.client.delete;

import com.google.gwt.http.client.URL;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;

/**
 * Delete repository command handler, performs deleting Git repository.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 21, 2011 5:57:30 PM anya $
 *
 */
public class DeleteRepositoryCommandHandler extends GitPresenter implements DeleteRepositoryHandler
{
   /**
    * @param eventBus event handlers manager
    */
   public DeleteRepositoryCommandHandler(HandlerManager eventBus)
   {
      super(eventBus);

      eventBus.addHandler(DeleteRepositoryEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.git.client.delete.DeleteRepositoryHandler#onDeleteRepository(org.exoplatform.ide.git.client.delete.DeleteRepositoryEvent)
    */
   @Override
   public void onDeleteRepository(DeleteRepositoryEvent event)
   {
      getWorkDir();
   }

   /**
    * Confirm, that user wants to delete Git repository.
    * 
    * @param repository
    */
   protected void askBeforeDelete(String repository)
   {
      Dialogs.getInstance().ask(GitExtension.MESSAGES.deleteGitRepositoryTitle(),
         GitExtension.MESSAGES.deleteGitRepositoryQuestion(repository), new BooleanValueReceivedHandler()
         {
            @Override
            public void booleanValueReceived(Boolean value)
            {
               if (value != null && value)
               {
                  doDeleteRepository();
               }
            }
         });
   }

   /**
    * Perform deleting Git repository.
    */
   public void doDeleteRepository()
   {
      if (workDir == null || workDir.isEmpty())
         return;
      GitClientService.getInstance().deleteWorkDir(workDir, new AsyncRequestCallback<String>()
      {
         @Override
         protected void onSuccess(String result)
         {
            eventBus.fireEvent(new OutputEvent(GitExtension.MESSAGES.deleteGitRepositorySuccess(), Type.INFO));
            String href = URL.decode(workDir);
            //TODO to fix this with encoding symbol "@"
            href = href.replaceAll("@", "%40");
            eventBus.fireEvent(new RefreshBrowserEvent(new Folder(getParentFolder(workDir)), new Folder((href))));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.git.client.GitPresenter#onWorkDirReceived()
    */
   @Override
   public void onWorkDirReceived()
   {
      askBeforeDelete(workDir);
   }

   public String getParentFolder(String child)
   {
      String href = child.endsWith("/") ? child.substring(0, child.lastIndexOf("/")) : child;
      href = href.substring(0, href.lastIndexOf("/") + 1);
      href = URL.encode(href);
      //TODO to fix this with encoding symbol "@"
      return href.replaceAll("@", "%40");
   }
}
