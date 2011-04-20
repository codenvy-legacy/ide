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

import com.google.gwt.event.dom.client.HasClickHandlers;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.Border;
import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.VPanel;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.git.client.GitClientBundle;
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

   public static final String TYPE = "modal";

   public static final String TITLE = "Remote repositories";

   private static final int BUTTON_HEIGHT = 22;

   private static final int BUTTON_WIDTH = 90;

   /*Elements IDs*/

   private static final String ADD_BUTTON_ID = "ideRemoteViewAddButton";

   private static final String DELETE_BUTTON_ID = "ideRemoteViewDeleteButton";

   private static final String CANCEL_BUTTON_ID = "ideRemoteViewCancelButton";

   /* Element's titles*/
   private static final String ADD_BUTTON_TITLE = "Add";

   private static final String DELETE_BUTTON_TITLE = "Delete";

   private static final String CANCEL_BUTTON_TITLE = "Cancel";

   /**
    * Create remote repository button.
    */
   private IButton addButton;

   /**
    * Delete remote repository button.
    */
   private IButton deleteButton;

   /**
    * Cancel button.
    */
   private IButton cancelButton;

   /**
    * Grid with remote repositories.
    */
   private RemoteGrid remoteGrid;

   public RemoteView()
   {
      super(ID, TYPE, TITLE, null, WIDTH, HEIGHT);

      VPanel vPanel = new VPanel();
      Border border = new Border();
      vPanel.add(border);

      remoteGrid = new RemoteGrid();
      remoteGrid.setSize("100%", "100%");
      border.add(remoteGrid);
      border.setMargin(5);

      addButtonsLayout(vPanel);
      add(vPanel);
   }

   /**
    * Add buttons to the pointed panel.
    * 
    * @param panel
    */
   private void addButtonsLayout(Panel panel)
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setHeight(50 + "px");
      buttonsLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      buttonsLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
      buttonsLayout.setSpacing(5);

      addButton =
         createButton(ADD_BUTTON_ID, ADD_BUTTON_TITLE, GitClientBundle.INSTANCE.add(),
            GitClientBundle.INSTANCE.addDisabled());
      deleteButton =
         createButton(DELETE_BUTTON_ID, DELETE_BUTTON_TITLE, GitClientBundle.INSTANCE.remove(),
            GitClientBundle.INSTANCE.removeDisabled());
      cancelButton =
         createButton(CANCEL_BUTTON_ID, CANCEL_BUTTON_TITLE, GitClientBundle.INSTANCE.cancel(),
            GitClientBundle.INSTANCE.cancelDisabled());

      buttonsLayout.add(addButton);
      buttonsLayout.add(deleteButton);
      buttonsLayout.add(cancelButton);

      buttonsLayout.setCellWidth(cancelButton, "100%");
      buttonsLayout.setCellHorizontalAlignment(cancelButton, HasHorizontalAlignment.ALIGN_RIGHT);
      panel.add(buttonsLayout);
   }

   /**
    * Creates button.
    * 
    * @param id button's id
    * @param title button's title
    * @param icon button's normal icon
    * @param disabledIcon button's icon in disabled state
    * @return {@link IButton}
    */
   private IButton createButton(String id, String title, ImageResource icon, ImageResource disabledIcon)
   {
      IButton button = new IButton(title);
      button.setID(id);
      button.setIcon(icon.getURL(), disabledIcon.getURL());
      button.setHeight(BUTTON_HEIGHT);
      button.setWidth(BUTTON_WIDTH);
      return button;
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
    * @see org.exoplatform.ide.git.client.remote.RemotePresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
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
