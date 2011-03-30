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

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.git.client.GitClientBundle;
import org.exoplatform.ide.git.client.clone.CloneRepositoryEvent;

/**
 * Control for cloning remote repository to local one.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 22, 2011 3:49:23 PM anya $
 *
 */
public class CloneRepositoryControl extends SimpleControl implements IDEControl, ItemsSelectedHandler
{
   /**
    * Control ID.
    */
   public static final String ID = "Git/Clone repository";

   /**
    * Control's title.
    */
   public static final String TITLE = "Clone repository";

   /**
   * Control's prompt, when user hovers the mouse on it.
   */
   public static final String PROMPT = "Clone repository to local one";

   /**
    * @param id
    */
   public CloneRepositoryControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(GitClientBundle.INSTANCE.cloneRepo(), GitClientBundle.INSTANCE.cloneRepoDisabled());
      setEvent(new CloneRepositoryEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   @Override
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      setVisible(true);
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
      //Check whether folder is selected:
      boolean enabled = (event.getSelectedItems().get(0) instanceof Folder);
      setEnabled(enabled);
   }
}
