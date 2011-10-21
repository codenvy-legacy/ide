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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.git.client.GitExtension;
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
   private static final int HEIGHT = 290;

   private static final int WIDTH = 480;

   public static final String ID = "ideResetFilesView";

   /*Elements IDs*/
   private static final String RESET_BUTTON_ID = "ideResetFilesViewResetButton";

   private static final String CANCEL_BUTTON_ID = "ideResetFilesViewCancelButton";

   /**
    *Reset files button.
    */
   @UiField
   ImageButton resetButton;

   /**
    * Cancel button.
    */
   @UiField
   ImageButton cancelButton;

   /**
    * The grid to view files in index.
    */
   @UiField
   IndexFilesGrid indexFilesGrid;

   interface ResetFilesViewUiBinder extends UiBinder<Widget, ResetFilesView>
   {
   }

   private static ResetFilesViewUiBinder uiBinder = GWT.create(ResetFilesViewUiBinder.class);

   public ResetFilesView()
   {
      super(ID, ViewType.MODAL, GitExtension.MESSAGES.resetFilesViewTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      resetButton.setButtonId(RESET_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
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
