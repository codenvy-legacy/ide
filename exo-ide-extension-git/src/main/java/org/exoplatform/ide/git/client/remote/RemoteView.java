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
package org.exoplatform.ide.git.client.remote;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.shared.Remote;

/**
 * View for remote repositories list with possibility to add and delete remote repository. 
 * Must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 18, 2011 12:31:05 PM anya $
 *
 */
public class RemoteView extends ViewImpl implements RemotePresenter.Display
{
   private static final int HEIGHT = 280;

   private static final int WIDTH = 480;

   public static final String ID = "ideRemoteView";

   /*Elements IDs*/

   private static final String ADD_BUTTON_ID = "ideRemoteViewAddButton";

   private static final String DELETE_BUTTON_ID = "ideRemoteViewDeleteButton";

   private static final String CLOSE_BUTTON_ID = "ideRemoteViewCloseButton";

   /**
    * Create remote repository button.
    */
   @UiField
   ImageButton addButton;

   /**
    * Delete remote repository button.
    */
   @UiField
   ImageButton deleteButton;

   /**
    * Close button.
    */
   @UiField
   ImageButton closeButton;

   /**
    * Grid with remote repositories.
    */
   @UiField
   RemoteGrid remoteGrid;

   interface RemoteViewUiBinder extends UiBinder<Widget, RemoteView>
   {
   }

   private static RemoteViewUiBinder uiBinder = GWT.create(RemoteViewUiBinder.class);

   public RemoteView()
   {
      super(ID, ViewType.MODAL, GitExtension.MESSAGES.remotesViewTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
      
      addButton.setButtonId(ADD_BUTTON_ID);
      deleteButton.setButtonId(DELETE_BUTTON_ID);
      closeButton.setButtonId(CLOSE_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.git.client.remote.RemotePresenter.Display#getAddButton()
    */
   @Override
   public HasClickHandlers getAddButton()
   {
      return addButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.remote.RemotePresenter.Display#getCloseButton()
    */
   @Override
   public HasClickHandlers getCloseButton()
   {
      return closeButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.remote.RemotePresenter.Display#getRemoteGrid()
    */
   @Override
   public ListGridItem<Remote> getRemoteGrid()
   {
      return remoteGrid;
   }

   /**
    * @see org.exoplatform.ide.git.client.remote.RemotePresenter.Display#getSelectedRemote()
    */
   @Override
   public Remote getSelectedRemote()
   {
      return remoteGrid.getSelectedRemote();
   }

   /**
    * @see org.exoplatform.ide.git.client.remote.RemotePresenter.Display#getDeleteButton()
    */
   @Override
   public HasClickHandlers getDeleteButton()
   {
      return deleteButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.remote.RemotePresenter.Display#enableDeleteButton(boolean)
    */
   @Override
   public void enableDeleteButton(boolean enable)
   {
      deleteButton.setEnabled(enable);
   }
}
