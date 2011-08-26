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
package org.exoplatform.ide.git.client.remove;

import com.google.gwt.core.client.GWT;

import com.google.gwt.user.client.ui.Image;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.component.Border;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.git.client.GitClientBundle;
import org.exoplatform.ide.git.client.GitExtension;

/**
 * View for removing files from working directory and index.
 * Must be added to Views.gwt.xml file.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 12, 2011 5:57:31 PM anya $
 *
 */
public class RemoveFilesView extends ViewImpl implements RemoveFilesPresenter.Display
{
   private static final int HEIGHT = 290;

   private static final int WIDTH = 480;

   public static final String ID = "ideRemoveFilesView";

   private static final int BUTTON_HEIGHT = 22;

   private static final int BUTTON_WIDTH = 90;

   /*Elements IDs*/
   private static final String REMOVE_BUTTON_ID = "ideRemoveFilesViewRemoveButton";

   private static final String CANCEL_BUTTON_ID = "ideRemoveFilesViewCancelButton";

   /**
    *Save changes button.
    */
   private ImageButton removeButton;

   /**
    * Cancel button.
    */
   private ImageButton cancelButton;

   /**
    * The grid to view files in index.
    */
   private IndexFilesGrid indexFilesGrid;

   public RemoveFilesView()
   {
      super(ID, ViewType.MODAL, GitExtension.MESSAGES.removeFilesViewTitle(), null, WIDTH, HEIGHT);

      VerticalPanel mainLayout = new VerticalPanel();
      mainLayout.setWidth("100%");
      mainLayout.setHeight("100%");
      mainLayout.setSpacing(10);
      mainLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      mainLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

      Border border = GWT.create(Border.class);
      border.setWidth("100%");
      indexFilesGrid = new IndexFilesGrid();
      indexFilesGrid.setWidth("100%");
      indexFilesGrid.setHeight(180);
      border.add(indexFilesGrid);
      
      mainLayout.add(border);

      addButtonsLayout(mainLayout);

      add(mainLayout);
   }

   /**
    * Add buttons to the pointed panel.
    * 
    * @param panel
    */
   private void addButtonsLayout(VerticalPanel panel)
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setHeight(BUTTON_HEIGHT + 10 + "px");
      buttonsLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
      buttonsLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
      buttonsLayout.setSpacing(5);

      removeButton =
         createButton(REMOVE_BUTTON_ID, GitExtension.MESSAGES.buttonRemove(), GitClientBundle.INSTANCE.ok(),
            GitClientBundle.INSTANCE.okDisabled());
      cancelButton =
         createButton(CANCEL_BUTTON_ID, GitExtension.MESSAGES.buttonCancel(), GitClientBundle.INSTANCE.cancel(),
            GitClientBundle.INSTANCE.cancelDisabled());

      buttonsLayout.add(removeButton);
      buttonsLayout.add(cancelButton);

      panel.add(buttonsLayout);
   }

   /**
    * Creates button.
    * 
    * @param id button's id
    * @param title button's title
    * @param icon button's normal icon
    * @param disabledIcon button's icon in disabled state
    * @return {@link ImageButton}
    */
   private ImageButton createButton(String id, String title, ImageResource icon, ImageResource disabledIcon)
   {
      ImageButton button = new ImageButton(title);
      button.setButtonId(id);
      button.setImages(new Image(icon), new Image(disabledIcon));
      button.setHeight(BUTTON_HEIGHT + "px");
      button.setWidth(BUTTON_WIDTH + "px");
      return button;
   }

   /**
    * @see org.exoplatform.ide.git.client.remove.RemoveFilesPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.remove.RemoveFilesPresenter.Display#getRemoveButton()
    */
   @Override
   public HasClickHandlers getRemoveButton()
   {
      return removeButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.remove.RemoveFilesPresenter.Display#getIndexFilesGrid()
    */
   @Override
   public ListGrid<IndexFile> getIndexFilesGrid()
   {
      return indexFilesGrid;
   }
}
