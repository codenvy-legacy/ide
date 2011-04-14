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
package org.exoplatform.ide.git.client.reset;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.git.client.GitClientBundle;
import org.exoplatform.ide.git.client.remove.IndexFile;
import org.exoplatform.ide.git.client.remove.IndexFilesGrid;

/**
 * View for reseting files in index.
 * Must be added to Views.gwt.xml file.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 12, 2011 5:57:31 PM anya $
 *
 */
public class ResetFilesView extends ViewImpl implements ResetFilesPresenter.Display
{
   private static final int HEIGHT = 280;

   private static final int WIDTH = 480;

   public static final String ID = "ideResetFilesView";

   public static final String TYPE = "modal";

   public static final String TITLE = "Select files to reset";

   private static final int BUTTON_HEIGHT = 22;

   private static final int BUTTON_WIDTH = 90;

   /*Elements IDs*/
   private static final String RESET_BUTTON_ID = "ideResetFilesViewResetButton";

   private static final String CANCEL_BUTTON_ID = "ideResetFilesViewCancelButton";

   /* Element's titles*/
   private static final String RESET_BUTTON_TITLE = "Reset";

   private static final String CANCEL_BUTTON_TITLE = "Cancel";

   /**
    *Reset files button.
    */
   private IButton resetButton;

   /**
    * Cancel button.
    */
   private IButton cancelButton;

   /**
    * The grid to view files in index.
    */
   private IndexFilesGrid indexFilesGrid;

   public ResetFilesView()
   {
      super(ID, TYPE, TITLE, null, WIDTH, HEIGHT);

      VerticalPanel mainLayout = new VerticalPanel();
      mainLayout.setWidth("100%");
      mainLayout.setHeight("100%");
      mainLayout.setSpacing(10);
      mainLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      mainLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

      indexFilesGrid = new IndexFilesGrid();
      indexFilesGrid.setWidth("100%");
      indexFilesGrid.setHeight(180);
      mainLayout.add(indexFilesGrid);

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

      resetButton =
         createButton(RESET_BUTTON_ID, RESET_BUTTON_TITLE, GitClientBundle.INSTANCE.ok(),
            GitClientBundle.INSTANCE.okDisabled());
      cancelButton =
         createButton(CANCEL_BUTTON_ID, CANCEL_BUTTON_TITLE, GitClientBundle.INSTANCE.cancel(),
            GitClientBundle.INSTANCE.cancelDisabled());

      buttonsLayout.add(resetButton);
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
    * @see org.exoplatform.ide.git.client.reset.ResetFilesPresenter.Display#getResetButton()
    */
   @Override
   public HasClickHandlers getResetButton()
   {
      return resetButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.reset.ResetFilesPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.reset.ResetFilesPresenter.Display#getIndexFilesGrid()
    */
   @Override
   public ListGrid<IndexFile> getIndexFilesGrid()
   {
      return indexFilesGrid;
   }
}
