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
package org.exoplatform.ide.git.client.control;

import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.git.client.GitClientBundle;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.clone.CloneRepositoryEvent;

/**
 * Control for cloning remote repository to local one.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 22, 2011 3:49:23 PM anya $
 *
 */
public class CloneRepositoryControl extends GitControl
{
   public CloneRepositoryControl()
   {
      super(GitExtension.MESSAGES.cloneControlId());
      setTitle(GitExtension.MESSAGES.cloneControlTitle());
      setPrompt(GitExtension.MESSAGES.cloneControlPrompt());
      setImages(GitClientBundle.INSTANCE.cloneRepo(), GitClientBundle.INSTANCE.cloneRepoDisabled());
      setEvent(new CloneRepositoryEvent());
   }

   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() != 1)
      {
         setEnabled(false);
         return;
      }
      //Check whether folder is selected:
      boolean enabled =
         ((event.getSelectedItems().get(0) instanceof Folder) && !isWorkspaceSelected(event.getSelectedItems().get(0)
            .getHref()));
      setEnabled(enabled);
   }
}
